import stackless;
from Log import logPrint;
import sha;

class distribute:
    """
    The distribute class handles sending data out to all of the other servers every few minutes
    for the purposes of synchronization.
    """
    def __init__(self, databaseFile, socketModule):
        self.file = databaseFile;
        self.socketModule = socketModule;
        stackless.tasklet(self.run)();
    
    def run(self):
        """
        Execute the distribution.
        """
        try:
            servers = self.getServerList();
            
            if not len(servers):
                logPrint("There are no servers to send to.  Aborting distribution.");
                return;
            
            logPrint("Sending to %d servers..." % (len(servers)));
                
            f = open(self.file, "r");
            data = f.readlines();
            f.close();
            
            data = "".join(data);
                
            sum = self.checksum(data);
                
            for target in servers:
                try:
                    s = self.socketModule.socket(self.socketModule.AF_INET, self.socketModule.SOCK_STREAM);
                    s.connect(target);
                    s.settimeout(10);
                    
                    s.send("GETFILE\n%d\n" % (len(data)));
                    
                    response = s.recv(8).strip();
                    if response == "OK":
                        self.sendfile(s, data);
                    else:
                        msg = "Target server %s did not approve of file transfer." % (target[0])
                        logPrint(msg);
                        continue;
                        
                    # Now they want a sha1sum calculated to check the file's integrity.
                    clientSum = s.recv(80).strip();
                        
                    if sum != clientSum:
                        s.send("NO");
                            
                        # The client will try again.
                        response = s.recv(8);
                            
                        # Expected: "OK"
                        if response == "OK":
                            self.sendfile(s, data);
                        else:
                            msg = "Target server %s did not approve of file transfer after failing once." % (
                                                                                                target[0])
                            logPrint(msg);
                            
                    else:
                        s.send("OK");
                        
                        s.close();
                        
                        logPrint("Data sent to %s" % (target[0]));
                except self.socketModule.error, e:
                    # Catch socket errors.
                    logPrint("Didn't send to %s: %s" % (target[0], str(e)));
        except Exception, ex:
            logPrint("An exception occurred while trying to distribute the list: %s" % (str(ex)));
            import traceback;
            traceback.print_exc(ex);
    
    def sendfile(self, s, data):
        """
        Convenience method for sending the file to the client.
        @param s Socket to use.
        @param data File data to transfer.
        """
        filesize = len(data);
        bytes_sent = 0;
        bytes_left = filesize;
        while bytes_left > 0:
            num = s.send(data[bytes_sent :]);
            if num == 0:
                logPrint("Error: Socket closed during send attempt.");
                break;
            
            bytes_sent += num;
            bytes_left -= num;

    def checksum(self, data):
        """
        Calculate the sum of a file using the native command-line tool.  Used for file propagation
        integrity checks.
        @return The calculated sha1sum.
        """
        return sha.sha(data).hexdigest();
    
    def getServerList(self, file="./wow.conf"):
        """
        Read a target wow.conf file to interpret the server list.
        """
        f = open(file, "r");
        lines = f.readlines();
        f.close();
        
        servers = [];
        for entry in lines:
            if entry.startswith("#") or entry == "":
                continue;
            
            entry = entry.split(":");
            host = entry[0].strip();
            port = int(entry[1].strip());
            
            try:
                target = (self.socketModule.gethostbyname(host), port);
                servers.append(target);
            except self.socketModule.error, e:
                logPrint("Invalid server entry: %s - Reason: %s" % (entry, str(e)));
        
        return servers;
    