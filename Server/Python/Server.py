import sys
import Log
from Person import Person
import XMLConnection
import Data
from socket import *
import threading
def handler(clientsock,addr):
    data = clientsock.recv(BUFSIZ)
    #clientsock.send('echoed:..', data)
    if data:
        print data
        message = Data.handleMessage(data)
        print "sending ["+message+"]"
        clientsock.send(message)
        clientsock.close()

HOST = 'localhost'
PORT = 5280
BUFSIZ = 1024
ADDR = (HOST, PORT)
serversock = socket(AF_INET, SOCK_STREAM)
serversock.bind(ADDR)
serversock.listen(2)
#Data.handleMessage("ADDDEPT: SoSE")
while 1:
      print 'waiting for connection...'
      clientsock, addr = serversock.accept()
      print '---connected from:', addr
      thread = threading._start_new_thread(handler, (clientsock, addr))
 
serversock.close()