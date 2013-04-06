package src.sose.wowDA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import src.sose.wowBL.Email;
import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowLibs.ConnectionHandler;

public class HackDA implements DataAccess{

	private String server;
	private int port;
	private String serverID;
	
	/**
	 * Default constructor for class HackDA
	 * 
	 * @param _server address of the server
	 * @param _port port of the server
	 * @param _servID server's ID
	 */
	public HackDA(String _server, int _port, String _servID)
	{
		server = _server;
		port = _port;
		serverID = _servID;
	}
	
	/**
	 * Gets the name of the server of type String
	 * 
	 * @return server's ID
	 */
	public String getName()
	{
		return serverID;
	}
	
	/**
	 * Set the name (ID) of the server
	 * 
	 * @param s name to be set
	 */
	public void setName(String s)
	{
		serverID = s;
	}
	
	/**
	 * Receives every entry in the database.  It checks for repeats
	 * and then adds entries one-by-one to the ArrayList.
	 * 
	 * @return everyone in database
	 */
	public ArrayList<Person> query()
	{
		ArrayList<Person> results = new ArrayList<Person>();
		String line = "|";
		String incoming = "";
		String[] tempString;
		ArrayList<String> sameStrings = new ArrayList<String>();

		incoming = connect("QRY: " + line + line + line + line + line + line + line);

		tempString = incoming.split("\\$");

		// Makes sure there are no repeats in incoming records
		for(String piece : tempString)
		{
			if(piece.length() != 0)
			{
				sameStrings.add(piece);
				int same = 0;
				for(int i=0;i<sameStrings.size();i++)
				{
					if(piece.equals(sameStrings.get(i)))
					{
						same++;
					}
				}
				if(same==1)
				{
					Person tempPerson = convertString2Person(piece);
					tempPerson.setServer(serverID);
					if(tempPerson.getEmail().equals("_@_._"))
					{
						ArrayList<Email> blank = new ArrayList<Email>();
						blank.add(new Email("", ""));
						tempPerson.setEmailAddresses(blank);
					}
					results.add(tempPerson);
				}

			}
		}

		return results;
	}
	
	/**
	 * Query the database and receive information (first and last name)
	 * of the matching Person.
	 * 
	 * @param subject object for the queried Person
	 * @return results matching subject
	 */
	public ArrayList<Person> query(Person subject)
	{
		ArrayList<Person> results = new ArrayList<Person>();
		String line = "|";
		String incoming = "";
		String[] tempString;
		ArrayList<String> sameStrings = new ArrayList<String>();

		incoming = connect("QRY: " + subject.getFirstName() + line + subject.getLastName() 
				+ line + line + line + line + line + line + line);

		tempString = incoming.split("\\$");

		// Makes sure there are no repeats in incoming records
		for(String piece : tempString)
		{
			if(piece.length() != 0)
			{
				sameStrings.add(piece);
				int same = 0;
				for(int i=0;i<sameStrings.size();i++)
				{
					if(piece.equals(sameStrings.get(i)))
					{
						same++;
					}
				}
				if(same==1)
				{
					Person tempPerson = convertString2Person(piece);
					tempPerson.setServer(serverID);
					if(tempPerson.getEmail().equals("_@_._"))
					{
						ArrayList<Email> blank = new ArrayList<Email>();
						blank.add(new Email("", ""));
						tempPerson.setEmailAddresses(blank);
					}
					results.add(tempPerson);
				}

			}
		}

		return results;
	}
	
