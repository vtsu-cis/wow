/**
 * 
 */
package wowServTests.server;

//import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wowServ.server.Server;
import wowServ.wowBL.Field;
import wowServ.wowBL.Record;

/**
 * @author asibley
 *
 */
public class ServerTest extends junit.framework.TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new Server();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		server = null;
	}

	/**
	 * Test method for {@link wowServ.server.Server#formatFixedRecordForPrint(wowServ.wowBL.FixedRecord)}.
	 */
	@Test
	public final void testFormatRecordForPrint() {
		java.util.ArrayList<Field> fields = new java.util.ArrayList<Field>();
		fields.add(new Field("First Name", "test"));
		fields.add(new Field("Last Name", "test"));
		fields.add(new Field("Phone Number", "(802)000-0000"));
		fields.add(new Field("Email", "test@test.test"));
		
		Record r = new Record(0, fields);
		assertEquals("test|test|(802)000-0000|test@test.test|||||", wowServLibs.XMLDatabase.formatRecordForPrint(r));
	}

	/**
	 * Test method for {@link wowServ.server.Server#getFixedRecordFromString(java.lang.String)}.
	 */
	@Test
	public final void testGetFixedRecordFromString() {
		Record r = null;
		String rec = "QRY: test|testa|(802)000-0000|test@test.test|||||";
		r = wowServLibs.XMLDatabase.stringToRecord(rec);
		
		assertTrue(r.getFirstName().equals("test"));
		assertTrue(r.getLastName().equals("testa"));
		assertTrue(r.getData("Email").get(0).equals("test@test.test"));
		assertTrue(r.getData("Phone Number").get(0).equals("(802)000-0000"));
		assertTrue(r.getData("Campus").get(0).equals(""));
		assertTrue(r.getData("Role").get(0).equals(""));
		assertTrue(r.getData("Department").get(0).equals(""));
		assertTrue(r.getData("Fax").get(0).equals(""));
		assertTrue(r.getData("Office").get(0).equals(""));
	}
	
	/**
	 * Test commands against the server.
	 * @deprecated This test is now obsolete.  Administrator actions require authentication.
	 */
	@Test
	public final void testServerConnections() {
		// This test is now obsolete.  Administrator actions require authentication.
	}
	
	public String send(String msg) throws java.io.IOException {
		final String addr = "localhost";
		Socket socket = new Socket(addr, 5280);
		socket.setSoTimeout(10000);
		
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
					socket.getInputStream()));
		
		out.println(msg);
		String result = in.readLine();
		
		socket.close();
		
		return result;
	}
	
	@SuppressWarnings("unused")
	private Server server;
}
