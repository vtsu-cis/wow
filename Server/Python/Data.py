import Log
from Person import Person
import XMLConnection

#People and Departments are held in the following lists for the duration of runtime
people = []
departments = []
serverVersion = "1.0py"
hits = 0
ahits = 0
dhits = 0
uhits = 0


#Fill the lists above from the XML Database
people = XMLConnection.getPeople()
departments = XMLConnection.getDepartments()

def addDept(dept):
    if XMLConnection.isDepartment(departments, dept):
        return 1
    else:
        departments.append(dept)
        XMLConnection.writeXML(departments, people)
        return 1
    return 0

#When using the QRY and AQRY command, this function is called, taking in a list of parts
#that are obtained by exploding the command string by the | character - requires 9 pipes
# - One for each piece of data for each Person - on failing to find a given field, the 
# search is stopped, and the next person is checked. Case no longer effects the results.
def queryPipes(parts):
    results = []
    for person in people:
        if person.getFirstName().lower().find(parts[0].lower()) >= 0 or parts[0] == "":
            if person.getLastName().lower().find(parts[1].lower()) >= 0 or parts[1] == "" or parts[1] == None:
                if person.getPhoneNumber().lower().find(parts[2].lower()) >= 0 or parts[2] == "" or parts[2] == None:
                    if person.getEMail().lower().find(parts[3].lower()) >= 0  or parts[3] == "":
                        if person.getCampus().lower().find(parts[4].lower()) >= 0 or parts[4] == "":
                            if person.getRole().lower().find(parts[5].lower()) >= 0 or parts[5] == "":
                                if person.getDepartment().lower().find(parts[6].lower()) >= 0 or parts[6] == "":
                                    if person.getFax().lower().find(parts[7].lower()) >= 0 or parts[7] == "":
                                        if person.getOffice().lower().find(parts[8].lower()) >= 0 or parts[8] == "":
                                            print "found Person"
                                            results.append(person)
    return results

#Given the UPD: command, this function will find the person to update (with the given
# id) and update whichever fields are given, leaving non given fields as they originally were.
def updateID(_id, parts):
    for person in people:
        if person.getID() == _id:
            print "got"+_id
            if parts[0] != "":
                person.setFirstName(parts[0])
            if parts[1] != "":
                person.setLastName(parts[1])
            if parts[2] != "":
                person.setPhoneNumber(parts[2])
            if parts[3] != "":
                person.setEMail(parts[3])
            if parts[4] != "":
                person.setCampus(parts[4])
            if parts[5] != "":
                person.setRole(parts[5])
            if parts[6] != "":
                if XMLConnection.isDepartment(departments, parts[6]):
                    person.setDepartment(parts[6])
                else:
                    addDept(parts[6])
            if parts[7] != "":
                person.setFax(parts[7])
            if parts[8] != "":
                person.setOffice(parts[8])
            Log.writeLog("Updated record "+ _id)
            return person

#Takes a message (from client) and breaks it down into what needs to be done, and calls the
# correct functions
def handleMessage(msg):
    print "Got Message ["+msg+"]"
    #UPD: Updates a record, using the updateID method above, and for now prints out the change
    if msg.startswith("UPD: "):
        #uhits = uhits+1
        msg = msg.replace("UPD: ", "").strip()
        parts = msg.split("|")
        ID = parts.pop(0)
        uperson = updateID(ID, parts)
        print uperson.getFirstName(), uperson.getLastName(), uperson.getPhoneNumber()
        XMLConnection.writeXML(departments, people)
        return "OK"
    #Prints out the department list, nothing fancy
    elif msg.startswith("DEPTLIST"):
        for dept in departments:
            print dept
    #if either the QRY or AQRY commands are sent to the server, this takes care of them.
    #the QRY or AQRY tag is removed, newline characters chopped, and if the command is
    #AQRY then a flag is set, so that we know to return the ID with the result.
    elif msg.startswith("QRY: " or "AQRY: "):
        aqry = 0
        if msg.startswith("AQRY: "):
            msg = msg.replace("QRY: ", "").strip()
            aqry = 1
        else:
            msg = msg.replace("QRY: ", "").strip()
        results = queryPipes(msg.split("|")) 
        retMsg = ""
        for person in results:
            if aqry == 1:
                retMsg = retMsg + person.getID() + "|" + person.getAll("|") + "\n"
            else:
                retMsg = retMsg + person.getAll("|") + "\n"
        return retMsg
    
    elif msg.startswith("GETFILE\n"):
        msg.replace("GETFILE\n", "")
        f = open("wow.xml", "w")
        for line in msg:
            f.write(line)
    #Create a person with given attributes, and add them to our list above - then write
    #the xml document.
    elif msg.startswith("ADD: "):
        msg = msg.replace("ADD: ", "").strip()
        parts = msg.split("|")
        addDept(parts[6])
        people.append(Person(len(people), parts))
        XMLConnection.writeXML(departments, people)
        return "OK"
    #Adds a department to our list, and then updates the xml
    elif msg.startswith("ADDDEPT: "):
        msg = msg.replace("ADDDEPT: ", "").strip()
        departments.append(msg)
        XMLConnection.writeXML(departments, people)
        
    elif msg.startswith("VER"):
        return serverVersion
    
    elif msg.startswith("SETVER: "):
        msg.replace("SETVER: ", "").strip()
        serverVersion = msg
        Log.writeLog("Updated to version:", msg)
    
    else:
        return 0