package src.sose.wowClientGui;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.LocalDA;
import src.sose.wowDA.MockDataAccess;
import src.sose.wowLibs.About;
import src.sose.wowLibs.EmailCollection;
import src.sose.wowLibs.GUIBase;
import src.sose.wowLibs.GeneralInfoWindow;
import src.sose.wowLibs.PopupMessage;
import src.sose.wowLibs.Readme;
import src.sose.wowLibs.WowHelper;

public class MainClientWindow extends GUIBase implements ActionListener, ClipboardOwner, Printable{

	private javax.swing.JButton searchButton;
	private javax.swing.JButton genInfoButton;
	private javax.swing.JButton printerButton;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu toolsMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem fileItem3;
	private javax.swing.JMenuItem toolsItem1;
	private javax.swing.JMenuItem toolsItem2;
	//private javax.swing.JMenuItem toolsItem3;
	private javax.swing.JMenuItem toolsItem4;
	private javax.swing.JMenuItem helpItem1;
	private javax.swing.JMenuItem helpItem3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	public javax.swing.JTextField searchTextBox;

	//*******************
	private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton expandButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JScrollPane jScrollPane2;
    private JButton returnButton;
    private JButton addPhoneButton;
    private JButton addEmailButton;
    private JButton addCustomButton;
    private JButton deleteSelectedButton;
   
    
	ArrayList<JTextField> names = new ArrayList<JTextField>();
	ArrayList<JTextField> values = new ArrayList<JTextField>();
	ArrayList<Field> fields = new ArrayList<Field>();
	ArrayList<JCheckBox> jCheckBoxes = new ArrayList<JCheckBox>();
	ArrayList<JLabel> validity = new ArrayList<JLabel>();
	boolean editingFlag = false;
	String[] tempEdit;


	public int phoneCount = 1;
	public int emailCount = 1;
	public int customCount = 0;
	ArrayList<JTextField> cType = new ArrayList<JTextField>();
	ArrayList<JTextField> cValue = new ArrayList<JTextField>();
	
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();

	EmailCollection ec = new EmailCollection();
	
    //**********************
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	MouseListener adminListener;
	MockDataAccess mda = new MockDataAccess();
	ArrayList<DataAccess> dataAccess = new  ArrayList<DataAccess>();
	DefaultTableModel model;
	private static int rowSelected = -1;//used for right click menu
	boolean[] ascend = {false, true, true, true};//used for table sorting
	boolean isNewSearch = true;
	static int count = 0;
	
	
	public static boolean adminState;
	public ArrayList<String> serverNames = new ArrayList<String>();
	
	
	ImageIcon redStar;
	ImageIcon redDot;
	ImageIcon greenDot;
	ImageIcon titlebarIcon;
	ImageIcon printerIcon;
	ImageIcon giIcon;
	ImageIcon rightArrows;
	ImageIcon leftArrows;
	
	public String version = "1.0";
	
	public MainClientWindow() {
//		try {
//		      UIManager.setLookAndFeel(new SubstanceLookAndFeel());
//		    } catch (UnsupportedLookAndFeelException ulafe) {
//		      System.out.println("Substance failed to set");
//		    }
//		SubstanceLookAndFeel.setCurrentTheme("org.jvnet.substance.theme.SubstanceSteelBlueTheme");
		initComponents();
		dataAccess.add(new LocalDA("wow.xml"));
		dataAccess.addAll(WowHelper.getServerList());
	}

	//************************************Building Main Window******************************************
	private void initComponents() {
		
		setImages();
		adminState = false;
		searchButton = new javax.swing.JButton();
		genInfoButton = new javax.swing.JButton();
		printerButton = new javax.swing.JButton();
		searchTextBox = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		fileItem3 = new javax.swing.JMenuItem();
		toolsMenu = new javax.swing.JMenu();
		toolsItem1 = new javax.swing.JMenuItem();
		toolsItem2 = new javax.swing.JMenuItem();
//		toolsItem3 = new javax.swing.JMenuItem();
		toolsItem4 = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		helpItem1 = new javax.swing.JMenuItem();
		helpItem3 = new javax.swing.JMenuItem();
		jPanel1 = new javax.swing.JPanel();
		expandButton = new javax.swing.JButton();
		
		
		this.setIconImage(titlebarIcon.getImage());
		createPopupMenu();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Window on the World - v." + version);
		searchTextBox.setText("Type WoW search here...");
		searchTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				jTextField1KeyPressed(evt);
			}
		});

		searchTextBox.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				searchTextBoxMousePressed(evt);
			}
		});
		
		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				blankRows, columnNames
		));

		jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTable1MouseClicked(evt);
			}
		});


		JTableHeader header =  jTable1.getTableHeader();
		setTableHeaderRenderer(jTable1);
		
		header.addMouseListener(new ColumnHeaderListener());

		jScrollPane1.setViewportView(jTable1);

		searchButton.setText("Search");
		searchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchButtonActionPerformed(evt);
			}
		});

		genInfoButton.setIcon(giIcon);
		genInfoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				genInfoButtonActionPerformed(evt);
			}
		});
		genInfoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		genInfoButton.setContentAreaFilled(false);
		genInfoButton.setBorder(new EmptyBorder(genInfoButton.getInsets()));
		genInfoButton.setToolTipText("General Info Page");
		
		printerButton.setIcon(printerIcon);
		printerButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				printerButtonActionPerformed(evt);
			}
		});
		printerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		printerButton.setContentAreaFilled(false);
		printerButton.setBorder(new EmptyBorder(printerButton.getInsets()));
		printerButton.setToolTipText("Print Full Contact List");
		
		expandButton.setIcon(rightArrows);
        expandButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				expandButtonActionPerformed(evt);
			}
		});
		
		fileMenu.setText("File");
		fileItem3.setText("Close");
		fileMenu.add(fileItem3);
		fileItem3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				fileItem3MouseReleased(evt);
			}
		});

		menuBar.add(fileMenu);

		toolsMenu.setText("Tools");
		toolsItem1.setText("Print Server Contact List");
		toolsItem1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem1MouseReleased(evt);
			}
		});
		toolsMenu.add(toolsItem1);
		
		toolsItem2.setText("Administrate Local DB");
		toolsItem2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem2MouseReleased(evt);
			}
		});
		toolsMenu.add(toolsItem2);
		
