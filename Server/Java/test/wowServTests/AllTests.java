package wowServTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import wowServTests.server.RequestThreadTest;
import wowServTests.server.ServerTest;
import wowServTests.server.XMLDatabaseTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for wowServTests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ServerTest.class);
		suite.addTestSuite(RequestThreadTest.class);
		suite.addTestSuite(XMLDatabaseTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
