# Test.py
# Stackless Python Server
import time;
import stackless;
import sys;
from Record import Record;
from Field import Field;
from Department import Department;
import re as regex;
import datetime;

def debugPrint(msg):
    """
    Print a message to stdout with a time stamp.
    @param msg Message to print (do not include time stamp).
    """
    print "[" + str(time.strftime("%H:%M:%S")) + "]",msg;
    

def logPrint(msg, logfile="./WOWd.log"):
    """
    Prints a message to stdout and to a log file.
    @param msg Message to write.
    @param logfile File to write to.
    """
    now = time.strftime("%H:%M:%S");
    day = datetime.date.today();
    
    f = open(logfile, "a");
    f.write("(%s %s) %s\n" % (str(day), str(now), str(msg)));
    f.close();
    
    print "[" + now + "]",msg;

def wowAssert(expected, actual):
    """
    Like python's 'assert', but more flexible.
    @param expected What to expect.
    @param actual What actually happens.
    @return True if the assertion passed, otherwise it will exit with an error.
    """
    # Run comparisons on some known types.
    try:
        if isinstance(expected, Record):
            # If comparing records, compare it field-by-field.
            expected = expected.getAllFields();
            actual = actual.getAllFields();
        
        # Standard comparison.
        if expected != actual:
            logPrint("ASSERTION FAILED:\nExpected %s, got %s" % (str(expected), str(actual)));
            sys.exit(1);
    except Exception, e:
        logPrint("ASSERTION FAILED (EXCEPTION ERROR):\nExpected %s, got exception: %s" % (str(expected), str(e)));
        sys.exit(2);
    
    return True;

def mainTests(xmlHandler):
    """
    Run unit tests.
    @param xmlHandler XML database object.
    """
    debugPrint("Done.");
    # Test mode does not run a server.
    debugPrint("Running test queries.");
    debugPrint("Adding test user.");
        
    # Assert that this record is successfully added.
    wowAssert(True, xmlHandler.add("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||"));
        
    debugPrint("User added.");
    debugPrint("Querying user...");
        
    # Assert that the query is successful.
    wowAssert("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||", 
                xmlHandler.query("AndyAHH|Sibley AHH|||||||")[0]);
        
    debugPrint("Found user.");
    debugPrint("Adding duplicate user...");
        
    # Assert that a user cannot be added (because of unique constraint).
    wowAssert(False, xmlHandler.add("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||"));
        
    debugPrint("Duplicate user was not added.");
    debugPrint("Performing admin query for ID...");
    
    # Assert that an administration query is successful.
    query = xmlHandler.query("AndyAHH|Sibley AHH|||||||", True)[0];
    re = regex.compile("[0-9]+|AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||");
    wowAssert(True, re.match(query) != None);
              
    debugPrint("Found user.");
    debugPrint("Creating record from data...");
    
    # Assert that a record can be successfully created.
    query += "Southern Office";
    record = xmlHandler.createRecord(query.split("|"), True);
    wowAssert(True, record != None);
    
    debugPrint("Record created.");
    debugPrint("Updating old user with new record...");
    
    # Assert that an update on an existing user works.
    wowAssert(True, xmlHandler.update(int(query.split("|")[0]), record));
    
    debugPrint("User updated.");
    debugPrint("Querying with expected update...");
    
    # Assert that the update took place.
    wowAssert("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||Southern Office", 
              xmlHandler.query("AndyAHH|Sibley AHH|||||||")[0]);
    
    debugPrint("Found updated user.");
    debugPrint("Deleting user...");
        
    # Assert that the deletion was successful.
    wowAssert(True, xmlHandler.delete("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||Southern Office"));
        
    debugPrint("User deleted.");
    debugPrint("Deleting same user again...");
        
    # Assert that you cannot delete the same record twice.
    wowAssert(False, xmlHandler.delete("AndyAHH|Sibley AHH|(802)527-0679|andysib@AHH.com|||||"));
    
    debugPrint("Non-existent user not deleted.");
    debugPrint("Querying deleted user...");
    
    # Assert that the user was really deleted.
    wowAssert("", xmlHandler.query("AndyAHH|Sibley AHH|||||||")[0]);
    
    debugPrint("User not found.");
    debugPrint("Obtaining list of departments.");
    
    departments = xmlHandler.getDepartments();
    
    debugPrint("Found department list.");
    debugPrint("Attempting to add department.");
    
    # Assert that we can add a department.
    wowAssert(True, xmlHandler.addDepartment("BOOGA BOOGA BOOGA"));
    
    debugPrint("Test department added.");
    debugPrint("Attempting to add duplicate department.");
    
    # Assert that two of the same department cannot exist.
    wowAssert(False, xmlHandler.addDepartment("BOOGA BOOGA BOOGA"));
    
    debugPrint("Duplicate department failed successfully.");
    debugPrint("Attempting to change department name.");
    
    # Assert that the name can be changed.
    wowAssert(True, xmlHandler.modifyDepartment("BOOGA BOOGA BOOGA", "BOOGAAA"));
    
    debugPrint("Department modified.");
    debugPrint("Deleting department.");
    
    # Assert that our department will be deleted.
    wowAssert(True, xmlHandler.deleteDepartment("BOOGAAA"));
    
    debugPrint("Department removed.");
    debugPrint("Deleting same department again.");
    
    # Assert that attempting to delete the same department twice doesn't work.
    wowAssert(False, xmlHandler.deleteDepartment("BOOGAAA"));
    
    debugPrint("Deleting non-existent department success.");
    debugPrint("All tests are successful.");
    
    return True;

if __name__ == "__main__":
    import os;
    if os.getcwd().endswith("src"):
        # Set to project root.
        os.chdir("../");

    import XMLDatabase;
    xmlHandler = XMLDatabase.XMLDatabase();
    xmlHandler.load();
    
    mainTests(xmlHandler);