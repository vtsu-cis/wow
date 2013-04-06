/****************************************************************************
FILE          : netstream.cpp
LAST REVISED  : 2007-02-15
SUBJECT       : Main implementation file for the netstream library.
PROGRAMMER    : (C) Copyright 2007 by Peter C. Chapin

LICENSE

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANT-
ABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

Please send comments or bug reports pertaining to this file to

     Peter C. Chapin
     Electrical and Computer Engineering Technology
     Vermont Technical College
     Randolph Center, VT 05061
     pchapin@ecet.vtc.edu
****************************************************************************/

#include "environ.h"
#include <iostream>
#include <stdexcept>
using namespace std;

#include "netstream.h"

#if eOPSYS == eWIN32
#include <windows.h>
#endif

namespace netstream {

    Init::Init( )
    {
        #if eOPSYS == eWIN32
        WSADATA     winsock_information;

        // Try to intialize Winsock.
        if ( WSAStartup( MAKEWORD( 2, 0 ), &winsock_information ) != 0 ) {
            throw runtime_error( "Unable to initialize Winsock" );
        }
        if ( !( LOBYTE( winsock_information.wVersion ) == 2 &&
                HIBYTE( winsock_information.wVersion ) == 0 ) ) {
            WSACleanup( );
            throw runtime_error( "Available Winsock does not support version 2.0" );
        }
        #endif
    }

    Init::~Init( )
    {
        #if eOPSYS == eWIN32
        WSACleanup( );
        #endif
    }

#ifdef BROKEN
    //
    // int netstream::overflow(int ch)
    //
    // Used to dump the buffer when it is full and a new character needs to
    // be added.
    //
    int netstream::overflow( int ch )
    {
        // Send the characters that are buffered. (Errors? What are those?)
        channel.write( start, static_cast<int>( pptr( ) - pbase( ) ) );

        // Reset the put pointers.
        setp( pbase( ), epptr( ) );

        // Put the given character into the buffer.
        if ( ch != EOF ) {
            *pptr( ) = Ch;
            pbump( 1 );
        }

        return 0;
    }

    //
    // int netstream::underflow()
    //
    // Used to reload the buffer when it runs out of characters. This
    // function is tricky because of the need to copy the last character
    // currently in the buffer to the putback position. Also we need to
    // deal with the fact that we might not get all the characters we want
    // from the network connection.
    //
    int netstream::underflow()
    {
        // I need to know where the buffer is, how it is allocated, and how
        // I can get at it. I need this information so that I can set up
        // the get area pointers properly if they are null (first call to
        // under- flow()?) and when I get fewer than the desired number of
        // characters from the stream. My copy of the draft standard is
        // quite lacking in its description of that material.
    }
#endif

}
