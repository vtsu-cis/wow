package src.sose.wowClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import src.sose.wowBL.Person;
import src.sose.wowDA.DataAccess;
import src.sose.wowLibs.WowHelper;

/**
 * This is the main entry point for the command line version of WoW
 * 
 * @author - Nick Guertin and Boomer Ransom
 */

public class CLI {
	
	private static int cmdCount;
	private static Person cmdPerson = new Person();;
	private static ArrayList<Person> people = new ArrayList<Person>();
	private static InputStreamReader stdin =
		new InputStreamReader(System.in);
	private static ArrayList<DataAccess> mda = new ArrayList<DataAccess>();

	public static void main(String[] args) 
	{	    
		mda = WowHelper.getServerList();
		
		cmdCount = args.length;
	
		//direct program progression based on user input switches or keywords
		if(cmdCount == 0)
		{
			System.out.println("No arguments found. \nUsage: " + "-s firstname lastname" +
					" phone email\n");
		}
		else if (cmdCount > 5)
		{
			System.out.println("Too many arguments. \nUsage: " + args[0] + " firstname lastname" +
					" phone email\n");
		}
		else if (args[0].toLowerCase().equals("--help")|| args[0].toLowerCase().equals("/?"))
		{
			printHelp();
		}
		else if(args[0].toLowerCase().equals("search") || args[0].toLowerCase().equals("-s"))
		{
			searchName(args);
		}
		else if(args[0].toLowerCase().equals("add") || args[0].toLowerCase().equals("-a"))
		{
			add(args, mda);
		}
		else if(args[0].toLowerCase().equals("delete") || args[0].toLowerCase().equals("-d"))
		{
			delete(args, mda);
		}
		else if(args[0].toLowerCase().equals("update") || args[0].toLowerCase().equals("-da") || args[0].toLowerCase().equals("-u"))
		{
			update(args, mda);
		}
		else
		{
			System.out.println("\nERROR: switch \""+args[0]+"\" is not recognized. \nPlease try again.\n");
		}
		
	}
	
	/**
	 * This method will parse the cmd line input and will either search 
	 *  the DB using the Person.Query method and output the results, or will give back 
	 *  an error message. 
	 *                  
	 * @param cmdLineInput input imported directly from the command line
	 * @return - 0 if valid, 1 if errors existed in setting name
	 */
		public static int searchName(String[] cmdLineInput)
		{
			String srchName = "";
			int setFlag = 1;
			
			if(cmdLineInput.length < 2)
			{
				System.out.println("\nPlease enter a lastname to search: ");
				srchName = getUserInput(stdin);
			}
			else if(cmdLineInput.length > 3)
			{
				System.out.println("\nToo Many arguments for search.\nOnly searching by " +
						"firstname lastname supported.\n");
				return 1;
			}
			else
			{
				srchName = cmdLineInput[1];
			}
			
			if(cmdLineInput.length < 3)
			{
				if(cmdLineInput[1].length()!=1)
				{
					setFlag = cmdPerson.setLastName(srchName);
				}
				else
				{
					System.out.println("Please search for last names longer than one letter or add a first name.");
					return 1;
				}
				
				if(setFlag == 0)
				{
					people = cmdPerson.query(mda);

					if(people.isEmpty())
					{
						System.out.println("\nNo entries found");
					}
					else
					{
						printPeople(people);
					}

				}
				else
				{
					System.out.println("Invalid Entry. Goodbye.");
				}
			}
			
			if(cmdLineInput.length == 3)
			{
				setFlag = cmdPerson.setLastName(cmdLineInput[1]);
				if(setFlag == 0){setFlag = cmdPerson.setFirstName(cmdLineInput[2]);}
	 
				if(setFlag == 0)
				{
					people = cmdPerson.query(mda);
				
					if(people.isEmpty())
					{
						System.out.println("\nNo entries found");
					}
					else
					{
						printPeople(people);
					}
					
				}
				else
				{
					System.out.println("Invalid Entry. Goodbye.");
				}
			}
			
			return setFlag;
		}
		
		/**
		 * This method will attempt to add a person to the DB
		 *                  
		 * @param cmdLineInput input imported directly from the command line
		 * @param mda collection of servers to search
		 * 
		 * @return 0 if valid, 1 if errors exist
		 */
		public static int add(String[] cmdLineInput, ArrayList<DataAccess> mda)
		{
			String[] info = new String[5];
			
			if(cmdLineInput.length == 1)
			{
				System.out.println("\nPlease enter a first name   : ");
				info[1] = getUserInput(stdin);
				System.out.println("\nPlease enter a last name    : ");
				info[2] = getUserInput(stdin);
				System.out.println("\nPlease enter a phone number : ");
				info[3] = getUserInput(stdin);
				System.out.println("\nPlease enter a email        : ");
				info[4] = getUserInput(stdin);
			}
			else if(cmdLineInput.length > 5)
			{
				System.out.println("\nToo Many arguments. Please try again.");
				return 1;
			}
			else if(cmdLineInput.length > 1 && cmdLineInput.length < 5)
			{
				System.out.println("\nNot enough arguments.\nUsage: " +
						"-a firstname lastname phone email\n");
				return 1;
			}
			else
			{
				info = cmdLineInput;
			}
			
			if(cmdPerson.setFirstName(info[1]) == 1)
			{
				System.out.println("Invalid First Name.");
				return 1;
			}
			
			if(cmdPerson.setLastName(info[2]) == 1)
			{
				System.out.println("Invalid Last Name.");
				return 1;
			}
		
			if(cmdPerson.addPhone(info[3]) == 1)
			{
				System.out.println("Invalid Phone Number.");
				return 1;
			}
			
			if(cmdPerson.addEmail(info[4]) == 1)
			{
				System.out.println("Invalid Email Address.");
				return 1;
			}
			
			System.out.println(cmdPerson.add(mda));
			
			return 0;
		}
		
