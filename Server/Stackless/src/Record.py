# Record.py
# Stackless Python Server

class Record:
    """
    The Record class tracks individual data about a person, including their ID number.
    """
    
    def __init__(self, id, fieldList, element):
        """
        Create a new Record.
        @param id Unique identification number of this user.
        @param fieldList Dictionary of Field objects that looks like: {fieldName, [field1, field2, ...]}
        @param element XML DOM record element that this user belongs to.
        """
        # Set data.
        self.id = int(id);
        self.fields = fieldList;
        self.element = element;
        
    def getID(self):
        """
        Grab this person's ID number.
        @return ID of the integer variety.
        """
        return self.id;
    
    def setID(self, newID):
        """
        Replace the existing ID with a new ID.
        @param newID Unique integer that this record will be identified by.
        """
        self.id = int(newID);
    
    def getElement(self):
        """
        Grab the XML record element that this person belongs to.
        @return xml.dom object.
        """
        return self.element;
    
    def setElement(self, element):
        """
        Replaces this record's existing XML DOM element with a given one.
        @param xml.dom element to insert into this record.
        """
        self.element = element;
    
    def getAllFields(self):
        """
        Convenience method.  Get all fields as an array.
        @return array of strings.
        """
        arr = [];

        arr.append(self.fields["First Name"][0].getData());
        arr.append(self.fields["Last Name"][0].getData());
        arr.append(self.fields["Phone Number"][0].getData());
        arr.append(self.fields["Email"][0].getData());
        arr.append(self.fields["Campus"][0].getData());
        arr.append(self.fields["Role"][0].getData());
        arr.append(self.fields["Department"][0].getData());
        arr.append(self.fields["Fax"][0].getData());
        arr.append(self.fields["Office"][0].getData());
        
        return arr;
    
    def getField(self, fieldName):
        """
        Obtain a field based on the requested field name.
        @param fieldName Field to grab.
        @return Returns an array list of data from the field.
        """
        if fieldName in self.fields:
            return self.fields[fieldName];
    
    def getFields(self):
        """
        Simply return all field objects.  This is handy for things like XML looping.
        @return array list of fields.
        """
        fieldList = [];
        for fieldName in self.fields:
            for field in self.fields[fieldName]:
                fieldList.append(field);
        
        return fieldList;
    
    def replaceFields(self, newFields):
        """
        Replace the old fields with a dictionary of new fields.
        The fields should be formatted like:
        {fieldName: [field1, field2], fieldName2, [field1, field2, field3]}
        @param newFields Dictionary of fields. 
        """
        self.fields = newFields;
    
#    def setFields(self, fieldName, fieldData):
#        """
#        Over-writes all existing data that are under the given field name.
#        @param fieldName Name of the fields to change.
#        @param fieldData Array of fields to replace the old data with.
#        """
#        if fieldName in self.fields:
#            self.fields[fieldName] = fieldData;
#    
#    def setField(self, fieldName, oldData, newData):
#        """
#        Rather than over-writing all of the fields, replace a single field based on what data it used to have.
#        @param fieldName Name of the field to replace.
#        @param oldData Data that the field used to contain.
#        @param newData Data to replace.
#        """
#        for field in self.fields[name]:
#            if self.fields[name][field].getData() == oldData:
#                self.fields[name][field] = newData;
#                return None;
    
    def getFirstName(self):
        """
        Convenience function.  Obtain the first name from the list of fields.
        @return This person's first name.  If the field isn't set, it returns None.
        """
        return self.fields["First Name"][0].getData();
    
    def getLastName(self):
        """
        Convenience function.  Obtain the last name from the list of fields.
        @return This person's last name.  If the field isn't set, it returns None.
        """
        return self.fields["Last Name"][0].getData();
    
    def fieldCount(self):
        """
        Return the total number of fields.
        @return Integer.
        """
        return len(self.fields);
    