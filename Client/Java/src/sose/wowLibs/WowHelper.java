package src.sose.wowLibs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import src.sose.wowBL.Person;
import src.sose.wowDA.Address;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.HackDA;
import src.sose.wowDA.Server;

/**
 * Contains several static helper methods 
 * @author Boomer
 *
 */
public class WowHelper {
	private static boolean foundMain = false;
	private static Address mainServer = null;

	/**
	 * Reads a list of servers from a configuration file and builds a DataAccess Object for each of the servers listed
	 * 
	 * @return an ArrayList of DataAccess Objects based on the data in the input file
	 */
	public static ArrayList<DataAccess> getServerList()
	{
		ArrayList<DataAccess> serverList = new ArrayList<DataAccess>();
		ArrayList<Server> servers = new ArrayList<Server>();
		String line = "";
		String notFound = "";
		String[] addr;
		Boolean found = new Boolean(false); 

		try
		{
			String configPath = (new File(".").getAbsolutePath());
			File config = new File(configPath + "/wow.conf");

			FileReader fileReader = new FileReader(config);
			BufferedReader reader = new BufferedReader(fileReader);
			
			while ((line = reader.readLine()) != null)
			{
				addr = line.split(":");

				if(addr[0].startsWith("#"))
				{
					found = true;
				}
				
				for(Server temp : servers)
				{
					if(found == false && temp.GetName().equals(addr[2]))
					{
						temp.AddAddress(addr[0], Integer.parseInt(addr[1]), Integer.parseInt(addr[3]));
						found = true;
					}	
				}
				
				if(!found)
				{
					servers.add(new Server(addr[2], addr[0], Integer.parseInt(addr[1]), Integer.parseInt(addr[3])));
				}

				found = false;
			}

			for(Server temp : servers)
			{
				int i;
				found = false;
				
				for(i = 0; i < temp.GetAddresses().size(); i++)
				{
					Socket s = new Socket();
					
					try {
						s.connect(new InetSocketAddress(temp.GetAddresses().get(i).GetAddress(), 
								temp.GetAddresses().get(i).getPort()), 800);
						s.close();
						found = true;
						System.out.println("Found " + temp.GetAddresses().get(i).GetAddress() + ":" +
								temp.GetAddresses().get(i).getPort());
						serverList.add(new HackDA(temp.GetAddresses().get(i).GetAddress(), 
								temp.GetAddresses().get(i).getPort(), temp.GetName()));
					} catch (UnknownHostException e) {
						System.out.println("No DNS entry found for " + temp.GetAddresses().get(i).GetAddress());
					} catch (IOException e) {
						System.out.println("Unable to establish a connection with " + temp.GetAddresses().get(i).GetAddress());
					}
				}

				/*if(found == true)
				{
					System.out.println("Found " + temp.GetAddresses().get(i).GetAddress() + ":" +
							temp.GetAddresses().get(i).getPort());
					serverList.add(new HackDA(temp.GetAddresses().get(i).GetAddress(), 
							temp.GetAddresses().get(i).getPort(), temp.GetName()));
				}
				else
				{
					if(notFound == "")
					{
						notFound = temp.GetName();
					}
					else
					{
						notFound = notFound + "\n" + temp.GetName();
					}
				}*/
			}

			reader.close();
			fileReader.close();
		}
		catch(Exception ex)
		{
			System.out.println("File I/O Error");
			ex.printStackTrace();
		}
		
		if(notFound != "")
		{
			NoServerPopup nsp = new NoServerPopup(notFound);
			nsp.setLocationRelativeTo(null);
			nsp.setVisible(true);
			nsp.setAlwaysOnTop(true);
		}

		return serverList;
	}
	
	/**
	 * Removes any custom formatting from a JTable, ie. colors, special fonts, etc. 
	 * @param table JTable to be cleaned
	 * @return the cleaned JTable
	 */
	public static JTable getPrintableTable(JTable table)
	{
		JTable newTable = table;
		GUIBase gu = new GUIBase();
	
		newTable.setDefaultRenderer(Object.class, gu.new BlankCellRenderer());
		return newTable;
	}
	
	/**
	 * Returns a formatted ArrayList of String arrays that are formatted for use in printing
	 * 
	 * @param da A collection of DataAccess Objects to pull Person objects from
	 * @return ArrayList of String arrays
	 */
	public static ArrayList<String[]> getAllPeople(ArrayList<DataAccess> da)
	{
		ArrayList<Person> people = new ArrayList<Person>();
		
		for(DataAccess dataA : da)
		{
			people.addAll(dataA.query());
		}
		
		return formatPeopleForPrinting(people);
	}
	
	
	/**
	 * Takes a collection of fully qualified people and returns a stripped down version used for printing reports.
	 *  
	 * @param people collection of Person objects to print
	 * @return collection of String arrays
	 */
	public static ArrayList<String[]> formatPeopleForPrinting(ArrayList<Person> people)
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		if(people != null && people.size() != 0)
		{			
			for(Person searchResult : people) 
			{
				if(!(searchResult.getFields().size() == 5))
				{
					String[] data = {" " + searchResult.getLastName()+ ", " + searchResult.getFirstName(), 
							searchResult.getCampus(), " " + searchResult.getPhoneNumber(), " " 
							+ searchResult.getEmail(), searchResult.getServer(), searchResult.getCampus()};  
					if(!data[0].equals(" , "))
					{
						result.add(data);
					}
				}
				else
				{	
					String[] data = {" " + searchResult.getLastName()+ ", " + searchResult.getFirstName(), 
							searchResult.getFields().get(4).getValue(), " " + searchResult.getPhoneNumber(), 
							" " + searchResult.getEmail(), searchResult.getServer(), searchResult.getCampus()};  
					if(!data[0].equals(" , "))
					{
						result.add(data);
					}
				}		
			}
		}
		GUIBase.tableData = result;
		