//		toolsItem3.setText("Open List Manager");
//		toolsItem3.addMouseListener(new java.awt.event.MouseAdapter() {
//			public void mouseReleased(java.awt.event.MouseEvent evt) {
//				toolsItem3MouseReleased(evt);
//			}
//		});
//		toolsMenu.add(toolsItem3);
		
		toolsItem4.setText("Print Search Results");
		toolsItem4.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem4MouseReleased(evt);
			}
		});
		toolsMenu.add(toolsItem4);
		
		menuBar.add(toolsMenu);

		helpMenu.setText("Help");
		helpItem1.setText("View Readme");
		helpMenu.add(helpItem1);
		helpItem1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				openReadmeMouseReleased(evt);
			}
		});

		helpItem3.setText("About");
		helpMenu.add(helpItem3);
		helpItem3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				aboutMouseReleased(evt);
			}
		});

		menuBar.add(helpMenu);
	        
		JMenu printIcon = new JMenu();
		printIcon.setIcon(printerIcon);
		printIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		printIcon.setToolTipText("Print Full Contact List");
		printIcon.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				printMousePressed(evt);
			}
		});
		
//		JMenu blank = new JMenu("                                                           " +
//				"                                                                           ");
//		blank.setEnabled(false);
		//menuBar.add(blank);
		menuBar.add(printIcon);
		setJMenuBar(menuBar);
		
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(searchButton))
                        .add(141, 141, 141)
                        .add(genInfoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 503, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genInfoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchButton)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pack();
        
       resizeTable(jTable1);
	}                       
	//************************************End Building Main Window******************************************
	
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainClientWindow().setVisible(true);
			}
		});
	}              

	//**********************************Working with Popup Menu***************************************
	public void createPopupMenu() {
		JMenuItem menuItem;

		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("Open in new Info Window");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		menuItem = new JMenuItem("Add to Email Collection");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		JPopupMenu.Separator sep = new JPopupMenu.Separator();
		popup.add(sep);
		
		menuItem = new JMenuItem("Copy Email to Clipboard");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		JPopupMenu.Separator sep2 = new JPopupMenu.Separator();
		popup.add(sep2);
		
		menuItem = new JMenuItem("Add to Local DB");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		MouseListener popupListener = new PopupListener(popup);
		jTable1.addMouseListener(popupListener);
	}
	
	public void createPopupMenu2() {
		JMenuItem menuItem;

		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("Copy Email to Clipboard");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		JPopupMenu.Separator sep = new JPopupMenu.Separator();
		popup.add(sep);
		
		menuItem = new JMenuItem("Add to Email Collection");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		JPopupMenu.Separator sep2 = new JPopupMenu.Separator();
		popup.add(sep2);
		
		menuItem = new JMenuItem("Cancel");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		MouseListener popupListener = new AdminPopupListener(popup);
		adminListener = popupListener;
		jScrollPane2.addMouseListener(adminListener);
	}
	
	public void searchPrintSort()
	{
		printTable(search(searchTextBox.getText(), dataAccess), jTable1);
		isNewSearch = true;
		sortTable(defaultSortColumn, jTable1, ascend, isNewSearch, getDefaultTableModel(jTable1));
		isNewSearch = false;
		jTable1.setDefaultRenderer(Object.class, new
				AttributiveCellRenderer());
		
	}
	
	public void searchPrintSort(String s)
	{
		printTable(search(s, dataAccess), jTable1);
		isNewSearch = true;
		sortTable(defaultSortColumn, jTable1, ascend, isNewSearch, getDefaultTableModel(jTable1));
		isNewSearch = false;
		jTable1.setDefaultRenderer(Object.class, new
				AttributiveCellRenderer());
		
	}
	//************************************************************************************************
	//************************************Listeners***************************************************
	//************************************************************************************************
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Open in new Info Window"))
		{
			int[] selectedRows = jTable1.getSelectedRows();

			for(int i : selectedRows)
			{
				if(rowSelected != -1)
				{
					if(jTable1.getValueAt(0, 0) != null)
					{
						if(!((String)jTable1.getValueAt(0, 0)).equals("No Results Found"))
						{
							expandInfo(jTable1, i);
						}
					}
				}
			}
		}
		else if(e.getActionCommand().equals("Add to Email Collection"))
		{
			int[] selectedRows = jTable1.getSelectedRows();

			for(int i : selectedRows)
			{
				if(rowSelected != -1)
				{
					if(jTable1.getValueAt(0, 0) != null)
					{
						if(!((String)jTable1.getValueAt(0, 0)).equals("No Results Found"))
						{
							if(!((String)jTable1.getValueAt(i, 3)).equals(""))
							{
								Person tempP = getPersonAtRow(i, jTable1);
								if(!GUIBase.listPeople.contains(tempP))
								{
									GUIBase.listPeople.add(tempP);
									ec.addEmail(tempP.getEmail());
								}
							}
						}
					}
				}
			}
			Point lowerRightCorn = new Point((this.getX()+this.getWidth()-ec.getWidth()), this.getY()+this.getHeight());
			ec.setLocation(lowerRightCorn);
			ec.setVisible(true);
		}
		else if(e.getActionCommand().equals("Add to Local DB"))
		{
			PopupMessage notify;
			if(jTable1.getValueAt(0, 0) != null)
			{
				Person p = getPersonAtRow(rowSelected, jTable1);
				p = clearEmptyFields(p);
				if(!p.getServer().equals("LOC"))
				{
					p.setServer("LOC");

					if(!p.add(dataAccess).equals("OK"))
					{
						notify = new PopupMessage("Error: Cannot Add Record", true);
					}
					else
					{
						notify = new PopupMessage("Record Added Successfully", true);
						searchPrintSort();

					}
				}
				else
				{
					notify = new PopupMessage("Record Already Exists", true);
				}
				notify.setLocationRelativeTo(this);
				notify.setVisible(true);
			}
		}
		else if(e.getActionCommand().equals("Copy Email to Clipboard"))
		{
			if(jTable1.getValueAt(0, 0) != null)
			{
				Person p = getPersonAtRow(rowSelected, jTable1);
				copyEmailsToClipBoard(p.getEmail());
			}
		}
	}
	
	static class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) { 	
				double yLoc = e.getPoint().getY();
				int row = (int)(yLoc/((JTable)(e.getComponent())).getRowHeight());
				rowSelected = row;
				int[] selected = ((JTable)(e.getComponent())).getSelectedRows();
				boolean isSelected = false;
				for(int i : selected)
				{
					if(i==row)
					{
						isSelected = true;
						break;
					}
				}

				if(!isSelected)
				{
					((JTable)(e.getComponent())).getSelectionModel().setSelectionInterval( row, row);
				}

				popup.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}
	
	static class AdminPopupListener extends MouseAdapter {
		JPopupMenu popup;

		AdminPopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) { 	
				popup.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}
	
	private void genInfoButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			
			if(numGIWindowsOpen == 0)
			{
				GeneralInfoWindow gi = new GeneralInfoWindow();
				Point lowerRightCorn = new Point((this.getX()), this.getY()+this.getHeight());
				gi.setLocation(lowerRightCorn);
				gi.setVisible(true);
			}
		}
		else
		{
			if(numGIWindowsOpen == 0)
			{
				GeneralInfoWindow gi = new GeneralInfoWindow();
				Point maxBtmRightCorn = new Point((jScrollPane1.getX()+jScrollPane1.getWidth()-gi.getWidth()), jScrollPane1.getY()+jScrollPane1.getHeight()+53);
				gi.setLocation(maxBtmRightCorn);
				gi.setAlwaysOnTop(true);
				gi.setVisible(true);
			}
		}
	}
	
	private void printerButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		
	}
	
	private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
			searchPrintSort();
	}

	private void searchTextBoxMousePressed(java.awt.event.MouseEvent evt) {
		searchTextBox.selectAll();
	}

