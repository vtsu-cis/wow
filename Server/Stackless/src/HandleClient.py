# HandleClient.py
# Stackless Python Server

from stackless import schedule;
from Sendmail import MailMessage;
import Log;
from Test import *;
import socket;
import Distribute;

class HandleClient:
    """ The HandleClient class will handle incoming connections. """
    def __init__(self, clientSocket, xmlHandler, isMain):
        """ 
        Initialize a client handler.
        @param clientSocket Socket to communicate with a client with.
        @param xmlHandler XML Database object.
        @param isMain Set to True if this server runs as the main server, otherwise False.
        """
        self.clientSocket = clientSocket;
        self.xmlHandler = xmlHandler;
        self.isMain = isMain;
        
        # Let other tasklets go first.
        #schedule();
        
        # Talk with our client.
        try:
            self.handle();
        except socket.error, e:
            # Socket error occurred.
            Log.logPrint("Socket error while handling client: %s" % str(e), "WOWd.log");
        except Exception, e:
            # An error occurred while handling a client.
            Log.logPrint("Exception occurred during query: %s." % str(e), "WOWd.log");
        finally:
            # In any case: clean up socket.
            self.cleanup();
       
    def handle(self):
        """
        Interpret incoming packets.
        """
        # Receive up to 512 bytes of data.
        data = self.clientSocket.recv(512);
        data = data.rstrip();
        
        if data == "":
            Log.logPrint("Client closed socket before we could handle him.", "WOWd.log");
            return;
        
        print "Received",data;
        
        # Retrieve the command.
        input = [ arr.strip() for arr in data.split(" ") ];
        
        command = input[0].upper().replace(":", "");
        try:
            input[1] = data[data.find(" "):];
        except:
            pass;
        
        # Find and handle the command.
        if command == "QRY":
            # Incoming query.
            resultList = self.xmlHandler.query(input[1]);
            
            for line in resultList:
                self.printLine(line);
            
            stackless.tasklet(Log.recordHit)();
        
        elif command == "AQRY":
            # Incoming administration query.
            resultList = self.xmlHandler.query(input[1], True);
            
            for line in resultList:
                self.printLine(line);
            
            stackless.tasklet(Log.recordHit)();
        
        elif command == "ADD":
            # Incoming addition to the database.
            if self.isMain:
                # Attempt to add the record.
                result = self.xmlHandler.add(input[1]);
                
                if result:
                    self.printLine("Successfully added.");
                    
                    self.triggerDistribute();
                    
                    stackless.tasklet(Log.recordHit)("ADD");
                else:
                    self.printLine("ERROR: Record already exists.");
            else:
                self.printLine("Only the primary server can change records.");
            
        elif command == "UPD":
            # Incoming update to a record.
            if self.isMain:
                data = [ arr.strip() for arr in input[1].split('|') ];
                
                record = self.xmlHandler.createRecord(data, True);
                
                result = self.xmlHandler.update(record.getID(), record);
                
                if result:
                    self.printLine("OK");
                    
                    self.triggerDistribute();
                    
                    stackless.tasklet(Log.recordHit)("UPD");
                else:
                    self.printLine("Target not found.");
            else:
                self.printLine("Only the primary server can change records.");
            
        elif command == "DEL":
            # Delete a record.
            if self.isMain:
                result = self.xmlHandler.delete(input[1]);
                
                if result:
                    self.printLine("OK");
                    
                    self.triggerDistribute();
                    
                    stackless.tasklet(Log.recordHit)("DEL");
                else:
                    self.printLine("ERROR: Record doesn't exist!");
            else:
                self.printLine("Only the primary server can change records.");
        
        elif command == "DEPTLIST":
            # Return a list of departments.
            result = self.xmlHandler.getDepartments();
            
            for department in result:
                self.printLine(department);
        
        elif command == "DEPTADD":
            # Add a department.
            if self.isMain:
                result = self.xmlHandler.addDepartment(input[1]);
            
                if not result:
                    self.printLine("ERROR: That department already exists!");
                else:
                    self.printLine("Department successfully added.");
            else:
                self.printLine("This server cannot modify records, try a different one.");
        
        elif command == "DEPTUPD":
            # Update a department.
            if self.isMain:
                chachachachanges = input[1].split("|");
                result = self.xmlHandler.modifyDepartment(chachachachanges[0], chachachachanges[1]);
                
                if not result:
                    self.printLine("ERROR: That department already exists!");
                else:
                    self.printLine("Department successfully updated.");
            else:
                self.printLine("This server cannot modify records, try a different one.");
        
        elif command == "DEPTDEL":
            # Remove a department.
            if self.isMain:
                result = self.xmlHandler.deleteDepartment(input[1].strip());
                
                if not result:
                    self.printLine("ERROR: That department doesn't exist!");
                else:
                    self.printLine("Department successfully deleted.");
            else:
                self.printLine("This server cannot delete records, try a different one.");
        
        elif command == "PING":
            # Just ping it, fox!
            self.printLine("PONG");
        
        elif command == "HIT":
            queries = self.xmlHandler.getHits("QRY");
            self.printLine(queries);
        
        elif command == "HITALL":
            hits = self.xmlHandler.getHits("ALL");
            
            for arr in hits:
                line = "%s %s" % (arr[0], arr[1]);
                
                self.printLine(line);
        
        elif command == "MAIN":
            if self.isMain:
                self.printLine("1");
            else:
                self.printLine("0");
        
        elif command == "UHIT":
            queries = self.xmlHandler.getHits("UPD");
            self.printLine(queries);
        
        elif command == "AHIT":
            queries = self.xmlHandler.getHits("ADD");
            self.printLine(queries);
        
        elif command == "DHIT":
            queries = self.xmlHandler.getHits("DEL");
            self.printLine(queries);
        
        elif command == "MAIL":
            # Forward a mail message to the IT department.
            mail = input[1].split("|");
            mailer = MailMessage(mail[0], mail[1], mail[2], mail[3], "Feedback (" + mail[2] + ")");
            
            if not mailer.mail():
                self.printLine("An error occurred while trying to send your mail. Try again later.");
                
                line = "A feedback e-mail failed from %s (%s)." % (mail[0], mail[1]);
                logPrint(line);
            else:
                self.printLine("Your message was sent successfully.");
                
                line = "A feedback e-mail was sent from %s (%s)." % (mail[0], mail[1]);
                logPrint(line);
        
        elif command == "VER":
            # Version of the WOW client.
            self.printLine(self.xmlHandler.version); 
        
        elif command == "SETVER":
            # Set the version of the WOW client.
            if self.isMain:
                self.xmlHandler.version = input[1];
                self.printLine("Version set.");
            else:
                self.printLine("Not main server.");
        
        elif command.startswith("GETFILE"):
            # Retrieve a file 
            logPrint("Main server is sending the database file...");
            
            size = int(data.split("\n")[1]);
            
            data = self.getfile(size);
            
            # Now get sha1sum and check it against the server.
            sum = self.xmlHandler.checksum(data);
            self.printLine(sum);
            
            response = self.clientSocket.recv(8).strip();
            
            doLoad = True;
            if response == "NO":
                # File transfer failed.  Try one more time.
                data = self.getfile(size);
                
                sum = self.xmlHandler.checksum(data);
                if sum != response:
                    # Two file transfers failed in a row.
                    # This server will e-mail the IT staff with an error.
                    # Until then, hold existing data in memory.
                    doLoad = False;
                    errorMessage = "An error occurred during a file transfer from the main server to this one. ";
                    errorMessage += "The checksum did not match on two attempts. ";
                    errorMessage += "Please check the main server and other servers.";
                    try:
                        errorMail = MailMessage(self.socketModule.gethostname(), 
                                            self.socketModule.gethostbyname(self.socketModule.gethostname()),
                                            "ERROR",
                                            errorMessage,
                                            "SERVER ERROR");
                        if not errorMail.mail():
                            # The checksum failed and the mailer failed.
                            logPrint("File transfer failed and mail notification failed.");
                    except self.socketModule.error, e:
                        logPrint("Attempted to mail error notification but failed with error: %s" % (str(e)));
                    
            if doLoad:
                logPrint("Received new file from main.  Checksum: %s" % (sum));
                
                f = open(self.xmlHandler.getDefaultFile(), "w");
                f.write(data);
                f.close();
                
                self.xmlHandler.load();
            
        else:
            # Sent an invalid command.
            self.printLine("Invalid command!");
    
    def printLine(self, msg):
        """
        Append a line to the message and send it off.
        """
        self.clientSocket.send(str(msg) + "\n")
    
    def getfile(self, filesize):
        """
        Obtain the file data from the server.
        @param filesize Size of the data.
        @return data on success.
        """
        self.printLine("OK");
        filedata = '';
        bytes_received = 0;
        bytes_left = filesize;
        while bytes_left > 0:
            chunk = self.clientSocket.recv(bytes_left);
            
            bytes_received += len(chunk);
            bytes_left -= len(chunk);
            
            filedata += chunk;
            
        return filedata;
    
    def triggerDistribute(self):
        """
        Invoke a list distribution if the server is the main one.
        """
        if self.isMain:
            Distribute.distribute(self.xmlHandler.getDefaultFile(), socket);
    
    def cleanup(self):
        """
        Close the connection.
        """
        try:
            self.clientSocket.close();
        except:
            pass;
 