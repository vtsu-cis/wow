package wowServTests.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import wowServ.wowBL.Field;
import wowServ.wowBL.Record;
import wowServLibs.XMLDatabase;

public class XMLDatabaseTest extends junit.framework.TestCase {
	private XMLDatabase db;
	
	/**
	 * Set up the XML database.
	 */
	@BeforeClass public void setUp() {
		if (db == null) {
			try {
				db = new XMLDatabase("profiles.xml");
			}
			catch (IOException e) {
				fail("Cannot execute XML database tests: " + e);
			}
		}
	}
	
	/**
	 * Test if we can obtain a fresh ID that does not conflict with other ID numbers.
	 */
	@Test public void testGetUnusedID() {
		final Vector<Record> people = db.getPeople();
		final int newID = db.getUnusedID();
		
		// Iterate through each person to make sure the ID does not exist.
		for (Record person : people) {
			if (person.getID() == newID) {
				fail("XMLDatabase.getUnusedID() did not generate a unique ID number.");
			}
		}
	}
	
	/**
	 * Test if we can add a new record that does not conflict with other ID numbers.  Additionally the test
	 * will query the added record, modify it, then delete it.
	 */
	@Test public void testAddModifyDelete() {
		final String FIRST_NAME 	= "B'ob. Villian";
		final String LAST_NAME 		= "Ca't Dog Jr.";
		final String PHONE_NUMBER 	= "(802)555-5555";
		final String EMAIL_ADDRESS 	= "b'obca't@bob.cat";
		
		ArrayList<Field> fields = new ArrayList<Field>();
		fields.add(new Field("First Name", FIRST_NAME));
		fields.add(new Field("Last Name", LAST_NAME));
		fields.add(new Field("Phone Number", PHONE_NUMBER));
		fields.add(new Field("Email", EMAIL_ADDRESS));
		fields.add(new Field("Campus", ""));
		fields.add(new Field("Role", ""));
		fields.add(new Field("Department", ""));
		fields.add(new Field("Fax", ""));
		
		Record testRecord = new Record(-1, fields);
		
		try {
			final boolean reply = db.add(testRecord);
			assertTrue("Database add failed.", reply);
		}
		catch (Exception e) {
			fail("Exception thrown during attempt: " + e.getMessage());
		}
		
		final ArrayList<Record> reply1 = db.query(testRecord);
		final ArrayList<Record> reply2 = db.query(
			FIRST_NAME + "|" + LAST_NAME + "|" + PHONE_NUMBER + "|" + EMAIL_ADDRESS + "|||||");
		
		if (reply1 == null) {
			fail("Query on test record failed.");
		}
		
		else if (reply1.size() == 0) {
			fail("Query on test record did not appear as a result of the query.");
		}
		
		if (reply2 == null) {
			fail("Query on test record as a string failed.");
		}
		
		else if (reply2.size() == 0) {
			fail("Query on test record as a string did not appear as a result of the query.");
		}
		
		if (reply1.get(0).getID() != reply2.get(0).getID()) {
			fail("The same query of two different types returned unequivalent values.");
		}
		
		// Change Bob's name to John.
		fields.get(0).setName("John");
		testRecord.setFields(fields);
		final boolean result = db.update(testRecord);
		assertTrue("Update on test record failed. (first name)", result);
		
		fields.get(1).setName("Phil");
		testRecord.setFields(fields);
		final boolean result2 = db.update(testRecord);
		assertTrue("Update on test record failed (last name).", result2);
		
		// Now remove the record.
		final boolean wasRemoved = db.delete(testRecord.getID());
		assertTrue("The database did not delete the test record.", wasRemoved);
	}
}
