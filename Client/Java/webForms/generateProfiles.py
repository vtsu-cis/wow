
#Query everything in the database, save to queried.dat.  Then remove blank emails and blank entries and store in profiles.dat.  Generate unique 8-digit hexadecimal keys for everyone.

import os
import sys
from socket import *

wowIP = '155.42.234.35'  #ip address
wowPort = 5280           #port

s = socket(AF_INET, SOCK_STREAM)
s.settimeout(5.0)
try:
    s.connect((wowIP, wowPort))
except Exception, ex:
    print "Could not connect to %s on port %s: %s" % (wowIP, wowPort, ex.message())
print 'Connected.'

msg = "QRY: ||||||||\n" #newline char because the server does readLine()
s.send(msg)

print 'Requesting profiles...'
try:
    data = recv_basic(s)
except Exception, ex:
    print 'Server error: Could not receive:', ex.message()

s.close()

f = open("queried.dat", 'w')
f.write(data)
f.close()

f = open("queried.dat",  'r')
g = open("profiles.dat", 'w')

print 'Generating profiles...'
keylist = []
count = 0
for line in f:
    info = line.split("|")

    while (1):
        newkey = genhexkey()
        if not keylist.__contains__(newkey):
            keylist.append(newkey)
            break
    
    if not (info[3] == "_@_._") or not (info[1] == ""):
        saved = "%s|%s|%s|%s\n" % (info[0], info[1], info[3], keylist[count])
        g.write(saved)
    count += 1

print 'Done.'
f.close()
g.close()

#Generate random 8-digit hex value
def genhexkey():
	from random import randint
	
	num = [0,0,0,0,0,0,0,0]
	for i in range(0, len(num)):
		num[i] = randint(0,15)

	hexval = ''
	for i in range(0, len(num)):
		hexval += str(hex(num[i])[2:])
	return hexval.upper()

#Receive data until done
def recv_basic(the_socket):
    total_data=[]
    while True:
        data = the_socket.recv(8192)
        if not data: break
        total_data.append(data)
    return ''.join(total_data)
