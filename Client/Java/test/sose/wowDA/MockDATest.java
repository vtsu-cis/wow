package test.sose.wowDA;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.sose.wowBL.Person;
import src.sose.wowDA.MockDataAccess;
/**
 * @author Nick Guertin and Boomer Ransom
 */
/**
 * @className - MockDATest
 * @author - Nick Guertin
 * @description - Tests for the MockDataAccess class
 */

public class MockDATest {

	ArrayList<Person> tester;
	MockDataAccess mockDA;
	Person testee;
	Person nick = Person.newPerson("Nick", "Guertin", "524-6367", "n_guertin@yahoo.com");
	Person boomer = Person.newPerson("Boomer", "Ransom", "123-4567", "DRansom@vtc.vsc.edu");
	Person chris = Person.newPerson("Chris", "Beattie", "350-7430", "CBeattie@vtc.vsc.edu");
	Person notExists = Person.newPerson("I-Dont", "Exist", "(802)-555-1234", "not.exists@fake.com");
	
	@Before
	public void setup()
	{
		tester = new ArrayList<Person>();
		mockDA = new MockDataAccess();
		testee = new Person();
	}
	
	@Test
	public void testConstructor()
	{
		testee = nick;
		assertEquals(testee.getPhoneNumber(), mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals(testee.getEmail(), mockDA.query(testee).get(0).getEmail());
		
		testee = boomer;
		assertEquals(testee.getPhoneNumber(), mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals(testee.getEmail(), mockDA.query(testee).get(0).getEmail());
		
		testee = chris;
		assertEquals(testee.getPhoneNumber(), mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals(testee.getEmail(), mockDA.query(testee).get(0).getEmail());
	}
	
	@Test
	public void testAdd()
	{
		testee = notExists;
		mockDA.add(testee);
		assertEquals(testee, mockDA.query(testee).get(0));
	}
	
	@Test
	public void testAddExists()
	{
		testee = nick;
		assertEquals("Error: Record already exists", mockDA.add(testee));
	}
	
	@Test
	public void testUpdateExists()
	{
		testee = Person.newPerson("Nick", "Guertin", "272-1116", "nrg09040@vtc.edu");
		assertEquals("OK", mockDA.update(testee));
		assertEquals(testee.getPhoneNumber(), mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals(testee.getEmail(), mockDA.query(testee).get(0).getEmail());
	}
	
	@Test
	public void testUpdateNotExists()
	{
		testee = notExists;
		assertEquals("Error: Record not found", mockDA.update(testee));
		assertEquals(tester, mockDA.query(testee));
	}
	
	@Test
	public void testDeleteExists()
	{
		testee = nick;
		assertEquals("OK", mockDA.delete(testee));
		assertEquals(tester, mockDA.query(testee));
	}
	
	@Test
	public void testDeleteNotExists()
	{
		testee = notExists;
		assertEquals("Error: Record does not exist", mockDA.delete(testee));
		assertEquals(tester, mockDA.query(testee));
		testee = nick;
		assertEquals(testee.getPhoneNumber(), mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals(testee.getEmail(), mockDA.query(testee).get(0).getEmail());
	}
	
	@Test
	public void testQueryPartialName()
	{
		testee = Person.newPerson("nI", "gU", "", "");
		assertEquals("(802)524-6367", mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals("n_guertin@yahoo.com", mockDA.query(testee).get(0).getEmail());
		
		testee = Person.newPerson("nI", "", "", "");
		assertEquals("(802)524-6367", mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals("n_guertin@yahoo.com", mockDA.query(testee).get(0).getEmail());
		
		testee = Person.newPerson("", "gU", "", "");
		assertEquals("(802)524-6367", mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals("n_guertin@yahoo.com", mockDA.query(testee).get(0).getEmail());
		
		testee = Person.newPerson("bO", "", "", "");
		assertEquals("(802)123-4567", mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals("DRansom@vtc.vsc.edu", mockDA.query(testee).get(0).getEmail());
		
		testee = Person.newPerson("", "ns", "", "");
		assertEquals("(802)123-4567", mockDA.query(testee).get(0).getPhoneNumber());
		assertEquals("DRansom@vtc.vsc.edu", mockDA.query(testee).get(0).getEmail());
	}
	
	@After
	public void teardown()
	{
		tester = null;
		mockDA = null;
		testee = null;
	}
}
