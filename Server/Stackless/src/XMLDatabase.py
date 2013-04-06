# XMLDatabase.py
# Stackless Python Server

import xml.dom.minidom;
from xml.dom.minidom import Node;
from Record import Record;
from Field import Field;
from Department import Department;
from Test import *;
from Log import *;
import sha;

DEFAULT_FILE = "./profiles.xml";

class XMLDatabase:
    """ 
    The XMLDatabase class will handle all data storage and tracking.
    """
    def __init__(self, file=DEFAULT_FILE):
        """ Initialize XML and load the database file. """
        # Store the name of the database file.
        self.file = file;
        
        self.version = "1.00";
        
        self.hits = [0, 0, 0, 0, []];
    
    def load(self):
        """
        Load all of the XML data.
        """
        # Parse the given file, and store it in an XML doc object.
        self.doc = xml.dom.minidom.parse(self.file);
        
        # Save the root element of the file.
        self.rootElement = self.doc.getElementsByTagName("data-group")[0];
        
        # Save the record and department root element.
        self.peopleElement = self.rootElement.getElementsByTagName("people")[0];
        self.departmentsElement = self.rootElement.getElementsByTagName("departments")[0];
        
        # Set the XML mapping of records to an empty dictionary object.
        self.records = {};
        self.departments = {};
        
        temp = self.obtainRecords(self.peopleElement);
        
        # Iterate through each record and store the objects in a data dictionary.
        for record in temp:
            self.records[record.getID()] = record;
        
        temp = self.obtainDepartments(self.departmentsElement);
        
        # Store the department list as a data dictionary.
        for department in temp:
            self.departments[department.getName()] = department;
    
    def write(self, filename=DEFAULT_FILE):
        """
        Save the XML data.
        """
        f = open(filename, "w");
        self.doc.writexml(f);
        
        f.close();
    
    def getHits(self, type=None):
        """
        Get the tuple containing data about queries.
        @param type 
        """
        if type == "QRY":
            return self.hits[0];
        elif type == "ADD":
            return self.hits[1];
        elif type == "DEL":
            return self.hits[2];
        elif type == "UPD":
            return self.hits[3];
        elif type == "ALL":
            return self.hits[4];
        else:
            return self.hits;
    
    def getText(self, nodelist):
        """
        Obtain the text inside of an element.
        @param nodelist Node(s) to parse.
        @return Text inside of the node, or "" otherwise.
        """
        rc = "";
        for node in nodelist:
            if node.nodeType == Node.TEXT_NODE:
                rc = rc + node.data;
        
        return rc;
    
    def obtainRecords(self, recordNode):
        """
        Obtain a list of records (use when parsing doc).
        @param recordNode Obtain the parent node containing all of the records.
        @return Array of Record objects.
        """
        # Obtain a node list containing every record.
        nodeList = recordNode.getElementsByTagName("record");
        
        recordList = [];
        for recordElement in nodeList:
            # Store the record's ID.
            id = int(self.getText(recordElement.getElementsByTagName("id")[0].childNodes));
            fieldNodes = recordElement.getElementsByTagName("field");
            
            fields = {};
            for node in fieldNodes:
                # Loop through every field and obtain the field name and field data.
                fieldName = node.getAttribute("name");
                fieldData = self.getText(node.childNodes);
                field = Field(fieldName, fieldData);
                
                # Add the new field to the list of fields.
                if fieldName in fields:
                    fields[fieldName].append(field);
                else:
                    fields[fieldName] = [field];
            
            # Create a new record.
            record = Record(id, fields, recordElement);
            
            # Append the record to the list of records.
            recordList.append(record);
            
        return recordList;
    
    def obtainDepartments(self, departmentNode):
        """
        Obtain a list of departments (use when parsing doc).
        @param departmentNode Department node list.
        """
        # Obtain a node list containing every department.
        nodeList = departmentNode.getElementsByTagName("entry");
        
        departmentList = [];
        for entryElement in nodeList:
            # Store the record's ID.
            departmentName = self.getText(entryElement.getElementsByTagName("name")[0].childNodes);

            # Append a new department to the list of departments.
            departmentList.append(Department(departmentName, entryElement));
            
        return departmentList;
    
    def recordToString(self, record, prependID=False):
        """
        Convert a record to a packet-friendly string.
        @param record Record to convert.
        @param prependID Prepend the ID of the record to the string.  Optional.  Default: False.
        @return Pipe-delimited string containing a record.
        """
        if not prependID:
            return "%s|%s|%s|%s|%s|%s|%s|%s|%s" % (
             record.getFirstName(),
             record.getLastName(),
             record.getField("Phone Number")[0].getData(),
             record.getField("Email")[0].getData(),
             record.getField("Campus")[0].getData(),
             record.getField("Role")[0].getData(),
             record.getField("Department")[0].getData(),
             record.getField("Fax")[0].getData(),
             record.getField("Office")[0].getData());
        else:
            return "%d|%s|%s|%s|%s|%s|%s|%s|%s|%s" % (
             record.getID(),
             record.getFirstName(),
             record.getLastName(),
             record.getField("Phone Number")[0].getData(),
             record.getField("Email")[0].getData(),
             record.getField("Campus")[0].getData(),
             record.getField("Role")[0].getData(),
             record.getField("Department")[0].getData(),
             record.getField("Fax")[0].getData(),
             record.getField("Office")[0].getData());
            
    def query(self, line, prependID=False):
        """
        Query the database for one or more records.  Expected input:
        'First Name|Last Name|Phone Number|Email|Campus|Role|Department|Fax|Office'
        @param line The query packet, but without the command.
        @param prependID True if you want the ID included with the results (AQRY).  Optional.  Default: False.
        @return Array list of results.
        """
        if line.strip() == "||||||||":
            # They want everyone in the database.  No need to search.
            return [ self.recordToString(self.records[record], prependID) for record in self.records ];
        
        # Split a person by the '|' delimiter.
        person = [ element.strip() for element in line.split("|") ];
        
        if len(person) < 9:
            for i in xrange(len(person), 9):
                person.append("");
        
        resultList = [];
        for id in self.records:
            record = self.records[id];
            
            # Obtain all fields.
            fields = record.getAllFields();
            
            valid = True;
            for i in xrange(0, len(person)):
                if person[i] == "":
                    continue;
                
                if person[i].upper() not in fields[i].upper():
                    valid = False;
                    break;
            
            if valid:
                resultList.append(self.recordToString(record, prependID));
        
        if len(resultList) == 0:
            resultList.append("");
        
        self.addHit();
        
        return resultList;
    
    def addHit(self, type="QRY"):
        """
        Add a hit.  Records it into a log.
        """
        now = recordHit(type);
        self.hits[4].append([type, now]);
        
        if type == "QRY":
            self.hits[0] += 1;
        elif type == "ADD":
            self.hits[1] += 1;
        elif type == "UPD":
            self.hits[2] += 1;
        elif type == "DEL":
            self.hits[3] += 1;
        else:
            return False;
        
        return True;
    
    def add(self, line):
        """
        Add a record to the database.  It will check to see if it's first/last/phone/email exist already.
        If not, it should have no problems.  The expected line is the same as a query.
        @param line String of the person to be added, but without the ADD command.
        """
        # Split a person by the '|' delimiter.
        person = [ element.strip() for element in line.split("|") ];
        
        # If the first name, last name, phone number, and e-mail are the same as someone elses,
        # then consider it non-unique.
        for id in self.records:
            unique = False;
            
            if person[0].upper() != self.records[id].getFirstName().upper():
                unique = True;
            
            if person[1].upper() != self.records[id].getLastName().upper():
                unique = True;
            
            if person[2] == "(000)000-0000" or person[2] != self.records[id].getField("Phone Number")[0].getData():
                unique = True;
            
            if person[3] == "_@_._" or person[3] != self.records[id].getField("Email")[0].getData():
                unique = True;
            
            if not unique:
                return False;
            
        
        if len(person) < 9:
            return "ERROR: Invalid send!  Must contain 9 fields.";
        
        # Create fields for this record.
        fields = {};
        fields["First Name"] = [Field("First Name", person[0])];
        fields["Last Name"] = [Field("Last Name", person[1])];
        fields["Phone Number"] = [Field("Phone Number", person[2])];
        fields["Email"] = [Field("Email", person[3])];
        fields["Campus"] = [Field("Campus", person[4])];
        fields["Role"] = [Field("Role", person[5])];
        fields["Department"] = [Field("Department", person[6])];
        fields["Fax"] = [Field("Fax", person[7])];
        fields["Office"] = [Field("Office", person[8])];
        
        newID = self.getUnusedID();
        newGuy = Record(newID, fields, None);
        
        element = self.createRecordElement(newGuy);
        
        self.peopleElement.appendChild(element);
        
        newGuy.setElement(element);
        
        self.records[newID] = newGuy;
        
        self.addHit("ADD");
        
        logPrint("New record added: %s %s." % (person[0], person[1]));
        
        self.write();
        
        return True;
    
    def delete(self, line):
        """
        Delete a record from the database.
        @param line The submitted string (without the command).
        """
        # Split the record into different parts.
        person = [ element.strip() for element in line.split("|") ];
        
        # Obtain the person's ID.
        id = self.findPerson(person);
        
        if id < 0:
            return False;
        
        # Remove the element from the XML document.
        try:
            element = self.records[id].element;
            
            del self.records[id];
            
            self.peopleElement.removeChild(element);
            
            self.write();
        except Exception, e:
            print "DELETE ERROR:",e;
            return False;
        
        self.addHit("DEL");
        
        logPrint("Deleted record %s %s." % (person[0], person[1]));
        
        return True;
    
    def update(self, id, newRecord):
        """
        Update an existing record (found by the passed ID) with a new record, which should contain new fields.
        @param id Unique identification number of the old record.
        @param newRecord Record object containing new fields and an element - this won't create them for you.
        """
        if isinstance(newRecord, Record):
            if id in self.records:
                self.peopleElement.removeChild(self.records[id].element);
                self.records[id] = newRecord;
                self.peopleElement.appendChild(self.records[id].element);
                
                logPrint("Updated record for %s %s." % (
                    newRecord.getField("First Name")[0].getData(), 
                    newRecord.getField("Last Name")[0].getData()));
                
                self.write();
            else:
                print "UPDATE ERROR: Given ID (" + str(id) + ") does not exist.";
                return False;
        else:
            print "UPDATE ERROR: Passed parameter is not a record.";
            return False;
        
        self.addHit("UPD");
        
        return True;
    
    def addDepartment(self, departmentName):
        """
        Add a department to the list.
        @return True on success, False if the department exists.
        """
        if departmentName in self.departments:
            return False;
        
        deptElement = self.createDepartmentElement(departmentName);
        self.departmentsElement.appendChild(deptElement);
        
        self.departments[departmentName] = Department(departmentName, deptElement);
        
        self.write();
        
        return True;
    
    def modifyDepartment(self, oldDepartmentName, newDepartmentName):
        """
        Change the name of a department.
        @param oldDepartmentName What the department name is currently called.
        @param newDepartmentName What to change the department to.
        @return True on success, False if the new name exists.
        """
        if newDepartmentName in self.departments or oldDepartmentName not in self.departments:
            return False;
        
        deptObject = self.departments[oldDepartmentName];
        deptElement = deptObject.getElement();
        self.departmentsElement.removeChild(deptElement);
        
        del self.departments[oldDepartmentName];
        
        element = self.createDepartmentElement(newDepartmentName)
        self.departmentsElement.appendChild(element);
        
        deptObject.setName(newDepartmentName);
        deptObject.setElement(element);
        
        self.departments[newDepartmentName] = deptObject;
        
        self.write();
        
        return True;
    
    def deleteDepartment(self, departmentName):
        """
        Remove a department from the list.
        @return True on success, false if the department doesn't exist.
        """
        if departmentName not in self.departments:
            return False;
        
        deptElement = self.departments[departmentName].getElement();
        
        del self.departments[departmentName];
        
        self.departmentsElement.removeChild(deptElement);
        
        self.write();
        
        return True;
        
    def findPerson(self, data):
        """
        Find a person based on the given data.
        @param data Array containing the elements of a person.
        @return ID of the record.
        """
        data = [ field.upper() for field in data ];
        for id in self.records:
            record = self.records[id];
            
            # Obtain all fields.
            fields = record.getAllFields();
            
            if data[0] != "" and not data[0].upper() in (fields[0].upper()):
                continue;
            if data[1] != "" and not data[1].upper() in (fields[1].upper()):
                continue;
            if data[2] != "" and not data[2].upper() in (fields[2].upper()):
                continue;
            if data[3] != "" and not data[3].upper() in (fields[3].upper()):
                continue;
            if data[4] != "" and not data[4].upper() in (fields[4].upper()):
                continue;
            if data[5] != "" and not data[5].upper() in (fields[5].upper()):
                continue;
            if data[6] != "" and not data[6].upper() in (fields[6].upper()):
                continue;
            if data[7] != "" and not data[7].upper() in (fields[7].upper()):
                continue;
            if len(data) > 8 and data[8] != "" and not data[8].upper() in (fields[8].upper()):
                continue;
            
            return id;
        
        # Didn't find any matches.
        return -1;
    
    def createRecordElement(self, record):
        """
        Creates a record element.  This makes appending a child node easy.
        @param record Record to use.
        """
        # Create a new element.
        recordElement = self.doc.createElement("record");
        
        # Create an ID node as a child of the record.
        idNode = self.doc.createElement("id");
        
        # Place the record's ID inside of the "id" node.
        idNode.appendChild(self.doc.createTextNode(str(record.getID())));
        
        # Place the ID node into the record node.
        recordElement.appendChild(idNode);
        
        # Now to repeat this process for every field.
        for field in record.getFields():
            fieldElement = self.doc.createElement("field");
            fieldElement.setAttribute("name", field.getName());
            fieldValue = self.doc.createTextNode(field.getData());
            fieldElement.appendChild(fieldValue);
            
            recordElement.appendChild(fieldElement); 
        
        return recordElement;
    
    def createDepartmentElement(self, departmentName):
        """
        Creates a DOM element under the Department list.
        @return Department element (not appended).
        """
        # First create the department element.
        departmentElement = self.doc.createElement("entry");
        
        # Now it has one child node: name
        nameNode = self.doc.createElement("name");
        
        # The department's name goes into the name element.
        nameNode.appendChild(self.doc.createTextNode(str(departmentName)));
        
        # Attach the name node to the department element.
        departmentElement.appendChild(nameNode);
        
        return departmentElement;
    
    def createRecord(self, arr, containsID=False):
        """
        Create a new record (convenience method).
        @param arr Array containing the data of the new record (minus the command).
        @param containsID Is this record prepended with an ID number?  Optional.  Default: False.
        @return Record object on success, None on failure.
        """
        counter = 0;
        if containsID:
            id = arr[0];
            counter += 1;
        else:
            id = getUnusedID();
        
        fields = {};
        fields["First Name"] = [Field("First Name", arr[counter])];
        counter += 1;
        fields["Last Name"] = [Field("Last Name", arr[counter])];
        counter += 1;
        fields["Phone Number"] = [Field("Phone Number", arr[counter])];
        counter += 1;
        fields["Email"] = [Field("Email", arr[counter])];
        counter += 1;
        fields["Campus"] = [Field("Campus", arr[counter])];
        counter += 1;
        fields["Role"] = [Field("Role", arr[counter])];
        counter += 1;
        fields["Department"] = [Field("Department", arr[counter])];
        counter += 1;
        fields["Fax"] = [Field("Fax", arr[counter])];
        counter += 1;
        fields["Office"] = [Field("Office", arr[counter])];
        
        r = Record(id, fields, None);
        r.setElement(self.createRecordElement(r));
        
        return r;
    
    def getUnusedID(self):
        """
        Find an ID not in use.
        @return unique ID.
        """
        id = 1;
        
        for id in xrange(0, 100000):
            if id not in self.records:
                return id;
        
        return -1;
    
    def getDepartments(self):
        """
        Get the list of departments.
        @return Dictionary list of departments.
        """
        return self.departments;
    
    def getDefaultFile(self):
        return DEFAULT_FILE;
    
    def checksum(self, data):
        """
        Calculate the sum of a file using the native command-line tool.  Used for file propagation
        integrity checks.
        @param data Data to check.
        @return The calculated sha1sum.
        """
        return sha.sha(data).hexdigest();
    
    def runTests(self):
        """
        Run standard tests.
        """
        pass
        
        

if __name__ == "__main__":
    import time;
    # Run debug session.
    parseStartTime = time.time();
    db = XMLDatabase();
    startTime = time.time();
    db.load();
    endTime = time.time();
    
    print "Found %d records." % (len(db.records));
    print "Found %d departments." % (len(db.departments));
    print "Took %.4f seconds to parse file." % (startTime - parseStartTime);
    print "Took %.4f seconds to load data." % (endTime - startTime);
    print "\tTotal: %.4f seconds." % (endTime - parseStartTime);
    
    print "Performing checksum of",DEFAULT_FILE;
    sum = db.checksum(db.doc.toxml());
    print "Result:",sum;
     
    