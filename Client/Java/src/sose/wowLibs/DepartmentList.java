package src.sose.wowLibs;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 * A pop-up selection list of departments available for searching.
 * A user can double-click or right-click on an entry and add it to the search bar.
 * 
 * @author Andy Sibley
 * 
 */
public class DepartmentList extends JFrame implements javax.swing.event.ListSelectionListener {
	
	/**
	 * Initialize pop-up frame.
	 * @param parent Grants access to parent functions and members.
	 */
	public DepartmentList(src.sose.wowBasicSearch.MainClientGUIBasic parent) {
		super("Department List");
		
		this.parent = parent;
		
		initComponents();
	}
	
	public DepartmentList(src.sose.wowAdminGui.MainAdminWindow parent) {
		super("Department List");
		
		this.aparent = parent;
		
		initComponents();
	}
	
	/**
	 * Handle list selection events.
	 */
	public void valueChanged(ListSelectionEvent l) {
		JList list = (JList)l.getSource();
		
		String dept = (String)list.getSelectedValue();
		
		if (dept.equalsIgnoreCase("ALL")) {
			dept = " ";
		}
		
		if (parent != null) {
			parent.searchTextBox.setText("@dept " + dept);
			parent.searchPrintSort();			
		}
		else {
			aparent.searchTextBox.setText("@dept " + dept);
			aparent.searchPrintSort();
		}
		
		this.dispose();
	}
	
	/**
	 * Initialize JFrame components.
	 */
	private void initComponents() {
		deptLabel = new JLabel();
		close = new JButton();
		listModel = new DefaultListModel();
		
		listModel.addElement("ALL");
		
		/*
		 * Will read from dept-list.txt to create a list of departments.
		 */
		java.io.File f = new java.io.File("dept-list.txt");
		try {
			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.FileReader(f));
			java.util.Scanner scan = new java.util.Scanner(reader);
			
			while (scan.hasNextLine()) {
				listModel.addElement(scan.nextLine());
			}
		}
		catch (java.io.FileNotFoundException sadface) {
			System.err.println(sadface);
			JOptionPane.showMessageDialog(null, "The file 'dept-list.txt' was not found.\n" + 
					"Please reinstall the WOW client.", "File not found.", JOptionPane.ERROR_MESSAGE);
		}
		
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(-1);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(10);
		
		scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createLineBorder(java.awt.Color.black, 1));
		
		close.setText("Close");
		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				closeButtonClicked(e);
			}
		});
		
		deptLabel.setText("Search by department...");
		
		/*this.add(deptLabel, java.awt.BorderLayout.PAGE_START);
		this.add(scrollPane, java.awt.BorderLayout.CENTER);
		this.add(close, java.awt.BorderLayout.PAGE_END);*/
		
		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
		}
		catch (java.io.IOException e) { 
			e.printStackTrace();
			System.err.println("(Unable to load images/WoWsuperSMALL.gif)");
		}
		
		//Set up layout
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutocreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(layout.createSequentialGroup()
										.addContainerGap()
										.add(deptLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
										.add(scrollPane)
										.add(close, org.jdesktop.layout.GroupLayout.CENTER, 50, Short.MAX_VALUE)
		)));
		layout.setVerticalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.add(deptLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
						.addContainerGap()
						.add(scrollPane)
						.addContainerGap()
						.add(close, org.jdesktop.layout.GroupLayout.CENTER, 30, 100))
		);
		pack(); // Adjust frame to fit the components.
	}
	
	/**
	*	Called when the user wants to close the window.
	*/
	private void closeButtonClicked(java.awt.event.ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}
	
	private JLabel deptLabel;
	private DefaultListModel listModel;
	private JScrollPane scrollPane;
	private JList list;
	private JButton close;
	
	private src.sose.wowBasicSearch.MainClientGUIBasic parent;
	private src.sose.wowAdminGui.MainAdminWindow aparent;
	
}