	/**
	 * Query the database and receive information based on the requested
	 * criteria.
	 * 
	 * @param criteria what is being searched for
	 * @return results matching criteria
	 */
	public ArrayList<Person> query(Field criteria)
	{
		ArrayList<Person> results = new ArrayList<Person>();
		String line = "|";
		String incoming = "";
		String[] tempString;
		ArrayList<String> sameStrings = new ArrayList<String>();
		String queryString = "QRY: ";
		
		if(criteria.getName().equals("First Name"))
		{
			queryString += criteria.getValue() + line + line + line + line + line + line + line + line;
		}
		else if(criteria.getName().equals("Last Name"))
		{
			queryString += line + criteria.getValue() + line + line + line + line + line + line + line;
		}
		else if(criteria.getName().equals("Phone"))
		{
			queryString += line + line + criteria.getValue() + line + line + line + line + line + line;
		}
		else if(criteria.getName().equals("Email"))
		{
			queryString += line + line + line + criteria.getValue() + line + line + line + line + line;
		}
		else if(criteria.getName().equals("Campus"))
		{
			queryString += line + line + line + line + criteria.getValue() + line + line + line + line;
		}
		else if(criteria.getName().equals("Role"))
		{
			queryString += line + line + line + line + line + criteria.getValue() + line + line + line;
		}
		else if(criteria.getName().equals("Department"))
		{
			queryString += line + line + line + line + line + line + criteria.getValue() + line + line;
		}
		else if(criteria.getName().equals("Fax"))
		{
			queryString += line + line + line + line + line + line + line + criteria.getValue() + line;
		}
		else if(criteria.getName().equals("Office"))
		{
			queryString += line + line + line + line + line + line + line + line + criteria.getValue();
		}
		else
		{
			return results;
		}
		
		incoming = connect(queryString);

		tempString = incoming.split("\\$");


		// Makes sure there are no repeats in incoming records
		for(String piece : tempString)
		{
			if(piece.length() != 0)
			{
				sameStrings.add(piece);
				int same = 0;
				for(int i=0;i<sameStrings.size();i++)
				{
					if(piece.equals(sameStrings.get(i)))
					{
						same++;
					}
				}
				if(same==1)
				{
					Person tempPerson = convertString2Person(piece);
					tempPerson.setServer(serverID);
					if(tempPerson.getEmail().equals("_@_._"))
					{
						ArrayList<Email> blank = new ArrayList<Email>();
						blank.add(new Email("", ""));
						tempPerson.setEmailAddresses(blank);
					}
					results.add(tempPerson);
				}

			}
		}

		return results;
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
		incoming = connect("QRY: " + line + line + line + line + line + line + line + line);
		
		String[] temp = incoming.split("\\$");
		
		for (String t : temp) {
			t.replace("_@_._", ""); //Replace empty e-mails with an empty string.
			results.add(t.split("\\|", -1));
		}
		
		return results;
	}
	
	/**
	 * Update an existing record of a specified person
	 * 
	 * @param subject person that is being updated
	 * @return "ERROR: Record not found" is shown if record does not exist,<br>
	 * 		   or, if successful, returns a message saying so.
	 */
	public String update(Person subject)
	{
		String result = "";
		String line = "|";
		String incoming = "";

		//incoming = connect("QRY: " + subject.getFirstName() + line + subject.getLastName() + line + line + line + line + line + line + line); 

		//if(incoming.equals(""))
		//{
		//	result = "ERROR: Record not found";
		//}
		//else
		//{
			if(subject.getEmail().equals(""))
			{
				ArrayList<Email> blank = new ArrayList<Email>();
				blank.add(new Email("", "_@_._"));
				subject.setEmailAddresses(blank);
			}
			try {
			if(subject.getFields().size() == 5)
				{
					ConnectionHandler.sendMessage(
						"UPD: " + subject.getID() + line + subject.getFirstName() + line + subject.getLastName() 
						+ line + subject.getPhoneNumber() + line + subject.getEmail() + line 
						+ subject.getFields().get(0).getValue() + line + subject.getFields().get(1).getValue() + line 
						+ subject.getFields().get(2).getValue() + line + subject.getFields().get(3).getValue() + line 
						+ subject.getFields().get(4).getValue());
					
					result = ConnectionHandler.readLine();
				}
				else
				{
					ConnectionHandler.sendMessage(
						"UPD: " + subject.getID() + line + subject.getFirstName() + line + subject.getLastName() 
						+ line + subject.getPhoneNumber() + line + subject.getEmail() + line + line + line + line + line);
					
					result = ConnectionHandler.readLine();
				}
			}
			catch (IOException e) {
				result = "ERROR: Cannot connect to the server!";
			}
		//}

		return result;
	}
	
