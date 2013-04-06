/****************************************************************************
FILE          : sem.cpp
LAST REVISED  : 2007-02-10
SUBJECT       : Implementation of the cross-platform semaphore classes.
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

#include <limits.h>
#include "sem.h"

namespace spica {

    //=================================
    //           counting_sem
    //=================================

    #if eOPSYS == ePOSIX

    counting_sem::counting_sem( int initial )
    {
        if( initial < 0 ) initial = 0;

        raw_count = initial;
        pthread_mutex_init( &lock, 0 );
        pthread_cond_init( &non_zero, 0 );
    }

    counting_sem::~counting_sem( )
    {
        pthread_mutex_destroy( &lock );
        pthread_cond_destroy( &non_zero );
    }

    void counting_sem::up( )
    {
        pthread_mutex_lock( &lock );
        raw_count++;
        pthread_mutex_unlock( &lock );
        pthread_cond_signal( &non_zero );
    }

    void counting_sem::down( )
    {
        pthread_mutex_lock( &lock );
        while ( raw_count == 0 )
            pthread_cond_wait( &non_zero, &lock );

        raw_count--;
        pthread_mutex_unlock( &lock );
    }

    #endif


    #if eOPSYS == eOS2

    counting_sem::counting_sem( int initial )
    {
        if( initial < 0 ) initial = 0;

        raw_count = initial;
        DosCreateMutexSem( 0, &lock, 0, FALSE );
        DosCreateEventSem( 0, &non_zero, 0, FALSE );
    }

    counting_sem::~counting_sem( )
    {
        DosCloseMutexSem( lock );
        DosCloseEventSem( non_zero );
    }

    void counting_sem::up( )
    {
        DosRequestMutexSem( lock, SEM_INDEFINITE_WAIT );
        raw_count++;
        if( raw_count == 1 ) DosPostEventSem( non_zero );
        DosReleaseMutexSem( lock );
    }

    void counting_sem::down( )
    {
        ULONG post_count;

        DosRequestMutexSem( lock, SEM_INDEFINITE_WAIT );

        // If we are downing a non-zero semaphore, proceed without complications.
        if( raw_count > 0 ) {
            raw_count--;
            if( raw_count == 0 ) DosResetEventSem( non_zero, &post_count );
        }

        // Otherwise we are trying to down a zero.
        else {

            // This loop deals with various race conditions.
            do {
                DosReleaseMutexSem( lock );
                DosWaitEventSem( non_zero, SEM_INDEFINITE_WAIT );
                DosRequestMutexSem( lock, SEM_INDEFINITE_WAIT );
            } while( raw_count == 0 );
      
            // We own the lock and the raw count is not zero. We won the race!
            raw_count--;
            if( raw_count == 0 ) DosResetEventSem( non_zero, &post_count );
        }

        DosReleaseMutexSem( lock );
    }

    #endif


    #if eOPSYS == eWIN32

    counting_sem::counting_sem( int initial )
    {
        if( initial < 0 ) initial = 0;
        the_sem = CreateSemaphore( 0, initial, INT_MAX, 0 );
    }

    #endif


    //===========================
    //           rw_sem
    //===========================

    //
    // THIS CLASS IS CURRENTLY UNIMPLEMENTED!
    //

}