		for(String[] s : result)
		{
			String temp = s[5];
			s[5] = s[4];
			s[4] = temp;
			
			if(s[4].equals("Williston"))
			{
				s[4] = " WL";
			}
			else if(s[4].equals("Randolph"))
			{
				s[4] = " RA";
			}
			else if(s[4].equals("Brattleboro"))
			{
				s[4] = " BR";
			}
			else if(s[4].equals("Windsor"))
			{
				s[4] = " WD";
			}
			else if(s[4].equals("Bennington"))
			{
				s[4] = " BE";
			}
			else
			{
				s[4] = " OT";
			}
		}
		return result;
	}
	
	/**
	 * Ask the server to send back a list of departments.
	 * @param has_none Set to true to add "None" as the first element of the list.
	 * @return Array list of String objects on success, empty array list on failure.
	 */
	public static String[] retrieveAllDepartments(boolean has_none) {
		final Address MAIN_SERVER = getMainServerAddress();
		
		ArrayList<String> temp = new ArrayList<String>();
		String[] result = null;
		
		boolean success = false;
		String errMsg = "";
		Socket socket = null;
		PrintStream out = null;
		BufferedReader in = null;
		try {
			socket = new Socket(MAIN_SERVER.GetAddress(), MAIN_SERVER.getPort());
			out = new PrintStream(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println("DEPTLIST");
			
			String dept = "";
			if (has_none)
				temp.add("None");
			while ((dept = in.readLine()) != null) {
				temp.add(dept);
			}
			
			success = true;
		}
		catch (IOException e) {
			errMsg = e.getMessage();
		}
		finally {
			try {
				socket.close();
			}
			catch (IOException e) {}
		}
		
		if (!success) {
			JOptionPane.showMessageDialog(null, "Unable to retrieve the department list\n(" + errMsg + ")");
			result = new String[1];
			result[0] = "";
		}
		else {
			result = new String[temp.size()];
			for (int c = 0; c < temp.size(); c++) {
				result[c] = temp.get(c);
			}
		}
		
		java.util.Arrays.sort(result);
		
		return result;
	}
	
	public static Address getMainServerAddress() {
		if (foundMain) {
			return mainServer;
		}
		
		String line = "";
		//final String notFound = "No main server is currently available -- updates cannot be made.";
		String[] addr = null;
		FileReader fileReader = null; 
		BufferedReader reader = null;
		
		try {
			String configPath = (new File(".").getAbsolutePath());
			File config = new File(configPath + "/wow.conf");

			fileReader = new FileReader(config);
			reader = new BufferedReader(fileReader);
			
			while ((line = reader.readLine()) != null)
			{
				if (line.equals("")) {
					continue;
				}
				
				addr = line.split(":");
				if(addr[0].startsWith("#"))
				{
					continue;
				}
				
				Socket s = null;
				try {
					s = new Socket();
					s.connect(new InetSocketAddress(addr[0], Integer.parseInt(addr[1])), 500);
					s.getOutputStream().write("MAIN\n".getBytes());
					byte[] buf = new byte[1];
					s.getInputStream().read(buf, 0, 1);
					
					if ((char)buf[0] == '1') {
						mainServer = new Address(addr[0], Integer.parseInt(addr[1]), Integer.parseInt(addr[3]));
						System.out.println("Main server: " + addr[0] + ":" + addr[1]);
						foundMain = true;
						
						return mainServer;
					}
					
				} catch (UnknownHostException e) {
					System.out.println("No DNS entry found for " + addr[0] + ":" + addr[1]);
				} catch (IOException e) {
					System.out.println("Unable to establish a connection with " + addr[0] + ":" + addr[1]);
				}
				finally {
					try {
						s.close();
					}
					catch (Exception e) {}
				}
			}

			reader.close();
			fileReader.close();
		}
		catch(Exception ex)
		{
			System.out.println("File I/O Error");
			ex.printStackTrace();
		}
		finally {
			try {
				fileReader.close();
				reader.close();
			}
			catch (Exception e) {}
		}
		
		/*
		 * Let someone else notify.
		 *
		 * NoServerPopup nsp = new NoServerPopup(notFound);
		 * nsp.setLocationRelativeTo(null);
		 * nsp.setVisible(true);
		 * nsp.setAlwaysOnTop(true);
		 */
		
		return null;
	}
}
