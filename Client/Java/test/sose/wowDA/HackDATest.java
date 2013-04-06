package test.sose.wowDA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.sose.wowBL.Person;
import src.sose.wowDA.HackDA;

public class HackDATest {
	
	HackDA hackDA;

	String correctPersonFormat = "Nick|Guertin|789-4567|nguertin@email.com";
	Person nicksPerson = Person.newPerson("Nick", "Guertin", "789-4567", "nguertin@email.com");
	
	@Before
	public void setUp() throws Exception 
	{
		hackDA = new HackDA("atlantis.ecet.vtc.edu", 5280, "VTC");
	}

	@After
	public void tearDown() throws Exception 
	{
		hackDA = null;
	}
	
	@Test
	public void testConnect()
	{
		hackDA.connect("ADD: Nick|Geurtinator|(802)524-6367|nguertin@yahoo.com|||||");
		System.out.println("Nick added.");
		hackDA.connect("ADD: Bomber|Ransomator|(802)192-8475|bomber@ransom.com|||||");
		System.out.println("Bomber added.");
		assertEquals("Nick|Geurtinator|(802)524-6367|nguertin@yahoo.com|||||", hackDA.connect("QRY: Nick|Geurtinator|||||||"));
		System.out.println("Query of Nick successful.");
		assertEquals("", hackDA.connect("QRY: I-Dont|Exist|||||||"));
		System.out.println("Query of non-existant record successful.");
		assertEquals("OK", hackDA.connect("DEL: Nick|Geurtinator||nguertin@yahoo.com|||||"));
		System.out.println("Deletion of Nick successful.");
		assertEquals("OK", hackDA.connect("DEL: Bomber|Ransomator||bomber@ransom.com|||||"));
		System.out.println("Deletion of Bomber successful.");
	}

	@Test
	public void testAddAndDelete() 
	{
		assertEquals("OK", hackDA.add(Person.newPerson("NoFirstName", "NoLastName", "999-9999", "NoNoNONONON@name.com")));
		System.out.println("Mr. NoName added.");
		assertEquals("ERR", hackDA.add(Person.newPerson("NoFirstName", "NoLastName", "999-9999", "NoNoNONONON@name.com")).substring(0, 3));
		System.out.println("Mr. NoName successfully not added again.");
		
		assertEquals("OK", hackDA.delete(Person.newPerson("NoFirstName", "NoLastName", "", "NoNoNONONON@name.com")));
		System.out.println("Mr. NoName deleted.");
		assertEquals("ERR", hackDA.delete(Person.newPerson("NoFirstName", "NoLastName", "", "")).substring(0, 3));
		System.out.println("Mr. NoName was definitely deleted.");
	}

	
	@Test
	public void testConvertString2Person() {
		Person temp = HackDA.convertString2Person(correctPersonFormat);
		assertTrue(nicksPerson.equals(temp));	
	}

}