	/**
	 * Add a new person to the database.  The information is inserted by the
	 * user and sent to the server.
	 * 
	 * @param subject person to be added
	 * @return "ERROR: Record already exists" is shown if the user is trying to
	 * add a duplicate record,<br>or it will return a message saying the
	 * user is successful
	 */
	public String add(Person subject)
	{
		String result = "";
		String line = "|";
		String incoming = "";

		if(subject.getEmail().equals(""))
		{
			ArrayList<Email> blank = new ArrayList<Email>();
			blank.add(new Email("", "_@_._"));
			subject.setEmailAddresses(blank);
		}
		
		try {
			ConnectionHandler.sendMessage("QRY: " + subject.getFirstName() + line + subject.getLastName()
				+ line + subject.getPhoneNumber() + line + subject.getEmail() + line + line + line + line + line);
			
			incoming = ConnectionHandler.readLine().trim();
		}
		catch (IOException e) {
			return "ERROR: Cannot connect to the server!";
		}
		
		if(!incoming.equals(""))
		{
			result = "ERROR: Record already exists";
		}
		else
		{
			if(subject.getFields().size() == 5)
			{
				try {
					ConnectionHandler.sendMessage("ADD: " + subject.getFirstName() + line + subject.getLastName() 
							+ line + subject.getPhoneNumber() + line + subject.getEmail() + line 
							+ subject.getFields().get(0).getValue() + line + subject.getFields().get(1).getValue() + line 
							+ subject.getFields().get(2).getValue() + line + subject.getFields().get(3).getValue()+ line 
							+ subject.getFields().get(4).getValue());
					
					result = ConnectionHandler.readLine();
				} catch (IOException e) {
					result = "ERROR: Can't connect to the server!";
					e.printStackTrace();
				}
			}
			else {
				try {
					ConnectionHandler.sendMessage("ADD: " + subject.getFirstName() + line + subject.getLastName() 
						+ line + subject.getPhoneNumber() + line + subject.getEmail() 
						+ line + line + line + line + line);
				
					result = ConnectionHandler.readLine();
				}
				catch (IOException e) {
					result = "ERROR: Connection was lost!";
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);

		return result;
	}
	
	/**
	 * Remove a person from the database
	 * 
	 * @param subject person to be removed
	 * @return "ERROR: Record does not exist" is shown if the user tries
	 * to delete a record that does not exist,<br>or it will return a message saying the
	 * user is successful
	 */
	public String delete(Person subject)
	{
		String result = "";
		String line = "|";
		String incoming = "";

		incoming = connect("QRY: " + subject.getFirstName() + line + subject.getLastName() 
				+ line + line + line + line + line + line + line); 

		if(incoming.equals(""))
		{
			result = "ERROR: Record does not exist";
		}
		else
		{
			if(subject.getEmail().equals(""))
			{
				ArrayList<Email> blank = new ArrayList<Email>();
				blank.add(new Email("", "_@_._"));
				subject.setEmailAddresses(blank);
			}
			
			try {
				ConnectionHandler.sendMessage("DEL: " + subject.getFirstName() + line + subject.getLastName() 
					+ line + subject.getPhoneNumber() + line + subject.getEmail() + line + 
					subject.getCampus() + line + line + line + line);
				
				result = ConnectionHandler.readLine();
			}
			catch (IOException e) {
				result = "ERROR: Cannot connect to server!";
			}
		}
		return result;
	}
	
	/**
	 * Attempts to access the server.  If it can't, an IOException is thrown
	 * and shows a failure message to the user.
	 * 
	 * @param message message to be sent
	 * @return output from the server
	 */
	public String connect(String message)
	{
		String output = "";
		String incoming = "";
		
		Socket socket = null;
		try 
		{
			socket = ConnectionHandler.createTemporarySocket();
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println(message);
			
			StringBuffer buf = new StringBuffer();
			while((incoming = in.readLine()) != null)
			{
				buf.append(incoming);
				buf.append("$");
			}
			
			output = buf.toString();
			
			ConnectionHandler.refreshTimeout();
		} 
		catch (IOException e) {
			output = "ERROR: " + e.getMessage();
		}
		finally {
			try {
				if (socket != null)
					socket.close();
			}
			catch (IOException e) {}
		}
		
		return output;
	}
	
	/**
	 * Converts a String containing all of the information about a person
	 * to a type Person.  <br><br>Data fields are separated by "|" characters, so
	 * the method splits each field into it's own String array, and the information
	 * is converted to type Person piece-by-piece.
	 * 
	 * @param pInfo data containing info about person
	 * @return converted information of type Person
	 */
	public static Person convertString2Person(String pInfo)
	{
		Person tempPerson = new Person();
		String[] info;

		info = pInfo.split("\\|");
		
		if(info.length <= 3)
		{
			return tempPerson;
		}
		
		
			tempPerson.setFirstName(info[0]);
			tempPerson.setLastName(info[1]);
			tempPerson.addPhone("Phone", info[2]);
			tempPerson.addEmail("Email", info[3]);
	
		
		if(info.length > 4)
		{
			tempPerson.addField("Campus", info[4]);
		}
		else
		{
			tempPerson.addField("Campus", "");
		}
		if(info.length > 5)
		{
		tempPerson.addField("Role", info[5]);
		}
		else
		{
			tempPerson.addField("Role", "");
		}
		if(info.length > 6)
		{
			tempPerson.addField("Department", info[6]);
		}
		else
		{
			tempPerson.addField("Department", "");
		}
		if(info.length > 7)
		{
			tempPerson.addField("Fax", info[7]);
		}
		else
		{
			tempPerson.addField("Fax", "");
		}
		if(info.length > 8)
		{
			tempPerson.addField("Office", info[8]);
		}
		else
		{
			tempPerson.addField("Office", "");
		}
		
		return tempPerson;
	}
	
	public Address getAddress() {
		return new Address(server, port, 0);
	}
}

