package src.sose.wowBasicSearch;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.HackDA;
import src.sose.wowDA.MockDataAccess;
import src.sose.wowDA.Server;
import src.sose.wowLibs.EmailCollection;
import src.sose.wowLibs.GUIBase;
import src.sose.wowLibs.GeneralInfoWindow;
import src.sose.wowLibs.NoServerPopup;
import src.sose.wowLibs.WowHelper;

public class WebStartBasic extends GUIBase implements ActionListener, ClipboardOwner, Printable{

	private javax.swing.JButton searchButton;
	private javax.swing.JButton genInfoButton;
	private javax.swing.JButton printerButton;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu toolsMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem fileItem3;
	private javax.swing.JMenuItem toolsItem1;
	private javax.swing.JMenuItem toolsItem4;
	private javax.swing.JMenuItem helpItem1;
	private javax.swing.JMenuItem helpItem3;
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
	
	public String version = "1.0";
	
	public WebStartBasic() {
		initComponents();
		//dataAccess.add(new LocalDA("wow.xml"));
		dataAccess.addAll(getServerList());
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
		toolsItem4 = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		helpItem1 = new javax.swing.JMenuItem();
		helpItem3 = new javax.swing.JMenuItem();

		
		
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

		toolsMenu.setText("Tools");
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
				new WebStartBasic().setVisible(true);
			}
		});
	}              

	//**********************************Working with Popup Menu****************************************
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
		ReadmeWeb newReadme = new ReadmeWeb();
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
	
	private void toolsItem1MouseReleased(java.awt.event.MouseEvent evt) {
		printAllRecords(jTable1, dataAccess, isNewSearch);
		searchPrintSort();
	}
	
	private void toolsItem4MouseReleased(java.awt.event.MouseEvent evt) {
		ArrayList<String[]> temp = WowHelper.formatPeopleForPrinting(searchResults);
		
		printReport(temp, jTable1, false);
		
		searchPrintSort();
	}
	
	private void aboutMouseReleased(java.awt.event.MouseEvent evt) {
		AboutWeb newAbout = new AboutWeb(version);
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
		
		public ArrayList<DataAccess> getServerList()
		{
			ArrayList<DataAccess> serverList = new ArrayList<DataAccess>();
			ArrayList<Server> servers = new ArrayList<Server>();
			String line = "";
			String notFound = "";
			String[] addr;
			Boolean found = new Boolean(false); 
		    //BufferedReader reader = null;

		    try { 
		    	ClassLoader cl = this.getClass().getClassLoader();
		    	InputStream is = cl.getResourceAsStream("config/wow.conf");
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader reader = new BufferedReader(isr);
				//ClassLoader cl = this.getClass().getClassLoader();
				//File config = new File(cl.getResource("wow.conf"));

				//FileReader fileReader = new FileReader(config);
				//BufferedReader reader = new BufferedReader(fileReader);
				
				while ((line = reader.readLine()) != null)
				{
					addr = line.split(":");

					if(addr[0].startsWith("#"))
					{
						found = true;
					}
					
					for(Server temp : servers)
					{
						if(found == false && temp.GetName().equals(addr[2]))
						{
							temp.AddAddress(addr[0], Integer.parseInt(addr[1]), Integer.parseInt(addr[3]));
							found = true;
						}	
					}
					
					if(!found)
					{
						servers.add(new Server(addr[2], addr[0], Integer.parseInt(addr[1]), Integer.parseInt(addr[3])));
					}

					found = false;
				}

				for(Server temp : servers)
				{
					int i;
					found = false;
					
					for(i = 0; i < temp.GetAddresses().size(); i++)
					{
						Socket s = new Socket();
						
						try {
							s.connect(new InetSocketAddress(temp.GetAddresses().get(i).GetAddress(), 
									temp.GetAddresses().get(i).getPort()), 800);
							s.close();
							found = true;
							break;
						} catch (UnknownHostException e) {
							System.out.println("No DNS entry found for " + temp.GetAddresses().get(i).GetAddress());
						} catch (IOException e) {
							System.out.println("Unable to establish a connection with " + temp.GetAddresses().get(i).GetAddress());
						}
					}

					if(found == true)
					{
						serverList.add(new HackDA(temp.GetAddresses().get(i).GetAddress(), 
								temp.GetAddresses().get(i).getPort(), temp.GetName()));
					}
					else
					{
						if(notFound == "")
						{
							notFound = temp.GetName();
						}
						else
						{
							notFound = notFound + "\n" + temp.GetName();
						}
					}
				}

				reader.close();
				is.close();
			}
			catch(Exception ex)
			{
				System.out.println("File I/O Error");
				ex.printStackTrace();
				serverList.add(new HackDA("atlantis.ecet.vtc.edu",5280, "VTC"));
			}
			
			if(notFound != "")
			{
				NoServerPopup nsp = new NoServerPopup(notFound);
				nsp.setLocationRelativeTo(null);
				nsp.setVisible(true);
				nsp.setAlwaysOnTop(true);
			}

			return serverList;
		}
		
		public class ReadmeWeb extends javax.swing.JFrame {
			
			byte[] infile = new byte[100000];
			String filestring;
			String fileName = "Readme.txt";
			
		    public ReadmeWeb() {
		        initComponents();
		    }
		    
		    public ReadmeWeb(String s) {
		        initComponents();
		        fileName = s;
		        setTitle(fileName);
		    }

		    private void initComponents() {
				try {
					ClassLoader cl = this.getClass().getClassLoader();
					this.setIconImage(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif")));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        jScrollPane1 = new javax.swing.JScrollPane();
		        jTextPane1 = createTextPane();
		        jButton1 = new javax.swing.JButton();

		        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		        jScrollPane1.setViewportView(jTextPane1);

		        jButton1.setText("Close");
		        jButton1.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                jButton1ActionPerformed(evt);
		            }
		        });

		        setTitle("ReadMe.txt");
		       
		        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		        getContentPane().setLayout(layout);
		        layout.setHorizontalGroup(
		            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
		            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
		                .addContainerGap(281, Short.MAX_VALUE)
		                .add(jButton1)
		                .add(261, 261, 261))
		        );
		        layout.setVerticalGroup(
		            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		            .add(layout.createSequentialGroup()
		                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
		                .add(18, 18, 18)
		                .add(jButton1)
		                .addContainerGap())
		        );
		        pack();
		    }// </editor-fold>
		    
		    public JTextPane createTextPane()
		    {
				// read the file
				try {
			    	ClassLoader cl = this.getClass().getClassLoader();
			    	InputStream is = cl.getResourceAsStream("config/Readme.txt");
			       // InputStreamReader isr = new InputStreamReader(is);
			       // BufferedReader reader = new BufferedReader(isr);
					DataInputStream dis = new DataInputStream(is);
					try {
						int filelength = dis.read(infile);
						filestring = new String(infile, 0, filelength);
						//System.out.println("FILE CONTENT=" + filestring);
					} catch(IOException iox) {
						filestring = new String("File read error...");
						iox.printStackTrace();
					}
				} catch (Exception fnf) {
					filestring = new String("File not found...");
					fnf.printStackTrace();
				}

		     JTextPane textPane = new JTextPane();
		     textPane.setFont(new Font("Courier New", Font.PLAIN, 12));
		     StyledDocument doc = textPane.getStyledDocument();

//		     Load the text pane with styled text.
		     try {
		         //for (int i=0; i < filestring.length(); i++) {
		     	doc.insertString(doc.getLength(), filestring, null);
		         //}
		     } catch (BadLocationException ble) {
		         System.err.println("Couldn't insert initial text into text pane.");
		     }
		     
		     textPane.setCaretPosition(1);
		     textPane.setEditable(false);
		     return textPane;
		    }
		    
		    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) 
		    {
		    	this.setVisible(false);
		    }
		    // Variables declaration - do not modify
		    private javax.swing.JButton jButton1;
		    private javax.swing.JScrollPane jScrollPane1;
		    private javax.swing.JTextPane jTextPane1;
		    // End of variables declaration
		    
		}

		public class AboutWeb extends JFrame {

			private String version;
			
			public AboutWeb() {
				version = "";
				initComponents();
			}

			public AboutWeb(String versionNum) {
				version = versionNum;
				initComponents();
			}
			
			private void initComponents() {
				jButton1 = new javax.swing.JButton();
				jScrollPane1 = new javax.swing.JScrollPane();
				jTextArea1 = new javax.swing.JTextArea();

				try {
					ClassLoader cl = this.getClass().getClassLoader();
					this.setIconImage(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif")));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	setTitle("About");
				setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
				setIconImage(getIconImage());
				jButton1.setText("Close");
				jButton1.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButton1ActionPerformed(evt);
					}
				});

				jTextArea1.setColumns(20);
				jTextArea1.setEditable(false);
				jTextArea1.setFont(new java.awt.Font("Bookman Old Style", 0, 16));
				jTextArea1.setLineWrap(true);
				jTextArea1.setRows(5);
				
				if(version.equals(""))
				{
					jTextArea1.setText("WoW\n\nAuthors\nNick Guertin - n_guertin@yahoo.com\nDavid Ransom - dransom@vtc.edu\n");
				}
				else
				{

					jTextArea1.setText("WoW version " + version + "\n\nAuthors\nNick Guertin - n_guertin@yahoo.com\nDavid Ransom - dransom@vtc.edu\n");	
				}
				
				jTextArea1.setText("WoW version 1.0\n\nAuthors\nNick Guertin - n_guertin@yahoo.com\nDavid Ransom - dransom@vtc.edu\n");
				jScrollPane1.setViewportView(jTextArea1);

				org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
				getContentPane().setLayout(layout);
				layout.setHorizontalGroup(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(layout.createSequentialGroup()
								.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
										.add(layout.createSequentialGroup()
												.addContainerGap()
												.add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
												.add(layout.createSequentialGroup()
														.add(149, 149, 149)
														.add(jButton1)))
														.addContainerGap())
				);
				layout.setVerticalGroup(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
								.addContainerGap()
								.add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jButton1)
								.addContainerGap())
				);
				pack();
			}

			private javax.swing.JButton jButton1;
			private javax.swing.JScrollPane jScrollPane1;
			private javax.swing.JTextArea jTextArea1; 
			
			private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) 
			{  	
				setVisible(false);
			}
		}
}


