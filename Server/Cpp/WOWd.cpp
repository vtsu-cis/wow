/*****************************************************************************
FILE          : WOWd.cpp
LAST REVISION : 2007-04-22
PROGRAMMER    : (C) Copyright 2007 by Vermont Technical College

This program is a prototype WOW server. It is intended only to help
illustrate the WOW concept to potential users. The final server will
probabaly look nothing like this.

Thanks to the VTC Computer Club for providing the RTWho server which
was the base for this program.

Please send comments or bug reports to

     SoSE
     c/o Chris Beattie
     Vermont Technical College
     Williston, VT
     sose@ecet.vtc.edu
*****************************************************************************/

#include <fstream>
#include <sstream>
#include <string>
#include <vector>
using namespace std;

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <unistd.h>

#include "netstream.h"
#include "sem.h"

#define BUFFER_SIZE 128

// The schema of this "WOW domain" :-)
struct profile_info {
    string first;
    string last;
    string phone;
    string email;
    string campus;
    string role;
    string dept;
    string fax;
    string office;
};

// The vector of profile data.
typedef vector< profile_info > profiledata_t;
profiledata_t    profiles;
spica::mutex_sem profiles_lock;

//
// This function detaches this process from its controlling terminal.
// See "Advanced Programming in the Unix Environment" by W. Richard
// Stevens, Chapter 13, for more information.
//
int daemonize( )
{
    pid_t pid;
 
    if( ( pid = fork( ) ) < 0 ) return -1;
    else if( pid != 0 ) exit( 0 );

    setsid( );
    umask( 077 );
    return( 0 );
}


//
// The following function splits a '|' delimited profile record into its
// various parts and returns the resulting fields in a structure. This
// function is quite inelegant and does essentially no error checking. It
// should only be called on strings with exactly seven fields.
//
profile_info split_record( const string &raw_record )
{
    profile_info      result;
    string::size_type start_marker = 0;
    string::size_type end_marker;
    
    end_marker = raw_record.find_first_of( "|", start_marker );
    result.first = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;
    
    end_marker = raw_record.find_first_of( "|", start_marker );
    result.last = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;
    
    end_marker = raw_record.find_first_of( "|", start_marker );
    result.phone = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;
    
    end_marker = raw_record.find_first_of( "|", start_marker );
    result.email = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;

    end_marker = raw_record.find_first_of( "|", start_marker );
    result.campus = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;

    end_marker = raw_record.find_first_of( "|", start_marker );
    result.role = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;
    
    end_marker = raw_record.find_first_of( "|", start_marker );
    result.dept = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;

    end_marker = raw_record.find_first_of( "|", start_marker );
    result.fax = raw_record.substr( start_marker, end_marker - start_marker );
    start_marker = end_marker + 1;

    end_marker = raw_record.find_first_of( "|", start_marker );
    result.office = raw_record.substr( start_marker, end_marker - start_marker ); 
    start_marker = end_marker + 1;

    return( result );
}


//
// This function reads profile information out of a file. This read
// occurs without locking the profile data so it should be done before
// any processing threads are created.
//
void read_profiles( )
{
    ifstream input( "profiles.dat" );
    string   line;
    
    while( input ) {
        getline( input, line );
        profile_info record = split_record( line );
        profiles.push_back( record );
    }
}


//
// This function writes profile information into a file. This function
// is called under profiles_lock already so there is no need to try
// and lock the profiles again.
//
void write_profiles( )
{
    ofstream output( "profiles.dat" );
    profiledata_t::iterator p;
    
    for( p = profiles.begin( ); p != profiles.end( ); ++p ) {
        ostringstream formatter;
        formatter << p->first    << "|"
                  << p->last     << "|"
                  << p->phone    << "|"
                  << p->email    << "|"
                  << p->campus   << "|"
                  << p->role     << "|"
                  << p->dept     << "|"
                  << p->fax      << "|"
                  << p->office   << "\n";
        output << formatter.str( );
    }
}


//
// The following function handles update requests from the client.
//
void do_update( netstream::Connection *client, const string &raw_update )
{
    profile_info update = split_record( raw_update );
    
    spica::mutex_sem::grabber lock_manager( profiles_lock );
    profiledata_t::iterator p;
    
    // Search for a matching profile.
    for( p = profiles.begin( ); p != profiles.end( ); ++p ) {
       if( p->last == update.last ) {
          if( p->first == update.first ) {
             if( p->email == update.email ) {
                if( p->campus == update.campus ) {
                   // Match found.
                   *p = update;
                   write_profiles( );
                   client->write( "OK\n", 3 );
                   return;
                }
             }
          }
       }
    }
    
    // No matches found, this must be a new profile.
    profiles.push_back( update );
    write_profiles( );
    client->write( "OK\n", 3 );
}

