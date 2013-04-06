package src.sose.wowAdminGui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowBL.Phone;
import src.sose.wowDA.Address;
import src.sose.wowDA.DataAccess;
import src.sose.wowDA.MockDataAccess;
import src.sose.wowLibs.About;
import src.sose.wowLibs.ConnectionHandler;
import src.sose.wowLibs.DepartmentList;
import src.sose.wowLibs.FaxNumberList;
import src.sose.wowLibs.FeedbackWindow;
import src.sose.wowLibs.GUIBase;
import src.sose.wowLibs.LoginNotifier;
import src.sose.wowLibs.LoginWindow;
import src.sose.wowLibs.PopupMessage;
import src.sose.wowLibs.Readme;
import src.sose.wowLibs.WowHelper;


public class MainAdminWindow extends GUIBase implements ActionListener, WindowListener
{   
	// Makes the compiler happy.
	private static final long serialVersionUID = 1L;

	public static LoginWindow loginWindow = null;
	public static MainAdminWindow adminWindow = null;
	
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox campusDropDown;
    private javax.swing.JLabel campusLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTxtBox;
    private javax.swing.JLabel faxLabel;
    private javax.swing.JTextField faxTxtBox;
    private javax.swing.JLabel firstLabel;
    private javax.swing.JTextField firstTxtBox;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem helpMenuItem1;
    private javax.swing.JMenuItem helpMenuItem2;
    private javax.swing.JMenuItem fileMenuItem4;
    private javax.swing.JMenuItem toolsMenuItem1;
    private javax.swing.JMenuItem toolsMenuItem2;
    private javax.swing.JMenuItem toolsMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lastLabel;
    private javax.swing.JTextField lastTxtBox;
    private javax.swing.JLabel phoneLabel;
    private javax.swing.JTextField phoneTxtBox;
    private javax.swing.JComboBox roleDropDown;
    private javax.swing.JLabel roleLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton searchButton;
    public javax.swing.JTextField searchTextBox;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel deptLabel;
    private javax.swing.JLabel officeLabel;
    //private javax.swing.JTextField deptTxtBox;
    private javax.swing.JComboBox deptDropDown;
    private javax.swing.JTextField officeTxtBox;
    private javax.swing.JButton cancelButton;
    
    private javax.swing.JMenu moreInfo; 
	private javax.swing.JMenuItem departmentList;
	private javax.swing.JMenuItem faxList;
	private javax.swing.JMenuItem feedBack;
	private javax.swing.JMenuItem phoneSearch;
	private javax.swing.JMenuItem officeList;
	
	private JButton dept_addButton;
	private JButton dept_editButton;
	private JButton dept_deleteButton;
    
    static MyOwnFocusTraversalPolicy newPolicy;
	MockDataAccess mda = new MockDataAccess();
	ArrayList<DataAccess> dataAccess = new  ArrayList<DataAccess>();
	DefaultTableModel model;
	private static int rowSelected = -1;//used for right click menu
	public static boolean isNewSearch = true;
	static ArrayList<String> spawnedMIWindows = new ArrayList<String>();
	
	static int count = 0;
	boolean editingFlag = false;
	Person tempEdit;
	ImageIcon redStar;
	ImageIcon redDot;
	ImageIcon greenDot;
	ImageIcon titlebarIcon;
	
	/**
	 * Track the administrator client's current version.
	 */
	public static String version = "1.00";
	
	/**
	 * Initializes GUI and obtains server list
	 *
	 */
    public MainAdminWindow() {
        initComponents();
        
        dataAccess = WowHelper.getServerList();
        
        startThreadTimer();
    }
    
