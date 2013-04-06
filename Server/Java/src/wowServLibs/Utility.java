package wowServLibs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.mail.MessagingException;
import wowServ.server.Server;

/**
 * Provides various utilities for WOW use.
 */
public class Utility {
	/**
	 * Get a SHA1 checksum (hex digest) of the given data. It will be a string
	 * of exactly 40 hexadecimal characters. Calls convertToHex().
	 * 
	 * @param data
	 *            Data to hash.
	 * @return Hashed data.
	 * @throws NoSuchAlgorithmException
	 *             Thrown if the SHA algorithm is unavailable. This case occurs
	 *             if this program is being run on an old version of Java.
	 */
	public static String checksum(String data) throws NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance("SHA");

		return convertToHex(algorithm.digest(data.getBytes()));
	}

	/**
	 * Convert a series of bytes to a hex string. Used for SHA-1 hashes.
	 * 
	 * @param data
	 *            Data to convert.
	 * @return Converted hash.
	 */
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Retrieve a list of WOW servers.
	 * 
	 * @return Array list of strings (Format: "[server_address]:[port]") or an
	 *         empty String on failure.
	 */
	public static ArrayList<String> getServerList() {
		ArrayList<String> finalList = new ArrayList<String>();
		ArrayList<String> serverList = new ArrayList<String>();
		String line = "";
		String[] addr;
		boolean found = false;

		try {
			File config = new File("wow.conf");
			FileReader fileReader = new FileReader(config);
			BufferedReader reader = new BufferedReader(fileReader);

			done: while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("[SERVERS]")) {
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.startsWith("#") || line.isEmpty()) {
							continue;
						}

						// Remove any stray comments at the end of lines.
						if (line.contains("#")) {
							line = line.substring(0, line.indexOf("#"));
						}

						// Check if the line is starting at a new section.
						if (line.contains("[")) {
							break done;
						}

						addr = line.split(":");

						for (String temp : serverList) {
							if (temp.equals(addr[0] + ":" + addr[1])) {
								found = true;
								break;
							}
						}

						if (!found) {
							serverList.add(new String(addr[0] + ":" + addr[1]));
						}

						found = false;
					}

					break done;
				}
			}

			for (String temp : serverList) {
				String s_address = "";
				int s_port = 0;
				Socket s = new Socket();
				found = false;
				try {
					s_address = temp.split(":")[0];
					s_port = Integer.parseInt(temp.split(":")[1]);

					s.connect(new InetSocketAddress(s_address, s_port), 800);
					s.close();
					found = true;
				} catch (UnknownHostException e) {
					Log.write("No DNS entry found for " + s_address);
				} catch (IOException e) {
					Log.write("Unable to establish a connection with "
							+ s_address);
				} catch (Exception e) {
					System.err.println("Corrupted server entry: " + temp);
					Log.write("Corrupted server entry: " + temp);
				}

				if (found) {
					finalList.add(new String(s_address + ":" + s_port));
				}
			}

			reader.close();
			fileReader.close();
		} catch (IOException ex) {
			Log.write("Error retrieving server list: " + ex);
		}

		return finalList;
	}

	/**
	 * Distribute the database file to all non-main servers. This immediately
	 * returns if the server is not set to main.
	 */
	public static void distributeDatabase() throws IOException {
		if (!Server.main) {
			return;
		}
		
		final String data = XMLDatabase.getFileContents();
		final ArrayList<String> servers = getServerList();
		String checksum = null;
		try {
			checksum = checksum(data);
		} 
		catch (NoSuchAlgorithmException e) {
			Log.write("Unable to perform checksum: " + e + ". Update Java.");

			try {
				mailClient.HAITClient.sendMessage(
					"Critical WOW Error: Needs attention",
					"WOW requires the SHA-1 algorithm in Java. "
							+ "This is only available in Java 1.4 or higher. "
							+ "This algorithm appears to be missing from the server. "
							+ "Please update the server to the latest version of Java!\n\n"
							+ "Exception details: "
							+ e.getMessage());
			} catch (MessagingException ex) {
				Log.write(
						"Unable to send notification e-mail about missing algorithm. Details: "
								+ ex);
			}

			return;
		}

		// Loop through the servers and attempt to send the file.
		for (String server : servers) {
			final String[] serverData = server.split("\\:");
			final String host = serverData[0];
			final int port = Integer.parseInt(serverData[1]);
			System.out.println("Sending to " + server + "...");

			PrintStream out = null;
			BufferedReader in = null;
			Socket socket = new Socket();
			try {
				socket.connect(new InetSocketAddress(host, port), 600);
				socket.setSoTimeout(1000);
				out = new PrintStream(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));

				out.println("GETFILE\n" + data.length() + "\n" + checksum);

				String reply = in.readLine();
				if (reply == null || !reply.equalsIgnoreCase("OK")) {
					Log.write("Server refused GETFILE request. Reply: "
									+ reply);
					System.out.println("Server " + host + ":" + port
							+ " denied request to accept database file: "
							+ reply);

					continue;
				}
				
				socket.setSoTimeout(10000);

				sendData(socket, data);

				reply = in.readLine();
				if (!reply.equalsIgnoreCase("OK")) {
					// File transfer failed. Try again.
					sendData(socket, data);

					reply = in.readLine();
					if (!reply.equalsIgnoreCase("OK")) {
						// Transfer failed again!
						Log.write("The transfer to target server "
							+ host + ":" + port + " failed twice in a row."
							+ " Details may be available in the target server's log.");
						
						System.out.println("Transfer to " + host + ":" + port + " failed.");

						continue;
					}
				}

				System.out.println("File was transferred to " + host + ":"
						+ port + " successfully.");
				Log.write(Server.DEFAULT_DATABASE_FILE + " was sent to " + host
						+ ":" + port + ".");
			} catch (IOException e) {
				System.err.println("Unable to distribute database to " + host
						+ ":" + port + ": " + e);
			} finally {
				out.close();
				in.close();
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		
		System.out.println("Done database distribution.");
	}

	/**
	 * Convenience method for sending data through the given socket.
	 * 
	 * @param out
	 *            Stream to print the file to.
	 * @param fileData
	 *            Data to send.
	 */
	public static void sendData(Socket out, String fileData) throws IOException {
		// Define the size of each "chunk".
		final int CHUNK_SIZE = 1024;
		
		final int length = fileData.length();
		
		int bytesSent = 0;
		int bytesLeft = length;
		while (bytesSent < length) {
			// Figure out how much should be sent.
			final int bytesToSend = bytesLeft <= CHUNK_SIZE ? bytesLeft : CHUNK_SIZE;
			
			// Fill the out-buffer with CHUNK_SIZE bytes (or less, if bytesLeft <= CHUNK_SIZE)
			byte[] buf = fileData.substring(bytesSent, bytesSent + bytesToSend).getBytes();
			
			// Write the data to the socket.
			out.getOutputStream().write(buf, 0, buf.length);
			
			bytesSent += bytesToSend;
			bytesLeft -= bytesToSend;
		}
		
		// Flush remaining data.
		out.getOutputStream().flush();
	}
}
