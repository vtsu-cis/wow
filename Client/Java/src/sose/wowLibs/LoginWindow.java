package src.sose.wowLibs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import src.sose.wowDA.Address;

/**
 * A login window, which allows the user to 
 */
public class LoginWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private LoginNotifier callback 			= null;
	
	private JLabel instruction				= null;
	private JLabel loginText				= null;
	private JTextField loginField 			= null;
	private JLabel passwordText				= null;
	private JPasswordField passwordField	= null;
	private JButton loginButton				= null;
	private JButton quitButton				= null;
	
	/**
	 * Initialize the GUI components and display them.
	 * @param callback Class to call when a login is finished.
	 */
	public LoginWindow(LoginNotifier callback) {
		this.callback = callback;
		initializeComponents();
	}
	
	/**
	 * Initialize the GUI components and then display them. This constructor is especially useful
	 * because it allows the programmer to change the top instructions (in case of a time out or such).
	 * @param callback Class to call when a login is finished.
	 * @param instructions Instructions to display on top of the screen.
	 */
	public LoginWindow(LoginNotifier callback, String instructions) {
		this.callback = callback;
		initializeComponents();
		
		instruction.setText(instructions);
	}
	
	/**
	 * Set up the GUI components and display the screen.
	 */
	private void initializeComponents() {
		// Initialize components.
		instruction 	= new JLabel("Please enter your login information to continue.");
		loginText		= new JLabel("Login:");
		loginField 		= new JTextField();
		passwordText	= new JLabel("Password:");
		passwordField 	= new JPasswordField();
		loginButton		= new JButton("Login");
		quitButton		= new JButton("Quit");
		
		// Configure components.
		loginField.addActionListener(new LoginHandler());
		passwordField.addActionListener(new LoginHandler());
		
		loginButton.addActionListener(new LoginHandler());
		loginButton.setBackground(new Color(102, 255, 102)); // light green.
		
		quitButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		quitButton.setBackground(new Color(255, 102, 102)); // light red
		
		// Set up layout.
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		add(instruction, c);
		
		c.gridwidth = 1;
		c.gridy = 1;
		c.ipadx = 20;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(15, 30, 5, 5);
		add(loginText, c);
		
		c.gridx = 1;
		c.ipadx = 100;
		c.insets = new Insets(15, 5, 2, 5);
		c.anchor = GridBagConstraints.WEST;
		add(loginField, c);
		
		c.gridx = 0;
		c.ipadx = 20;
		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(2, 30, 5, 5);
		add(passwordText, c);
		
		c.gridx = 1;
		c.ipadx = 100;
		c.insets = new Insets(2, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		add(passwordField, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.ipadx = 20;
		c.insets = new Insets(10, 15, 10, 5);
		c.anchor = GridBagConstraints.WEST;
		add(quitButton, c);
		
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 5, 10, 15);
		add(loginButton, c);
		
		// Set window icon.
		setIconImage(Toolkit.getDefaultToolkit().getImage("images/WoWsuperSMALL.gif"));
		
		// Set miscellaneous options for the frame.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Window on the World - Login");
		setResizable(false);
		
		// Resize the window according to the window's components.
		pack();
		setLocationRelativeTo(null);
		
		// Display the window.
		setVisible(true);
	}
	
	/**
	 * Set new instructions, which are displayed at the top of the login window.
	 * @param instructions Instructions to display.
	 */
	public void setInstructions(String instructions) {
		instruction.setText(instructions);
	}
	
	/**
	 * Listen for when the login button is pressed.
	 */
	private final class LoginHandler implements ActionListener {
		/**
		 * Check the text box fields to see if they are valid.
		 * @return True if they are valid.
		 */
		private boolean isValidInput() {
			if (loginField.getText().equals("") || passwordField.getPassword().length == 0) {
				return false;
			}
			
			if (Pattern.matches("[^A-Za-z0-9-_]", loginField.getText())) {
				// Login contains invalid characters.
				return false;
			}
			
			return true;
		}
		
		@Override public void actionPerformed(ActionEvent e) {
			if (!isValidInput()) {
				JOptionPane.showMessageDialog(LoginWindow.this, 
					"The login or password fields are invalid.\n" +
					"Please make sure both fields are filled in.\n" +
					"A login name can only contain letters, numbers, and the '-', '_' characters.", 
					"Cannot login - Errors.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			final Address addr = WowHelper.getMainServerAddress();
			try {
				ConnectionHandler.connectTo(addr.GetAddress(), addr.getPort());
				
				ConnectionHandler.sendMessage(
					"LOGIN: " + 
					loginField.getText() + "|" + 
					new String(passwordField.getPassword()));
				
				final String reply = ConnectionHandler.readLine();
				System.out.println("Reply: " + reply);
				if (!reply.equalsIgnoreCase("OK")) {
					callback.loginFailed("Incorrect login or password. Please try again.");
					
					return;
				}
				
				callback.successfulLogin();
			}
			catch (IOException ex) {
				callback.loginFailed("Unable to connect to the main server. Please try again later.\n\n" +
					"Exception details:\n" + ex.getMessage());
				
				ConnectionHandler.disconnect();
			}
		}
		
	}
}