//
// The following function handles delete requests from the client.
//
void do_delete( netstream::Connection *client, const string &raw_delete )
{

   profile_info delete_info = split_record( raw_delete );
   profile_info temp;
   
   spica::mutex_sem::grabber lock_manager( profiles_lock );
   profiledata_t::iterator p;

    

   // Search for matching profile.
   for( p = profiles.begin( ); p!= profiles.end( ); ++p ) {
      if( p->last == delete_info.last ) {
         if( p->first == delete_info.first ) {
            if( p->email == delete_info.email ) {
               if( p->campus == delete_info.campus ) {
                  // Match found.
                  profiles.erase( p );
                  write_profiles( );
                  client->write( "OK\n", 3 );
                  return;
               }
            }
         }
      }
   }
}

//
// Returns 0 if the first string is not found in the second
// Returns 1 if it is found
//
int case_ins_find(string s1, string s2)
{
   for( unsigned int i = 0; i < s1.length(); i++ )
   {
      s1[i] = toupper(s1[i]);
   }
 
   for( unsigned int i = 0; i < s2.length(); i++)
   {
      s2[i] = toupper(s2[i]);
   }

   if ( s2.find(s1) == string::npos ) {
      return 1;
   }
    
   return 0;
 
}


//
// The following function handles query requests from the client.
//
void do_query( netstream::Connection *client, const string &raw_query )
{
    profile_info query = split_record( raw_query );
    
    spica::mutex_sem::grabber lock_manager( profiles_lock );
    profiledata_t::iterator p;

    // Scan over all profiles.    
    for( p = profiles.begin( ); p != profiles.end( ); ++p ) {
        bool is_match = true;
        
        // If this profile is a mismatch, make note of that.
        if( query.first != "" && case_ins_find( query.first, p->first ) ) {
            is_match = false;
        }
        if( query.last != "" && case_ins_find( query.last, p->last ) ) {
            is_match = false;
        }
        if( query.phone != "" && p->phone.find( query.phone ) == string::npos ) {
            is_match = false;
        }
        if( query.email != "" && p->email.find( query.email ) == string::npos ) {
            is_match = false;
        }
        if( query.campus != "" && case_ins_find( query.campus, p->campus ) ) {
            is_match = false;
        }
        if( query.role != "" && case_ins_find( query.role, p->role ) ) {
            is_match = false;
        }
        if( query.dept != "" && case_ins_find( query.dept, p->dept ) ) {
            is_match = false;
        }
        if( query.fax != "" && case_ins_find( query.fax, p->fax ) ) {
            is_match = false;
        }
        if( query.office != "" && case_ins_find( query.office, p->office ) ) {
            is_match = false;
        }

        
        // Send this profile back to the client if it is a match.
        if( is_match ) {
            ostringstream formatter;
            formatter << p->first  << "|"
                      << p->last   << "|"
                      << p->phone  << "|"
                      << p->email  << "|"
                      << p->campus << "|"
                      << p->role   << "|"
                      << p->dept   << "|"
                      << p->fax    << "|"
                      << p->office << "\n";

             client->write( formatter.str( ).c_str( ), formatter.str( ).size( ) );
        }
    }
}

//
// This function deals with a single connection. A new thread is created
// for each connection that arrives and that thread executes this
// function. Thus there might be multiple activations of this function
// occuring at the same time.
//
void *connection_processor( void *arg )
{
    netstream::Connection *client = static_cast< netstream::Connection * >( arg );

    char   buffer[1024];
    int    buffer_index = 0;
    int    buffer_size  = 0;
    string command;
  
    // Read a line of arbitrary length from the client.
    while( 1 ) {
        if( buffer_index == buffer_size ) {
            buffer_size = client->read( buffer, 1024 );
            if( buffer_size == 0 || buffer_size == -1 ) break;
            buffer[buffer_size] = '\0';
            buffer_index = 0;
        }
        char ch = buffer[buffer_index++];
        if( ch == '\r' ) continue;
        if( ch == '\n' ) break;
        command += ch;
    }
  
    // What command is it?
    if( command.substr( 0, 3 ) == "UPD" ) {
        do_update(client, command.substr(5));
    }
    else if( command.substr( 0, 3 ) == "QRY" ) {
        do_query( client, command.substr( 5 ) );
    }
    else if( command.substr( 0, 3 ) == "DEL" ) {
        do_delete( client, command.substr( 5 ) );
    }
    else {
        client->write( "ERROR\n", 6 );
    }
  
    delete client;
    return( NULL );
}


//
// This function is the main server loop. It accepts connections and
// starts a thread to handle each one. This function returns zero if
// it ended normally; one otherwise.
//
int main_loop( netstream::Server *acceptor )
{
    pthread_t  connection_thread; // ID of connection handling thread.

    while( 1 ) {
        netstream::Connection *client = acceptor->accept( );
        pthread_create( &connection_thread, NULL, connection_processor, client );
        pthread_detach( connection_thread );
    }
    return( 0 );
}


//
// Main Program
//
int main( int argc, char **argv )
{
    unsigned short  port = 0;

    if( argc != 2 ) {
      cerr << "Usage: " << argv[0] << " port\n";
      return 1;
    }
    port = atoi( argv[1] );

    // Do various initializations.
    read_profiles( );
    if( daemonize( ) == -1 ) return 1;
    netstream::TCP_Server acceptor( port );
    if( !acceptor.isOk( ) ) return 1;

    // Deal with network connections.
    return main_loop( &acceptor );
}

