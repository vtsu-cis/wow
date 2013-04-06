#!/usr/local/bin/python
# Check to see if a server is healthy.

# ###################

global TARGET, PORT, MAILER, LIST, LOOP, LOOPTIME;

# Default target host.
TARGET = "wow.vtc.edu";

# Default target port.
PORT = 5280;

# Set the exact path to sendmail.
MAILER = "/usr/sbin/sendmail -t -oi";

# Set the addresses to be notified in case the server is not up.  Comma-delimited.
LIST = "andysib@gmail.com, cbeattie@vtc.edu";

# Want this script to loop forever? Set to 1.  Otherwise, set it to 0.  This can be modified via "-l" or "--loop".
LOOP = 0;

# Define the time, in seconds, that it waits before checking.   If LOOP is set to 0, this does not matter.
LOOP_TIME = 300;

# ###################

import sys, os;
import socket;

def sendAlert(alertMsg):
    try:
        # Open SENDMAIL for writing.
            p = os.popen("%s" % (MAILER), "w");
            
            p.write("From: %s\n" % (socket.gethostbyname(TARGET)));
            p.write("To: %s\n" % LIST);
            p.write("Subject: WOW SERVER ERROR\n");
            p.write("\n");
            p.write("%s" % (alertMsg));
                        
            # Close SENDMAIL and obtain the result.
            result = p.close();
            
            if result: # Returned non-zero.
                print "ERROR: Sendmail returned ",result;
                return 1;
            return 0;
    except Exception, e:
            print ("ERROR:%s",e);
            return 2;

def main(argv=[]):
    global TARGET, PORT, MAILER, LIST, LOOP, LOOPTIME;
    
    # Check for command line arguments.
    if len(argv) > 1:
        for arg in argv:
            if arg == argv[0]:
                continue;
            
            if arg.startswith("-t"):
                # define target.
                arg = arg[len("-t"):];
                if arg == "":
                    print "-t usage:  -t[target host]";
                    return 1;
                else:
                    TARGET = arg;
                    try:
                        socket.gethostbyname(TARGET);
                    except socket.error, e:
                        print "Unable to find target host: %s" % (str(e));
                        return 1;
            
            elif arg.startswith("--target="):
                # define target.
                arg = arg[len("--target="):];
                if arg == "":
                    print "--target usage:  --target=[target host]";
                    return 1;
                else:
                    TARGET = arg;
            
            elif arg.startswith("--test"):
                # request a test e-mail.
                answer = raw_input("Send a test e-mail? (y/n) ").strip();
                if answer.lower() == "y":
                    sent = sendAlert("""
                    
                        This is a test e-mail distributed by the script that is meant to check up on the WOW server periodically. 
                        You are on the list to receive an e-mail if the server does not respond normally (possibly indicating that it crashed). 
                        If you don't want to be part of this list, e-mail andysib@gmail.com or modify the WOW/trunk/Scripts/Check.py script yourself. 
                        
                        Current list:
                        %s
                        
                    """ % (LIST));
                    
                    if sent:
                        print "An e-mail error occurred and the message was not sent.";
                        
                sys.exit(0);
            
            elif arg.startswith("-p"):
                # define port
                arg = arg[len("-p"):];
                try:
                    PORT = int(arg);
                    if PORT < 0:
                        raise ValueError, "Port numbers cannot be less than zero.";
                except ValueError, v:
                    print "-p usage: -p[target port]";
                    return 1;
            
            elif arg.startswith("--port="):
                # define port
                arg = arg[len("--port="):];
                try:
                    PORT = int(arg);
                    if PORT < 0:
                        raise ValueError, "Port numbers cannot be less than zero.";
                except ValueError, v:
                    print "--port usage: --port=[target port]";
                    return 1;
            
            elif arg == "-l" or arg == "--loop":
                # loop forever
                LOOP = 1;
                print "Looping.";
            
            else:
                # Print help.
                print "\nDefault command-line arguments:";
                print "-t[Host] - Define target host. (e.g. -twow.vtc.edu or -t192.168.1.1)";
                print "\t--target=[Host]";
                print "-p[Port] - Define target port. (e.g. -p5280 or -p31337)";
                print "\t--port=[Port]";
                print "-l - Tell the script to loop every %d seconds, never actually exiting." % (LOOP_TIME);
                print "\t--loop"
                print "--test - Test to ensure that e-mails can be sent.";
                print "\t";
                return 0;
    
    outSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM);

    try:
        outSocket.connect((TARGET,PORT)); #make the connection
        
        outSocket.send("VER");
        response = outSocket.recv(8).strip();
        if response == "":
            print "The server sent a blank packet, indicating EOF.";
            sent = sendAlert("""
                A WoW server is not responding!
                SERVER HOST: %s
                SERVER PORT: %d
                ERROR DESCRIPTION:
                The server sent a blank packet indicating EOF. 
                This means that the socket terminated the connection instead of replying as expected, either manually
                or by crashing.  Please check the logs.
                
                The following people received this message:
                %s
            """ % (TARGET, PORT, LIST));
            
            if sent:
                print "The e-mail was not able to be sent.";
                return 1;
        else:
            print "OK";
    except socket.error, err: #catch socket error
        print "ERROR:", err;
        sent = sendAlert("""
            The script threw an exception while querying the server!
            SERVER HOST: %s
            SERVER PORT: %d
            ERROR DESCRIPTION:
            The socket suddenly raised an exception, causing the script to force the connection closed.  This is sometimes
            a case of getting disconnected mid-connection or if the target host doesn't exist.  If this script is set to run more
            than once, and the target server is down, this message may be sent repeatedly, so disable the script during server downtime.
            
            Please ensure that the target server is alive.
            
            The following people received this message:
            %s
            """ % (TARGET, PORT, LIST));
            
        if sent:
            print "The e-mail was not able to be sent.";
            return 1;
    
    outSocket.close();
    
    return 0;

# Execute at least once.
result = main(sys.argv);
if not LOOP:
    sys.exit(result);
    
import time;
while not result:
    time.sleep(LOOP_TIME);
    result = main(sys.argv);
