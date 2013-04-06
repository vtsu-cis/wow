package wowServLibs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Supports Utilities for reading and writing configuration files for HAIT
 * Client
 * 
 * @author Isaac Parenteau
 * 
 */
public class GlobalConfig {

	private static String host;
	private static String userName;
	private static String password;
	private static String sendTo;
	private static String mailProtocol;
	private static String ipStart;
	private static String ipEnd;

	public static final String HOST_PROPERTY = "HOST_NAME";
	public static final String USER_PROPERTY = "USER_NAME";
	public static final String PASS_PROPERTY = "PASSWORD";
	public static final String MAIL_PROPERTY = "PROTOCOL";
	public static final String SEND_PROPERTY = "SEND_TO";
	public static final String IP_START_PROPERTY = "ipRangeStart";
	public static final String IP_END_PROPERTY = "ipRangeEnd";

	/**
	 * Generates Configuration Files
	 * 
	 * @throws IOException
	 */
	public static void generateConfigFile() throws IOException {

		PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(
				"wow.conf")));

		output.println("[MAIL]");
		output.println(HOST_PROPERTY + " = Host Address");
		output.println(USER_PROPERTY + " = User Name");
		output.println(PASS_PROPERTY + " = Password");
		output.println(MAIL_PROPERTY + " = smtp");
		output.println(SEND_PROPERTY + " = Sent to");
		output.println(" ");
		output.println("[SERVERS]");
		output.println("#Enter any number of server names and ports here.");
		output.println("#Separate each server entry with a new line, " +
				"and define each server like:");
		output.println("#serverName:portNumber");
		output.println("localhost:5280");
		output.println(" ");
		output.println("[SERVER_CONFIG]");
		output.println("#This is your on-campus IP Address range. "
				+ "Enter the range of IP address belonging to the campus.");
		output.println("#ipRangeStart is the beginning of the IP range. "
				+ "Enter the beginning of the IP range.");
		output.println("#ipRangeEnd is the end of the IP range. Enter the "
				+ "ending of the IP range. Please see example below.");
		output.println(" ");
		output.println(IP_START_PROPERTY + " = 155.42.0.0");
		output.println(IP_END_PROPERTY + " = 155.42.255.255");
		output.close();

		System.out.println("Generation Complete");
	}

	/**
	 * Loads the configuration file and parses the data to store the information
	 * into the variables
	 * 
	 * @throws IOException
	 */
	public static void loadConfigFile() throws IOException {
		BufferedReader input = new BufferedReader(new FileReader("wow.conf"));

		String buffer = null;
		while ((buffer = input.readLine()) != null) { // Reads each line from
														// the conf file
			if (buffer.startsWith("#")) {
				continue;
			}

			if (buffer.contains(HOST_PROPERTY)) {
				host = buffer.substring(HOST_PROPERTY.length() + 3);
			} else if (buffer.contains(USER_PROPERTY)) {
				userName = buffer.substring(USER_PROPERTY.length() + 3);
			} else if (buffer.contains(PASS_PROPERTY)) {
				password = buffer.substring(PASS_PROPERTY.length() + 3);
			} else if (buffer.contains(MAIL_PROPERTY)) {
				mailProtocol = buffer.substring(MAIL_PROPERTY.length() + 3);
			} else if (buffer.contains(SEND_PROPERTY)) {
				sendTo = buffer.substring(SEND_PROPERTY.length() + 3);
			} else if (buffer.contains(IP_START_PROPERTY)) {
				ipStart = buffer.substring(IP_START_PROPERTY.length() + 3);
			} else if (buffer.contains(IP_END_PROPERTY)) {
				ipEnd = buffer.substring(IP_END_PROPERTY.length() + 3);
			}
		}

		input.close();
	}

	/**
	 * Returns host name
	 * 
	 * @return host
	 */
	public static String getHost() {
		return host;
	}

	/**
	 * Returns the User Name
	 * 
	 * @return userName
	 */
	public static String getUserName() {
		return userName;
	}

	/**
	 * Returns the password
	 * 
	 * @return password
	 */
	public static String getPassword() {
		return password;
	}

	/**
	 * Returns the mail protocol
	 * 
	 * @return mailProtocol
	 */
	public static String getMailProtocol() {
		return mailProtocol;
	}

	/**
	 * REturns the send to address
	 * 
	 * @return sendTo
	 */
	public static String getSendTo() {
		return sendTo;
	}

	/**
	 * Returns the IP Starting range
	 * 
	 * @return ipStart
	 */
	public static String getIpStartRange() {
		return ipStart;
	}

	/**
	 * Returns the IP Ending Range
	 * 
	 * @return ipEnd
	 */
	public static String getIpEndRange() {
		return ipEnd;
	}
}