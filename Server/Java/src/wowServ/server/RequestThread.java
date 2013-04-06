package wowServ.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.mail.MessagingException;

import sun.management.ManagementFactory;
import wowServ.wowBL.Department;
import wowServ.wowBL.Record;
import wowServLibs.AdminTracker;
import wowServLibs.Log;
import wowServLibs.Statistics;
import wowServLibs.Utility;
import wowServLibs.XMLDatabase;

import com.sun.management.OperatingSystemMXBean;

public class RequestThread implements Runnable {
	private Socket socket = null;
	private XMLDatabase db = null;

	private PrintWriter out = null;
	private BufferedReader in = null;
	private AdminTracker tracker = null;

	/**
	 * Initialize thread.
	 * 
	 * @param socket
	 *            a connecting client.
	 * @param parent
	 *            access to parent methods.
	 */
	public RequestThread(Socket socket, XMLDatabase _db) {
		this.socket = socket;
		this.db = _db;

		System.out.println("Accepted client at "
				+ socket.getRemoteSocketAddress());
	}

	/**
	 * Do the threaded actions. Called by Thread.start().
	 */
	public void run() {
		boolean isAdmin = false;
		try {
			socket.setSoTimeout(60000);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));

			String inputLine;
			do {
				// Read until a newline character is reached:
				inputLine = in.readLine();
				if (inputLine == null) {
					// Client closed socket.
					break;
				}

				if (!inputLine.startsWith("LOGIN") && !inputLine.startsWith("PING")) {
					System.out.println("Client@"
						+ socket.getRemoteSocketAddress().toString() + ": "
						+ inputLine);
				}
				
				inputLine = inputLine.trim();

				if (inputLine.startsWith("QRY")) {
					// Handle client query.
					if (inputLine.split("\\|", -1).length < 8) {
						inputLine = "||||||||";
					}

					System.out.println("Performing query: " + inputLine);

					ArrayList<Record> results = db.query(inputLine);
					if (results.size() == 0) {
						out.println("");
					} else {
						for (Record r : results) {
							out.println(XMLDatabase.formatRecordForPrint(r));
						}
					}

					Statistics.addQueryHit(socket.getInetAddress());
				} // Command QRY

				else if (inputLine.startsWith("AQRY")) {
					// Administrative query (they want the ID sent back as
					// well).

					// Handle client query.
					if (inputLine.split("\\|", -1).length < 8) {
						inputLine = "||||||||";
					}
					inputLine = inputLine.substring(1);

					ArrayList<Record> results = db.query(inputLine);
					if (results.size() == 0) {
						out.println("");
					} else {
						for (Record r : results) {
							out.println(XMLDatabase
									.formatRecordWithIDForPrint(r));
						}
					}

					Statistics.addQueryHit(socket.getInetAddress());
				} // Command AQRY

				else if (inputLine.equals("VER")) {
					// Check version.
					out.println(Server.clientVersion);
				} // Command VER
				
				else if (inputLine.startsWith("PING")) {
					// Send pong.
					
					out.println("PONG");
				} // Command PING

				else if (inputLine.startsWith("MAIL")) {
					// Forward a mail message to the IT department.

					// Checks to See if there is an colon in the mail message
					// and gets rid of it if necessary
					if (inputLine.startsWith("MAIL:")) {
						inputLine = inputLine.substring(6);
					} else {
						inputLine = inputLine.substring(5);
					}

					// Splits the inputline with the delimiter | to parse the
					// message
					String[] splitInputLine = inputLine.split("\\|");

					// Checks how long the split inputline is
					int numberOfElements = splitInputLine.length;

					// Checks to see if there is enough elements in the Mail
					if (numberOfElements != 4) {
						out.println("ERROR: Invalid mail request.");

						Log.write("Received an corrupted packet from: "
								+ socket.getRemoteSocketAddress()
								+ ": Messege = " + inputLine);

						break;
					}

					// Formated Strings for the subject and the message
					final String subject = "Feedback (" + splitInputLine[2]
							+ ")";
					final String message = ("Feedback sent by "
							+ splitInputLine[0] + "\nEmail: "
							+ splitInputLine[1] + "\nType: "
							+ splitInputLine[2] + "\n--- Feedback start\n\n"
							+ splitInputLine[3] + "\n\n--- Feedback end\n")
							// Replace <> with &lt;&gt; so that no HTML can be inserted.
							.replace("<", "&lt;").replace(">", "&gt;");
					
					// Attempt to mail the message.
					try {
						mailClient.HAITClient.sendMessage(subject, message);
						
						out.println("Your e-mail was sent successfully.");
					}
					catch (MessagingException e) {
						out.println("ERROR: Your e-mail could not be sent. Please " +
								"contact the IT department.");
						
						Log.write("E-mail failed during MAIL command: " + e.getMessage());
					}
				} // Command MAIL
				
				else if (inputLine.startsWith("HITALL")) {
					// Send the total hits to this server.
					out.println(Statistics.getTotalQueries());
				} // Command HITALL

				else if (inputLine.startsWith("GETFILE")) {
					// File transfer.
					if (Server.main) {
						out.println("NO");

						Log.write("GETFILE was sent to the main server by "
								+ socket.getRemoteSocketAddress());
					} else {
						handleFileTransfer();
					}
				} // Command GETFILE

				else if (inputLine.equals("DEPTLIST")) {
					// Retrieve a list of departments.
					java.util.Vector<Department> result = db.getDepartments();

					for (Department d : result) {
						out.println(d.getName());
					}
				} // Command DEPTLIST

				else if (inputLine.startsWith("STATS")) {
					// User wants to view the WOW statistics.
					out.println(Statistics.getStats());
				} // Command STATS

				else if (inputLine.equals("HEALTH")) {
					// User wants to view the health of the server.
					DecimalFormat formatter = (DecimalFormat) DecimalFormat
							.getInstance();
					formatter.setMaximumFractionDigits(2);

					final OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory
							.getOperatingSystemMXBean();
					out.println("OS: " + bean.getName() + " "
							+ bean.getVersion() + " (" + bean.getArch() + ")");
					out.println("System load average: "
							+ (bean.getSystemLoadAverage() < 0 ? "N/A" : bean
									.getSystemLoadAverage()));
					out.println("Processors: " + bean.getAvailableProcessors());
					out.println("Average query response time: "
							+ db.getAverageQueryResponseTime());
					out.println("Available physical memory: "
							+ formatter.format((float) bean
									.getFreePhysicalMemorySize() / 1000000f)
							+ "/"
							+ formatter.format((float) bean
									.getTotalPhysicalMemorySize() / 1000000f)
							+ "MB");
					out
							.println("Memory allocated: "
									+ formatter
											.format((float) bean
													.getCommittedVirtualMemorySize() / 1000000f)
									+ "MB");
					out
							.println("Swap size: "
									+ formatter.format((float) bean
											.getFreeSwapSpaceSize() / 1000000f)
									+ "/"
									+ formatter
											.format((float) bean
													.getTotalSwapSpaceSize() / 1000000f)
									+ "MB");
				} // Command HEALTH

				else if (inputLine.startsWith("LOGIN")) {
					// User wants to log in as an administrator.
					if (inputLine.startsWith("LOGIN:")) {
						// Remove any colons to make it easier on us.
						inputLine.replaceFirst(":", "");
					}

					inputLine = inputLine.substring("LOGIN ".length());

					// Expected:
					// inputLine = "Username|password"
					final String[] loginInformation = inputLine.split("\\|");

					if (handleLogin(loginInformation[0], loginInformation[1])) {
						isAdmin = true;
						socket.setSoTimeout(300000);
					}
				}

				else if (inputLine.startsWith("MAIN")) {
					// User asking if we are a main server.
					out.println((Server.main) ? "1" : "0");
				}

				else {
					// Command not recognized or is an admin command.
					if (isAdmin) {
						tracker.interpretMessage(inputLine);
					} else {
						out.println("ERROR: Command not recongized.");
						Log.write("Unrecognized command sent from "
								+ socket.getRemoteSocketAddress() + ": "
								+ inputLine);
					}
				}
			} while (isAdmin);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

