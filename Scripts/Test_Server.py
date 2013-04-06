import os
import sys
from socket import *
import time;

#Receive data until done
def recv_basic(the_socket):
    total_data=[]
    while True:
        data = the_socket.recv(8192)
        if not data: break
        total_data.append(data)
    return ''.join(total_data)

wowIP = ''
if len(sys.argv) == 2: 
    wowIP = sys.argv[1]
    print "Target server:", wowIP;

else: 
    wowIP = 'porkbarrel.cis.vtc.edu'  #ip address
    print "Target server:", wowIP;
wowPort = 5280           #port



while 1:
	msg = raw_input("Enter a message to send (\"q\" to exit): ")
	if msg == "q": break
	if not msg.endswith("\n"): msg += "\n"
	try:
		firstTime = time.time();
		s = socket(AF_INET, SOCK_STREAM)
		s.settimeout(5.0)
		s.connect((wowIP, wowPort))
		startTime = time.time();
		s.send(msg)
		data = recv_basic(s)
		endTime = time.time();
		print data
		print "------------"
		print "Socket creation: %.4f seconds\nData exchange: %.4f seconds\n\tTotal: %.4f seconds\n" % (startTime-firstTime, endTime-startTime, endTime-firstTime);
		s.close()
	except Exception, ex:
		print "Could not connect to %s on port %s %s" % (wowIP, wowPort, ex)



