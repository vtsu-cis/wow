package wowServLibs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;

import mailClient.HAITClient;

/**
 * Handle output of error logging and statistics.
 * 
 * @author Andrew Sibley
 */
public class Log {
	/**
	 * Default logging file name.
	 */
	public static final String LOG_FILE = "WOWd.log";

	/**
	 * Record an entry in the log file. Typically errors or suspicious activity
	 * is logged. The date is recorded as 'year-month-day', and the time is
	 * logged as 'hour:minute:second AM/PM'.
	 * 
	 * @param msg
	 *            The message to be recorded
	 */
	public static synchronized void write(String msg) {
		String output = new String();
		String date = dateFormat("yyyy-MM-dd");

		output += "(" + date + ") " + dateFormat("hh:mm:ss aaa") + ": " + msg;
		if (!output.endsWith(System.getProperty("line.separator"))) {
			// Add one and only one newline character.
			output += System.getProperty("line.separator");
		}

		File log = new File(LOG_FILE);
		try {
			FileWriter writer = new FileWriter(log, true);

			writer.write(output);
			writer.flush(); // Ensure all characters are written.

			writer.close(); // Clean up
		} catch (IOException e) {
			// An error occurred while attempting to log; probably a permissions
			// error.
			System.err.println("ERROR: Could not log (" + e.getMessage() + ", "
					+ msg + ")");

			try {
				HAITClient
						.sendMessage(
								"WOW Server error - attention needed.",
								"Attempted to log an error to "
										+ LOG_FILE
										+ ", but was unable to do so.\n"
										+ "Most likely file permissions are not correctly set so that the server can\n"
										+ "write to the file. The server should be able to read, write, and create the file "
										+ LOG_FILE + "\n\n"
										+ "Exception details:\n"
										+ e.getMessage());
			} catch (MessagingException e1) {
				System.err
						.println("Unable to send an e-mail alert. Nothing I can do about it.\n"
								+ "Exception:\n" + e1);
			}
		}
	}

	/**
	 * Get a formatted version of a specified pattern (see: SimpleDateFormat
	 * Java documentation).
	 * 
	 * @return formatted String.
	 */
	public static String dateFormat(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date today = new Date();
		return formatter.format(today);
	}
}