			Log.write("Error: Algorithm missing: + " + e.getMessage());

			sendMail(
					"Critical WOW Error: Needs attention",
					"WOW requires the SHA-1 algorithm in Java. "
							+ "This is only available in Java 1.4 or higher. "
							+ "This algorithm appears to be missing from the server. "
							+ "Please update the server to the latest version of Java!\n\n"
							+ "Exception details: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();

			Log.write("Error while handling "
					+ (isAdmin ? "administrator" : "client") + ": "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();

			Log.write("Exception while handling client: " + e);
		} finally {
			// Terminate sockets.
			out.close();
			try {
				in.close();
				socket.close();
			} catch (IOException f) {
			}
		}
	}

	/**
	 * Convenience method for sending a mail message. This may not work: the
	 * method will catch any exceptions thrown by the mail client and log the
	 * error.
	 * 
	 * @param subject
	 *            Subject of the mail letter.
	 * @param message
	 *            Content of the mail letter.
	 * @return True if the message was successfully sent. False if an exception
	 *         was thrown.
	 */
	private boolean sendMail(String subject, String message) {
		try {
			mailClient.HAITClient.sendMessage(subject, message);

			return true;
		} catch (MessagingException n) {
			Log
					.write("The server attempted to send a mail message but failed due to the following "
							+ "exception: "
							+ n.getMessage()
							+ ". Upgrade the server's version of Java.");
		}

		return false;
	}

