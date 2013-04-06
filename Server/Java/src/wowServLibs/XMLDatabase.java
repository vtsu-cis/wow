package wowServLibs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import wowServ.server.Server;
import wowServ.wowBL.Department;
import wowServ.wowBL.Field;
import wowServ.wowBL.Record;

public class XMLDatabase {
	/**
	 * Initialize an XML file parser.
	 * 
	 * @param _file
	 *            File to load.
	 */
	public XMLDatabase(String _file) throws IOException {
		file = _file;
		doc = XMLParser.load(file);
		if (doc == null) {
			throw new IOException("Database file "
					+ Server.DEFAULT_DATABASE_FILE + " cannot be loaded.");
		}

		// Initialize all data:
		loadPeople();
	}

	/**
	 * Get a list of every record in the XML database file.
	 */
	public void loadPeople() {
		people = new Vector<Record>(1000, 0);
		departments = new Vector<Department>(200, 0);

		NodeList deptList = null;
		NodeList peopleList = null;
		XPathFactory xPathFactory = null;

		try {
			xPathFactory = XPathFactory.newInstance();
			xPath = xPathFactory.newXPath();

			deptList = (NodeList) xPath.evaluate(
					"/data-group/departments/entry", doc,
					XPathConstants.NODESET);
			peopleList = (NodeList) xPath.evaluate("/data-group/people/record",
					doc, XPathConstants.NODESET);

			if (deptList == null || peopleList == null) {
				System.err.println("ERROR: Something didn't load right!");
				return;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		// load departments.
		int deptLength = deptList.getLength();
		for (int i = 0; i < deptLength; i++) {
			departments.add(loadDepartment((Element) deptList.item(i)));
		}

		int peopleLength = peopleList.getLength();
		for (int i = 0; i < peopleLength; i++) {
			people.add(loadRecord((Element) peopleList.item(i)));
		}
	}

	/**
	 * Parses an XML document and returns all information about a record.
	 * 
	 * @return New record on successful find, <code>null</code> on failure.
	 */
	private Record loadRecord(Element _id) {
		ArrayList<Field> fields = new ArrayList<Field>(10);

		String id = XMLParser.textValue(_id, "id");

		NodeList nl = _id.getElementsByTagName("field");
		for (int c = 0; c < nl.getLength(); c++) {
			// Expected: parent=field, children=name,value
			Element e = (Element) nl.item(c);

			fields.add(new Field(e.getAttribute("name"), e.getTextContent()));
		}

		return Record.newRecord(Integer.parseInt(id), fields, _id);
	}

	/**
	 * Parses an XML document and returns every department discovered.
	 * 
	 * @param _dept
	 *            Element root to search for departments.
	 * @return Department object
	 */
	public Department loadDepartment(Element _dept) {
		ArrayList<Field> fields = new ArrayList<Field>(5);

		String dname = XMLParser.textValue(_dept, "name");

		NodeList nl = _dept.getElementsByTagName("field");

		for (int c = 0; c < nl.getLength(); c++) {
			Element e = (Element) nl.item(c);

			fields.add(new Field(e.getAttribute("name"), e.getTextContent()));
		}

		return Department.makeDepartment(dname, fields, _dept);
	}

	/**
	 * Add a record to the database.
	 * 
	 * @param _record
	 *            Record to add.
	 * @return <code>true</code> on successful add, <code>false</code> on
	 *         failure.
	 */
	public synchronized boolean add(Record _record) throws Exception {
		String firstname = _record.getField("First Name").get(0).getData();
		String lastname = _record.getField("Last Name").get(0).getData();
		if (firstname == null || lastname == null) {
			System.err.println("\"ADD\" packet failed: first name=" + firstname
					+ ", lastname=" + lastname);
			return false;
		}

		for (Record p : people) {
			if (p.getField("First Name").get(0).getData().equalsIgnoreCase(
					firstname)
					&& p.getField("Last Name").get(0).getData()
							.equalsIgnoreCase(lastname)) {
				System.err.println("ADD failed: Duplicate record exists for "
						+ firstname + " " + lastname + ".");
				return false;
			}
		}
		// Grab root element.
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		Element rootEle = null;
		try {
			rootEle = (Element) xPath.evaluate("/data-group/people", doc,
					XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return false;
		}

		_record.setID(getUnusedID());
		Element person = createRecordElement(_record);
		rootEle.appendChild(person);
		people.add(Record.newRecord(_record.getID(), _record.getFields(),
				person));
		sortByID(people);

		XMLParser.write(doc, file);

		return true;
	}

	/**
	 * Add a department to the database. The department name must be unique.
	 * 
	 * @param _dept
	 *            Department to add.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	public synchronized boolean add(Department _dept) {
		String deptName = _dept.getName();

		for (Department d : departments) {
			if (d.getName().equalsIgnoreCase(deptName)) {
				return false;
			}
		}

		// Grab root element.
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		Element rootEle = null;
		try {
			rootEle = (Element) xPath.evaluate("/data-group/departments", doc,
					XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		Element dept = createDepartmentElement(_dept);
		rootEle.appendChild(dept);
		departments.add(Department.makeDepartment(_dept.getName(), _dept
				.getFields(), dept));

		XMLParser.write(doc, file);

		return true;
	}

	/**
	 * Remove a person based on their ID.
	 * 
	 * @param id
	 *            Unique number given to each record.
	 * @return <code>true</code> on successful delete, <code>false</code> on
	 *         unsuccessful delete (i.e. record not found).
	 */
	public synchronized boolean delete(int id) {
		for (int c = 0; c < people.size(); c++) {
			if (people.get(c).getID() == id) {
				// Grab root element.
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				Element rootEle = null;
				try {
					rootEle = (Element) xPath.evaluate("/data-group/people",
							doc, XPathConstants.NODE);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}

				Element person = people.get(c).getElement();
				rootEle.removeChild(person);
				people.remove(c);
				sortByID(people);

				XMLParser.write(doc, file);
				return true;
			}
		}

		return false;
	}

	/**
	 * Remove a department from the list.
	 * 
	 * @param name
	 *            Name of the department to remove. Must be exact.
	 * @return <code>true</code> if the department was successfully removed,
	 *         otherwise <code>false</code> (i.e. the department does not
	 *         exist).
	 */
	public synchronized boolean removeDepartment(String name) {
		for (Department d : departments) {
			if (d.getName().equalsIgnoreCase(name)) {
				// Grab root element.
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				Element rootEle = null;
				try {
					rootEle = (Element) xPath
							.evaluate("/data-group/departments", doc,
									XPathConstants.NODE);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}

				Element dept = d.getElement();
				rootEle.removeChild(dept);

				XMLParser.write(doc, file);
				departments.remove(d);
				return true;
			}
		}

		return false;
	}

	/**
	 * Queries the database, checking every record to see if any record matches
	 * a given record.
	 * 
	 * @param _line
	 *            |-delimited String.
	 */
	public ArrayList<Record> query(String _line) {
		Record _record = stringToRecord(_line);

		return query(_record);
	}

	/**
	 * Queries the database, checking every record to see if any record matches
	 * a given record.
	 * 
	 * @param _record
	 *            Record to query.
	 */
	public ArrayList<Record> query(Record _record) {
		final long startTime = System.nanoTime();
		queriesHandled++;
		ArrayList<Record> results = new ArrayList<Record>();
		boolean match = true;
		for (Record r : people) {
			match = true;

			nomatch: for (Field f : r.getFields()) {
				for (Field g : _record.getFields()) {
					if (g.getName().equalsIgnoreCase(f.getName())) {
						if (!g.getData().equals("")
								&& !f.getData().toUpperCase().contains(
										g.getData().toUpperCase())) {
							match = false;
							break nomatch;
						}
					}
				}
			}

			if (match) {
				results.add(r);
			}
		}

		final long endTime = System.nanoTime();
		final double resultTime = (endTime - startTime) / 100000000f;
		calculateAverageResponseTime(resultTime);

		return results;
	}

	/**
	 * Get ALL records from the database.
	 * 
	 * @return A compiled list of records.
	 */
	public ArrayList<Record> queryAll() {
		return new ArrayList<Record>(people);
	}

	/**
	 * Attempts to update a record by replacing it's current fields with new
	 * ones.
	 * 
	 * @param _record
	 *            Record to update.
	 * @param newFields
	 *            Fields to add to the new record.
	 * @return <code>true</code> if the record was found and updated, otherwise
	 *         <code>false</code>.
	 */
	public synchronized boolean update(Record _record) {
		final int records = people.size();
		for (int c = 0; c < records; c++) {
			if (people.get(c).getID() == _record.getID()) {
				// Match found.
				// Grab the document's root element.
				Element rootEle = null;
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				try {
					rootEle = (Element) xPath.evaluate("/data-group/people", doc,
							XPathConstants.NODE);
				} catch (XPathExpressionException ex) {
					ex.printStackTrace();
					return false;
				}
				
				// Remove the old record.
				rootEle.removeChild(people.get(c).getElement());
				
				Element e = createRecordElement(_record);
				Record record = Record.newRecord(_record.getID(), _record.getFields(), e);
				
				people.set(c, record);
				
				// Add the updated record.
				rootEle.appendChild(e);
				
				XMLParser.write(doc, file);

				return true; // update successful.
			}
		}

		return false; // If it can reach this point, it's false.
	}

	/**
	 * Update a department. Specifically, change it's name (if applicable) and
	 * modify it's fields.
	 * 
	 * @param oldName
	 *            What the department is currently called.
	 * @param newName
	 *            What the department will be called.
	 * @param newFields
	 *            The new fields to set.
	 * @return <code>true</code> on successful update, <code>false</code> if the
	 *         department wasn't found.
	 */
	public synchronized boolean updateDepartment(String oldName,
			String newName, ArrayList<Field> newFields) {
		for (int c = 0; c < departments.size(); c++) {
			if (departments.get(c).getName().equalsIgnoreCase(oldName)) {
				departments.get(c).setFields(newFields);
				Element e = departments.get(c).getElement();
				departments.set(c, Department.makeDepartment(newName,
						newFields, e));
				XMLParser.write(doc, file);
				return true; // Updated the department successfully.
			}
		}

		return false; // If it can reach this point, it's false.
	}

	/**
	 * Return the first occurrence of a number that isn't in use.
	 * 
	 * @return
	 */
	public int getUnusedID() {
		int id = 1;
		for (; id < Integer.MAX_VALUE; id++) {
			boolean found = false;
			for (Record r : people) {
				if (r.getID() == id) {
					found = true;
					break;
				}
			}

			if (!found) {
				break;
			}
		}

		return id;
	}

	/**
	 * Sort a list of records by their ID.
	 * 
	 * @param records
	 *            Record list to organize.
	 */
	public static void sortByID(Vector<Record> records) {
		boolean done = false;

		while (!done) {
			done = true;
			for (int c = 0; c < records.size() - 1; c++) {
				if (records.get(c).getID() > records.get(c + 1).getID()) {
					Record temp = records.get(c);
					records.set(c, records.get(c + 1));
					records.set(c + 1, temp);
					done = false;
				}
			}
		}
	}

	/**
	 * Creates a new element that stores the information in the specified person
	 * 
	 * @param subject
	 *            person to be stored
	 * @return newly created element
	 */
	private Element createRecordElement(Record _record) {
		Element el = doc.createElement("record");

		Element idElement = doc.createElement("id");
		Text id = doc.createTextNode(new Integer(_record.getID()).toString());
		idElement.appendChild(id);

		el.appendChild(idElement);

		for (Field fi : _record.getFields()) {
			Element fieldEle = doc.createElement("field");
			fieldEle.setAttribute("name", fi.getName());
			Text fieldValText = doc.createTextNode(fi.getData());
			fieldEle.appendChild(fieldValText);

			el.appendChild(fieldEle);
		}

		return el;
	}

	/**
	 * Create a department entry to append to the list of departments.
	 * 
	 * @param _dept
	 *            Department to convert to an element.
	 * @return The department as an element, containing the name and all fields.
	 */
	private Element createDepartmentElement(Department _dept) {
		Element el = doc.createElement("entry");

		Element idElement = doc.createElement("name");
		Text id = doc.createTextNode(_dept.getName());
		idElement.appendChild(id);
		el.appendChild(idElement);

		for (Field fi : _dept.getFields()) {
			Element fieldEle = doc.createElement("field");
			fieldEle.setAttribute("name", fi.getName());
			Text fieldValText = doc.createTextNode(fi.getData());
			fieldEle.appendChild(fieldValText);

			el.appendChild(fieldEle);
		}

		return el;
	}

	/**
	 * Parses a |-delimited string and converts it to a Record.
	 * 
	 * @param rawString
	 *            entry to parse.
	 * @return converted record.
	 */
	public static Record stringToRecord(String rawString) {
		String line = null;
		line = rawString.replace("QRY: ", "").replace("ADD: ", "").replace(
				"UPD: ", "").replace("DEL: ", "").replace("AQRY: ", "");

		String[] fields = line.split("\\|", -1);
		ArrayList<Field> newFields = new ArrayList<Field>(9);
		Record temp = null;

		newFields.add(new Field("First Name", fields[0]));
		newFields.add(new Field("Last Name", fields[1]));
		newFields.add(new Field("Phone Number", fields[2]));
		newFields.add(new Field("Email", fields[3]));
		newFields.add(new Field("Campus", fields[4]));
		newFields.add(new Field("Role", fields[5]));
		newFields.add(new Field("Department", fields[6]));
		newFields.add(new Field("Fax", fields[7]));
		if (fields.length == 9) {
			newFields.add(new Field("Office", fields[8]));
		} else {
			newFields.add(new Field("Office", ""));
		}

		temp = new Record(0, newFields);

		return temp;
	}

	/**
	 * Parses a |-delimited String and converts it to a record. The ID must be a
	 * unique identifying number.
	 * 
	 * @param rawString
	 *            String to parse, i.e. incoming packets.
	 * @return A record with all of the fields given from the String.
	 */
	public static Record stringToRecord(String rawString, int ID) {
		String line = null;
		line = rawString.replace("QRY: ", "").replace("ADD: ", "").replace(
				"UPD: ", "").replace("DEL: ", "").replace("AQRY: ", "");

		String[] fields = line.split("\\|", -1);
		ArrayList<Field> newFields = new ArrayList<Field>(10);
		Record temp = null;

		newFields.add(new Field("First Name", fields[0]));
		newFields.add(new Field("Last Name", fields[1]));
		newFields.add(new Field("Phone Number", fields[2]));
		newFields.add(new Field("Email", fields[3]));
		newFields.add(new Field("Campus", fields[4]));
		newFields.add(new Field("Role", fields[5]));
		newFields.add(new Field("Department", fields[6]));
		newFields.add(new Field("Fax", fields[7]));
		if (fields.length == 9) {
			newFields.add(new Field("Office", fields[8]));
		} else {
			newFields.add(new Field("Office", ""));
		}

		temp = new Record(ID, newFields);

		return temp;
	}

	/**
	 * Converts a database entry into a |-delimited String.
	 * 
	 * @param r
	 *            Record to format.
	 * @return |-delimited String.
	 */
	public static String formatRecordForPrint(Record r) {
		String line = r.getFirstName() + "|" + r.getLastName() + "|"
				+ r.getData("Phone Number").get(0) + "|"
				+ r.getData("Email").get(0) + "|" + r.getData("Campus").get(0)
				+ "|" + r.getData("Role").get(0) + "|"
				+ r.getData("Department").get(0) + "|"
				+ r.getData("Fax").get(0) + "|" + r.getData("Office").get(0);

		return line;
	}

	/**
	 * Converts a database entry into a |-delimited String. This also includes
	 * the person's ID.
	 * 
	 * @param r
	 *            Record to format.
	 * @return |-delimited String.
	 */
	public static String formatRecordWithIDForPrint(Record r) {
		String line = Integer.toString(r.getID()) + "|" + r.getFirstName()
				+ "|" + r.getLastName() + "|"
				+ r.getData("Phone Number").get(0) + "|"
				+ r.getData("Email").get(0) + "|" + r.getData("Campus").get(0)
				+ "|" + r.getData("Role").get(0) + "|"
				+ r.getData("Department").get(0) + "|"
				+ r.getData("Fax").get(0) + "|" + r.getData("Office").get(0);

		return line;
	}

	/**
	 * Obtain a person based on their ID. Returns null on failure.
	 * 
	 * @param ID
	 *            Unique surrogate key of the record.
	 * @return A record object containing a person or null if it can't be found.
	 */
	public Record findPerson(int ID) {
		// Loop through every record and search for the ID.
		synchronized (this) {
			for (Record r : people) {
				if (r.getID() == ID) {
					return r;
				}
			}
		}

		// Failed.
		return null;
	}

	/**
	 * Tries to find a person by information that is generally considered
	 * unique.
	 */
	public Record findPerson(String firstName, String lastName,
			String phoneNumber, String email) {
		Record record = null;
		for (Record r : people) {
			if (r.getFirstName().equalsIgnoreCase(firstName)
					&& r.getLastName().equalsIgnoreCase(lastName)) {
				System.out.println("Ok for " + r.getFirstName() + " "
						+ r.getLastName());
				boolean matchesEmail = false;
				ArrayList<Field> emails = r.getField("Email");

				for (int c = 0; c < emails.size(); c++) {
					if (emails.get(c).getData().equalsIgnoreCase(email)) {
						matchesEmail = true;
						break;
					}
				}

				boolean matchesPhoneNumber = false;
				ArrayList<Field> phoneNumbers = r.getField("Phone Number");

				for (int c = 0; c < phoneNumbers.size(); c++) {
					if (phoneNumbers.get(c).getData().equalsIgnoreCase(
							phoneNumber)) {
						matchesPhoneNumber = true;
						break;
					}
				}

				if (matchesEmail && matchesPhoneNumber) {
					record = r;
					break;
				}
			}
		}

		return record;
	}

	/**
	 * Verify that a user is an administrator within the database.
	 * 
	 * @param username
	 *            Username of the potential administrator.
	 * @param password
	 *            Unhashed password to check.
	 * @return True if it is an admin, false if not.
	 */
	public boolean verifyAdministrator(String username, String password)
			throws IOException, NoSuchAlgorithmException {

		return AdminTracker.isAdministrator(username, password);
	}

	/**
	 * Simple get method for the XML document.
	 */
	public Document getDocument() {
		return doc;
	}

	/**
	 * Returns the path of the XML document.
	 */
	public String getPath() {
		return file;
	}

	/**
	 * Return a list of everyone in the XML database.
	 */
	public Vector<Record> getPeople() {
		return people;
	}

	/**
	 * Get a list of every department in the XML database.
	 */
	public Vector<Department> getDepartments() {
		return departments;
	}

	/**
	 * Get the average response time (in seconds) for a regular query.
	 * 
	 * @return Average response time (in seconds) as a string.
	 */
	public String getAverageQueryResponseTime() {
		return BigDecimal.valueOf(avgResponseTime).toString();
	}

	/**
	 * Method for calculating how many seconds it takes (on average) to complete
	 * a query.
	 * 
	 * @param latestResponseTime
	 *            Number of seconds it takes for the latest query.
	 */
	private void calculateAverageResponseTime(double latestResponseTime) {
		// (oldAvg * n) + (latestResponseTime / (n + 1))
		avgResponseTime = ((avgResponseTime * queriesHandled) + Math
				.abs(latestResponseTime))
				/ (queriesHandled + 1);

	}

	/**
	 * Synchronized method for reading the entire XML database file into memory.
	 * 
	 * @return File data as a string.
	 * @throws IOException
	 *             Thrown in the event that the XML database cannot be read for
	 *             some reason.
	 */
	public static synchronized String getFileContents() throws IOException {
		FileReader in = new FileReader(Server.DEFAULT_DATABASE_FILE);
		BufferedReader reader = new BufferedReader(in);

		String line = null;
		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
			buffer.append('\n');
		}

		reader.close();

		return buffer.toString();
	}

	/**
	 * Synchronized method for writing new data to the XML database. Completely
	 * overwrites the existing file with the given data.
	 * 
	 * @param data
	 *            Data to overwrite the file with.
	 * @throws IOException
	 *             Thrown if the XML database cannot be written to for some
	 *             reason.
	 */
	public static synchronized void writeToFile(String data) throws IOException {
		FileWriter out = new FileWriter(
				wowServ.server.Server.DEFAULT_DATABASE_FILE, false);

		out.write(data);
		out.flush();

		out.close();
	}

	private Document doc;
	private String file;
	private Vector<Record> people;
	private Vector<Department> departments;
	private XPath xPath;
	private double avgResponseTime = 0;
	private int queriesHandled = 0;
}
