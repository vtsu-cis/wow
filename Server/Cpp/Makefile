#
# FILE        : Makefile
# LAST REVISED: 2007-04-22
# AUTHOR      : (C) Copyright 2007 by Vermont Technical College
#

# Cygwin doesn't seem to understand the -pthread option. Does that mean
# it doesn't properly support multithreaded programs or does it mean
# that -pthread is just redundant on Cygwin?
#
ifdef CYGWIN
THREAD_FLAGS =
else
THREAD_FLAGS = -pthread
endif

CPPOPTS = -c -Wall -g -I. $(THREAD_FLAGS)
OBJS    = WOWd.o netstream.o connection.o sem.o

all:		WOWd

WOWd:		$(OBJS)
	g++ -o WOWd -g $(OBJS) $(THREAD_FLAGS)

WOWd.o:		WOWd.cpp
	g++ $(CPPOPTS) WOWd.cpp

netstream.o:    netstream.cpp
	g++ $(CPPOPTS) netstream.cpp
        
connection.o:   connection.cpp
	g++ $(CPPOPTS) connection.cpp

sem.o:		sem.cpp
	g++ $(CPPOPTS) sem.cpp
        
#
# Utility targets
#

clean:
	rm -f WOWd WOWd.exe *.o core *~
