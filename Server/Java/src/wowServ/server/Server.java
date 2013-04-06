package wowServ.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import mailClient.HAITClient;
import wowServLibs.GlobalConfig;
import wowServLibs.Log;
import wowServLibs.Statistics;
import wowServLibs.StatsConstants;
import wowServLibs.XMLDatabase;

public class Server {
	public static final String DEFAULT_DATABASE_FILE = "profiles.xml";
	public static final long INTERVAL = 600000; // To seconds: [number] / 1000

	private int port;
	private XMLDatabase xmlDb;
	private static boolean listening = true;
	private static Object lock = new Object();
	private ServerSocket serverSocket = null;

	/**
	 * Tell the server to stop listening, essentially stopping the server.
	 */
	public static void stopListening() {
		synchronized (lock) {
			listening = false;
		}
	}

	/**
	 * Ask whether or not the server is still listening.
	 * 
	 * @return True if the server is accepting new clients.
	 */
	public static boolean isListening() {
		synchronized (lock) {
			return listening;
		}
	}

	/**
	 * Track the WOW Client's most recent version. This is set either manually
	 * or by sending the server the command, "SETVER [ver]"
	 */
	public static String clientVersion = "2.00";

	/**
	 * Track whether or not this server is the "main" server. A main server has
	 * the ability to add, update, and delete records.
	 */
	public static boolean main = false;

	/**
	 * Initialize server with default port 5280.
	 */
	public Server() throws IOException {
		initServer(5280);
	}

	/**
	 * Initialize server with specified port.
	 */
	public Server(int _port) throws IOException {
		initServer(_port);
	}

	/**
	 * Obtain access to the server socket. Since this is a top level class, this
	 * is typically only called by unit tests.
	 * 
	 * @return Server socket object.
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * Get the port number that the server listens on.
	 * 
	 * @return Unsigned short integer (0-65535).
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Initialize the server.
	 * 
	 * @param _port
	 *            Port to listen on.
	 */
	public void initServer(int _port) throws IOException {
		port = _port;
		Log.write("Server started on port " + port + ". "
				+ ((main) ? "Running as main." : ""));

		// Initialize and load the XML database.
		try {
			xmlDb = new XMLDatabase(DEFAULT_DATABASE_FILE);
		} catch (IOException e) {
			Log.write("Error: Cannot load XML database: " + e);
		}
	}

	/**
	 * main() is called when the program is started.
	 * 
	 * @param args
	 *            command line arguments. The first argument must be empty or a
	 *            port.
	 * @throws IOException
	 *             when an I/O error occurs.
	 */
	public static void main(String[] args) throws IOException,
			NullPointerException {
		System.out.println("Window on the World server "
				+ ManagementFactory.getRuntimeMXBean().getName()
				+ " is initializing.");

		int port = 5280;
		if (args.length > 0) {
			for (int c = 0; c < args.length; c++) {
				try {
					if (args[c].equalsIgnoreCase("-main")
							|| args[c].equalsIgnoreCase("--main")) {
						System.out.println("Server running as main.");
						main = true;
						continue;
					} else if (args[c].equalsIgnoreCase("-g")) {
						System.out
								.println("Generating configuration and statistics file.");
						GlobalConfig.generateConfigFile();
						Statistics.generateHitFile();
						return;
					}
					port = Integer.parseInt(args[c]);
				} catch (NumberFormatException n) {
					System.out.println("Unknown argument: " + args[c]);
				}
			}
		}

		// If loadConfigFile fails (IOException) it won't generate a new one.
		// The generated configuration is not valid and only used as an example.
		GlobalConfig.loadConfigFile();
		try {
			Statistics.loadHitFile();
		} catch (FileNotFoundException e) {
			System.err
					.println("Statistics file not found -- generating new one.");
			Log.write("No stats file was found, so a new one was created.");
			Statistics.generateHitFile();

			Statistics.loadHitFile();
		}

		Server serv = new Server(port);
		System.exit(serv.mainLoop());
	}

	/**
	 * Loop until death. The loop will handle incoming clients.
	 * 
	 * @throws IOException
	 *             when an error occurs with initializing the server socket or
	 *             if an error occurs when handling clients.
	 */
	public int mainLoop() throws IOException {
		new IntervalStatsCalculation();

		System.out.println("Server is now listening for clients...");

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port + ": " + e);
			return 1;
		}

		ExecutorService executor = Executors.newCachedThreadPool();
		while (listening) {
			executor.execute(new RequestThread(serverSocket.accept(), xmlDb));
		}

		serverSocket.close();
		executor.shutdown();

		return 0;
	}

	/**
	 * At every interval (default 10 minutes) this will trigger a stats
	 * calculation.
	 */
	private class IntervalStatsCalculation implements Runnable {
		/**
		 * The constructor will automatically start this as a thread.
		 */
		public IntervalStatsCalculation() {
			// Start and run the thread.
			new Thread(this).start();
		}

		/**
		 * Called by Thread.start(), this is where the multi-threaded actions
		 * occur.
		 */
		public void run() {
			// Keep a boolean open to let us know if a mail alert was sent yet.
			boolean notified = false;

			// Loop while the server is accepting conections.
			while (listening) {
				try {
					Thread.sleep(1000); // Sleep for ([number] / 1000) seconds.

					// Check if the time has passed the interval.
					final Calendar timestamp = Calendar.getInstance();
					final long elapsed = timestamp.getTimeInMillis()
							- Statistics.getLastCalculationInMillis();
					if (elapsed > INTERVAL) {
						Statistics.calculateStats();

						// Reset notified to false so if an error occurs again,
						// it will re-send the message.
						notified = false;
					}
				} catch (InterruptedException e) {
					// This thread was interrupted by another thread.
					Log.write("Stats calculation thread was interrupted. Attempting to continue...");
				} catch (IOException e) {
					// Stats calculation error.
					Log.write("Error - unable to calculate stats: " + e);

					if (!notified) {
						try {
							// Attempt to send a mail message to an
							// administrator.
							HAITClient
									.sendMessage(
											"WOW Error - Unable to calculate statistics.",
											"An error occurred while attempting to calculate the stats.\nMost likely, "
													+ "the "
													+ StatsConstants.STATS_FILE
													+ " file is unreadable or missing.\n"
													+ "Please investigate this.\n\nError details:\n"
													+ e);

							notified = true;
						} catch (MessagingException ex) {
							// Mail alert failed.
							ex.printStackTrace();

							Log.write("Error - attempt to deliver alert e-mail failed: " + ex);
						}
					}
				}
			}
		} // run()
	}
}