//	private void customBoxMousePressed(java.awt.event.MouseEvent evt) {
//		if(((JTextField)(evt.getSource())).getText().contains("Custom"))
//		{
//			((JTextField)(evt.getSource())).setText("");
//		}
//		else
//		{
//			if(!((JTextField)(evt.getSource())).getSelectedText().equals(((JTextField)(evt.getSource())).getText()))
//			{
//				((JTextField)(evt.getSource())).selectAll();
//			}
//			
//		}
//	}
	
//	private void notAvailableYetMouseReleased(java.awt.event.MouseEvent evt) {
//		PopupMessage newMessage = new PopupMessage();
//		newMessage.setLocationRelativeTo(this);
//		newMessage.setVisible(true);
//	}
			
	private void openReadmeMouseReleased(java.awt.event.MouseEvent evt) {
		Readme newReadme = new Readme();
		newReadme.setLocationRelativeTo(null);
		newReadme.setVisible(true);
	}

	
	private void adminPanelMouseClicked(java.awt.event.MouseEvent evt) 
	{
		
	}
	
	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			JTable target = (JTable)evt.getSource();
			rowSelected = target.getSelectedRow();
			if(!((String)jTable1.getValueAt(0, 0)).equals("No Results Found"))
			{
				if(adminState)
				{
					if(!editingFlag)
					{
						//setBaseState();
						if(clearEmptyFields(getPersonAtRow(rowSelected, jTable1)).getServer().equals("LOC"))
						{
							phoneCount = clearEmptyFields(getPersonAtRow(rowSelected, jTable1)).getPhoneList().size();
							emailCount = clearEmptyFields(getPersonAtRow(rowSelected, jTable1)).getEmailList().size();
							customCount = clearEmptyFields(getPersonAtRow(rowSelected, jTable1)).getFields().size();
							deleteButton.setEnabled(true);
							editButton.setEnabled(true);
						}
						else
						{
							deleteButton.setEnabled(false);
							editButton.setEnabled(false);
						}

						populateAdminBoxes(getFieldData(clearEmptyFields(getPersonAtRow(rowSelected, jTable1))), false);
					}
					else
					{
						setBaseState();
						jTable1MouseClicked(evt);
					}
				}
				else
				{
					expandInfo(jTable1, rowSelected);
				}
			}
		}
	}

	private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {                                       
		if(evt.getKeyCode() ==  java.awt.event.KeyEvent.VK_ENTER)
		{
				searchPrintSort();
		}
	} 

	private void fileItem3MouseReleased(java.awt.event.MouseEvent evt) {
		System.exit(1);
	}
	
	private void toolsItem1MouseReleased(java.awt.event.MouseEvent evt) {
		printAllRecords(jTable1, dataAccess, isNewSearch);
		searchPrintSort();
	}
	
	private void toolsItem2MouseReleased(java.awt.event.MouseEvent evt) {
		String s = searchTextBox.getText();
		if(adminState)
		{	
			if(jTable1.getValueAt(0, 1) == null)
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);

			}
			else
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);
				if(!searchTextBox.getText().equals("Lastname Firstname"))
				{
					searchPrintSort();
				}
			}
			toolsItem2.setText("Administrate Local DB");
		}
		else
		{
			localAdminView2();
			setBaseState();
			toolsItem2.setText("Return to Search");
		}
	}
	
