package mailClient;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import wowServLibs.GlobalConfig;

public class HAITClient {

	/**
	 * @param args
	 * @throws IOException
	 *             When GlobalConfig cannot be accessed
	 * @throws MessagingException
	 *             When sendMessage doesn't pass in a subject and content
	 */
	public static void main(String[] args) throws IOException,
			MessagingException {

		GlobalConfig.loadConfigFile();

		String subject = "Test";
		String content = "This is only a test message sent from the HAIT mail Client";

		sendMessage(subject, content);
	}

	/**
	 * Sends message
	 * 
	 * @param subject
	 *            Sets the subject of the message
	 * @param content
	 *            Sets the content of the message
	 * @throws MessagingException
	 *             When a message cannot be sent. Either an invalid
	 *             InternetAddress not setting the MimeMessage right or unable
	 *             to connect and send message
	 */
	public static void sendMessage(String subject, String content)
			throws MessagingException {
		// Retrieves information from the conf file and stores them in the
		// variables.
		String host = GlobalConfig.getHost();
		String username = GlobalConfig.getUserName();
		String password = GlobalConfig.getPassword();
		String client = GlobalConfig.getMailProtocol();
		String sendTo = GlobalConfig.getSendTo();

		// Sets new properties for the message with and loads the host into it
		// under mail.host
		Properties props = new Properties();
		props.put("mail.host", host);

		// Starts a new session and gets the default instances with props
		Session session = Session.getDefaultInstance(props, null);

		// creates a new Address for send to
		Address toAddress = new InternetAddress(sendTo);

		// Creates a new MimeMessage with the information from session
		// Loads in the content and subject from the parameters
		// Also makes the recipient to type from the toAddress variable
		// Saves the changes
		MimeMessage message = new MimeMessage(session);
		message.setContent(content, "text/plain");
		message.setSubject(subject);
		message.addRecipient(Message.RecipientType.TO, toAddress);
		message.saveChanges();

		// Calls on the Transport functions and passes in the mail transport
		// protocol from
		// the client. Then it tries to connect to the mail host passing in the
		// user name and
		// password for sending. Once it connects It sends the Mime Message to
		// all the
		// recipients.
		Transport transport = session.getTransport(client);
		transport.connect(host, username, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		System.out.println("An e-mail was sent to " + GlobalConfig.getHost()
				+ " about: " + subject + ".");
	}

}
