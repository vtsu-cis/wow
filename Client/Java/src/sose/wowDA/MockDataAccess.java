package src.sose.wowDA;

import java.util.ArrayList;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;

/**
 * This is a mock object implementing DataAccess.
 * It's purpose is to allow testing of DA type functionality without dealing with real work.                  
 * @author Nick Guertin and Boomer Ransom
 */
public class MockDataAccess implements DataAccess {

	ArrayList<Person> people;
	public String serverID = "MOK";
	
	public String getName()
	{
		return "serverID";
	}
	
	public void setName(String _name)
	{
		serverID = _name;
	}
	
	public MockDataAccess()
	{
		people = new ArrayList<Person>();
		people.add(Person.newPerson("Nick", "Guertin", "524-6367", "n_guertin@yahoo.com"));
		people.add(Person.newPerson("Boomer", "Ransom", "123-4567", "DRansom@vtc.vsc.edu"));
		people.add(Person.newPerson("Chris", "Beattie", "350-7430", "CBeattie@vtc.vsc.edu"));
		people.add(Person.newPerson("Peter", "Chapin", "987-6543", "pchapin@ecet.vtc.edu"));
		people.add(Person.newPerson("Craig", "Damon", "111-1111", "cdamon@vtc.edu"));
		people.add(Person.newPerson("Mike", "Soulia", "555-6543", "soulia@aol.com"));
		people.add(Person.newPerson("Nagi", "Basha", "789-1837", "nbasha@vtc.edu"));
	}
	
	public MockDataAccess(String serverName)
	{
		serverID = serverName;
		people = new ArrayList<Person>();
		people.add(Person.newPerson("Nick", "Guertin", "524-6367", "n_guertin@yahoo.com"));
		people.add(Person.newPerson("Nikoli", "Ransom", "123-4567", "DRansom@vtc.vsc.edu"));
		people.add(Person.newPerson("Chris", "Beattie", "350-7430", "CBeattie@vtc.vsc.edu"));
		people.add(Person.newPerson("Peter", "Chapin", "987-6543", "pchapin@ecet.vtc.edu"));
		people.add(Person.newPerson("Craig", "Damon", "111-1111", "cdamon@vtc.edu"));
		people.add(Person.newPerson("Mike", "Soulia", "555-6543", "soulia@aol.com"));
		people.add(Person.newPerson("Nagi", "Basha", "789-1837", "nbasha@vtc.edu"));
	}
	
	public ArrayList<Person> query()
	{
		return new ArrayList<Person>();
	}
	
	public ArrayList<Person> query(Field subject)
	{
		return new ArrayList<Person>();
	}
	
	public ArrayList<Person> query(Person subject) 
	{
		ArrayList<Person> result = new ArrayList<Person>();
		for(Person single : people)
		{
			if(single.getLastName().toLowerCase().contains(subject.getLastName().toLowerCase()))
			{
				if(single.getFirstName().toLowerCase().contains(subject.getFirstName().toLowerCase()))
				{
					single.setServer(serverID);
					result.add(single);
				}
			}
		}
		
		return result;
	}

	public String update(Person subject) 
	{
		for(Person single : people)
		{
			if(subject.getLastName().equals(single.getLastName()) && subject.getFirstName().equals(single.getFirstName()))
			{
				people.set(people.indexOf(single), subject);
				return "OK";
			}
		}
		
		return "Error: Record not found";
	}
	
	public String add(Person subject)
	{
		for(Person single : people)
		{
			if(subject.getLastName().equals(single.getLastName()) && subject.getFirstName().equals(single.getFirstName()))
			{
				return "Error: Record already exists";
			}
		}
		
		people.add(subject);
		
		return "OK";
	}
	
	public String delete(Person subject)
	{
		for(Person single : people)
		{
			if(subject.getLastName().equals(single.getLastName()) && subject.getFirstName().equals(single.getFirstName()))
			{
				people.remove(single);
				return "OK";
			}
		}
		
		return "Error: Record does not exist";
	}

	/**
	 * Query the database for every user and return a list. 
	 * This query is meant to be used when grabbing a list for data not necessarily available in the
	 * Person class.
	 */
	public ArrayList<String[]> getUnformattedAll() {
		ArrayList<String[]> results = new ArrayList<String[]>();
		final String line = "|";
		String incoming = new String("");
		
		//Query all:
		//incoming = connect("QRY: " + line + line + line + line + line + line + line + line);
		
		String[] temp = incoming.split("\\$");
		
		for (String t : temp) {
			t.replace("_@_._", ""); //Replace empty e-mails with an empty string.
			results.add(t.split("\\|", -1));
		}
		
		return results;
	}
	
	public Address getAddress() {
		return null;
	}
}
