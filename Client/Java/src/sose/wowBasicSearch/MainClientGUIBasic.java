package src.sose.wowBasicSearch;
import java.awt.Component;
import java.awt.Cursor;
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
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.MockDataAccess;
import src.sose.wowLibs.About;
import src.sose.wowLibs.DepartmentList;
import src.sose.wowLibs.EmailCollection;
import src.sose.wowLibs.FaxNumberList;
import src.sose.wowLibs.FeedbackWindow;
import src.sose.wowLibs.GUIBase;
import src.sose.wowLibs.GeneralInfoWindow;
import src.sose.wowLibs.Readme;
import src.sose.wowLibs.WowHelper;

public class MainClientGUIBasic extends GUIBase implements ActionListener, ClipboardOwner, Printable{
	
	private javax.swing.JButton searchButton;
	private javax.swing.JButton genInfoButton;
	private javax.swing.JButton printerButton;
	private javax.swing.JMenu fileMenu;
	//private javax.swing.JMenu toolsMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem fileItem3;
	/*private javax.swing.JMenuItem toolsItem1;
	private javax.swing.JMenuItem toolsItem4;*/
	private javax.swing.JMenu moreInfo;
	private javax.swing.JMenuItem departmentList;
	private javax.swing.JMenuItem faxList;
	private javax.swing.JMenuItem phoneSearch;
	private javax.swing.JMenuItem officeList;
	private javax.swing.JMenuItem helpItem1;
	private javax.swing.JMenuItem helpItem3;
	private javax.swing.JMenuItem helpItem4; //quick tutorial
	private javax.swing.JMenuItem helpItem5; //feedback
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	public javax.swing.JTextField searchTextBox;

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
	
	public String version = "1.00";
	