    public static void startThreadTimer() {
    	// Start a thread that periodically checks if the socket timed out.
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (!ConnectionHandler.isConnected() || ConnectionHandler.timedOut()) {
						adminWindow.setVisible(false);
						
						forceLogin();
						
						while (loginWindow.isVisible()) {
							try {
								Thread.sleep(20);
							} 
							catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					else {
						// Send a ping to the server if possible.
						try {
							ConnectionHandler.sendPing();
						}
						catch (IOException e) {
							// Disconnecting will trigger isConnected() to be false.
							ConnectionHandler.disconnect();
						}
					}
				}
				
			}
		}.start();
    }
    
    /**
     * Initializes components for GUI
     *
     */
    private void initComponents() {
    	
    	setImages();
    	
        searchTextBox = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        firstLabel = new javax.swing.JLabel();
        lastLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        phoneLabel = new javax.swing.JLabel();
        lastTxtBox = new javax.swing.JTextField();
        phoneTxtBox = new javax.swing.JTextField();
        emailTxtBox = new javax.swing.JTextField();
        firstTxtBox = new javax.swing.JTextField();
        statusLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileMenuItem4 = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        toolsMenuItem1 = new javax.swing.JMenuItem();
        toolsMenuItem2 = new javax.swing.JMenuItem();
        toolsMenuItem3 = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem1 = new javax.swing.JMenuItem();
        helpMenuItem2 = new javax.swing.JMenuItem();
        faxLabel = new javax.swing.JLabel();
        campusLabel = new javax.swing.JLabel();
        roleLabel = new javax.swing.JLabel();
        deptLabel = new javax.swing.JLabel();
        officeLabel = new javax.swing.JLabel();
        //deptTxtBox = new javax.swing.JTextField();
        deptDropDown = new javax.swing.JComboBox();
        officeTxtBox = new javax.swing.JTextField();
        faxTxtBox = new javax.swing.JTextField();
        campusDropDown = new javax.swing.JComboBox();
        roleDropDown = new javax.swing.JComboBox();
        
        moreInfo = new javax.swing.JMenu();
		departmentList = new javax.swing.JMenuItem();
		faxList = new javax.swing.JMenuItem();
		feedBack = new JMenuItem();
		phoneSearch = new JMenuItem();
		officeList = new JMenuItem();
		
		dept_addButton = new JButton();
		dept_editButton = new JButton();
		dept_deleteButton = new JButton();

        //getContentPane().setBackground(new Color(112, 166, 229, 255));
		
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
		
		officeList.setText("Office Search");
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
		
		this.setIconImage(titlebarIcon.getImage());
	
		createPopupMenu();
		setTitle("Window on the World - Administrator v." + version);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        searchTextBox.setText("Type WoW search here...");
		searchTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				searchTextBoxKeyPressed(evt);
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
		
		for(int i=0;i<jTable1.getColumnCount();i++)
		{
			jTable1.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(iconHeaderRenderer);
		}
		
		JTableHeader header =  jTable1.getTableHeader();
		header.addMouseListener(new ColumnHeaderListener());
		
        jScrollPane1.setViewportView(jTable1);

        firstLabel.setText("First Name");

        lastLabel.setText("Last Name");

        emailLabel.setText("College E-Mail");

        phoneLabel.setText("College Phone #");

        faxLabel.setText("Fax Number");

        campusLabel.setText("Campus");

        roleLabel.setText("Role");

        deptLabel.setText("Department");

        officeLabel.setText("Office");
        
        campusDropDown.setModel(new javax.swing.DefaultComboBoxModel(
        		new String[] { "Bennington", "Brattleboro", "Randolph", "Williston", "Windsor", "Other"}));
        campusDropDown.setEnabled(false);
        campusDropDown.setSelectedIndex(1);
        
        roleDropDown.setModel(new javax.swing.DefaultComboBoxModel(
        		new String[] { "Faculty - Adjunct", "Faculty - FT", "Staff", "Other" }));
      	roleDropDown.setEnabled(false);
      	roleDropDown.setSelectedIndex(3);
      	String[] list = WowHelper.retrieveAllDepartments(true);
      	if (list.length == 1 && list[0].equals("")) {
      		list = readDeptList();
      	}
      	else {
      		writeLocalDeptList(list);
      	}
      	deptDropDown.setModel(new javax.swing.DefaultComboBoxModel(list));
      	deptDropDown.setEnabled(false);
      	deptDropDown.setSelectedIndex(0);
      	
      	dept_addButton.setText("+");
      	dept_editButton.setText("#");
      	dept_deleteButton.setText("-");
      	dept_addButton.setToolTipText("Add a department");
        dept_editButton.setToolTipText("Edit selected department");
        dept_deleteButton.setToolTipText("Delete selected department");
        dept_addButton.setMargin(new Insets(0,0,0,0));
        dept_editButton.setMargin(new Insets(0,0,0,0));
        dept_deleteButton.setMargin(new Insets(0,0,0,0));
        dept_addButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		addDepartment(e);
        	}
        });
        dept_editButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		editDepartment(e);
        	}
        });
        dept_deleteButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		deleteDepartment(e);
        	}
        });
      	
        statusLabel.setBackground(new java.awt.Color(51, 255, 51));
        
      
		statusLabel.setIcon(greenDot);
        statusLabel.setText("Read Only");

	    lastTxtBox.setEditable(false);
	    firstTxtBox.setEditable(false);
	    phoneTxtBox.setEditable(false);
	    emailTxtBox.setEditable(false);
	    faxTxtBox.setEditable(false);
	    officeTxtBox.setEditable(false);
	    //deptTxtBox.setEditable(false);
	    
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchButtonActionPerformed(evt);
			}
		});
		searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				searchButtonMousePressed(evt);
			}
		});

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
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

        fileMenu.setText("File");

        fileMenuItem4.setText("Close");
		fileMenuItem4.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				fileMenuItem4MouseReleased(evt);
			}
		});
        fileMenu.add(fileMenuItem4);

        jMenuBar1.add(fileMenu);

        toolsMenu.setText("Tools");
        toolsMenuItem1.setText("Print Server Contact List");
        toolsMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem1MouseReleased(evt);
			}
		});
        toolsMenu.add(toolsMenuItem1);
        
        toolsMenu.add(new JSeparator());
        
        toolsMenuItem2.setText("Total Record Count");
        toolsMenuItem2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				toolsItem2MouseReleased(evt);
			}
		});
        toolsMenu.add(toolsMenuItem2);
        
        toolsMenu.add(new JSeparator());
        
        toolsMenuItem3.setText("Lock");
        toolsMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 
        		Event.CTRL_MASK));
        toolsMenuItem3.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				ConnectionHandler.disconnect();
			}
        });
        toolsMenu.add(toolsMenuItem3);
        
        jMenuBar1.add(toolsMenu);
        
        jMenuBar1.add(moreInfo);

        helpMenu.setText("Help");
        helpMenuItem1.setText("View ReadMe");
		helpMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				openReadmeMouseReleased(evt);
			}
		});
        helpMenu.add(helpMenuItem1);

        helpMenuItem2.setText("About");
        helpMenuItem2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				aboutMouseReleased(evt);
			}
		});
        helpMenu.add(helpMenuItem2);
        
        feedBack.setText("Feedback");
        helpMenu.add(feedBack);
        feedBack.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent evt) {
        		feedbackMouseReleased(evt);
        	}
        });

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        Vector<Component> order = new Vector<Component>(9);
        order.add(firstTxtBox);
        order.add(lastTxtBox);
        order.add(emailTxtBox);
        order.add(phoneTxtBox);
        order.add(campusDropDown);
        order.add(roleDropDown);
        //order.add(deptTxtBox);
        order.add(deptDropDown);
        order.add(faxTxtBox);
        order.add(officeTxtBox);
        newPolicy = new MyOwnFocusTraversalPolicy(order);
        setFocusTraversalPolicy(newPolicy);
        
        JPanel deptButtons = new JPanel();
        deptButtons.setLayout(new GridLayout(3,1));
        deptButtons.add(dept_addButton);
        deptButtons.add(dept_editButton);
        deptButtons.add(dept_deleteButton);
        
        for (Component c : deptButtons.getComponents()) {
        	c.setEnabled(false);
        }
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(searchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 296, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(addButton)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(deleteButton)
                                .add(editButton)))))
                .add(40, 40, 40)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statusLabel)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(saveButton)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(cancelButton))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(firstLabel)
                                        .add(lastLabel)
                                        .add(emailLabel)
                                        .add(phoneLabel)
                                        .add(campusLabel)
                                        .add(officeLabel))
                                    .add(16, 16, 16)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, campusDropDown, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, phoneTxtBox)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, firstTxtBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, lastTxtBox)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, emailTxtBox)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, officeTxtBox)))))
                        .add(31, 31, 31))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(roleLabel)
                            .add(faxLabel)
                            .add(deptLabel))
                        .add(16, 16, 16)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(faxTxtBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                            .add(deptDropDown, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .add(roleDropDown, 0, 150, Short.MAX_VALUE))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(deptButtons, org.jdesktop.layout.GroupLayout.LEADING, 15,15))
                        .addContainerGap())))
        );

        layout.linkSize(new java.awt.Component[] 
                {campusDropDown, campusLabel, deptLabel, deptDropDown, emailLabel, emailTxtBox, faxLabel, faxTxtBox, 
        		firstLabel, firstTxtBox, lastLabel, lastTxtBox, officeLabel, officeTxtBox, phoneLabel, phoneTxtBox, 
        		roleDropDown, roleLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {addButton, deleteButton, editButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(firstTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(firstLabel))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(statusLabel)
                                .add(34, 34, 34)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(lastTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(emailTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(emailLabel)))
                            .add(lastLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(phoneLabel)
                            .add(phoneTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(campusLabel)
                            .add(campusDropDown, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(roleLabel)
                            .add(roleDropDown, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        	.add(deptButtons, org.jdesktop.layout.GroupLayout.LEADING, 40 , 40)
                        	.add(deptLabel)
                            .add(deptDropDown, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(faxTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(faxLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(officeTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(officeLabel))
                        .add(15, 15, 15)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(saveButton)
                            .add(cancelButton)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(searchTextBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(searchButton))
                            .add(layout.createSequentialGroup()
                                .add(addButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(editButton)
                                    .add(layout.createSequentialGroup()
                                        .add(32, 32, 32)
                                        .add(deleteButton)))))
                        .add(18, 18, 18)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 231, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] 
                {campusDropDown, campusLabel, deptLabel, emailLabel, emailTxtBox, faxLabel,
        		faxTxtBox, firstLabel, firstTxtBox, lastLabel, lastTxtBox, officeLabel, officeTxtBox, 
        		phoneLabel, phoneTxtBox, roleDropDown, roleLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
        
        resizeTable(jTable1);

    }
    
    /**
     * Presents a dialog to the user, forcing him or her to log in before proceeding.
     * If the user cancels the dialog, the program will be exited.
     * It can be safely assumed that if this function returns at all, the user has
     * successfully logged in and is connected to the server.
     */
    public static void forceLogin() {
    	// Create a callback for our login window.
		final LoginNotifier callback = new LoginNotifier() {
			/**
			 * Function called when an attempted login was a failure.
			 * @param errorMessage Reason why the login failed.
			 */
			@Override public void loginFailed(String errorMessage) {
				JOptionPane.showMessageDialog(loginWindow, 
					"Your attempt to login failed. " +
					"Reason:\n" + errorMessage , "Login failed!", JOptionPane.ERROR_MESSAGE);
				loginWindow.setInstructions("Could not log in to the server... Please try again.");
			}
			
			/**
			 * Function called when an attempted login succeeded.
			 */
			@Override public void successfulLogin() {
				loginWindow.setVisible(false);
				loginWindow.dispose();
				loginWindow = null;
				
				if (adminWindow == null)
					adminWindow = new MainAdminWindow();
				adminWindow.setVisible(true);
			}
		};
    	
    	try {
    		SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					loginWindow = new LoginWindow(callback);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    /**
     * Handle window closing event
     * 
     * @param evt event occuring
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {                                   
// TODO add your handling code here:
    }                                  
    
    /**
     * Main entry point for the program
     * 
     * @param args command line arguments
     */
    public static void main(String args[]) {
    	// Set up a little "loading" screen so the user knows something is happening.
        JFrame frame = new JFrame("Loading...");
        frame.setLayout(new BorderLayout());
        
        JLabel label = new JLabel("  Locating main server, please wait...");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
        	(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif"))));
        frame.setSize(225, 75);
        
        frame.add(label, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // Attempt to find the main server.
    	if (WowHelper.getMainServerAddress() == null) {
    		frame.setTitle("Error!");
    		label.setText("  An error occurred. See the pop-up.");
    		JOptionPane.showMessageDialog(frame, 
    			"Sorry, but no server is available. Modifications cannot be made.\n" +
    			"Please report this issue to the IT department and try again later.\n" +
    			"\n" +
    			"Issue description:\n" +
    			"Client was unable to find a main server in the list described in the config file.\n" +
    			"The main server may be down, or the intended main server may not be marked as \"main\".", 
    			"No main server available.", JOptionPane.ERROR_MESSAGE);
    		System.exit(1);
    	}
    	
    	frame.dispose();
    	frame = null;
    	
    	forceLogin();
    }
                 
	//************************************************************************************************
	//************************************Helper Methods**********************************************
	//************************************************************************************************
	
	
	//**********************************Working with Popup Menu***************************************
    /**
     * Creates a popup menu
     * 
     */
	public void createPopupMenu() {
		JMenuItem menuItem;

		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("Edit Record");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("Delete Record");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		menuItem = new JMenuItem("Open in new Info Window");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		MouseListener popupListener = new PopupListener(popup);
		jTable1.addMouseListener(popupListener);
	}
	
	
	//*********************************End working with Popup Menu************************************
	
	//**********************************Working with Text Boxes***************************************
	/**
	 * Fill text boxes with person's information
	 * 
	 * @param data person's information
	 */
	public void populateTextBoxes(Person data) {
		
	    //deptTxtBox.setText("");
	    officeTxtBox.setText("");
	    faxTxtBox.setText("");
        campusDropDown.setSelectedIndex(1);
      	roleDropDown.setSelectedIndex(3);
      	
	    lastTxtBox.setText(data.getLastName());
	    firstTxtBox.setText(data.getFirstName());
	    phoneTxtBox.setText(data.getPhoneNumber());
	    emailTxtBox.setText(data.getEmail());
	    
	    ArrayList<Field> tempAL = data.getFields();
	    if(tempAL.size() == 5)
	    {
	    	campusDropDown.setSelectedItem(tempAL.get(0).getValue());
	    	roleDropDown.setSelectedItem(tempAL.get(1).getValue());
	    	deptDropDown.setSelectedItem(tempAL.get(2).getValue());
	    	//deptTxtBox.setText(tempAL.get(2).getValue());
	    	faxTxtBox.setText(tempAL.get(3).getValue());
	    	officeTxtBox.setText(tempAL.get(4).getValue());
	    }
	    
	    if(tempAL.size() == 4)
	    {
	    	campusDropDown.setSelectedItem(tempAL.get(0).getValue());
	    	faxTxtBox.setText(tempAL.get(3).getValue());
	    	roleDropDown.setSelectedItem(tempAL.get(1).getValue());
	    }
	}
	
	/**
	 * Sets base state - no entries selected
	 *
	 */
	public void setBaseState()
	{
        statusLabel.setBackground(new java.awt.Color(51, 255, 51));
		
		statusLabel.setIcon(greenDot);
        statusLabel.setText("Read Only");
        
        firstLabel.setIcon(null);
        lastLabel.setIcon(null);
        emailLabel.setIcon(null);
        phoneLabel.setIcon(null);
        faxLabel.setIcon(null);
        
	    lastTxtBox.setText("");
	    firstTxtBox.setText("");
	    phoneTxtBox.setText("");
	    emailTxtBox.setText("");
	    //deptTxtBox.setText("");
	    officeTxtBox.setText("");
	    faxTxtBox.setText("");
	    lastTxtBox.setEditable(false);
	    firstTxtBox.setEditable(false);
	    phoneTxtBox.setEditable(false);
	    emailTxtBox.setEditable(false);
	    faxTxtBox.setEditable(false);
	    //deptTxtBox.setEditable(false);
	    officeTxtBox.setEditable(false);
	    
        campusDropDown.setEnabled(false);
      	roleDropDown.setEnabled(false);
      	deptDropDown.setEnabled(false);
      	
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        dept_addButton.setEnabled(false);
        dept_editButton.setEnabled(false);
        dept_deleteButton.setEnabled(false);
	    
        editingFlag = false;
	}
	
	/**
	 * Make boxes editable when adding/editing information
	 *
	 */
	public void setTextBoxesEditable()
	{
		lastTxtBox.setEditable(true);
	    firstTxtBox.setEditable(true);
	    phoneTxtBox.setEditable(true);
	    emailTxtBox.setEditable(true);
	    faxTxtBox.setEditable(true);
	    //deptTxtBox.setEditable(true);
	    deptDropDown.setEnabled(true);
	    officeTxtBox.setEditable(true);
	    campusDropDown.setEnabled(true);
	    roleDropDown.setEnabled(true);
	    dept_addButton.setEnabled(true);
        dept_editButton.setEnabled(true);
        dept_deleteButton.setEnabled(true);
	}
	
	/**
	 * Checks each text field to see if it's valid
	 * 
	 * @return true if valid<br>
	 * 		   false if invalid
	 */
	public boolean[] checkTextBoxesValidity()
	{
		boolean[] valid = new boolean[]{false, false, false, false, false};
		
		Person testingPerson = new Person();
		
		if(testingPerson.setFirstName(firstTxtBox.getText()) == 0){valid[0] = true;};
		if(testingPerson.setLastName(lastTxtBox.getText()) == 0){valid[1] = true;};
		if(testingPerson.addEmail(emailTxtBox.getText()) == 0){valid[2] = true;};
		if(testingPerson.addPhone(phoneTxtBox.getText()) == 0){valid[3] = true;};
		if(faxTxtBox.getText().equals(""))
		{
			valid[4] = true;
		}
		else
		{
			if(testingPerson.addPhone(faxTxtBox.getText()) == 0)
			{
				valid[4] = true;
				ArrayList<Phone> tempPhones = new ArrayList<Phone>();
				tempPhones = testingPerson.getPhoneList();
				faxTxtBox.setText((tempPhones.get((tempPhones.size()-1)).getNumber()));
			};
		}
		return valid;
	}
	
	/**
	 * Sets an error icon next to invalid fields
	 * 
	 * @param valid select invalid text boxes
	 * @return <code>true</code> if all fields are valid <br> 
	 * <code>false</code> if one or more fields are invalid
	 */
	public boolean markInvalid(boolean[] valid)
	{
		firstLabel.setIcon(null);
        lastLabel.setIcon(null);
        emailLabel.setIcon(null);
        phoneLabel.setIcon(null);
        faxLabel.setIcon(null);
       
		int validFlag = 0;
		
		if(!valid[0])
		{     
	        firstLabel.setIcon(redStar);
	        firstLabel.setHorizontalTextPosition(JLabel.LEFT);
	        validFlag++;
		}
		if(!valid[1])
		{     
	        lastLabel.setIcon(redStar);
	        lastLabel.setHorizontalTextPosition(JLabel.LEFT);
	        validFlag++;
		}
		if(!valid[2])
		{        
	        emailLabel.setIcon(redStar);
	        emailLabel.setHorizontalTextPosition(JLabel.LEFT);
	        validFlag++;
		}			
		if(!valid[3])
		{
	        phoneLabel.setIcon(redStar);
	        phoneLabel.setHorizontalTextPosition(JLabel.LEFT);
	        validFlag++;
		}
		if(!valid[4])
		{
	        faxLabel.setIcon(redStar);
	        faxLabel.setHorizontalTextPosition(JLabel.LEFT);
	        validFlag++;
		}
		
		if(validFlag == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Gets information in text boxes and sets it to a person's record
	 * 
	 * @return person's information
	 */
	public Person convertTextBoxesToPerson()
	{
		Person newPerson = new Person();
		
		newPerson.setFirstName(firstTxtBox.getText().trim());
		newPerson.setLastName(lastTxtBox.getText().trim());
		newPerson.addEmail(emailTxtBox.getText().trim());
		newPerson.addPhone(phoneTxtBox.getText().trim());
		ArrayList<Field> tempFields = new ArrayList<Field>();
		tempFields.add(new Field("Campus", campusDropDown.getSelectedItem().toString()));
		tempFields.add(new Field("Role", roleDropDown.getSelectedItem().toString()));
		//tempFields.add(new Field("Department", deptTxtBox.getText()));
		String dept = deptDropDown.getSelectedItem().toString();
		if (dept.equals("None")) {
			dept = "";
		}
		
		tempFields.add(new Field("Department", dept));
		tempFields.add(new Field("Fax Number", faxTxtBox.getText()));
		tempFields.add(new Field("Office", officeTxtBox.getText()));
		
		newPerson.setFields(tempFields);
		
		return newPerson;
	}
	//*********************************End working with Text Boxes************************************
	
	/**
	 * Handle search information
	 * 
	 * @param s text to be searched
	 */
	public void searchAndPrint(String s)
	{
		searchTextBox.setText(s);
		setBaseState();
		searchPrintSort();
	}
	
	/**
	 * Find, sort, and print information in a table
	 *
	 */
	public void searchPrintSort()
	{
		printTable(search(searchTextBox.getText(), dataAccess), jTable1);
		isNewSearch = true;
		sortTable(defaultSortColumn, jTable1, ascend, isNewSearch, getDefaultTableModel(jTable1));
		isNewSearch = false;
		jTable1.setDefaultRenderer(Object.class, new
				AttributiveCellRenderer());
	}
	
	/**
	 * Tests to see if two people have the same information
	 * 
	 * @param one person one
	 * @param two person two
	 * @return true if they are the same<br>
	 * 		   false if they are different
	 */
	public boolean compareFields(Person one, Person two)
	{
		boolean same = true;
	
		if(one.getFields().size() != two.getFields().size())
		{
			return false;
		}
		
		for(int i = 0; i<one.getFields().size();i++)
		{
			if(!one.getFields().get(i).getValue().equals(two.getFields().get(i).getValue()))
			{
				return false;
			}
		}
		
		
		return same;
	}
	
	/**
	 * Read images stored in images/ folder
	 *
	 */
	public void setImages()
	{
		try {
			redStar = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/redStar.GIF")));
			redDot = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/redDot.GIF")));
			greenDot = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/greenDot.GIF")));
			titlebarIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	//************************************************************************************************
	//************************************End Helper Methods******************************************
	//************************************************************************************************
	
	//************************************************************************************************
	//************************************Listeners***************************************************
	//************************************************************************************************
	/**
	 * Handles user actions
	 * 
	 * @param e action event object
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Edit Record"))
		{
			editButtonActionPerformed(new java.awt.event.ActionEvent(this, 12165165, "Editing from Popup"));
		}
		else if(e.getActionCommand().equals("Delete Record"))
		{
			populateTextBoxes(getPersonAtRow(rowSelected, jTable1));
			deleteButtonActionPerformed(new java.awt.event.ActionEvent(this, 165461561, "Deleting from Popup"));
		}
		else if(e.getActionCommand().equals("Open in new Info Window"))
		{
			int[] selectedRows = jTable1.getSelectedRows();

			for(int i : selectedRows)
			{
				if(rowSelected != -1)
				{
					expandInfo(jTable1, i);
				}
			}
		}

	}
	
	static class PopupListener extends MouseAdapter {
		JPopupMenu popup;
		
		/**
		 * Initialize new popup listener
		 * 
		 * @param popupMenu popup menu to be set to member popup
		 */
		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}
		
		/**
		 * Handle actions when a mouse button is pressed down
		 * 
		 * @param e mouse event object
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		/**
		 * Handle actions when a mouse button is released
		 * 
		 * @param e mouse event object
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		/**
		 * Check to see where the mouse was clicked and show a popup
		 * 
		 * @param e mouse event object
		 */
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
	
	
	/**
	 * Handle action when search button is pressed - do searchPrintSort()
	 * 
	 * @param evt action event object
	 */
	private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  		
		searchPrintSort();
	}
	
	/*private void genInfoButtonActionPerformed(java.awt.event.ActionEvent evt) 
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
	}*/
	
	public void feedbackMouseReleased(java.awt.event.MouseEvent evt) 
	{
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			if(numFBWindowsOpen == 0)
			{
				FeedbackWindow gi = new FeedbackWindow();
				Point lowerRightCorn = new Point((this.getX()), this.getY()+this.getHeight());
				gi.setLocation(lowerRightCorn);
				//gi.setVisible(true);
			}
		}
		else
		{
			if(numFBWindowsOpen == 0)
			{
				FeedbackWindow gi = new FeedbackWindow();
				Point maxBtmRightCorn = new Point((jScrollPane1.getX()+jScrollPane1.getWidth()-gi.getWidth()), jScrollPane1.getY()+jScrollPane1.getHeight()+53);
				gi.setLocation(maxBtmRightCorn);
				gi.setAlwaysOnTop(true);
				//gi.setVisible(true);
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
	
	/**
	 * Handle action if user clicks Add - set up text boxes for editing
	 * 
	 * @param evt action event object
	 */
	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
	    lastTxtBox.setText("");
	    firstTxtBox.setText("");
	    phoneTxtBox.setText("");
	    emailTxtBox.setText("");
	    faxTxtBox.setText("");
	    //deptTxtBox.setText("");
	    officeTxtBox.setText("");
	    
	    lastTxtBox.setEditable(true);
	    firstTxtBox.setEditable(true);
	    phoneTxtBox.setEditable(true);
	    emailTxtBox.setEditable(true);
	    faxTxtBox.setEditable(true);
	    //deptTxtBox.setEditable(true);
	    officeTxtBox.setEditable(true);
	    
        campusDropDown.setEnabled(true);
      	roleDropDown.setEnabled(true);
      	deptDropDown.setEnabled(true);
      	dept_addButton.setEnabled(true);
        dept_editButton.setEnabled(true);
        dept_deleteButton.setEnabled(true);
      
		statusLabel.setIcon(redDot);
	    statusLabel.setText("Adding New Record");
        
	    saveButton.setEnabled(true);
	    cancelButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
	    editingFlag = true;
	}
	
	/**
	 * Handles action when Cancel is pushed - remove editable items
	 * 
	 * @param evt action event object
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		setBaseState();
	}
	
	/**
	 * Handles action when Save is clicked - add record to database
	 * 
	 * @param evt action event object
	 */
	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		PopupMessage notify = new PopupMessage();
		if(statusLabel.getText().equals("Adding New Record"))
		{
			boolean[] valid = checkTextBoxesValidity();
	        boolean validFlag = markInvalid(valid);
			if(validFlag)
			{
				Person newP = convertTextBoxesToPerson();
				String result = newP.add(dataAccess);
				notify = new PopupMessage(result, true);
				searchTextBox.setText(lastTxtBox.getText() + " " + firstTxtBox.getText());
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
	        boolean validFlag = markInvalid(valid);
	        boolean same = true;
	        
			if(validFlag)
			{
				Person newP = convertTextBoxesToPerson();
				
				if(same){same = tempEdit.getLastName().equals(newP.getLastName());}
				if(same){same = tempEdit.getFirstName().equals(newP.getFirstName());}
				if(same){same = tempEdit.getPhoneNumber().equals(newP.getPhoneNumber());}
				if(same){same = tempEdit.getEmail().equals(newP.getEmail());}
				if(same){same = compareFields(tempEdit, newP);}
				newP.setID(tempEdit.getID());
				
				String result = "";
				if(same)
				{
					notify = new PopupMessage("Nothing Changed", true);
				}
				else if((result = newP.update(dataAccess)).equalsIgnoreCase("OK"))
				{
					notify = new PopupMessage("Record Updated Successfully", true);
				}
				else
				{
					notify = new PopupMessage("ERROR: " + result, true);
				}
				System.out.println(newP.getID());
				
				searchTextBox.setText(lastTxtBox.getText() + " " + firstTxtBox.getText());
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
	
	/**
	 * Handles action when Edit is clicked - edit existing person
	 * 
	 * @param evt action event object
	 */
	private void editButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		final Address MAIN_SERVER = WowHelper.getMainServerAddress();
		final String STANDARD_SERVER = MAIN_SERVER.GetAddress();
		final int STANDARD_PORT = MAIN_SERVER.getPort();
		if(!editingFlag)
		{
			rowSelected = jTable1.getSelectedRow();
			
			Person p = getPersonAtRow(rowSelected, jTable1);
			// Now connect to the server and get this person's ID.
			java.net.Socket sock = null;
			java.io.PrintStream out = null;
			java.io.BufferedReader in = null;
			int id = -1;
			try {
				sock = new java.net.Socket(STANDARD_SERVER, STANDARD_PORT);
				out = new java.io.PrintStream(sock.getOutputStream());
				in = new java.io.BufferedReader(new java.io.InputStreamReader(sock.getInputStream()));
				
				final String AQRY = "AQRY: " + p.getFirstName() + "|" + p.getLastName() + "|" +
					p.getPhoneNumber() + "|" + p.getEmail() + "|||||";
				out.println(AQRY);
				
				String line = in.readLine();
				final String[] result = line.split("\\|");
				id = Integer.parseInt(result[0]);
				
				p.setID(id);
				
				in.close();
				out.close();
				sock.close();
			}
			catch (java.io.IOException e) {
				PopupMessage err = 
					new PopupMessage("The server could not be contacted. (" + e.getMessage() + ")",true);
				
				err.setLocationRelativeTo(this);
				err.setVisible(true);
				return;
			}
		
			populateTextBoxes(p);
		
			tempEdit = getPersonAtRow(rowSelected, jTable1);
			tempEdit.setID(id);
			
			editingFlag = true;
			setTextBoxesEditable();
		
			
			statusLabel.setIcon(redDot);
			statusLabel.setText("Editing Record");
		
			saveButton.setEnabled(true);
			cancelButton.setEnabled(true);
		}
	}
	
	/**
	 * Handles action when Delete is clicked - remove entry from list
	 * 
	 * @param evt action event object
	 */
	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		if(!editingFlag)
		{
			editingFlag = true;
			
			Person temp = getPersonAtRow(rowSelected, jTable1);

			/*Confirmation confirm = new Confirmation("Are you sure you want to delete " + temp.getFirstName() + " " + temp.getLastName() + "?", dataAccess, jTable1, searchTextBox, temp);
			confirm.setLocationRelativeTo(this);
			confirm.setVisible(true);*/

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
		}
	}
	
	/**
	 * Handles action when mouse is clicked on the search text box
	 * 
	 * @param evt mouse event object
	 */
	private void searchTextBoxMousePressed(java.awt.event.MouseEvent evt) {
		searchTextBox.selectAll();
		setBaseState();
	}
	
	/**
	 * Handles action when Search button is clicked - set base state
	 * 
	 * @param evt mouse event object
	 */
	private void searchButtonMousePressed(java.awt.event.MouseEvent evt) {
		setBaseState();
	}
	
//	private void notAvailableYetMouseReleased(java.awt.event.MouseEvent evt) {
//		PopupMessage newMessage = new PopupMessage();
//		newMessage.setLocationRelativeTo(this);
//		newMessage.setVisible(true);
//	}
	
	/**
	 * Handles action when user clicks on readme - opens readme
	 * 
	 * @param evt mouse event object
	 */
	private void openReadmeMouseReleased(java.awt.event.MouseEvent evt) {
		Readme newReadme = new Readme();
		newReadme.setLocationRelativeTo(null);
		newReadme.setVisible(true);
	}
	
	/**
	 * Handles action when About is clicked - show user About screen
	 * 
	 * @param evt mouse event object
	 */
	private void aboutMouseReleased(java.awt.event.MouseEvent evt) {
		About newAbout = new About(version);
		newAbout.setLocationRelativeTo(null);
		newAbout.setVisible(true);
	}
	
	/**
	 * Handles action when table is clicked
	 * 
	 * @param evt mouse event object
	 */
	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
		if(!editingFlag){
			if (evt.getClickCount() == 2) {
				JTable target = (JTable)evt.getSource();
				rowSelected = target.getSelectedRow();
				
				String info;
				info = (String)jTable1.getValueAt(rowSelected, 0);

				if(info != null)
				{
					if((!info.equals("No Results Found")))
					{
						populateTextBoxes(getPersonAtRow(rowSelected, jTable1));
						deleteButton.setEnabled(true);
					}
					
				}
				
			}
			
			if (evt.getClickCount() == 1) {
				JTable target = (JTable)evt.getSource();
				rowSelected = target.getSelectedRow();
				
				if((jTable1.getValueAt(rowSelected, 1) == null))
				{
					
				}
				else
				{
					editButton.setEnabled(true);
				}

			}
		}
	}
	
	/**
	 * Handles action when key is pressed inside of search text box<br>
	 * Only does something when enter is pressed
	 * 
	 * @param evt key event object
	 */
	private void searchTextBoxKeyPressed(java.awt.event.KeyEvent evt) {                                       
		if(evt.getKeyCode() ==  java.awt.event.KeyEvent.VK_ENTER)
		{
			searchPrintSort();
		}
	} 
	
	/**
	 * Handles action when File -> exit menu item is pressed: exits program 
	 * 
	 * @param evt mouse event object
	 */
	private void fileMenuItem4MouseReleased(java.awt.event.MouseEvent evt) {
		System.exit(1);
	}
	
	/**
	 * Print server contact list when Tools -> Print Server Contact List is clicked
	 * 
	 * @param evt mouse event object
	 */
	private void toolsItem1MouseReleased(java.awt.event.MouseEvent evt) {
		printAllRecords(jTable1, dataAccess, isNewSearch);
	}
	
	/**
	 * Print server contact list when Tools -> Print Server Contact List is clicked
	 * 
	 * @param evt mouse event object
	 */
	private void toolsItem2MouseReleased(java.awt.event.MouseEvent evt) {
		int total = (WowHelper.getAllPeople(dataAccess)).size();
		JOptionPane.showMessageDialog(this,
			    "Total Record Count: " + total, 
			    "Record Count", 
			    JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void showDepartmentList(java.awt.event.MouseEvent evt) {
		DepartmentList list = new src.sose.wowLibs.DepartmentList(this);
		list.setLocationRelativeTo(this);
		list.setVisible(true);
	}
	
	private void addDepartment(MouseEvent evt) {
		String newDept = JOptionPane.showInputDialog(
				this, "Enter the name of a new department:", "Add Department", JOptionPane.OK_CANCEL_OPTION);
		
		if (newDept == null) {
			//They pressed cancel.
			return;
		}
		else if ((newDept = newDept.trim()).equals("")) {
			//They entered a blank string.
			return;
		}
		
		String[] currentDeptList = readDeptList();
		
		for (String dept : currentDeptList) {
			if (dept.compareToIgnoreCase(newDept) == 0) {
				JOptionPane.showMessageDialog(this, "This department already exists!", "Cannot add department",
						JOptionPane.ERROR_MESSAGE ); 
				return;		
			}
		}
		
		String result = modifyDepartment(newDept, 0);
		
		if (result.contains("success")) {
			String[] temp = new String[currentDeptList.length + 1];
			for (int c = 0; c < currentDeptList.length; c++) {
				temp[c] = currentDeptList[c];
			}
			temp[currentDeptList.length] = newDept;
			if (temp == null) System.err.println("TEmp is null");
			sortDepartmentList(temp);
			writeLocalDeptList(temp);
			
			deptDropDown.setModel(new javax.swing.DefaultComboBoxModel(temp));
		}
		
		JOptionPane.showMessageDialog(this, result, "Result", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void editDepartment(MouseEvent evt) {
		String dept = deptDropDown.getSelectedItem().toString();
		if (dept.equals("None")) {
			return;
		}
		
		String newDept = JOptionPane.showInputDialog(
				this, "Change " + dept + " to:" , "Edit Department", JOptionPane.OK_CANCEL_OPTION);
		
		if (newDept == null) {
			//Cancel button pressed.
			return;
		}
		else if ((newDept = newDept.trim()).equals("")) {
			//Empty string.
			return;
		}
		
		String[] currentDeptList = readDeptList();
		
		for (String d : currentDeptList) {
			if (d.compareToIgnoreCase(newDept) == 0) {
				JOptionPane.showMessageDialog(this, "Another department of the same name already exists!", 
						"Cannot change department", JOptionPane.ERROR_MESSAGE ); 
				return;		
			}
		}
		
		String result = modifyDepartment(dept+"|"+newDept, 2);
		
		if (result.contains("success")) {
			String[] temp = new String[currentDeptList.length + 1];
			for (int c = 0; c < currentDeptList.length; c++) {
				temp[c] = currentDeptList[c];
				if (temp[c].equalsIgnoreCase(dept)) {
					temp[c] = "";
				}
			}
			temp[currentDeptList.length] = newDept;
			sortDepartmentList(temp);
			writeLocalDeptList(temp);
			
			deptDropDown.setModel(new javax.swing.DefaultComboBoxModel(temp));
		}
		
		JOptionPane.showMessageDialog(this, result, "Result", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void deleteDepartment(MouseEvent evt) {
		String dept = deptDropDown.getSelectedItem().toString();
		if (dept.equals("None")) {
			return;
		}
		
		int n = JOptionPane.showConfirmDialog(this, "Really delete " + dept + "?", 
				"Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if (n == JOptionPane.YES_OPTION) {
			String result = modifyDepartment(dept, 1);
			
			if (result.contains("success")) {
				String[] currentDeptList = readDeptList();
				for (int c = 0; c < currentDeptList.length; c++) {
					if (currentDeptList[c].equalsIgnoreCase(dept)) {
						currentDeptList[c] = "";
						break;
					}
				}
				sortDepartmentList(currentDeptList);
				writeLocalDeptList(currentDeptList);
				
				deptDropDown.setModel(new javax.swing.DefaultComboBoxModel(readDeptList()));
				
				JOptionPane.showMessageDialog(this, result, "Result", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Change a department in the server's list of departments.
	 * The user is able to add, delete, or modify any departments. To add or delete a department, simply pass
	 * the name of the department as the parameter.  To modify the department, include a | symbol in-between
	 * the old department name (first) and the new department name (second).  For example, to change Nursing
	 * to Medical Care:<br>
	 * Nursing|Medical Care<br>
	 * @param dept Department to change.  If type = edit, pass as old_name|new_name, or it won't work.
	 * @param type Type of change to make:<br>
	 * 				0 - Add a department
	 * 				1 - Delete a department
	 * 				2 - Edit a department
	 */
	public String modifyDepartment(String dept, int type) {
		String result = "";
		// Trim any leading or trailing whitespace.
		dept = dept.trim();
		
		final Address MAIN_SERVER = WowHelper.getMainServerAddress();
		
		final int ADD_DEPT = 0;
		final int DEL_DEPT = 1;
		final int EDIT_DEPT = 2;
		
		/*java.net.Socket s = null;
		PrintWriter out = null;
		BufferedReader in = null;*/
		try {
			/*s = new java.net.Socket(MAIN_SERVER.GetAddress(), MAIN_SERVER.getPort());
			out = new PrintWriter(s.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					s.getInputStream()));*/
			
			if (type == ADD_DEPT) {
				//out.println("DEPTADD: " + dept);
				ConnectionHandler.sendMessage("DEPTADD: " + dept);
			}
			else if (type == DEL_DEPT) {
				//out.println("DEPTDEL: " + dept);
				ConnectionHandler.sendMessage("DEPTDEL: " + dept);
			}
			else if (type == EDIT_DEPT) {
				if (!dept.contains("|")) {
					return "Invalid attempt to edit a department.\n(Requires old name and new name)";
				}
				
				final String newDepartment = dept.split("\\|")[1];
				for (final String existingDept : readDeptList()) {
					if (newDepartment.equalsIgnoreCase(existingDept)) {
						return "Invalid attempt to edit a department.\n(New name already exists!)";
					}
				}
				
				//out.println("DEPTUPD: " + dept);
				ConnectionHandler.sendMessage("DEPTUPD: " + dept);
			}
			
			//result = in.readLine();
			result = ConnectionHandler.readLine();
		}
		catch (IOException e) {
			return "Cannot contact the VTC server " + MAIN_SERVER.GetAddress() + "\n" +
					"(" + e.getMessage() + ")";
		}
		finally {
			/*try {
				s.close();
			}
			catch (IOException e) {
			}*/
		}
		
		return result;
	}
	
	/**
	 * Write a newer list of departments to dept-list.txt.
	 * @param list List of departments to write.
	 */
	public void writeLocalDeptList(String[] list) {
		try {
			PrintWriter out = new PrintWriter(new java.io.FileWriter("dept-list.txt"));
			for (String line : list) {
				if (!line.equals("") && !line.equals("None")) {
					out.println(line);
				}
			}
			
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sort the list of departments alphabetically in ascending order.
	 * @param list The most recent list of departments.
	 */
	public String[] sortDepartmentList(String[] deptList) {
		String[] list = deptList;
		
		boolean done = false;
		while (!done) {
			done = true;
			for (int c = 1; c < list.length - 1; c++) {
				if (list[c].compareToIgnoreCase(list[c+1]) > 0) {
					String temp = list[c];
					list[c] = list[c+1];
					list[c+1] = temp;
					done = false;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Do actions when window is closing
	 * 
	 * @param e window event object
	 */
	public void windowClosing(WindowEvent e) {
		System.out.println("Closed");
    }
    
	/**
	 * Do actions when window has closed
	 * 
	 * @param e window event object
	 */
    public void windowClosed(WindowEvent e) {
    	System.out.println("Closed");
    }
    
    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    
    }

    public void windowGainedFocus(WindowEvent e) {
    }

    public void windowLostFocus(WindowEvent e) {
    
    }

    public void windowStateChanged(WindowEvent e) {
    }
   
    /**
     * Reads from a department list to provide a dropdown model.
     */
    public String[] readDeptList() {
    	ArrayList<String> temp = new ArrayList<String>();
    	String[] result;
    	
    	temp.add("None");
    	
    	java.io.File f = new java.io.File("dept-list.txt");
		try {
			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.FileReader(f));
			java.util.Scanner scan = new java.util.Scanner(reader);
			
			while (scan.hasNextLine()) {
				temp.add(scan.nextLine());
			}
			
			scan.close();
			try {
				reader.close();
			}
			catch (IOException e) {}
		}
		catch (java.io.FileNotFoundException sadface) {
			System.err.println(sadface);
			JOptionPane.showMessageDialog(null, "The file 'dept-list.txt' was not found.\n" + 
					"Please reinstall the WOW admin client.", "File not found.", JOptionPane.ERROR_MESSAGE);
		}
		
		result = new String[temp.size()];
		for (int c = 0; c < temp.size(); c++) {
			result[c] = temp.get(c);
		}
		
		return result;
    }
    
    
//************************************************************************************************
//*****************************************End Listeners******************************************
//************************************************************************************************
//	borrowed from: http://www.exampledepot.com/egs/javax.swing.table/ColHeadEvent.html
	public class ColumnHeaderListener extends MouseAdapter {
		/**
		 * Handles actions when mouse is clicked.<br>
		 * Checks to see which column has been selected, if any.
		 * 
		 * @param evt mouse event object
		 */
		public void mouseClicked(MouseEvent evt) {
			JTable table = ((JTableHeader)evt.getSource()).getTable();
			TableColumnModel colModel = table.getColumnModel();

			// The index of the column whose header was clicked
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			int mColIndex = table.convertColumnIndexToModel(vColIndex);

			// Return if not clicked on any column header
			if (vColIndex == -1) {
				return;
			}
			DefaultTableModel dtm = getDefaultTableModel(table);
			
			sortTable(mColIndex, table, ascend, false, dtm);
			editButton.setEnabled(false);
		}
	}
	 
}
