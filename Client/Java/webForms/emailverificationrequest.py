
#Get everyone in profiles.dat and send them the message located in logininfo.txt

import os
import string
import libgmail
from getpass import getpass

f = open('logininfo.txt', 'r')
lines = f.readlines()
f.close()

print "Logging in to " + lines[1] + "..."
account = lines[1]
password = getpass()

account = libgmail.GmailAccount(account, password)

account.login()
print "Logged in."

f = open('profiles.dat', 'r')
info = f.readlines()
for line in f:
    info[line] = line.rstrip()

for user in range(0,len(info)):
    userinfo = info[user].split("|")
    to = userinfo[2]
    
    data = open('logininfo.txt','rt')
    msginfo = data.readlines()
    data.close()
    
    subject = msginfo[5]
    body = msginfo[7] % (userinfo[0], userinfo[1], userinfo[3])
    message = body

    gmessage = libgmail.GmailComposedMessage(to, subject, message)

    if account.sendMessage(gmessage):
        print "Sent to %s %s (%s)" % (userinfo[0], userinfo[1], userinfo[3])
    else:
        print "Error sending msg to %s %s (%s)" % (userinfo[0], userinfo[1], userinfo[3])

f.close()

