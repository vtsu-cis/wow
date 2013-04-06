# Server.py - __main__
# Stackless Python Server
import stackless;

from HandleClient import HandleClient;
from XMLDatabase import XMLDatabase;
import stacklesssocket;
stacklesssocket.uninstall();
stacklesssocket.install();
from Test import *;

from socket import *;
from Log import *;
import time;
import sys;
import sha;
import os;

if os.getcwd().endswith("src"):
    # Set to project root.
    os.chdir("../");

# GLOBAL CONSTANTS #
DEFAULT_PORT=5280; # Use port 5280 by default.

# GLOBAL VARIABLES #
isMain=False; # This server will not act as the main by default.
testMode=False; # Run unit tests.
tasklets=[]; # Track active tasklets.

class Server:
    """ 
    The Server class accepts incoming connections. 
    """
    def __init__(self, xmlObj, port=DEFAULT_PORT):
        """ 
        Initialize a Server object.  Creates a server socket member and enables listening. 
        @param xmlObj Pass an initialized XMLDatabase object to the server.
        @param port Optional. Pass a custom port through.
        """
        self.port = port;
        self.xmlHandler = xmlObj;
        
        # Create a new socket object that will handle TCP connections.
        self.serverSocket = socket(AF_INET, SOCK_STREAM);
        
        # Set socket option to reuse the address even if it is in use.
        self.serverSocket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1);
        
        # Bind socket for listening.
        host = ('', self.port);
        self.serverSocket.bind(host);
        
        # Enable listening.  Allow 5 pending connections. 
        self.serverSocket.listen(5);
        
    def listen(self):
        """
        Accept incoming connections and handle them appropriately.
        """
        global tasklets, isMain;
        
        # Loop indefinitely.
        while 1:
            try:
                # Accept a connection.
                (clientSocket, addr) = self.serverSocket.accept();
                
                # Handle the incoming connection in a tasklet.
                stackless.tasklet(HandleClient)(clientSocket, self.xmlHandler, isMain);
                stackless.schedule();
            except socket.error, e:
                # If a connection error occurs with a client, ignore it and move on.
                pass;

def readArgs(argv):
    """
    Read command-line arguments.
    @return Tuple containing the port and True if the server should run as main (otherwise False).
    """
    global port, isMain, testMode;
    for arg in argv:
        if arg.endswith(".py"):
            # Ignore the first command-line argument, because it's the name of this python module.
            continue;
        try:
            # One way to test if the argument is a port is to try to convert it to an integer.
            # Generally try..except blocks aren't supposed to be used for program flow, but I'll
            # make an exception (giggles). 
            port = int(arg);
            debugPrint( "Port set to %d" % (port) );
        except ValueError, e:
            if arg == "-main":
                isMain = True;
                debugPrint("Started as main.");
            elif arg == "-test":
                # Unit testing.
                testMode = True;
                debugPrint("Started in test mode.");
            else:
                # Unrecognized command.
                debugPrint("Unrecognized flag: %s (ignoring)" % (arg));

def main(argv=[]):
    """ 
    Define the start of the program.  Initialize data and start listening. 
    @param argv Optional.  Command-line arguments.
    """
    global port, isMain, testMode;
    port = DEFAULT_PORT;
    isMain = False;
    if len(argv) > 1:
        readArgs(argv);
    
    if testMode:
        debugPrint("Parameters read.  Loading XML database...");
        startTime = time.time();
    
    # Initialize XML database.    
    xmlHandler = XMLDatabase();
    xmlHandler.load();
    
    # Read hit log.
    hits = readLogHits();
    if hits != False:
        xmlHandler.hits = hits;
    
    if testMode:
        debugPrint("XML Database loaded. (Took %.4f seconds)" % (time.time() - startTime));
        debugPrint("Initializing server.");
    
    # Initialize new Server object.
    server = Server(xmlHandler, port);
    
    if testMode:
        mainTests(xmlHandler);
        sys.exit(0);
    else:
        # Create the tasklet that manages incoming connections.
        logPrint("Server started on port %d" % (port));
        stackless.tasklet(server.listen)();
        
        # Run the stackless scheduler.
        stackless.run();
    
    # Even if it shouldn't reach here, always shut down a socket once it's done.
    server.serverSocket.close();
    sys.exit(1);

if __name__ == "__main__":
    # Pass command line parameters and run the main function.
    main(sys.argv);
    