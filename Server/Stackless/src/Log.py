# Log.py
# Stackless Python server
import time;
from Test import *;

# Set the default hit file.
DEFAULT_HIT_FILE = "./hits.log";

def recordHit(type="QRY", file=DEFAULT_HIT_FILE):
    """
    Writes into a log a hit.  Automatically adds a time stamp.
    @param type Optional (Default: QRY) Type of hit.
    @param file Optional (Default: DEFAULT_HIT_FILE) File to write.
    """
    timestamp = str(time.time());
    timestamp = timestamp[:timestamp.find(".")];
    
    f = open(file, "a");
    f.write("%s %s\n" % (type, timestamp));
    f.close();
    
    return timestamp;

def readLogHits(file=DEFAULT_HIT_FILE):
    """
    Returns a tuple.
    The first element will contain the number of queries found.
    The second will contain the number of adds.
    The third will contain the number of deletes.
    The fourth will contain the number of updates.
    The fifth will be an array formatted like:
    [ type, timestamp ]
    """
    try:
        f = open(file, "r");
        lines = f.readlines();
        f.close();
    except Exception,e:
        print "Didn't open log hit file:",e;
        return False;
    
    numQueries = 0;
    numAdds = 0;
    numDeletes = 0;
    numUpdates = 0;
    times = [];
    
    for line in lines:
        line = line.strip();
        cmd = line[:line.find(" ")];
        stamp = line[line.find(" "):];
        
        if cmd == "QRY": numQueries += 1;
        elif cmd == "ADD": numAdds += 1;
        elif cmd == "UPD": numUpdates += 1;
        elif cmd == "DEL": numDeletes += 1;
        
        times.append([ cmd, stamp ]);
        
    hits = [numQueries, numAdds, numDeletes, numUpdates, times];
    
    return hits;