		/**
		 * This method will attempt to delete a person from the DB
		 *                  
		 * @param cmdLineInput input imported directly from the command line
		 * @param mda collection of servers to search
		 * 
		 * @return 0 if valid, 1 if errors exist
		 */
		public static int delete(String[] cmdLineInput, ArrayList<DataAccess> mda)
		{			
			String[] info = new String[5];
			
			if(cmdLineInput.length < 3)
			{
				System.out.println("\nPlease enter a first name: ");
				info[1] = getUserInput(stdin);
				System.out.println("\nPlease enter a last name: ");
				info[2] = getUserInput(stdin);
			}
			else if(cmdLineInput.length > 5)
			{
				System.out.println("\nToo Many arguments.\nUsage: " + "-d firstname lastname\n");
				return 1;
			}
			else
			{
				info = cmdLineInput;
			}
			
			if(cmdPerson.setFirstName(info[1]) == 1)
			{
				System.out.println("Invalid First Name.");
				return 1;
			}
			
			if(cmdPerson.setLastName(info[2]) == 1)
			{
				System.out.println("Invalid Last Name.");
				return 1;
			}
			
			System.out.println(cmdPerson.delete(mda));
					
			return 0;
		}
		
		/**
		 * This method will attempt to update a person's record to the DB
		 *                  
		 * @param cmdLineInput input imported directly from the command line
		 * @param mda collection of servers to search
		 * 
		 * @return 0 if valid, 1 if errors exist
		 */
		public static int update(String[] cmdLineInput, ArrayList<DataAccess> mda)
		{
			String[] info = new String[5];
			
			if(cmdLineInput.length == 1)
			{
				System.out.println("\nPlease enter a first name   : ");
				info[1] = getUserInput(stdin);
				System.out.println("\nPlease enter a last name    : ");
				info[2] = getUserInput(stdin);
				System.out.println("\nPlease enter a phone number : ");
				info[3] = getUserInput(stdin);
				System.out.println("\nPlease enter a email        : ");
				info[4] = getUserInput(stdin);
			}
			else if(cmdLineInput.length > 5)
			{
				System.out.println("\nToo Many arguments. Please try again.");
				return 1;
			}
			else if(cmdLineInput.length > 1 && cmdLineInput.length < 5)
			{
				System.out.println("\nNot enough arguments. \nUsage:" +
						" -u firstname lastname phone email\n");
				return 1;
			}
			else 
			{
				info = cmdLineInput;
			}
			
			if(cmdPerson.setFirstName(info[1]) == 1)
			{
				System.out.println("Invalid First Name.");
				return 1;
			}
			
			if(cmdPerson.setLastName(info[2]) == 1)
			{
				System.out.println("Invalid Last Name.");
				return 1;
			}
		
			if(cmdPerson.addPhone(info[3]) == 1)
			{
				System.out.println("Invalid Phone Number.");
				return 1;
			}
			
			if(cmdPerson.addEmail(info[4]) == 1)
			{
				System.out.println("Invalid Email Address.");
				return 1;
			}

			System.out.println(cmdPerson.update(mda));
			
			return 0;
		}
		
		/** 
		 * This method will get a line of text from the command line
		 * input buffer and will return the result. This does no error checking.  
		 *                  
		 * @param stdin stream reader pointing to the command line
		 * @return whatever is in the input buffer
		 */
		public static String getUserInput(InputStreamReader stdin)
		{
			String input = "";
			
			BufferedReader console =
				new BufferedReader(stdin);
		
			try 
			{
				input = console.readLine();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			return input;
		}
		
		/**
		 * This method will print out the information of all the 
		 * Persons passed to it in a readable format.  
		 *                  
		 * @param people arraylist of people who's info to print
		 */
		static void printPeople(ArrayList<Person> people)
		{
			Collections.sort(people);
			
			System.out.println("\nLastName\tFirstName\tPhone\t\tEmail");
			for(Person printer : people)
			{
				System.out.println(printer.toString());
			}
		}
		
		/**
		 * This method will print out a help message outlining the methods 
		 * available to the user.
		 */
		static void printHelp()
		{
			String help = "\n\nUsage: wow [-s search] [-a add] [-u -da update] [-d delete]\n" +
					"\t   [first_name last_name [phone email]]\n" +
					"Options:\n" +
					"    -s search\t\tSearches a last_name first_name.\n" +
					"    -a add\t\tAdds a person to the database.\n" +
					"    -u -da update\tUpdates a person in the database.\n" +
					"    -d delete\t\tDeletes a person from the database.\n\n" +
					"  All options will prompt for input if none is specified.";
			System.out.println(help);
		}

}
