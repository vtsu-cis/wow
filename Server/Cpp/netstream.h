/****************************************************************************
FILE          : netstream.h
LAST REVISED  : 2007-02-11
SUBJECT       : Master header for the netstream library.
PROGRAMMER    : (C) Copyright 2007 by Peter C. Chapin

This header provides classes that implement an I/O-stream interface to
connection oriented network services. Also several utility classes are also
provided. Any type of connection supported by the class Connection family
will work with a netstream. All the normal iostream operations are also
supported.

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

#ifndef NETSTREAM_H
#define NETSTREAM_H

#include <iostream>

/*! \namespace netstream
    \brief Name space enclosing the netstream library.
    
    This name space contains all components in the netstream library. No
    third party or external library components are included in the
    netstream name space.
 */
 
/*! \file netstream.h
    \brief Master header for the netstream library.
     
    To simplify use of the netstream library this header is provided. It
    includes all other netstream headers. Simply include netstream.h in
    your project to access any netstream related component. Precompilation
    of this header is recommended.
 */

#include "connection.h"

namespace netstream {

    //! Encapsulates network initialization tasks.
    class Init {
    public:
        //! Prepares the underlying network library for use.
        Init( );

        //! Shuts down the underlying network library.
       ~Init( );
    };

#ifdef BROKEN
    // class netbuf : public std::streambuf {
    //   public:
    //     netbuf( Connection &c ) : channel( c ) { }
    //
    //   private:
    //     Connection &channel;
    // };

    //
    // class netstream
    //
    // I'm not entirely happy with this format. I don't think streambuf
    // should be an immediate base class of netstream. It doesn't quite
    // make sense. What probably *should* happen is: an intermediate class
    // needs to be defined, say "netstreambase," that contains a netbuf
    // object (see above) and that can serve as a base class for
    // onetstream, inetstream, and netstream. That class could also,
    // perhaps, take responsibility for opening the network connection. On
    // the other hand, I sort of like having the connection object
    // separate. That emphasizes how netstreams can use any type of network
    // connection. See the way the fstream classes are defined for more
    // specific ideas.
    //
    class netstream : public std::streambuf, public std::iostream {
    public:
        netstream( Connection &c ) : iostream( this ), channel( c ) { }

    private:
        Connection &channel;

        //
        // The following functions override the ones in streambuf and define
        // how this object is to get and store characters to the final stream.
        // In this case, of course, we read/write the connection object.
        //
        virtual int overflow( int ch );
        virtual int underflow();
    };
#endif

}

#endif
