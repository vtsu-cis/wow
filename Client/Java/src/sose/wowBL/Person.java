package src.sose.wowBL;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.sose.wowDA.Address;
import src.sose.wowDA.DataAccess;

/**
 * TODO List
 * - Make useful sets instead of the direct sets implemented
 * - Make newPerson use sets to fill out a person and return it
 * - Create query, update, add, delete
 * - If needed, make the constructor do something useful
 */
/**
 * This is the main business logic for the Window on the World application.
 * The attributes of a person and what a person object can do are outlined here.  
 * This class will grow and change when the fields are allowed to be defined in the database.
 * 
 * @author - Nick Guertin and Boomer Ransom
 */
public class Person implements Comparable<Person>{
	private Integer ID = -1;
	private String firstName = "";
	private String lastName = "";
	private ArrayList<Phone> phoneNumbers = new ArrayList<Phone>();
	private ArrayList<Email> emailAddresses = new ArrayList<Email>();
	private String server = "NA";
	private ArrayList<Field> fields = new ArrayList<Field>();
	
	/**
	 * Implements abstract method from Comparable interface
	 * 
	 * @param p Person object to compare to
	 * 
	 * @return equality value based on last name and first name
	 */
	public int compareTo(Person p)
	{
		Integer i;
		Integer j;
		j = Integer.valueOf((lastName.compareTo(p.getLastName())));
		
		if(j == 0)
		{
			i = Integer.valueOf(firstName.compareTo(p.getFirstName()));
			return i.compareTo(j);
		}
		
		return j;
	}
	
	/**
	 *	Default constructor
	 */
	public Person(){
		
	}
	
	/**
	 * Static method that returns a fully qualified person object
	 * 
	 * @param fn first name
	 * @param ln last name 
	 * @param pn collection of phone numbers
	 * @param em collection of email addresses
	 * @param fi collection of Field objects
	 * 
	 * @return Person object with members assigned to the input parameters. 
	 */
	static public Person newPerson(String fn, String ln, ArrayList<Phone> pn, ArrayList<Email> em, ArrayList<Field> fi)
	{
		Person fullPerson = new Person();

		fullPerson.setFirstName(fn);
		fullPerson.setLastName(ln);
		fullPerson.phoneNumbers = pn;
		fullPerson.emailAddresses = em;
		if(fi != null)
		{
			fullPerson.fields = fi;
		}
		else
		{
			fullPerson.fields = new ArrayList<Field>();
		}
	
		return fullPerson;	
	}
	
	/**
	 * Overloaded method that accepts single phone and single email with no fields
	 * 
	 * @param fn first name
	 * @param ln last name
	 * @param pn phone number 
	 * @param em email
	 * @return Person object with members assigned to the input parameters and blank (but non-null) fields
	 */
	static public Person newPerson(String fn, String ln, String pn, String em)
	{	
		ArrayList<Phone> pns = new ArrayList<Phone>();
		pns.add(new Phone("", pn));
		ArrayList<Email> ems = new ArrayList<Email>();
		ems.add(new Email("", em));
		
		return newPerson(fn, ln, pns, ems, null);		
	}
	
	/**
	 * Overloaded method that accepts single phone and single email with fields
	 * 
	 * @param fn first name
	 * @param ln last name
	 * @param pn phone number 
	 * @param em email
	 * @param fi collection of Field objects
	 * @return Person object with members assigned to the input parameters.
	 */
	static public Person newPerson(String fn, String ln, String pn, String em, ArrayList<Field> fi)
	{	
		ArrayList<Phone> pns = new ArrayList<Phone>();
		pns.add(new Phone("", pn));
		ArrayList<Email> ems = new ArrayList<Email>();
		ems.add(new Email("", em));
		
		return newPerson(fn, ln, pns, ems, fi);		
	}
	
	/**
	 * Obtain this person's ID.  Only works if an administrative query is performed.
	 * @return Person's ID.
	 */
	public Integer getID() {
		return ID;
	}
	
	/**
	 * Set this person's ID.  This number should be obtained from the server.
	 * @param i Integer to set.
	 */
	public void setID(Integer i) {
		ID = i;
	}
	
	/**
	 * Gets first name
	 * 
	 * @return firstname
	 */
	public String getFirstName()
	{
		return firstName;
	}
	
