package wowServLibs;

import java.io.File;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Static class that provides utilities for parsing XML documents.
 */
public class XMLParser {
	/**
	 * Parses the XML document.
	 */
	public static synchronized Document load(String _path) {
		Document rDoc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = null;
		File WOW = null;
		try {
			db = dbf.newDocumentBuilder();

			WOW = new File(_path);
			rDoc = db.parse(WOW);
		} catch (ParserConfigurationException p) {
			p.printStackTrace();
			System.err.println("Unable to parse " + _path + ": "
					+ p.getMessage());
			return null;
		} catch (SAXException se) {
			System.err.println(se);
			return null;
		} catch (java.io.FileNotFoundException f) {
			System.err.println("No database file found (" + f + ")");
			return null;
		} catch (java.io.IOException e) {
			System.err.println(e);
			return null;
		}

		return rDoc;
	}

	/**
	 * Write the XML document to a given path.
	 * 
	 * @param _path
	 *            The path of the output.
	 * @return <code>true</code> on success,<br>
	 *         <code>false</code> on failure.
	 */
	public static synchronized boolean write(Document doc, String _path) {
		boolean successful = false;

		try {
			Source source = new DOMSource(doc);

			File file = new File(_path);
			Result result = new StreamResult(file);

			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);

			successful = true;
		} catch (TransformerConfigurationException e) {
			System.err.println(e);
			Log.write("XML database write error: " + e.getMessageAndLocation());
		} catch (TransformerException e) {
			System.err.println(e);
			Log.write("XML database write error: " + e.getMessageAndLocation());
		}

		return successful;
	}

	/**
	 * Gets the value of a XML element.
	 * 
	 * @param ele
	 *            Element to parse.
	 * @param tagName
	 *            Name of the tag.
	 * @return text Value of the element.
	 */
	public static String textValue(Element _ele, String _tagName) {
		String textVal = new String("");
		NodeList nl = _ele.getElementsByTagName(_tagName);

		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			Node node = el.getFirstChild();
			if (node != null) {
				textVal = node.getNodeValue();
			}
		}

		return textVal;
	}
}
