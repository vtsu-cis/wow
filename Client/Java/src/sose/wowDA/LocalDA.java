package src.sose.wowDA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import src.sose.wowBL.Email;
import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowBL.Phone;

public class LocalDA implements DataAccess{

	private String serverID = "LOC";
	private ArrayList<Person> people = new ArrayList<Person>();
	private ArrayList<Element> elements = new ArrayList<Element>();
	private Document dom;
	private String dataFile;
	
	/**
	 * Writes document as a .XML file
	 * 
	 * @param doc document to be written to XML
	 * @param filename filename of the document
	 */
	public static void writeXmlFile(Document doc, String filename) {
		try {
			Source source = new DOMSource(doc);

			File file = new File(filename);
			Result result = new StreamResult(file);

			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
    }
	
	/**
	 * Default constructor for local data access, parses the XML file "_dataFile" runs populate()
	 * 
	 * @param _dataFile XML data file to be parsed
	 */
	public LocalDA(String _dataFile)
	{
		dataFile = _dataFile;
		parseXmlFile(dataFile);
		populate();
	}
	
	/**
	 * Gets the ID of the server
	 * 
	 * @return String serverID
	 */
	public String getName()
	{
		return serverID;
	}
	
	/**
	 * Sets the server's ID
	 * 
	 * @param name name to be set
	 */
	public void setName(String name)
	{
		serverID = name;
	}
	
	/**
	 * Gets an ArrayList of type Person
	 * 
	 */
	public ArrayList<Person> getPeople()
	{
		return people;
	}
	
	/**
	 * Populates list of people and elements 
	 *
	 */
	public void populate()
	{
		XPath xp;
		NodeList nl;
		XPathFactory xpf;

		try {
			xpf = XPathFactory.newInstance();
			xp = xpf.newXPath();
			nl = (NodeList)xp.evaluate("/data-group/record", dom, XPathConstants.NODESET);

			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {

					Element el = (Element)nl.item(i);

					Person p = getPerson(el);
					p.setServer(serverID);
					people.add(p);
					elements.add(el);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the list of everyone in the database
	 * 
	 */
	public ArrayList<Person> query()
	{
		return people;
	}
	
	/**
	 * Returns the list of everyone in the database who matches the queried subject
	 * (specific field check)
	 * 
	 * @param subject field to be checked
	 */
	public ArrayList<Person> query(Field subject)
	{
		ArrayList<Person> result = new ArrayList<Person>();
		for(Person single : people) 
		{
			for (Field aField : single.getFields()) 
			{
				if (aField.equals(subject)) 
				{
					result.add(single);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of everyone in the database who matches the queried subject 
	 * (first and last name)
	 * 
	 * @param subject person to be checked
	 */
	public ArrayList<Person> query(Person subject)
	{
		ArrayList<Person> result = new ArrayList<Person>();
		for(Person single : people)
		{
			if(single.getLastName().toLowerCase().contains(subject.getLastName().toLowerCase()))
			{
				if(single.getFirstName().toLowerCase().contains(subject.getFirstName().toLowerCase()))
				{
					result.add(single);
				}
			}
		}
		return result;
	}
	
	/**
	 * Updates the information of a specified person
	 * 
	 * @param subject person to be updated
	 */
	public String update(Person subject) 
	{
		String result = "";

		for(Person single : people)
		{
			if(single.getLastName().toLowerCase().contains(subject.getLastName().toLowerCase()))
			{
				if(single.getFirstName().toLowerCase().contains(subject.getFirstName().toLowerCase()))
				{
					result = delete(subject);
					if(result == "OK")
					{
						result = add(subject);
					}

					return result;
				}
			}
		}

		return "Error: Record not found";
	}
	
	/**
	 * Adds a person to the database if they don't already exist
	 * 
	 * @param subject person to be added
	 */
	public String add(Person subject)
	{
		for(Person single : people)
		{
			if(subject.getLastName().equals(single.getLastName()) && subject.getFirstName().equals(single.getFirstName()))
			{
				return "Error: Record already exists";
			}
		}

		String result = "";

		Element rootEle = dom.getDocumentElement();

		Element recordEle = createRecordElement(subject);
		rootEle.appendChild(recordEle);
		
		writeXmlFile(dom, "wow.xml");

		people.add(subject);
		elements.add(recordEle);
		result = "OK";

		return result;
	}

	/**
	 * Removes a specified person from the database
	 * 
	 * @param subject person to be deleted
	 */
	public String delete(Person subject)
	{		
		for(Person single : people)
		{
			if(single.getLastName().toLowerCase().contains(subject.getLastName().toLowerCase()))
			{
				if(single.getFirstName().toLowerCase().contains(subject.getFirstName().toLowerCase()))
				{
					Element current = elements.get(people.indexOf(single));
					Element parent = (Element)current.getParentNode();
					elements.remove(current);
					parent.removeChild(current);

//					try
//					{
//						OutputFormat format = new OutputFormat(dom);
//						format.setIndenting(true);
//
//						XMLSerializer serializer = new XMLSerializer(
//								new FileOutputStream(new File(dataFile)), format);
//
//						serializer.serialize(dom);
//
//					} catch(IOException ie) {
//						ie.printStackTrace();
//						return "Error: problem writing to file";
//					}
					
					writeXmlFile(dom, "wow.xml");

					people.remove(single);

					return "OK";
				}
			}
		}

		return "Error: Record not found";
	}
	
	/**
	 * Parses an XML file
	 * 
	 * @param dataFile file to be parsed
	 */
	private void parseXmlFile(String dataFile){

		final String sl = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(sl);
		StreamSource ss = new StreamSource("Wow.xsd");
		/*Schema schema = null;
		try {
			schema = factory.newSchema(ss);
		} catch (SAXException e) {
			e.printStackTrace();
		}*/
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
//		Validator v = schema.newValidator();
//		try {
//			v.validate(new StreamSource("wow.xml"));
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try {			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//dom = db.parse(dataFile);
			
			//ClassLoader cl = this.getClass().getClassLoader();
			File test = new File("wow.xml");
			
			dom = db.parse(test);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			//System.exit(1);
		}catch(SAXException se) {
			se.printStackTrace();
			//System.exit(1);
		}catch(IOException ioe) {
			ioe.printStackTrace();
			//System.exit(1);
		}
	}
	
	/**
	 * Gets the information on a given person
	 * 
	 * @param person elements of the person queried
	 * @return queried information of type Person
	 */
	private Person getPerson(Element person) {
		XPath xp;
		XPathFactory xpf;
		Element el = null;
		String first = new String();
		String last = new String();
		String phoneType = new String();
		String number = new String();
		String emailType = new String();
		String address = new String();
		ArrayList<Phone> phone = new ArrayList<Phone>();
		ArrayList<Email> email = new ArrayList<Email>();
		ArrayList<Field> fields = new ArrayList<Field>();
		Integer i;

		xpf = XPathFactory.newInstance();
		xp = xpf.newXPath();	

		first = getTextValue(person, "first");
		last = getTextValue(person, "last");

		try {
			el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/phone[1]", dom, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		if(el == null)
		{
			phone.add(new Phone("", ""));
		}

		i = 2;

		while(el != null)
		{
			phoneType = getTextValue(el, "type");
			number = getTextValue(el, "number");

			if(phoneType != null)
			{
				phone.add(new Phone(phoneType, number));
			}
			else
			{
				phone.add(new Phone("", getTextValue(el, "number")));
			}

			try {
				el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/phone[" + i.toString() + "]",
						dom, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}

			i++;
		}

		try {
			el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/email[1]", dom, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		if(el == null)
		{
			email.add(new Email("", ""));
		}

		i = 2;

		while(el != null)
		{
			emailType = getTextValue(el, "type");
			address = getTextValue(el, "address");

			if(emailType != null)
			{
				email.add(new Email(emailType, address));
			}
			else
			{
				email.add(new Email("", address));
			}

			try {
				el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/email[" + i.toString() + "]",
						dom, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}

			i++;
		}

		try {
			el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/field[1]", dom, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		if(el == null)
		{
			fields = null;
		}

		i = 2;

		while(el != null)
		{
			fields.add(new Field(getTextValue(el, "name"), getTextValue(el, "value")));

			try {
				el = (Element)xp.evaluate("/data-group/record[first='"+ first + "'][last='" + last + "']/field[" + i.toString() + "]",
						dom, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}

			i++;
		}

		Person result = Person.newPerson(first, last, phone, email, fields);

		return result;
	}
	
	/**
	 * Converts the queried information of a given element to a String
	 * 
	 * @param ele element queried
	 * @param tagName String to look for
	 * @return text value
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	/**
	 * Creates a new element that stores the information in the specified person
	 * 
	 * @param subject person to be stored
	 * @return newly created element
	 */
	private Element createRecordElement(Person subject){

		Element recordEle = dom.createElement("wow:record");

		Element firstEle = dom.createElement("first");
		Text firstText = dom.createTextNode(subject.getFirstName());
		firstEle.appendChild(firstText);
		recordEle.appendChild(firstEle);

		Element lastEle = dom.createElement("last");
		Text lastText = dom.createTextNode(subject.getLastName());
		lastEle.appendChild(lastText);
		recordEle.appendChild(lastEle);

		for(Phone ph : subject.getPhoneList())
		{	
			Element phoneEle = dom.createElement("phone");

			Element phoneTypeEle = dom.createElement("type");
			Text phoneTypeText;

			if(ph.getType() == "")
			{
				phoneTypeText = dom.createTextNode("Phone");
			}
			else
			{
				phoneTypeText = dom.createTextNode(ph.getType());
			}

			phoneTypeEle.appendChild(phoneTypeText);
			phoneEle.appendChild(phoneTypeEle);

			Element phoneNumEle = dom.createElement("number");
			Text phoneNumText = dom.createTextNode(ph.getNumber());
			phoneNumEle.appendChild(phoneNumText);
			phoneEle.appendChild(phoneNumEle);

			recordEle.appendChild(phoneEle);
		}

		for(Email em : subject.getEmailList())
		{
			if(em.getAddress() != "")
			{
				Element emailEle = dom.createElement("email");

				if(em.getType() != "")
				{
					Element emailTypeEle = dom.createElement("type");
					Text emailTypeText = dom.createTextNode(em.getType());
					emailTypeEle.appendChild(emailTypeText);
					emailEle.appendChild(emailTypeEle);
				}

				Element emailNumEle = dom.createElement("address");
				Text emailNumText = dom.createTextNode(em.getAddress());
				emailNumEle.appendChild(emailNumText);
				emailEle.appendChild(emailNumEle);

				recordEle.appendChild(emailEle);
			}
		}

		for(Field fi : subject.getFields())
		{
			Element fieldEle = dom.createElement("field");

			Element fieldTypeEle = dom.createElement("name");
			Text fieldTypeText = dom.createTextNode(fi.getName());
			fieldTypeEle.appendChild(fieldTypeText);
			fieldEle.appendChild(fieldTypeEle);

			Element fieldValEle = dom.createElement("value");
			Text fieldValText = dom.createTextNode(fi.getValue());
			fieldValEle.appendChild(fieldValText);
			fieldEle.appendChild(fieldValEle);

			recordEle.appendChild(fieldEle);
		}

		return recordEle;
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

