package test.sose.wowDA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.sose.wowBL.Person;
import src.sose.wowDA.LocalDA;

public class LocalDATest {
	
	LocalDA localDA;
	Person nick = Person.newPerson("Nick", "Guertin", "(802)524-6367", "n_guertin@yahoo.com");
	Person nrick = Person.newPerson("Nrick", "Gueertin", "(802)524-6367", "n_guertin@yahoo.com");
	Person notExists = Person.newPerson("I", "Dont", "123456789", "i.dont@exist.org");
	
	@Before
	public void setUp() throws Exception 
	{
		localDA = new LocalDA("test.xml");
	}

	@After
	public void tearDown() throws Exception 
	{
		localDA = null;
	}

	@Test
	public void testQuery() 
	{		
		assertTrue(nick.equals(localDA.query(nick).get(0)));
	}
	
	@Test
	public void testAddDelete()
	{
		assertEquals("OK", localDA.add(nrick));
		assertTrue(nrick.equals(localDA.query(nrick).get(0)));
		assertEquals("OK", localDA.delete(nrick));
		assertEquals(0, localDA.query(nrick).size());
		assertEquals("Error: Record not found", localDA.delete(notExists));
		assertEquals("Error: Record already exists", localDA.add(nick));
	}
}
