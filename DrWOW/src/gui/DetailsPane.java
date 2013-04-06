package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import util.ServerChannel;

/**
 * The details will be displayed for each server.
 */
public class DetailsPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private ServerChannel server;
	
	private JLabel hostNameLabel;
	private JTextField hostNameField;
	
	private JLabel portLabel;
	private JTextField portField;
	
	public DetailsPane() {
		super();
		
		initialize();
		
		refresh();
	}
	
	/**
	 * Get the server that this detail pane displays.
	 * @return ServerChannel object.
	 */
	public ServerChannel getServer() {
		return server;
	}
	
	/**
	 * Initialize components for the details pane.
	 */
	private void initialize() {
		hostNameLabel = new JLabel("Host name");
		hostNameField = new JTextField();
		hostNameField.addKeyListener(new CancelKeyListener());
		
		portLabel = new JLabel("Port");
		portField = new JTextField();
		portField.addKeyListener(new CancelKeyListener());
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 100;
		c.insets = new Insets(2, 10, 2, 10);
		c.anchor = GridBagConstraints.WEST;
		
		add(hostNameLabel, c);
		
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		
		add(portLabel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		
		add(hostNameField, c);
		
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		
		add(portField, c);
	}
	
	/**
	 * Refresh the component, displaying new items.
	 */
	public void refresh() {
		if (server == null) {
			hostNameField.setText("");
			portField.setText("");
		}
		else {
			hostNameField.setText(server.getHost());
			portField.setText(Integer.toString(server.getPort()));
		}
	}
	
	/**
	 * Display a server channel object.
	 * @param channel Server to communicate with.
	 */
	public void display(ServerChannel channel) {
		server = channel;
		
		refresh();
	}
	
	/**
	 * Override key handlers to prevent editing, but still allow selecting.
	 */
	private class CancelKeyListener implements KeyListener {
		@Override public void keyPressed(KeyEvent e) {
			e.consume();
		}

		@Override public void keyReleased(KeyEvent e) {
			e.consume();
		}

		@Override public void keyTyped(KeyEvent e) {
			e.consume();
		}
	}
}
