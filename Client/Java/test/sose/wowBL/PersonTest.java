package test.sose.wowBL;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.MockDataAccess;

/**
 * @author Nick Guertin and Boomer Ransom
 * TODO List
 * - Change set tests to check for good set behavior
 * - Write tests for any new methods
 */
/**
 * @className - PersonTest
 * @author - Nick Guertin and Boomer Ransom
 * @description - Tests for the Person class
 */

public class PersonTest {
	
	Person tester;
	public static ArrayList<DataAccess> da = new ArrayList<DataAccess>();
	public static MockDataAccess mda = new MockDataAccess();
	public static ArrayList<Field> fields = new ArrayList<Field>();
	
	@Before
	public void setup()
	{
		da.add(mda);
		tester = new Person();
	}
	
	@Test
	public void testSetFN()
	{
		assertEquals(0, tester.setFirstName("Nick"));
		assertEquals("Nick", tester.getFirstName());
	}
	
	@Test
	public void testSetLN()
	{
		assertEquals(0, tester.setLastName("Guertin"));
		assertEquals("Guertin", tester.getLastName());
	}
	
	@Test
	public void testSetPN()
	{
		assertEquals(0, tester.addPhone("524-6367"));
		assertEquals("(802)524-6367", tester.getPhoneNumber());
		
		assertEquals(0, tester.updatePhone("(802)524-6367", 0));
		assertEquals(0, tester.updatePhone("802-524-6367", 0));
		assertEquals(0, tester.updatePhone("8025246367", 0));
		assertEquals(0, tester.updatePhone("5246367", 0));
		
		assertEquals(1, tester.updatePhone("802524-6367", 0));
		assertEquals(1, tester.updatePhone("(802)-524-6367", 0));
		assertEquals(1, tester.updatePhone("5246-367", 0));
		assertEquals(1, tester.updatePhone("321", 0));
	}
	
	@Test
	public void testSetEmail()
	{
		assertEquals(0, tester.addEmail("n_guertin@yahoo.com"));
		assertEquals("n_guertin@yahoo.com", tester.getEmail());
		
		assertEquals(0, tester.updateEmail("n!guertin@yahoo.com", 0));
		assertEquals(0, tester.updateEmail("nguertin@morning.ecet.vtc.edu", 0));
		
		assertEquals(1, tester.updateEmail("nguertin.morning.ecet.vtc.edu", 0));
		assertEquals(1, tester.updateEmail("nguertin@morning", 0));
	}
	
	@Test
	public void testNewPerson()
	{
		tester = null;
		assertEquals(null, tester);
		tester = Person.newPerson("Nick", "Guertin", "524-6367", "n_guertin@yahoo.com");
		assertEquals("Nick", tester.getFirstName());
		assertEquals("Guertin", tester.getLastName());
		assertEquals("(802)524-6367", tester.getPhoneNumber());
		assertEquals("n_guertin@yahoo.com", tester.getEmail());		
	}
	
	@Test
	public void testAdd()
	{
		tester = null;
		tester = Person.newPerson("Nick", "Guertin", "524-6367", "n_guertin@yahoo.com");
		assertEquals("Error: Record already exists", tester.add(da));
		tester = Person.newPerson("Nick", "Nitreug", "524-6367", "n_guertin@yahoo.com");
		assertEquals("OK", tester.add(da));
		tester = Person.newPerson("", "", "524-6367", "n_guertin@yahoo.com");
		assertEquals("First name and last name required.", tester.add(da));
		
	}
	
	@Test
	public void testUpdate()
	{
		tester = null;
		tester = Person.newPerson("Nick", "jimbob", "9784641", "n_guertinopolis@yahoo.com");
		assertEquals("Error: Record not found", tester.update(da));
		tester = Person.newPerson("Nick", "Guertin", "1234564", "n_guertin@mailbag.com");
		assertEquals("OK", tester.update(da));
		
	}
	
	@Test
	public void testCheckName()
	{
		assertEquals(true, tester.checkName("Gertrude"));
		assertEquals(true, tester.checkName("Jean-Marie"));
		assertEquals(false, tester.checkName("BobVilla2"));
		assertEquals(false, tester.checkName("His@*#^$)($#(*&%asf"));
		assertEquals(false, tester.checkName("Ni.ck"));
		assertEquals(true, tester.checkName("Nick-"));
		assertEquals(false, tester.checkName("-Nick"));
		assertEquals(true, tester.checkName("n"));
		assertEquals(false, tester.checkName(""));
	}
	
	@Test
	public void testCheckClassroom()
	{
		assertEquals(true, tester.checkClassroom("GRN102"));
    	assertEquals(true, tester.checkClassroom("WIL125"));
    	assertEquals(false, tester.checkClassroom("GN102"));
    	assertEquals(false, tester.checkClassroom("GRN12"));
    	assertEquals(false, tester.checkClassroom("123GRN"));
    	assertEquals(true, tester.checkClassroom("WIL401A"));
       	assertEquals(false, tester.checkClassroom("WIL4014"));
	}
	
	@Test
	public void testServer()
	{
		assertEquals("NA", tester.getServer());
		tester.setServer("VTC");
		assertEquals("VTC", tester.getServer());
	}
	
	@Test
	public void testFields()
	{
		fields.add(new Field("One", "One"));
		fields.add(new Field("Two", "Two"));
		fields.add(new Field("Three", "Three"));
		tester.setFields(fields);
		assertEquals("One", tester.getFields().get(0).getName());
		assertEquals("One", tester.getFields().get(0).getValue());
		assertEquals("Two", tester.getFields().get(1).getName());
		assertEquals("Two", tester.getFields().get(1).getValue());
		assertEquals("Three", tester.getFields().get(2).getName());
		assertEquals("Three", tester.getFields().get(2).getValue());
	}
		
	@After
	public void teardown()
	{
		tester = null;
		da.remove(mda);
	}
}
