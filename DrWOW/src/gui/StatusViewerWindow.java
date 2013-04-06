package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.*;

/**
 * Let the user view and interact with a GUI displaying the health of each server.
 */
public class StatusViewerWindow extends JFrame {
	private static final long serialVersionUID = 1L; 
	
	private ArrayList<ServerChannel> servers;
	
	//! Components !//
	private JTabbedPane serverListPane;
	private JTabbedPane detailsPane;
	private DetailsPane details;
	private JButton refreshButton;
	private JButton addServerButton;
	private JButton removeServerButton;
	private ServerTableModel model;
	
	/**
	 * Constructor for the GUI. Initializes and displays the window.
	 */
	public StatusViewerWindow() {
		super("Dr. WOW");
		
		initialize();
		
		servers = new ArrayList<ServerChannel>();
		final ArrayList<Server> serverList = Config.getServers();
		for (Server server : serverList) {
			ServerChannel serverChannel = ServerChannel.makeServerChannel(server.getHost(), server.getPort()); 
			servers.add(serverChannel);
		}
		
		new Refresher().start();
		
		setVisible(true);
	}
	
	/**
	 * Initialize the components used by this JFrame window. 
	 */
	private void initialize() {
		// Instantiate components.
		refreshButton = new JButton("Refresh");
		addServerButton = new JButton("Add Server");
		removeServerButton = new JButton("Remove Server");
		removeServerButton.setEnabled(false);
		
		model = new ServerTableModel();
		serverListPane = new JTabbedPane();
		
		// Set up components.
		final JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionListener listener = new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					removeServerButton.setEnabled(false);
					details.display(null);
					return;
				}
				
				removeServerButton.setEnabled(true);
				
				details.display(model.getRowObject(e.getFirstIndex()));
			}
		};
		
		model.setListListener(listener);
		table.setSelectionModel(model.getListModel());
		
		final JScrollPane pane = new JScrollPane(table);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		serverListPane.addTab("Servers", pane);
		
		details = new DetailsPane();
		detailsPane = new JTabbedPane();
		detailsPane.add("Details", details);
		
		refreshButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		
		// Menu bar.
		final JMenuBar menuBar = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem quitItem = new JMenuItem("Quit");
		
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		fileMenu.add(quitItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		// The window will organize itself according to the grid bag layout manager.
		setLayout(new GridBagLayout());
		
		// Set up the layout using the grid bag rules.
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridy 	= 0;
		c.gridx 	= 1;
		c.ipadx 	= 0;
		c.ipady 	= 0;
		c.gridwidth = 1;
		c.insets 	= new Insets(5, 5, 10, 10);
		c.anchor 	= GridBagConstraints.NORTHEAST;
		
		add(refreshButton, c);
		
		c.gridx 	= 0;
		c.gridy 	= 0;
		c.gridwidth = 2;
		c.ipadx 	= 200;
		c.ipady 	= -300;
		c.anchor 	= GridBagConstraints.NORTH;
		c.insets 	= new Insets(10, 10, 10, 10);
		
		add(serverListPane, c);
		
		c.gridx 	= 0;
		c.gridy 	= 1;
		c.ipadx 	= 200;
		c.ipady 	= 50;
		c.gridwidth = 2;
		c.fill 		= GridBagConstraints.HORIZONTAL;
		c.anchor 	= GridBagConstraints.SOUTH;
		c.insets 	= new Insets(10, 10, 10, 10);
		
		add(detailsPane, c);
		
		c.gridy 	= 2;
		c.gridwidth = 1;
		c.ipadx 	= 0;
		c.ipady 	= 0;
		c.fill 		= GridBagConstraints.NONE;
		c.anchor 	= GridBagConstraints.WEST;
		
		add(addServerButton, c);
		
		c.gridx 	= 1;
		
		add(removeServerButton, c);
		
		// Miscellaneous settings.
		// The program will shut down when the window is closed.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// The window will be resized according to the components on the frame.
		pack();
		
		setLocationRelativeTo(null);
	}
	
	/**
	 * Request information from the servers and replace the table with updated
	 * information.
	 */
	public void refresh() {
		model.clear();
		for (ServerChannel server : servers) {
			model.addServer(server, server.getHealthReport());
		}
	}
	
	/**
	 * The Refresher's job is to obtain the latest results 
	 */
	private final class Refresher extends Thread {
		public void run() {
			while (true) {
				refresh();
				
				try {
					sleep(30000);
				}
				catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}
