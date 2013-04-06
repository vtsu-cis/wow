package wowServTests.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wowServ.server.RequestThread;
import wowServ.server.Server;
import wowServLibs.Utility;
import wowServLibs.XMLDatabase;

public class RequestThreadTest extends junit.framework.TestCase {

	@Before
	public void setUp() throws Exception {
		server = new Server(5280);
	}

	@After
	public void tearDown() throws Exception {
		server = null;
	}

	@Test
	public final void testRun() {
		try {
			new RequestThread(new java.net.Socket("wildfire", 5280), null);
			fail("Should have thrown an unknown host exception.");
		}
		catch (java.io.IOException e) {
		}
	}
	
	@Test
	public void testFileTransfer() {
		Thread th = new Thread() {
			public void run() {
				try {
					Socket socket = new ServerSocket(5280).accept();
					
					new RequestThread(socket, new XMLDatabase("profiles.xml")).run();
				}
				catch (IOException e) {
					fail("Server threw IO exception: " + e);
				}
			}
		};
		th.start();
		
		Socket client = null;
		try {
			client = new Socket("localhost", server.getPort());
			client.setSoTimeout(5000);
			
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			final String buffer = XMLDatabase.getFileContents();
			final String checksum = Utility.checksum(buffer);
			
			File f = new File("profiles.xml");
			if (!f.exists()) {
				fail("Requires profiles.xml to execute.");
			}
			
			out.println("GETFILE");
			out.println(buffer.length());
			out.println(checksum);
			
			String reply = in.readLine();
			if (!reply.equalsIgnoreCase("OK")) {
				fail("Server denied GETFILE request.");
			}
			
			Utility.sendData(client, buffer);
			
			reply = in.readLine();
			if (!reply.equalsIgnoreCase("OK")) {
				fail("File transfer failed. Reply: " + reply);
			}
		}
		catch (IOException e) {
			fail("Client threw IO exception: " + e);
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			fail("File transfer requires SHA algorithm. Exception: " + e);
		}
		finally {
			try {
				client.close();
				th.interrupt();
			}
			catch (Exception e) {}
		}
	}
	
	@SuppressWarnings("unused")
	private Server server;
}