	public MainClientGUIBasic() {
		initComponents();
		//dataAccess.add(new LocalDA("wow.xml"));
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
		/*toolsMenu = new javax.swing.JMenu();
		toolsItem1 = new javax.swing.JMenuItem();
		toolsItem4 = new javax.swing.JMenuItem();*/
		helpMenu = new javax.swing.JMenu();
		helpItem1 = new javax.swing.JMenuItem();
		helpItem3 = new javax.swing.JMenuItem();
		helpItem4 = new javax.swing.JMenu();
		helpItem5 = new javax.swing.JMenuItem();
		moreInfo = new javax.swing.JMenu();
		departmentList = new javax.swing.JMenuItem();
		faxList = new javax.swing.JMenuItem();
		phoneSearch = new javax.swing.JMenuItem();
		officeList = new javax.swing.JMenuItem();
		helpItem4 = new javax.swing.JMenuItem();
		helpItem5 = new javax.swing.JMenuItem();

		
		this.setIconImage(titlebarIcon.getImage());
		createPopupMenu();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Window on the World - Basic Search Client v." + version);
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
		
		fileMenu.setText("File");
		fileItem3.setText("Close");
		fileMenu.add(fileItem3);
		fileItem3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				fileItem3MouseReleased(evt);
			}
		});

		menuBar.add(fileMenu);

		/*toolsMenu.setText("Tools");
		toolsItem1.setText("Print Server Contact List");
		toolsItem1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem1MouseReleased(evt);
			}
		});
		toolsMenu.add(toolsItem1);
		
		
		toolsItem4.setText("Print Search Results");
		toolsItem4.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem4MouseReleased(evt);
			}
		});
		toolsMenu.add(toolsItem4);
		
		menuBar.add(toolsMenu);*/
		
		moreInfo.setText("Search+");
		departmentList.setText("Dept. List");
		moreInfo.add(departmentList);
		departmentList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				showDepartmentList(evt);
			}
		});
		
		faxList.setText("Fax Numbers");
		moreInfo.add(faxList);
		faxList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				faxNumbersMouseReleased(evt);
			}
		});
		
		officeList.setText("Office List");
		moreInfo.add(officeList);
		officeList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				searchTextBox.setText("@office ");
				searchTextBox.requestFocus();
				searchTextBox.setCaretPosition(searchTextBox.getText().length());
			}
		});
		
		phoneSearch.setText("Phone Number");
		moreInfo.add(phoneSearch);
		phoneSearch.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				searchTextBox.setText("@phone ");
				searchTextBox.requestFocus();
				searchTextBox.setCaretPosition(searchTextBox.getText().length());
			}
		});
		
		menuBar.add(moreInfo);
		
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
		
		helpItem4.setText("Quick Tutorial");
		helpMenu.add(helpItem4);
		helpItem4.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				showQuickTutorial(evt);
			}
		});
		helpItem4.setEnabled(false);
		
		helpItem5.setText("Feedback");
		helpMenu.add(helpItem5);
		helpItem5.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				feedbackMouseReleased(evt);
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
               // .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
                   // .add(expandButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pack();
        setSize(this.getWidth()+30, this.getHeight());
       resizeTable(jTable1);
	}                       
	//************************************End Building Main Window******************************************
	
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainClientGUIBasic().setVisible(true);
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

		MouseListener popupListener = new PopupListener(popup);
		jTable1.addMouseListener(popupListener);
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
	
	private void feedbackMouseReleased(java.awt.event.MouseEvent evt) 
	{
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			if(numFBWindowsOpen == 0)
			{
				FeedbackWindow gi = new FeedbackWindow();
				Point lowerRightCorn = new Point((this.getX()), this.getY()+this.getHeight());
				gi.setLocation(lowerRightCorn);
				gi.setVisible(true);
			}
		}
		else
		{
			if(numFBWindowsOpen == 0)
			{
				FeedbackWindow gi = new FeedbackWindow();
				Point maxBtmRightCorn = new Point((jScrollPane1.getX()+jScrollPane1.getWidth()-gi.getWidth()), jScrollPane1.getY()+jScrollPane1.getHeight()+100);
				gi.setLocation(maxBtmRightCorn);
				gi.setAlwaysOnTop(true);
				gi.setVisible(true);
			}
		}
	}
	
	private void faxNumbersMouseReleased(java.awt.event.MouseEvent evt) 
	{
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			if(numFLWindowsOpen == 0)
			{
				FaxNumberList gi = new FaxNumberList();
				Point lowerRightCorn = new Point((this.getX()), this.getY()+this.getHeight());
				gi.setLocation(lowerRightCorn);
				//gi.setVisible(true);
			}
		}
		else
		{
			if(numFLWindowsOpen == 0)
			{
				FaxNumberList gi = new FaxNumberList();
				Point maxBtmRightCorn = new Point((jScrollPane1.getX()+jScrollPane1.getWidth()-gi.getWidth()), jScrollPane1.getY()+jScrollPane1.getHeight()+53);
				gi.setLocation(maxBtmRightCorn);
				gi.setAlwaysOnTop(true);
				//gi.setVisible(true);
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

			
	private void openReadmeMouseReleased(java.awt.event.MouseEvent evt) {
		Readme newReadme = new Readme();
		newReadme.setLocationRelativeTo(null);
		newReadme.setVisible(true);
	}
	
	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			JTable target = (JTable)evt.getSource();
			rowSelected = target.getSelectedRow();
			if(!((String)jTable1.getValueAt(0, 0)).equals("No Results Found"))
			{
				expandInfo(jTable1, rowSelected);
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
	
	public void printAllRecords() {
		printAllRecords(jTable1, dataAccess, isNewSearch);
		searchPrintSort();
	}
	
	public void printAllRecordsByDept() {
		printAllRecordsByDept(dataAccess);
		searchPrintSort();
	}
	
	public void printCurrentRecords() {
		ArrayList<String[]> temp = WowHelper.formatPeopleForPrinting(searchResults);
		
		printReport(temp, jTable1, false);
		
		searchPrintSort();
	}
	
	private void aboutMouseReleased(java.awt.event.MouseEvent evt) {
		About newAbout = new About(version);
		newAbout.setLocationRelativeTo(null);
		newAbout.setVisible(true);
	}
	
	private void printMousePressed(java.awt.event.MouseEvent evt) {
		new src.sose.wowLibs.PrintWindow(this);
		
		JMenu menu = (JMenu)evt.getSource();
		if(menu.isSelected())
		{
			menu.setSelected(false);
		}
		//printAllRecords(jTable1, dataAccess, isNewSearch);
	//	searchPrintSort();
	}
	
	/**
	 * Allow access to the result table.
	 */
	public JTable getTable() {
		return jTable1;
	}
	
	
	private void showDepartmentList(java.awt.event.MouseEvent evt) {
		DepartmentList list = new src.sose.wowLibs.DepartmentList(this);
		list.setLocationRelativeTo(this);
		list.setVisible(true);
	}
	
	/**
	 * Show the user a short tutorial movie.
	 */
	private void showQuickTutorial(MouseEvent evt) {
		JMenuItem menu = (JMenuItem)evt.getSource();
		if (menu.isSelected()) {
			menu.setSelected(false);
		}
		
		/* TODO Show movie here */
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
		
		public void setImages()
		{
			try {
				ClassLoader cl = this.getClass().getClassLoader();
				redStar = new ImageIcon(ImageIO.read(cl.getResource("images/redStar.GIF")));
				redDot = new ImageIcon(ImageIO.read(cl.getResource("images/redDot.GIF")));
				greenDot = new ImageIcon(ImageIO.read(cl.getResource("images/greenDot.GIF")));
				titlebarIcon = new ImageIcon(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif")));
				printerIcon = new ImageIcon(ImageIO.read(cl.getResource("images/wowPrintIcon2.GIF")));
				giIcon = new ImageIcon(ImageIO.read(cl.getResource("images/GIimage.gif")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
