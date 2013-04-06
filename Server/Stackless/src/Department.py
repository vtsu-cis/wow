# Department.py
# Stackless Python Server

class Department:
    """
    The Record class tracks individual data about a person, including their ID number.
    """
    
    def __init__(self, name, element):
        """
        Create a new Department.
        @param name Unique department name.
        @param element XML DOM record element that this department belongs to.
        """
        # Set data.
        self.name = name;
        self.element = element;
        
    def getName(self):
        """
        Get this department's name.
        @return String holding the department's name.
        """
        return self.name;
    
    def setName(self, name):
        """
        Set the name of this department.
        @param name New name of the department.
        """
        self.name = name;
    
    def getElement(self):
        """
        Grab the XML record element that this person belongs to.
        @return xml.dom object.
        """
        return self.element;
    
    def setElement(self, element):
        """
        Element to set to this department.
        @param element Element to set.
        """
        self.element = element;
        