	/**
	 * Handle login from the client.
	 * 
	 * @param username
	 *            Username to attempt to login with.
	 * @param password
	 *            Password for the username.
	 * @return True if successful, false if not.
	 * @throws IOException
	 *             I/O error, such as reading the XML file.
	 * @throws NoSuchAlgorithmException
	 *             If a hash algorithm is missing.
	 */
	private boolean handleLogin(String username, String password)
			throws IOException, NoSuchAlgorithmException {
		username = username.trim();
		// Hash the password. The password should be hashed in the database too.
		password = Utility.checksum(password);
		if (!db.verifyAdministrator(username, password)) {
			// User is not an administrator.
			out.println("ERROR: Invalid username/password combination!");
			Log.write("Invalid username/password combination from "
					+ socket.getRemoteSocketAddress() + " (Attempted: "
					+ username + ")");

			return false;
		}

		// User is an administrator.
		out.println("OK");
		Log.write("Administrator " + username + " has logged in from "
				+ socket.getRemoteSocketAddress() + ".");

		tracker = new AdminTracker(socket, username, db);

		return true;
	}

	/**
	 * Called after GETFILE is sent. Handles receiving a new file.
	 * 
	 * @param fileSize
	 *            Size of the file.
	 * @return File data.
	 */
	private String getFile(int fileSize) throws IOException,
			NoSuchAlgorithmException {
		int bytesLeft = fileSize;
		StringBuffer buffer = new StringBuffer();
		while (bytesLeft > 0) {
			char[] cbuf = new char[bytesLeft];
			final int received = in.read(cbuf, 0, bytesLeft);
			if (received < 0) {
				throw new IOException(
					"Socket was suddenly closed during a profiles.xml file transfer.\n" +
					"Received a total of " + (fileSize - bytesLeft) + " bytes " +
					"(out of " + fileSize + ").");
			}
			
			buffer.append(cbuf, 0, received);
			bytesLeft -= received;
		}

		return buffer.toString();
	}

	/**
	 * Handles entirely the transfer between the server and the client (remote
	 * server).
	 * 
	 * @throws Exception
	 *             This method has a lot of room for errors, so it can throw
	 *             multiple exception types. Good luck!
	 */
	private void handleFileTransfer() throws Exception {
		Log.write("Received GETFILE command -- file transfer requested from "
				+ socket.getRemoteSocketAddress().toString());

		// Read again to obtain the file size.
		final String fileSizeString = in.readLine();
		final String remoteChecksum = in.readLine();

		// The server is transferring profiles.xml.
		final String configPath = (new File(".").getAbsolutePath());
		final File profiles = new File(configPath + "/profiles.xml");

		int fileSize = 0;
		try {
			fileSize = Integer.parseInt(fileSizeString);
		} catch (NumberFormatException e) {
			Log.write("Invalid packet sent from "
					+ socket.getRemoteSocketAddress().toString()
					+ " during GETFILE.");
			out.println("NO");
			return;
		}

		if (!profiles.canWrite()) {
			Log
					.write("Attempted to write profiles.xml but do not have permission to write to it.");
			out.println("NO");
			return;
		}

		// TODO verify as real.

		out.println("OK");

		// Receive file and calculate checksums.
		String fileData = getFile(fileSize);
		String localChecksum = Utility.checksum(fileData);

		// Compare checksums.
		if (!localChecksum.equals(remoteChecksum)) {
			// File transfer failed. Notify target server and try again.
			out.println("NO");

			System.err.println("Checksum failed:\nLocal: " + localChecksum
					+ ", remote: " + remoteChecksum);

			fileData = getFile(fileSize);
			localChecksum = Utility.checksum(fileData);

			if (localChecksum.equals(remoteChecksum)) {
				// Successful on attempt two.
				Log.write("Received profiles.xml from "
						+ socket.getInetAddress().getHostName() + ":"
						+ socket.getPort() + ". This took two attempts.");
			} else {
				// Failed the transfer after two tries.
				out.println("NO");

				final String errorMessage = "The file transfer failed between the target server and this server "
						+ "because the checksums did not match twice.  This is an unusual event for "
						+ "TCP transfers (although not entirely impossible) and suggests a coding error. "
						+ "Please investigate the file transfer method in the RequestThread class.";

				Log.write(errorMessage);
				
				FileWriter writer = new FileWriter(new File(Server.DEFAULT_DATABASE_FILE + ".fail"));
				writer.write(fileData);
				writer.flush();
				writer.close();

				mailClient.HAITClient.sendMessage(
						"WOW Server Error - Needs attention", errorMessage);

				return;
			}
		}

		// Transfer is OK!
		out.println("OK");

		XMLDatabase.writeToFile(fileData);

		// Done.
		Log.write("Received profiles.xml from "
				+ socket.getInetAddress().getHostName() + ":"
				+ socket.getPort());
	}
}
