package util.mock;

import java.util.Random;

import util.HealthReport;
import util.ServerChannel;

/**
 * The MockServerChannel allows a programmer to test the functionality of Dr. WOW without
 * actually connecting to any server.
 */
public class MockServerChannel extends ServerChannel {
	private boolean loggedIn = false;
	
	/**
	 * Initialize the mock server channel.
	 * @param host Host name, only used to initialize the parent class with something.
	 * @param port Port number, only used to iniitalize the parent class with something.
	 */
	public MockServerChannel(String host, int port) {
		super(host, port);
	}

	/**
	 * Generate a health report using randomly generated numbers. The report may
	 * choose to return a "healthy" server but is just as likely to report a
	 * dead server.
	 */
	@Override public HealthReport getHealthReport() {
		Random random = new Random();
		return new HealthReport(
			random.nextInt(300) + 1, 
			(random.nextDouble() % 20) + 1, 
			(random.nextDouble() % 500 + 1000), 
			(random.nextDouble() % 1000) + 1000);
	}
	
	/**
	 * 
	 */
	@Override public boolean startServer() {
		return loggedIn;
	}
	
	/**
	 * 
	 */
	@Override public boolean stopServer() {
		return false;
	}
	
	/**
	 * This does nothing on the mock server channel. However, it will still return
	 * false if the user is not logged in.
	 * @return True if the user is logged in, otherwise false.
	 */
	@Override public boolean restartServer() {
		return loggedIn;
	}

	/**
	 * Attempt to log into the WOW server. On the mock channel, this allows the user
	 * to execute administrative actions like restartServer().
	 * @return Will always return true.
	 */
	@Override public boolean login(String name, String password) {
		loggedIn = true;
		return true;
	}

	/**
	 * Asks whether or not the channel is connected. On the mock server channel, a user
	 * is considered "connected" to a server when the user is logged in.
	 * @see isLoggedIn()
	 * @return The result of isLoggedIn().
	 */
	@Override public boolean isConnected() {
		return loggedIn;
	}

	/**
	 * A user is logged in to the mock channel when the user has called login() at least once
	 * and the user has not timed out.
	 * @return True if the user is logged in, otherwise false. 
	 */
	@Override public boolean isLoggedIn() {
		return true;
	}
}