//	private void toolsItem3MouseReleased(java.awt.event.MouseEvent evt) {
//		ListManagerWindow lmw = new ListManagerWindow();
//		Point upperRightCorn = new Point((this.getX()+this.getWidth()), this.getY());
//		lmw.setLocation(upperRightCorn);
//		lmw.setSize(400, 550);
//		lmw.setVisible(true);
//	}
	
	private void toolsItem4MouseReleased(java.awt.event.MouseEvent evt) {
		ArrayList<String[]> temp = WowHelper.formatPeopleForPrinting(searchResults);
		
		printReport(temp, jTable1, false);
		
		searchPrintSort();
	}
	
	private void aboutMouseReleased(java.awt.event.MouseEvent evt) {
		About newAbout = new About();
		newAbout.setLocationRelativeTo(null);
		newAbout.setVisible(true);
	}
	
	private void printMousePressed(java.awt.event.MouseEvent evt) {
		JMenu menu = (JMenu)evt.getSource();
		if(menu.isSelected())
		{
			menu.setSelected(false);
		}
		
		printAllRecords(jTable1, dataAccess, isNewSearch);
		searchPrintSort();
	}
	
	//***************************************************************************************
	public void localAdminView(){
		adminState = true;
        statusLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        returnButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        expandButton = new javax.swing.JButton();
        addPhoneButton = new javax.swing.JButton();
        addEmailButton = new javax.swing.JButton();
        addCustomButton = new javax.swing.JButton();
        deleteSelectedButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        
        statusLabel.setBackground(new java.awt.Color(51, 255, 51));
        statusLabel.setIcon(greenDot);
        statusLabel.setText("Read Only");
	    
	    addButton.setText("Add New");
        addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

        editButton.setText("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				editButtonActionPerformed(evt);
			}
		});

        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});
        saveButton.setEnabled(false);

        cancelButton.setText("Cancel");
        cancelButton.setEnabled(true);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
        
        addPhoneButton.setText("Add Phone");
        addPhoneButton.setVisible(false);
        addPhoneButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addPhoneButtonActionPerformed(evt);
			}
		});
        addEmailButton.setText("Add Email");
        addEmailButton.setVisible(false);
        addEmailButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addEmailButtonActionPerformed(evt);
			}
		});        
        addCustomButton.setText("Add Custom");
        addCustomButton.setVisible(false);
        addCustomButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addCustomButtonActionPerformed(evt);
			}
		});
        
        deleteSelectedButton.setText("Delete Selected");
        deleteSelectedButton.setVisible(false);
        deleteSelectedButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        deleteSelectedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteSelectedButtonActionPerformed(evt);
			}
		});
        
        returnButton.setText("<< Return To Search");
        returnButton.setEnabled(true);
        returnButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				returnButtonActionPerformed(evt);
			}
		});
        
		expandButton.setIcon(leftArrows);
        expandButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				expandButtonActionPerformed(evt);
			}
		});
	}
	
	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		setBaseState();
		for(JTextField jtf: values)
		{
			jtf.setText("");
			jtf.setEditable(true);
			jtf.setColumns(12);
		}
	    
		for(JTextField jtf: names)
		{
			jtf.setEditable(true);
		}
		
		names.get(0).setEditable(false);
		names.get(1).setEditable(false);
		
	    
        statusLabel.setIcon(redDot);
	    statusLabel.setText("Adding New Record");
        
	    saveButton.setEnabled(true);
	    cancelButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addPhoneButton.setVisible(true);
        //deleteSelectedButton.setVisible(true);
        addEmailButton.setVisible(true);
        addCustomButton.setVisible(true);
	    editingFlag = true;
	}
	
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		setBaseState();
	}
	
	private void returnButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		String s = searchTextBox.getText();
		if(adminState)
		{	
			if(jTable1.getValueAt(0, 1) == null)
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);

			}
			else
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);
				if(!searchTextBox.getText().equals("Lastname Firstname"))
				{
					searchPrintSort();
				}
			}

		}
		else
		{
			localAdminView2();
			setBaseState();
			
		}
	}
	
	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		PopupMessage notify = new PopupMessage();
		if(statusLabel.getText().equals("Adding New Record"))
		{
			boolean[] valid = checkTextBoxesValidity();
	        boolean validFlag = markInvalid(valid, false);
			
			if(validFlag)
			{
				Person newP = convertTextBoxesToPerson();
				newP = clearEmptyFields(newP);
				if(newP.add(dataAccess).equals("OK"))
				{
					notify = new PopupMessage("Record Added Successfully", true);
				}
				else
				{
					notify = new PopupMessage("ERROR: Record Already Exists", true);
				}
				
				searchTextBox.setText(values.get(1).getText() + " " + values.get(0).getText());
				setBaseState();
					searchPrintSort();
			}
			else
			{
				notify = new PopupMessage("ERROR: Problems with entry", true);
			}
			
		}
		else if(statusLabel.getText().equals("Editing Record"))
		{
			boolean[] valid = checkTextBoxesValidity();
	        boolean validFlag = markInvalid(valid, true);
	        
			if(validFlag)
			{
				ArrayList<Field> tempFields = makeFieldsFromBoxes();
				populateAdminBoxes(tempFields, true);
				
				Person newP = convertTextBoxesToPerson();
				newP = clearEmptyFields(newP);
				if(newP.update(dataAccess).equals("OK"))
				{
					notify = new PopupMessage("Record Updated Successfully", true);
				}
				else
				{
					notify = new PopupMessage("ERROR: Record Not Found", true);
				}
				
				searchTextBox.setText(values.get(1).getText() + " " + values.get(0).getText());
				setBaseState();
					searchPrintSort();
			}
			else
			{
				notify = new PopupMessage("ERROR: Problems with entry", true);
				//do it again
			}
		}
		
		notify.setLocationRelativeTo(this);
		notify.setVisible(true);
		
	}
	
	private void editButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		if(!editingFlag)
		{
			editingFlag = true;
			setTextBoxesEditable();
		
			statusLabel.setIcon(redDot);
			statusLabel.setText("Editing Record");
		
			saveButton.setEnabled(true);
			cancelButton.setEnabled(true);
			deleteButton.setEnabled(false);
	        addPhoneButton.setVisible(true);
	        deleteSelectedButton.setVisible(true);
	        addEmailButton.setVisible(true);
	        addCustomButton.setVisible(true);
	        
			ArrayList<Field> tempFields = makeFieldsFromBoxes();
			populateAdminBoxes(tempFields, true);
			setTextBoxesEditable();
			
			names.get(0).setEditable(false);
			names.get(1).setEditable(false);
			values.get(0).setEditable(false);
			values.get(1).setEditable(false);
		}
	}
	
	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		if(!editingFlag)
		{
			editingFlag = true;
			
			Person temp = getPersonAtRow(rowSelected, jTable1);
			if(deleteConfirm(temp.getFirstName() + " " + temp.getLastName()))
			{  	
				PopupMessage notify;

				if(temp.delete(dataAccess).equals("OK"))
				{
					notify = new PopupMessage("Record Deleted Successfully", true);
					refreshTable(jTable1, searchTextBox, searchTextBox.getText(), dataAccess);
				}
				else
				{
					notify = new PopupMessage("ERROR: Record Not Found", true);
				}

				notify.setLocationRelativeTo(this);
				notify.setVisible(true);

			}
			else 
			{  	
				PopupMessage notify = new PopupMessage("Record has not been deleted", true);
				notify.setLocationRelativeTo(this);
				notify.setVisible(true);
			}
			
			setBaseState();
			//clearTable();
		}
	}
	
	private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		String s = searchTextBox.getText();
		if(adminState)
		{	
			if(jTable1.getValueAt(0, 1) == null)
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);

			}
			else
			{
				getContentPane().removeAll();
				initComponents();
				searchTextBox.setText(s);
				if(!searchTextBox.getText().equals("Lastname Firstname"))
				{
					searchPrintSort();
				}
			}

		}
		else
		{
			localAdminView2();
			setBaseState();
			
		}
		
	}
	private void addPhoneButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		phoneCount++;
		ArrayList<Field> tempFields = makeFieldsFromBoxes();
		tempFields.add(1+phoneCount, new Field("Phone" + Integer.toString(phoneCount), ""));
		
		if(statusLabel.getText().equals("Editing Record"))
		{
			populateAdminBoxes(tempFields, true);
		}
		else
		{
			populateAdminBoxes(tempFields, false);
		}
		
		setTextBoxesEditable();
		
	}
	private void addEmailButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		emailCount++;
		ArrayList<Field> tempFields = makeFieldsFromBoxes();
		tempFields.add(1+emailCount+phoneCount, new Field("Email" + Integer.toString(emailCount), ""));
		
		if(statusLabel.getText().equals("Editing Record"))
		{
			populateAdminBoxes(tempFields, true);
		}
		else
		{
			populateAdminBoxes(tempFields, false);
		}
		setTextBoxesEditable();
	}
	
	private void addCustomButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		customCount++;
		ArrayList<Field> tempFields = makeFieldsFromBoxes();
		tempFields.add(1+emailCount+phoneCount+customCount, new Field("Custom" + Integer.toString(customCount), ""));
		
		if(statusLabel.getText().equals("Editing Record"))
		{
			populateAdminBoxes(tempFields, true);
		}
		else
		{
			populateAdminBoxes(tempFields, false);
		}
		setTextBoxesEditable();
	}
	
	private void deleteSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		ArrayList<Integer> indicies = new ArrayList<Integer>();
		ArrayList<Field> tempFields = new ArrayList<Field>();
		Field placeHolder = new Field("delete", "this");
		int tempPhoneCount = phoneCount;
		int tempEmailCount = emailCount;
		
		for(JCheckBox jcb : jCheckBoxes)
		{
			if(jcb.isSelected())
			{
				indicies.add(new Integer(jCheckBoxes.indexOf(jcb)));
			}
		}
		
		if(indicies.size() > 0)
		{
			tempFields = makeFieldsFromBoxes();
			
			for(Integer index : indicies)
			{
				tempFields.set(index, placeHolder);
				
				if(index > 1 && index < 2+tempPhoneCount)
				{
					phoneCount--;
				}
				else if(index > 1+tempPhoneCount && index < 2+tempPhoneCount+tempEmailCount)
				{
					emailCount--;
				}
				else if(index > 1+tempPhoneCount+tempEmailCount)
				{
					customCount--;
				}
			}
			
			while(tempFields.contains(placeHolder))
			{
				tempFields.remove(placeHolder);
			}
			populateAdminBoxes(tempFields, true);
			
			for(JCheckBox jcb : jCheckBoxes)
			{
				jcb.setSelected(false);
			}
			setTextBoxesEditable();
		}
	}
	//**********************************Working with Text Boxes***************************************
	public void populateTextBoxes(String[] data) {
		for(int i=0;i<values.size();i++)
		{
			values.get(i).setText(data[i]);
		}
	}
	
	public void setBaseState()
	{
        statusLabel.setBackground(new java.awt.Color(51, 255, 51));
        statusLabel.setIcon(greenDot);
        statusLabel.setText("Read Only");
    	
        populateAdminBoxes(getBlankFieldData(), false);
		for(JTextField jl: names)
		{
			jl.setBorder(new EmptyBorder(jl.getInsets()));
			jl.setEditable(false);
			jl.setColumns(8);
		}

		for(JTextField jtf: values)
		{
			jtf.setText("");
			jtf.setEditable(false);
			jtf.setColumns(12);
		}
	    
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        addPhoneButton.setVisible(false);
        deleteSelectedButton.setVisible(false);
        addEmailButton.setVisible(false);
        addCustomButton.setVisible(false);
	    
        editingFlag = false;
        phoneCount = 1;
        emailCount = 1;
        customCount = 0;
	}
	
	public void setTextBoxesEditable()
	{
		for(JTextField jtf: values)
		{
			jtf.setEditable(true);
		}
		
		for(JTextField jtf: names)
		{
			jtf.setEditable(true);
		}
		
		names.get(0).setEditable(false);
		names.get(1).setEditable(false);
	}
	
	public boolean[] checkTextBoxesValidity()
	{
		boolean[] valid = new boolean[2+phoneCount+emailCount];
		
		for(int i=0;i<valid.length;i++)
		{
			valid[i] = false;
		}
		
		Person testingPerson = new Person();
		
		if(testingPerson.setFirstName(values.get(0).getText()) == 0){valid[0] = true;};
		if(testingPerson.setLastName(values.get(1).getText()) == 0){valid[1] = true;};
		
		if(phoneCount > 0)
		{
			for(int i=2;i<phoneCount+2;i++)
			{
				if(testingPerson.addPhone(values.get(i).getText()) == 0){valid[i] = true;};
			}
		}
		
		if(emailCount > 0)
		{
			for(int i=(2+phoneCount);i<emailCount+phoneCount+2;i++)
			{
				if(testingPerson.addEmail(values.get(i).getText()) == 0){valid[i] = true;};
			}
		}
		
		return valid;
	}
	
	public boolean markInvalid(boolean[] valid, boolean editing)
	{
		validity.clear();
		for(boolean b : valid)
		{
			if(!b)
			{
				validity.add(new JLabel("", redStar, SwingConstants.RIGHT));
			}
			else
			{
				validity.add(new JLabel(""));
			}
			
		}
		
		ArrayList<Field> tempFields = makeFieldsFromBoxes();
		populateAdminBoxes(tempFields, editing);
		setTextBoxesEditable();
        GridBagConstraints c = new GridBagConstraints();
        
		int validFlag = 0;
		int iter=0;
		for(boolean b : valid)
		{
			if(!b)
			{
				validFlag++;
			}
				c.insets = new Insets(5,0,10,0);
				c.weightx = 0.0;
				c.anchor = GridBagConstraints.LINE_END;
				c.fill = GridBagConstraints.NONE;
				c.weightx = 0.0;
				
				if(editing)
				{
					c.gridx = 4;
				}
				else
				{
					c.gridx = 3;
				}
				
				c.gridy = iter+1;

				jPanel2.add(validity.get(iter), c);
				
			iter++;
		}
		jPanel2.revalidate();
		
		if(validFlag == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Person convertTextBoxesToPerson()
	{
		Person newPerson = new Person();
		
		newPerson.setServer("LOC");
		newPerson.setFirstName(values.get(0).getText().trim());
		newPerson.setLastName(values.get(1).getText().trim());
		
		for(int i=2;i<phoneCount+2;i++)
		{
			newPerson.addPhone(names.get(i).getText().trim(), values.get(i).getText().trim());
		}
		
		for(int i=(2+phoneCount);i<emailCount+phoneCount+2;i++)
		{
			newPerson.addEmail(names.get(i).getText().trim(), values.get(i).getText().trim());
		}
			
		ArrayList<Field> tempFields = new ArrayList<Field>();
		
		for(int i=(2+phoneCount+emailCount);i<names.size();i++)
		{
			if(!names.get(i).equals(""))
			{
				if(!values.get(i).equals(""))
				{
					tempFields.add(new Field(names.get(i).getText(), values.get(i).getText()));
				}
			}
		}
		newPerson.setFields(tempFields);
		
		return newPerson;
	}
	//*********************************End working with Text Boxes************************************
	public void localAdminView2()
	{
		localAdminView();
		createPopupMenu2();
		getContentPane().removeAll();
    	
		setBaseState();
    	
        fields.clear();
    	fields.add(new Field("First Name", ""));
    	fields.add(new Field("Last Name", ""));
    	fields.add(new Field("Phone Num.", ""));
    	fields.add(new Field("Email Address", ""));
    	
    	names.clear();
    	values.clear();
    	
    	for(Field f : fields)
    	{
    		JTextField tempLabel = new JTextField(f.getName());
    		tempLabel.setBorder(new EmptyBorder(tempLabel.getInsets()));
    		tempLabel.setEditable(false);
    		names.add(tempLabel);
    		JTextField tempField = new JTextField(f.getValue());
    		tempField.setEditable(false);
    		values.add(tempField);
    	}


    	jPanel3.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();

    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
    	c.insets = new Insets(10,10,10,0);  //padding
    	jPanel3.add(statusLabel, c);

    	for(int i=0; i<names.size();i++)
    	{
    		c.insets = new Insets(5, 28, 10, 20);
    		c.gridx = 0;
    		c.gridy = i+1;
    		c.anchor = GridBagConstraints.LINE_START;
    		jPanel3.add(names.get(i), c);

    		c.insets = new Insets(5,0,10,20);
    		c.weightx = 0.0;
    		c.anchor = GridBagConstraints.LINE_END;
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.weightx = 0.5;
    		c.gridx = 1;
    		c.gridy = i+1;
    		jPanel3.add(values.get(i), c);
    	}
    	
        jScrollPane2.setViewportView(jPanel3);
		jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				adminPanelMouseClicked(evt);
			}
		});
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //*******************************End Admin Panel Layout*********************************
        
        //*******************************Main Layout*********************************************
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(searchButton)
                            .add(searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 124, Short.MAX_VALUE)
                        .add(genInfoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .add(30, 30, 30)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(returnButton)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 301, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(editButton)
                        .add(addButton)
                        .add(deleteButton)
                        .add(saveButton)
                        .add(cancelButton))
                    .add(addCustomButton)
                    .add(addEmailButton)
                    .add(addPhoneButton)
                    .add(deleteSelectedButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 19, Short.MAX_VALUE)
                .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(new java.awt.Component[] {addButton, cancelButton, deleteButton, editButton, saveButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {addCustomButton, addEmailButton, addPhoneButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(genInfoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(34, 34, 34)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(8, 8, 8)
                                .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(34, 34, 34)
                                .add(addPhoneButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addEmailButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addCustomButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deleteSelectedButton)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 13, Short.MAX_VALUE)
                        .add(saveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchButton))
                    .add(jScrollPane2, 0, 0, Short.MAX_VALUE))
                .add(9, 9, 9)
                .add(returnButton)
                .addContainerGap())
        );
        pack();
	}
	 
	 public void populateAdminBoxes(ArrayList<Field> fields, boolean editing)
	 {
		 if(!editing)
		 {
			 jPanel3.removeAll();
			 jPanel3.repaint();
			 jPanel1.removeAll();
			 jPanel1.repaint(); 
			 jPanel2.removeAll();
			 jPanel2.repaint();
			 names.clear();
			 values.clear();
				
				for(Field f : fields)
				{
					JTextField tempLabel = new JTextField(f.getName());
					tempLabel.setBorder(new EmptyBorder(tempLabel.getInsets()));
					tempLabel.setEditable(false);
					addAdminListener(tempLabel);
					names.add(tempLabel);
					JTextField tempField = new JTextField(f.getValue());
					tempField.setEditable(false);
					tempField.setColumns(12);
					addAdminListener(tempField);
					values.add(tempField);
					jCheckBoxes.add(new JCheckBox());
				}


				jPanel1.setLayout(new GridBagLayout());
				jPanel2.setLayout(new GridBagLayout());
				jPanel3.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();

				c.gridx = 0;
				c.gridy = 0;
				c.insets = new Insets(10,10,0,0);  //padding
				jPanel1.add(statusLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				c.weightx = 1.0;
				c.anchor = GridBagConstraints.LINE_END;
				jPanel1.add(new JLabel(""), c);
				
				c = new GridBagConstraints();
				for(int i=0; i<names.size();i++)
				{
					c.insets = new Insets(5, 10, 10, 20);
					c.gridx = 1;
					c.gridy = i+1;
					c.anchor = GridBagConstraints.LINE_START;
					jPanel2.add(names.get(i), c);

					c.insets = new Insets(5,0,10,10);
					c.weightx = 0.0;
					c.anchor = GridBagConstraints.LINE_END;
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;
					c.gridx = 2;
					c.gridy = i+1;
					jPanel2.add(values.get(i), c);
				}
				c.gridx = 0;
				c.gridy = 0;
				jPanel3.add(jPanel1, c);
				c.gridx = 0;
				c.gridy = 1;
				c.fill = GridBagConstraints.BOTH;
				jPanel3.add(jPanel2, c);
				
				validate();
				pack();
		 }
		 else
		 {
			 jPanel3.removeAll();
			 jPanel3.repaint();
			 jPanel1.removeAll();
			 jPanel1.repaint(); 
			 jPanel2.removeAll();
			 jPanel2.repaint();
			 names.clear();
			 values.clear();
				
				for(Field f : fields)
				{
					JTextField tempLabel = new JTextField(f.getName());
					tempLabel.setBorder(new EmptyBorder(tempLabel.getInsets()));
					addAdminListener(tempLabel);
					names.add(tempLabel);
					JTextField tempField = new JTextField(f.getValue());
					tempField.setEditable(false);
					tempField.setColumns(12);
					addAdminListener(tempField);
					values.add(tempField);
					jCheckBoxes.add(new JCheckBox());
				}


				jPanel1.setLayout(new GridBagLayout());
				jPanel2.setLayout(new GridBagLayout());
				jPanel3.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();

				c.gridx = 0;
				c.gridy = 0;
				c.insets = new Insets(10,10,0,0);  //padding
				jPanel1.add(statusLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				c.weightx = 1.0;
				c.anchor = GridBagConstraints.LINE_END;
				jPanel1.add(new JLabel(""), c);
				
				c = new GridBagConstraints();
				for(int i=0; i<names.size();i++)
				{
					c.insets = new Insets(5, 10, 10, 5);
					c.gridx = 0;
					c.gridy = i+1;
					c.anchor = GridBagConstraints.LINE_START;
					jPanel2.add(jCheckBoxes.get(i), c);
					if(i<2){jCheckBoxes.get(i).setVisible(false);}
					
					c.insets = new Insets(5, 5, 10, 20);
					c.gridx = 1;
					c.gridy = i+1;
					jPanel2.add(names.get(i), c);

					c.insets = new Insets(5,0,10,10);
					c.weightx = 0.0;
					c.anchor = GridBagConstraints.LINE_END;
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;
					c.gridx = 2;
					c.gridy = i+1;
					jPanel2.add(values.get(i), c);
				}
				c.gridx = 0;
				c.gridy = 0;
				jPanel3.add(jPanel1, c);
				c.gridx = 0;
				c.gridy = 1;
				c.fill = GridBagConstraints.BOTH;
				jPanel3.add(jPanel2, c);
				
				validate();
				pack();
		 }	 
	 }
	 
	 public ArrayList<Field> getFieldData(Person p)
	 {
		 ArrayList<Field> tempArrayList = new ArrayList<Field>();
		 tempArrayList.add(new Field("First Name", p.getFirstName()));
		 tempArrayList.add(new Field("Last Name", p.getLastName()));
		 

		 
		 for(int i=0;i<p.getPhoneList().size();i++)
		 {
			String phone = p.getPhoneList().get(i).getType();
			 if(phone.equals(""))
			 {
				 phone = ("Phone" + Integer.toString(i+1));
			 }
			tempArrayList.add(new Field( phone, p.getPhoneList().get(i).getNumber()));
			
		 }
		 for(int i=0;i<p.getEmailList().size();i++)
		 {
			String email = p.getEmailList().get(i).getType();
			if(email.equals(""))
			{
				email = ("Email" + Integer.toString(i+1));
			}
			tempArrayList.add(new Field(email, p.getEmailList().get(i).getAddress()));
			
		 }
			tempArrayList.addAll(p.getFields());
			return tempArrayList;
	 }
	 
	 public ArrayList<Field> getBlankFieldData()
	 {
		 ArrayList<Field> tempArrayList = new ArrayList<Field>();
		 tempArrayList.add(new Field("First Name", ""));
			tempArrayList.add(new Field("Last Name", ""));
			tempArrayList.add(new Field("Phone Num.", ""));
			tempArrayList.add(new Field("Email Address", ""));
			return tempArrayList;
	 }
	
	 public ArrayList<Field> makeFieldsFromBoxes()
	 {
		 ArrayList<Field> tempArrayList = new ArrayList<Field>();
		 
		 for(int i=0;i<names.size();i++)
		 {
			 tempArrayList.add(new Field(names.get(i).getText(), values.get(i).getText()));
		 }
		 
		 return tempArrayList;
	 }
	 
		/**
		 * Opens a confirmation dialog asking for confirmation for deleting record
		 * 
		 * @param name Name of person to be deleted
		 * @return <code>true</code> if deletion is confirmed <br> <code>false</code> if deletion is not confirmed
		 */
		public boolean deleteConfirm(String name)
		{
			int n = JOptionPane.showConfirmDialog(
				    this,
				    "Are you sure you would like to delete " + name + "?",
				    "Confirmation",
				    JOptionPane.YES_NO_OPTION);
			
			if(n==JOptionPane.YES_OPTION)
			{
				return true;
			}

			return false;
		}
		
		
		public void copyEmailsToClipBoard(String s)
		{
			StringSelection stringSelection = new StringSelection(s);
			clipboard.setContents(stringSelection, this );
		}
		
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			// TODO Auto-generated method stub

		}
		
		public void addAdminListener(Component c)
		{
			c.addMouseListener(adminListener);
		}
		
		public void printReport()
		{
			printingTable = WowHelper.getPrintableTable(jTable1);
	          PrinterJob pj=PrinterJob.getPrinterJob();
	          pj.setPrintable(MainClientWindow.this);
	          pj.printDialog();
	          try{ 
	            pj.print();
	          }catch (Exception PrintException) {}
	         searchPrintSort();
		}
		
		public void setImages()
		{
			try {
				redStar = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/redStar.GIF")));
				redDot = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/redDot.GIF")));
				greenDot = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/greenDot.GIF")));
				titlebarIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
				printerIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/wowPrintIcon2.GIF")));
				giIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/GIimage.gif")));
				rightArrows = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/expand.gif")));
				leftArrows = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/shrink.GIF")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
