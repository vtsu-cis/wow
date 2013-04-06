import xml.dom.minidom
from xml.dom.minidom import Node;
import Person
from Person import Person

doc = xml.dom.minidom.parse("wow.xml");
rootElement = doc.getElementsByTagName("data-group")[0];
peopleElement = rootElement.getElementsByTagName("people")[0];
departmentsElement = rootElement.getElementsByTagName("departments")[0];

def getDepartments():
    depts = []
    for d in departmentsElement.childNodes:
        for e in d.childNodes: #<entry>
            for n in e.childNodes: #<name>
                depts.append(n.data)
    return depts

def isDepartment(deptList, value):
    if value in deptList:
        return 1
    else:
        return 0
    
#Read all People from within the XML File, create a Person object for each, and store in a 
#list of People  
def getPeople():
    people = []

    for p in peopleElement.childNodes:
        id=0
        fields = []
        for r in p.childNodes:
            if r.nodeName == "id":
                id = r.firstChild.data
            elif r.nodeName == "field":
                try:
                    fields.append(r.firstChild.data)
                except AttributeError:
                    fields.append("None")
        if id != 0:
            tempPerson = Person(id, fields)
            people.append(tempPerson)
    return people

def getFile():
    xmlFile = ""
    f = open("wow.xml")
    for line in f:
        xmlFile = xmlFile + line
    f.close()
    return xmlFile

def setFile(strFile):
    f = open("wow2.xml", "w")
    f.write(strFile)
    f.close()

#For right now, this method updates a testXML.xml file, so that I don't 
#ruin the copy im using to test other methods. This is a very iffy way
#of printing this out - it works, and thats what matters - but I will 
#improve on this if I find a more efficient way.
def writeXML(departments, people):
    f = open("textXML.xml", "w")
    f.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
    f.write("<data-group>\n")
    f.write("\t<departments>\n")
    for dept in departments:
        f.write("\t\t<entry>\n")
        f.write("\t\t\t<name>"+dept+"</name>\n")
        f.write("\t\t</entry>\n")
    f.write("\t</departments>\n")
    f.write("\t<people>\n")
    for person in people:
        f.write("\t\t<record>\n")
        f.write("\t\t\t<id>"+str(person.getID())+"</id>\n")
        f.write("\t\t\t<field name=\"First Name\">"+person.getFirstName()+"</field>\n")
        f.write("\t\t\t<field name=\"Last Name\">"+person.getLastName()+"</field>\n")
        f.write("\t\t\t<field name=\"Phone Number\">"+person.getPhoneNumber()+"</field>\n")
        f.write("\t\t\t<field name=\"Email\">"+person.getEMail()+"</field>\n")
        f.write("\t\t\t<field name=\"Campus\">"+person.getCampus()+"</field>\n")
        f.write("\t\t\t<field name=\"Role\">"+person.getRole()+"</field>\n")
        f.write("\t\t\t<field name=\"Department\">"+person.getDepartment()+"</field>\n")
        f.write("\t\t\t<field name=\"Fax\">"+person.getFax()+"</field>\n")
        f.write("\t\t\t<field name=\"Office\">"+person.getOffice()+"</field>\n")
        f.write("\t\t</record>\n")
    f.write("\t</people>\n")
    f.write("</data-group>\n")