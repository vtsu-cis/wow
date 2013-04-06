# Field.py
# Stackless Python Server

class Field:
    """
    Store data about a person.  Each Field has a name and has data.
    """
    
    def __init__(self, fieldName, fieldData):
        """
        Create a field with initial data.
        @param fieldName Name of the field.
        @param fieldData Data contained within the field.
        """
        self.name = str(fieldName);
        self.data = str(fieldData);
    
    def setName(self, newFieldName):
        """
        Set the new name for this field.
        @param newFieldName Name to set.
        """
        self.name = newFieldName;
    
    def getName(self):
        """
        Obtain the name of the field.
        @return String containing the name of the field.
        """
        return self.name;
    
    def setData(self, newFieldData):
        """
        Set new data for this field.
        @param newFieldData Data to set.
        """
        self.data = newFieldData;
    
    def getData(self):
        """
        Obtain the data contained within the field.
        @return String containing this field's data.
        """
        return self.data;