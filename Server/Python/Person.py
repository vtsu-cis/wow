#Contains all information for an individual person, with Getters and Setters for all
#queryLastName() was a test and can be removed when finalized, queryAll() is probably
#pointless aswell
class Person:
    def __init__ (self, _id, _fields): # Create a person object
        self.ID = _id
        self.fields = {'fname': _fields[0], 'lname': _fields[1],
                       'phone': _fields[2], 'email': _fields[3],
                       'campus': _fields[4], 'role': _fields[5],
                       'department': _fields[6], 'fax': _fields[7],
                       'office': _fields[8]}
#Define Getters
    def getID(self):
        return self.ID
    def getFirstName(self):
        return self.fields['fname']

    def getLastName(self):
        return self.fields['lname']

    def getPhoneNumber(self):
        return self.fields['phone']
    
    def getEMail(self):
        return self.fields['email']

    def getCampus(self):
        return self.fields['campus']

    def getRole(self):
        return self.fields['role']

    def getDepartment(self):
        return self.fields['department']

    def getFax(self):
        return self.fields['fax']

    def getOffice(self):
        return self.fields['office']
    
    def getAll(self, divider):
        returnStr = self.fields['fname'] + divider + self.fields['lname'] + divider + self.fields['phone'] + divider + self.fields['email'] + divider + self.fields['campus'] + divider + self.fields['role'] + divider + self.fields['department'] + divider + self.fields['fax'] + divider + self.fields['office']
        return returnStr
#Define Setters
    def setFirstName(self, _fname):
         self.fields['fname'] = _fname

    def setLastName(self, _lname):
         self.fields['lname'] = _lname

    def setPhoneNumber(self, _phone):
         self.fields['phone'] = _phone
    
    def setEMail(self, _email):
         self.fields['email'] = _email

    def setCampus(self, _campus):
         self.fields['campus'] = _campus

    def setRole(self, _role):
         self.fields['role'] = _role

    def setDepartment(self, _dept):
         self.fields['department'] = _dept

    def setFax(self, _fax):
         self.fields['fax'] = _fax

    def setOffice(self, _office):
         self.fields['office'] = _office
    
    
#Useless Queries
    def querylastName(self, _lname):
        if self.fields['lname'] == _lname:
            return 1
        return 0
    
    def queryAll(self, _search):
        for key in self.fields:
            if self.fields[key] == _search:
                return 1
        return 0
