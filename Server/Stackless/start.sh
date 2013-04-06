#!/usr/bin/sh

SERVER_PATH="./src/Server.py"
INTERPRETER="python"

# Start the server.
nohup $INTERPRETER $SERVER_PATH $1 $2 $3 > /dev/null 2>&1 & 
