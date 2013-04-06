package test.sose.wowClient;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.sose.wowClient.CLI;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.MockDataAccess;

public class CLITest {

	String[] searchCorrect = {"-s", "Guertin"};
	String[] searchTooManyArgs = {"-s", "asdfasdf", "asdfaf", "adsfasdf", "asdfasdf"};
	String[] searchNoResults = {"-s", "asdf"};
	String[] addCorrect = {"-a", "Bob", "Builder", "894-4135", "bobthebuilder@builders.com"};
	String[] addNotEnoughInfo = {"-a", "Bob", "Builder"};
	String[] addTooManyArgs = {"-a", "Bob", "Builder", "894", "bob", "afsd", "asdfasg"};
	String[] updateCorrect = {"-u", "Nick", "Guertin", "7894685", "nsafd@asf.com"};
	String[] updateNotFound = {"-u", "Bob", "Builder"};
	String[] updateTooManyArgs = {"-u", "Bob", "Builder", "asfd", "asfdgsda", "asfdff"};
	String[] deleteCorrect = {"-d", "Nick", "Guertin"};
	String[] deleteNotFound = {"-d", "Bob", "Builder"};
	String[] deleteTooManyArgs = {"-d", "Bob", "Builder", "asfd", "asfdgsda", "asfdff"};
	
	public static ByteArrayInputStream bs = 
		new ByteArrayInputStream("test".getBytes());
	
	public static InputStreamReader stringIn =
		new InputStreamReader(bs);
	
	CLI cmdLineTest;
	public static ArrayList<DataAccess> MDA = new ArrayList<DataAccess>();
	public static MockDataAccess mda = new MockDataAccess();
	
	@Before
	public void setUp()
	{
		MDA.add(mda);
		cmdLineTest = new CLI();
	}

	@Test
	public void testSearchName()
	{
		assertEquals(0, CLI.searchName(searchCorrect));
		assertEquals(1, CLI.searchName(searchTooManyArgs));
		assertEquals(0, CLI.searchName(searchNoResults));
	}
	
	@Test
	public void testAdd()
	{
		assertEquals(0, CLI.add(addCorrect, MDA));
		assertEquals(1, CLI.add(addNotEnoughInfo, MDA));
		assertEquals(1, CLI.add(addTooManyArgs, MDA));
	}
	
	@Test
	public void testUpdate()
	{
		assertEquals(0, CLI.update(updateCorrect, MDA));
		assertEquals(1, CLI.update(updateNotFound, MDA));
		assertEquals(1, CLI.update(updateTooManyArgs, MDA));
	}
	
	@Test
	public void testDelete()
	{
		assertEquals(0, CLI.delete(deleteCorrect, MDA));
		assertEquals(0, CLI.delete(deleteNotFound, MDA));
		assertEquals(1, CLI.delete(deleteTooManyArgs, MDA));
	}
	
	@Test
	public void testGetUserInput()
	{
		assertEquals("test", CLI.getUserInput(stringIn));
	}
	
	@After
	public void tearDown() 
	{
		cmdLineTest = null;
		MDA.remove(mda);
	}

}
