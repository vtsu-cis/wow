import time
import datetime

#filename of the log
FILENAME = "log.txt"
    
#Defines the writeLog function that automatically puts the date and time stamp on a message
def writeLog(_message):
    print "Writing Log"
    log = open("log.txt", "a")
    log.write("(")
    log.write(datetime.datetime.now().strftime("%Y-%m-%d"))
    log.write(") ")
    log.write(_message)
    log.write("\n")
    log.close()

#Prints the log to the console, debugging
def readLog():
    log = open("log.txt", "r")
    for line in log:
        print line
    log.close()