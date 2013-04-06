#!/bin/bash
#Start the WOW server. Place in /sbin/local/wow/

#Java classpath
WOWCP=.

#Main class
MAIN=wowServ.server.Server

#Start the server as a daemon:
nohup java -classpath $WOWCP $MAIN $1 $2 $3 > /dev/null 2>&1 &

echo 'Started.'