	/**
	 * Sets first name
	 * 
	 * @param fn first name to be set
	 * 
	 * @return 0 if successful<br> 
	 *         1 if name is invalid and name not assigned
	 */
	public int setFirstName(String fn)
	{
		boolean flag = checkName(fn);
		if(flag)
		{
			firstName = fn;	
		}
		else
		{
			if(checkClassroom(fn))
			{
				firstName = fn;
			}
			else
			{
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Gets last name
	 * 
	 * @return last name
	 */
	public String getLastName()
	{
		return lastName;
	}
	
	/**
	 * Sets last name
	 * 
	 * @param ln last name to be set
	 * 
	 * @return 0 if successful<br> 
	 *         1 if name is invalid and name not assigned
	 */
	public int setLastName(String ln)
	{
		boolean flag = checkName(ln);
		if(flag)
		{
			lastName = ln;	
		}
		else
		{
			if(checkClassroom(ln))
			{
				lastName = ln;	
			}
			else
			{
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Gets the first phone number in list
	 * 
	 * @return first phone number. If list is empty, returns blank string.
	 */
	public String getPhoneNumber()
	{
		if(phoneNumbers.size() > 0)
		{
			return phoneNumbers.get(0).getNumber();
		}
		return "";
	}
	
	/**
	 * Gets full list of numbers
	 * 
	 * @return full list of phone numbers. Can be empty but never null.
	 */
	public ArrayList<Phone> getPhoneList()
	{
		return phoneNumbers;
	}
	
	/**
	 * Sets full list of phone numbers overwriting the previous one
	 * 
	 * @param nums list of phone numbers to be set
	 */
	public void setPhoneNumbers(ArrayList<Phone> nums)
	{
		phoneNumbers = nums;
	}
	
	/**
	 * Adds a single phone number to the end of the list of phone numbers
	 * 
	 * @param ty the type or label of the number. For example "Work" or "Home"
	 * @param pn the number
	 * 
	 * @return 0 if successful<br> 
	 *         1 if number is invalid and not added
	 */
	public int addPhone(String ty, String pn)
	{
		Phone temp = new Phone();
		temp.setType(ty);
		if(temp.setNumber(pn) == 0)
		{
			phoneNumbers.add(temp);
			return 0;
		}
		return 1;
	}
	
	/**
	 * Adds a phone number with no label
	 * 
	 * @param pn the phone number
	 * @return 0 if successful<br> 
	 *         1 if number is invalid and not added
	 */
	public int addPhone(String pn)
	{
		return addPhone("", pn);
	}
	
	/**
	 * Updates a phone number in a list
	 * 
	 * @param ty the new type or label. For example "Home" or "Work"
	 * @param pn the new number
	 * @param index the index in the list where the number should be updated
	 * 
	 * @return 0 if successful<br> 
	 *         1 if number is invalid and not updated
	 */
	public int updatePhone(String ty, String pn, int index)
	{
		phoneNumbers.get(index).setType(ty);
		return phoneNumbers.get(index).setNumber(pn);
	}
	
	/**
	 * Updates a phone number in a list with no label
	 * 
	 * @param pn the new number
	 * @param index the index in the list where the number should be updated
	 * 
	 * @return 0 if successful<br> 
	 *         1 if number is invalid and not updated
	 */
	public int updatePhone(String pn, int index)
	{
		return updatePhone("", pn, index);
	}
	
	/**
	 * Gets the first email in list
	 * 
	 * @return first email address. If list is empty, returns blank string.
	 */
	public String getEmail()
	{
		if(emailAddresses.size() > 0)
		{
			return emailAddresses.get(0).getAddress();
		}
		return "";
	}
	
	/**
	 * Gets full email list
	 * 
	 * @return list of all the emails. 
	 */
	public ArrayList<Email> getEmailList()
	{
		return emailAddresses;
	}
	
	/**
	 * Adds a single email to the list
	 * 
	 * @param ty type or label of email. For example, "Work" or "Home"
	 * @param em email address
	 * 
	 * @return 0 if successful<br> 
	 *         1 if address is invalid and not added
	 */
	public int addEmail(String ty, String em)
	{
		Email temp = new Email();
		temp.setType(ty);
		if(temp.setAddress(em) == 0)
		{
			emailAddresses.add(temp);
			return 0;
		}
		return 1;
	}
	
	/**
	 * Adds a single email to the list with no label
	 * 
	 * @param em email address
	 * 
	 * @return 0 if successful<br> 
	 *         1 if address is invalid and not added
	 */
	public int addEmail(String em)
	{
		return addEmail("", em);
	}
	
	/**
	 * Updates a email address in a list
	 * 
	 * @param ty the new type or label. For example "Home" or "Work"
	 * @param em the new email address
	 * @param index the index in the list where the email address should be updated
	 * 
	 * @return 0 if successful<br> 
	 *         1 if address is invalid and not updated
	 */
	public int updateEmail(String ty, String em, int index)
	{
		emailAddresses.get(index).setType(ty);
		return emailAddresses.get(index).setAddress(em);
	}
	
	/**
	 * Updates a email address in a list with a blank label
	 * 
	 * @param em the new email address
	 * @param index the index in the list where the email address should be updated
	 * 
	 * @return 0 if successful<br> 
	 *         1 if address is invalid and not updated
	 */
	public int updateEmail(String em, int index)
	{
		return updateEmail("", em, index);
	}
	
	/**
	 * Sets the whole list of email addresses overwriting the previous one
	 * 
	 * @param emails list of emails to be set
	 */
	public void setEmailAddresses(ArrayList<Email> emails)
	{
		emailAddresses = emails;
	}
	
	/** 
	 * Adds a field to the end of the list
	 * 
	 * @param na  name of the field
	 * @param val value of the field
	 * @return 0 always (error checking in future release)
	 */
	public int addField(String na, String val)
	{
		Field temp = new Field();
		temp.setName(na);
		temp.setValue(val);
		fields.add(temp);
		return 0;
	}
	
	/**
	 * Gets the server name
	 * 
	 * @return name of the server
	 */
	public String getServer()
	{
		return server;
	}
	
	/**
	 * Sets the server
	 * 
	 * @param s server name to be assigned
	 */
	public void setServer(String s)
	{
		server = s;
	}
	
	/**
	 * Checks all the fields for one with name campus and returns that value
	 * 
	 * @return name of the campus. Blank string if none found.
	 */
	public String getCampus()
	{
		String campus = "";
		for(Field f:fields)
		{
			if(f.getName().equals("Campus"))
			{
				campus = f.getValue();
			}
		}
		return campus;
	}
	
	/** Sets the campus field
	 * 
	 * @param s name of campus to be assigned
	 */
	public void setCampus(String s)
	{
		if(fields.size()!=0)
		{
			for(Field f:fields)
			{
				if(f.getName().equals("Campus"))
				{
					f.setValue(s);
				}
			}
		}
	}
	
	/** 
	 * Gets list of fields
	 * 
	 * @return list of fields
	 */
	public ArrayList<Field> getFields()
	{
		return fields;
	}
	
	/** Sets the list of fields
	 * 
	 * @param fi list of fields to be assigned
	 */
	public void setFields(ArrayList<Field> fi)
	{
		fields = fi;
	}
	
	/**
	 * Adds a single field to the field list
	 * 
	 * @param fi Field object to be added
	 */
	public void addField(Field fi)
	{
		fields.add(fi);
	}
	
	/**
	 * This method this will return an ArrayList of type Person
	 * which fit the search criteria provided in the local member varibles
 	 * @param mda collection of servers to search
 	 * 
	 * @return collection of person objects that matched the search criteria 
	 *         outlined in the local member variables.
	 */
	public ArrayList<Person> query(ArrayList<DataAccess> mda)
	{
		ArrayList<Person> result = new ArrayList<Person>();
		ArrayList<String> usedDAs = new ArrayList<String>(mda.size());
		for(DataAccess da : mda)
		{
			boolean doAdd = true;
			for (String used : usedDAs) {
				if (used.equalsIgnoreCase(da.getName())) {
					doAdd = false;
					break;
				}
			}
			
			if (doAdd) {
				result.addAll(da.query(this));
				usedDAs.add(da.getName());
			}
		}
		return result;
	}

	/**
	 * Method that will query the DA layer to see if this Person is 
	 * already in the DB. If so, will exit. If not, will add itself.   
	 *                  
	 * @param mda collection of servers to search
	 * 
	 * @return - an error message or other response from the DataAccess on the 
	 *           success of the addition
	 */
	public String add(ArrayList<DataAccess> mda)
	{
		String msg = "Main server not found in list.";
		
		if((this.getFirstName().length() == 0) && (this.getLastName().length() == 0))
		{
			msg = "First name and last name required.";
			return msg;
		}
		
		final Address mainServer 	= src.sose.wowLibs.WowHelper.getMainServerAddress();
		final String mainAddress 	= mainServer.GetAddress();
		final int mainPort 			= mainServer.getPort();
		for (DataAccess da : mda) {
			Address addr = da.getAddress();
			if (addr.GetAddress().equalsIgnoreCase(mainAddress) && (addr.getPort() == mainPort)) {
				return da.add(this);
			}
		}
	
		return msg;
	}
	
	/**
	 * Method that will query the DA layer to see if it is in the DB. If 
	 * it is, it will update the phone and email address contained in the member vars.  
	 *                  
	 * @param mda collection of servers to search
	 * @return - an error message or other response from the DataAccess on the 
	 *           success of the update
	 */
	public String update(ArrayList<DataAccess> mda)
	{			
		final Address mainServer 	= src.sose.wowLibs.WowHelper.getMainServerAddress();
		final String mainAddress 	= mainServer.GetAddress();
		final int mainPort 			= mainServer.getPort();
		for (DataAccess da : mda) {
			Address addr = da.getAddress();
			if (addr.GetAddress().equalsIgnoreCase(mainAddress) && (addr.getPort() == mainPort)) {
				return da.update(this);
			}
		}
		
		return null;
	}
	
	/**
	 * Method that will query the DA layer to see if it is in the DB. If 
	 * it is, it will delete that entry.   
	 *                  
	 * @param MDA collection of servers to search
	 * @return - an error message or other response from the DataAccess on the 
	 *           success of the deletion
	 */
	public String delete(ArrayList<DataAccess> mda)
	{
		final Address mainServer 	= src.sose.wowLibs.WowHelper.getMainServerAddress();
		final String mainAddress 	= mainServer.GetAddress();
		final int mainPort 			= mainServer.getPort();
		for (DataAccess da : mda) {
			Address addr = da.getAddress();
			if (addr.GetAddress().equalsIgnoreCase(mainAddress) && (addr.getPort() == mainPort)) {
				return da.delete(this);
			}
		}
		
		return null;
	}
	
	/**
	 * Overwrites method inherited from Object. Will return a person's information in a 
	 *    parsable format. Each field is separated by a tab. 
	 *                  
	 * @return Person's info
	 */
	public String toString()
	{
		return (lastName + "\t\t" + firstName + "\t\t" + phoneNumbers.get(0).getNumber() + "\t" + emailAddresses.get(0).getAddress());
	}
	
	/**
	 * Method that checks a name to make sure it isn't empty, and doesn't contain 
	 * anything but letters, hyphens, periods, apostrophes, and white spaces. 
	 * 
	 * @param name name to be checked
	 * @return <code>true</code> if valid<br> 
	 *         <code>false</code> if invalid
	 */
	public boolean checkName(String name)
	{
		boolean valid = false;
		
		if(name.length() == 0)
		{
			return valid;
		}
		
		String pat = "^[a-zA-Z]+[a-zA-Z[\\-.'[\\s]]]*";
		
        Pattern pattern = 
            Pattern.compile(pat);
        
        Matcher matcher1 = 
            pattern.matcher(name);
        
		
            if(matcher1.matches())
            {
            	valid = true;
            }
            else 
            {
            	valid = false;
            }
            
		return valid;
	}
	
	/**
	 * Method that checks a name to make sure it conforms to the standard for 
	 * classroom identifiers. More specifically, three letters followed by 
	 * three numbers and an optional letter tag. Ex. ABC103 or XYZ401B.
	 * 
	 * @param name name to be checked
	 * @return <code>true</code> if valid<br> 
	 *         <code>false</code> if invalid
	 */
	public boolean checkClassroom(String name)
	{
		boolean valid = false;
		
		if(name.length() == 0)
		{
			return valid;
		}
		
		String pat = "[a-zA-Z]{3}\\d{3}[a-zA-Z]?";
		
        Pattern pattern = 
            Pattern.compile(pat);
        
        Matcher matcher1 = 
            pattern.matcher(name);
        
		
            if(matcher1.matches())
            {
            	valid = true;
            }
            else 
            {
            	valid = false;
            }
            
		return valid;
	}
	/**
	 * Method that compares another Person object with this one to determine equality.
	 * Will check name, phone numbers, and email addresses.
	 * 
	 * @param comp Person object to be compared against
	 * @return <code>true</code> if equal<br> 
	 *         <code>false</code> if unequal
	 */
	public boolean equals(Person comp)
	{
		boolean result = true;

		if(!this.firstName.equalsIgnoreCase(comp.firstName))
		{
			result = false;
		}

		if(!this.lastName.equalsIgnoreCase(comp.lastName))
		{
			result = false;
		}

		for(int i = 0; i < phoneNumbers.size(); i++)
			
			if(this.phoneNumbers.get(i).getNumber().compareToIgnoreCase(comp.phoneNumbers.get(i).getNumber()) != 0)
			{
				result = false;
			}

		for(int i = 0; i < emailAddresses.size(); i++)	
			if(this.emailAddresses.get(i).getAddress().compareToIgnoreCase(comp.emailAddresses.get(i).getAddress()) != 0)
			{
				result = false;
			}

		return result;
	}
	
	/**
	 * Returns the hashcode for use in serializing objects of this type
	 * 
	 * @return hashcode for this object
	 */
	public int hashCode() {
		  assert false : "hashCode not designed";
		  return 42;
		  }

}	
