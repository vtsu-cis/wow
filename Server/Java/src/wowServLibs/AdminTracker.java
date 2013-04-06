package wowServLibs;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mailClient.HAITClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import wowServ.server.Server;
import wowServ.wowBL.Department;
import wowServ.wowBL.Field;
import wowServ.wowBL.Record;

/**
 * The administrator tracker handles the static tracking and handling of
 * administrators.
 */
public class AdminTracker {
	public static final String DEFAULT_ADMIN_FILE = "profiles.xml";

	private Socket clientSocket;
	private PrintStream out;
	private String accountName;
	private XMLDatabase db;
	private boolean done = false;

	/**
	 * Create an AdminTracker object.
	 * 
	 * @param _clientSocket
	 *            Socket to communicate with.
	 * @param _accountName
	 *            Name of the administrator.
	 * @param _db
	 *            Database object.
	 * @throws IOException
	 *             If a PrintStream cannot be created.
	 */
	public AdminTracker(Socket _clientSocket, String _accountName,
			XMLDatabase _db) throws IOException {
		clientSocket = _clientSocket;
		accountName = _accountName;
		db = _db;

		out = new PrintStream(_clientSocket.getOutputStream(), true);
	}

	/**
	 * Interpret the incoming message from the administrator.
	 * 
	 * @param message
	 *            Message to read.
	 */
	public void interpretMessage(String message) {
		boolean changeMade = false;
		System.out.println("Admin message: " + message);

		// Check to see if the administrator is logging out.
		if (message.equalsIgnoreCase("LOGOUT")) {
			System.out.println(accountName + " logged out.");
			Log.write(accountName + " has logged out.");
		} else if (message.startsWith("ADD")) {
			// Handle record additions to the database.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");
			} else {
				Record r = XMLDatabase.stringToRecord(message);
				boolean successful = false;
				try {
					successful = db.add(r);

					if (successful) {
						out.println("OK");

						Statistics.addAddHit(clientSocket.getInetAddress());

						changeMade = true;
					} else {
						out.println("ERROR: Record already exists.");
					}
				} catch (Exception e) {
					Log.write("ERROR WITH ADD: " + e.getMessage());
					successful = false;
					out.println("ERROR: " + e.getMessage());
				}
			}
		} else if (message.startsWith("UPD")) {
			// Handle updates to an existing record.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");
			} else {
				String[] data = (message.substring(5)).split("\\|", -1);
				Record r = db.findPerson(Integer.parseInt(data[0]));
				if (r == null) {
					out.println("ERROR: Record not found.");
				} else {
					java.util.ArrayList<Field> newFields = new java.util.ArrayList<Field>();
					newFields.add(new Field("First Name", data[1]));
					newFields.add(new Field("Last Name", data[2]));
					newFields.add(new Field("Phone Number", data[3]));
					newFields.add(new Field("Email", data[4]));
					newFields.add(new Field("Campus", data[5]));
					newFields.add(new Field("Role", data[6]));
					newFields.add(new Field("Department", data[7]));
					newFields.add(new Field("Fax", data[8]));
					if (data.length > 9) {
						newFields.add(new Field("Office", data[9]));
					} else {
						newFields.add(new Field("Office", ""));
					}
					r.setFields(newFields);
					boolean successful = db.update(r);

					if (successful) {
						out.println("OK");

						Statistics.addUpdateHit(clientSocket.getInetAddress());

						changeMade = true;
					} else {
						out.println("ERROR: Record not found.");
					}
				}
			}
		} else if (message.startsWith("DEL")) {
			// Delete a record.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");
			} else {
				String[] data = (message.substring(5)).split("\\|", -1);
				Record r = db.findPerson(data[0], data[1], data[2], data[3]);
				boolean successful = db.delete(r.getID());

				if (successful) {
					out.println("OK");

					Statistics.addDeleteHit(clientSocket.getInetAddress());

					changeMade = true;
				} else {
					out.println("ERROR: Record doesn't exist.");
				}
			}
		} else if (message.startsWith("DEPTADD: ")) {
			// Add a department.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");

				return;
			}

			String deptToAdd = message.substring(9);
			if (db.add(new Department(deptToAdd,
					new java.util.ArrayList<Field>()))) {
				out.println(deptToAdd + " was successfully added.");

				changeMade = true;
			} else {
				out.println("ERROR: Couldn't add " + deptToAdd);
			}
		} else if (message.startsWith("DEPTDEL")) {
			// Delete a department.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");

				return;
			}

			String deptToDel = message.substring(9);
			if (db.removeDepartment(deptToDel)) {
				out.println(deptToDel + " was successfully removed.");

				changeMade = true;
			} else {
				out.println("ERROR: Department no longer exists.");
			}
		} else if (message.startsWith("DEPTUPD")) {
			// Change a department's name from one name to a different name.
			if (!Server.main) {
				out.println("ERROR: This server cannot change records.");

				return;
			}

			String deptToUpd = message.substring(9);
			String depts[] = deptToUpd.split("\\|");

			if (depts.length > 1
					&& db.updateDepartment(depts[0], depts[1],
							new java.util.ArrayList<Field>())) {
				out.println("Department successfully updated.");

				changeMade = true;
			} else {
				out
						.println("ERROR: A problem occured when trying to modify this department.");
			}
		} else if (message.startsWith("CALC")) {
			// Force stats calculation.
			try {
				Statistics.calculateStats();
				out.println(Statistics.getStats());
			} catch (IOException e) {
				out.println("ERROR: Calculation failed: " + e);
				Log.write("Stats calculation failed: " + e);
			}
		} else {
			// Unrecognized message.
			out.println("ERROR: Unrecognized message.");
		}

		if (changeMade) {
			try {
				Utility.distributeDatabase();
			} catch (IOException e) {
				Log.write("Couldn't distribute database: " + e);

				try {
					HAITClient
							.sendMessage(
									"WOW Error - Cannot distribute database.",
									"The main WOW server tried to distribute the database, but could not.\n"
											+ "The server may not be able to find the file. The server does not report\n"
											+ "this error if it cannot send to only a select few servers.\n"
											+ "It may be helpful to check the logs and logs of target servers.\n");
				} catch (MessagingException ex) {
					Log.write("Couldn't send mail: " + ex);
				}
			}
		}
	}

	/**
	 * Get whether this class is done dealing with the client yet.
	 * 
	 * @return True if the client is done.
	 */
	public synchronized boolean isDone() {
		return done;
	}

	// ! Static methods.

	/**
	 * Check the administrator file to see if the given username and password
	 * exist.
	 * 
	 * @param _user
	 *            Username to check.
	 * @param _password
	 *            Password to check.
	 * @return True if the given combination matched existing entries.
	 *         Otherwise, the method returns false.
	 */
	public static boolean isAdministrator(String _user, String _password)
			throws IOException {
		Document doc = XMLParser.load(DEFAULT_ADMIN_FILE);
		if (doc == null) {
			throw new IOException("Unable to load admin file.");
		}

		ArrayList<Administrator> adminList = null;
		final Administrator testAdmin = new Administrator(_user, _password);
		try {
			adminList = loadAdministrators(doc);

			for (final Administrator admin : adminList) {
				if (testAdmin.equals(admin)) {
					return true;
				}
			}
		} catch (XPathException e) {
			throw new IOException(e.getMessage());
		}

		return false;
	}

	/**
	 * Load a list of administrators from the given XML document.
	 * 
	 * @param doc
	 *            XML document.
	 * @return ArrayList of Administrator objects.
	 * @throws XPathException
	 *             Thrown by Java if it comes across invalid XML.
	 */
	private static ArrayList<Administrator> loadAdministrators(Document doc)
			throws XPathException {
		ArrayList<Administrator> results = new ArrayList<Administrator>();
		NodeList adminList = null;
		XPathFactory xPathFactory = null;
		XPath xPath = null;

		try {
			xPathFactory = XPathFactory.newInstance();
			xPath = xPathFactory.newXPath();

			adminList = (NodeList) xPath.evaluate("/data-group/admins/admin",
					doc, XPathConstants.NODESET);

			// Load admin list.
			for (int i = 0; i < adminList.getLength(); i++) {
				// Convert the node into an element.
				Element element = (Element) adminList.item(i);

				final String username = XMLParser.textValue(element, "name");
				final String password = XMLParser
						.textValue(element, "password");

				results.add(new Administrator(username, password));
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	/**
	 * Create an XML admin element.
	 * 
	 * @param doc
	 *            Document to append the element to.
	 * @param admin
	 *            Admin to add.
	 * @return Element created.
	 */
	public static Element createAdminElement(Document doc, Administrator admin) {
		Element el = doc.createElement("admin");

		Element nameElement = doc.createElement("name");
		Element passwordElement = doc.createElement("password");

		Text name = doc.createTextNode(admin.getName());
		nameElement.appendChild(name);

		Text password = doc.createTextNode(admin.getPassword());
		passwordElement.appendChild(password);

		el.appendChild(name);
		el.appendChild(password);

		return el;
	}

	/**
	 * The Administrator class tracks a temporary instance of a superuser.
	 */
	static class Administrator {
		private String username;
		private String password;

		/**
		 * Construct an Administrator.
		 * 
		 * @param _user
		 *            Name of the admin.
		 * @param _password
		 *            Password of the admin.
		 */
		public Administrator(String _user, String _password) {
			username = _user;
			password = _password;
		}

		/**
		 * Tell if two administrators are equal to each other.
		 * 
		 * @param other
		 *            Administrator to compare it to.
		 * @return True if the administrators are equal, otherwise false.
		 */
		public boolean equals(Administrator other) {
			return username.equalsIgnoreCase(other.getName())
					&& password.equals(other.getPassword());
		}

		/**
		 * Get the Administrator's username.
		 * 
		 * @return String containing the username.
		 */
		public String getName() {
			return username;
		}

		/**
		 * Retrieve the password of the administrator.
		 * 
		 * @return String containing the password.
		 */
		public String getPassword() {
			return password;
		}
	}
}
