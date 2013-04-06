package src.sose.wowLibs;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import src.sose.wowBL.Field;

public class TabbedMoreInfo extends JFrame 
{
	protected static int numWindowsOpen=0;
	protected static int numTabsOpen=0;
    protected Rectangle dimension;
   
    private JTabbedPane tabPane;
    private JScrollPane jScrollPane1;
    private JPanel mainPane; 
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> servers = new ArrayList<String>();
    ImageIcon imageIcon;

    
    public JTabbedPane getTPane()
    {
    	return tabPane;
    }
    public TabbedMoreInfo() {
        initComponents(new ArrayList<Field>());
    }
    
    public TabbedMoreInfo(String title, String server, ArrayList<Field> fields) {
    	names.add(title);
    	servers.add(server);
    	initComponents(fields);
    }
    
    public TabbedMoreInfo(ArrayList<Field> fields) 
    {
    	names.add("Person");
    	servers.add("VTC");
        initComponents(fields);
    }
    
    public void initComponents(ArrayList<Field> fields)
    {
        numWindowsOpen++;
        ClassLoader cl = this.getClass().getClassLoader();
        
		try {
			imageIcon = new ImageIcon(ImageIO.read(cl.getResource("images/x2.GIF")));
		} catch (IOException e2) {
			System.err.println("Close Icon not found");
			e2.printStackTrace();
			
		}
    	try {
			this.setIconImage(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif")));
		} catch (IOException e2) {
			System.err.println("TitleBar Icon not found");
			e2.printStackTrace();
			
		}
		
    	this.setTitle("More Info Window");

    	setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    	addWindowListener(new java.awt.event.WindowAdapter() {
    		public void windowClosing(java.awt.event.WindowEvent evt) {
    			formWindowClosing(evt);
    		}
    	});
    	
    	
        try {
        	final Border bkgrnd = new CentredBackgroundBorder(ImageIO.read(cl.getResource("images/WoW.gif")));
			JPanel cp = new JPanel();
			cp.setBorder(bkgrnd);
	        cp.repaint();
	        setContentPane(cp);
		} catch (IOException e) {
			System.out.println("BackGround Image Not Found");
			e.printStackTrace();
		} 
        
		
    	tabPane = new JTabbedPane();
    	tabPane.addMouseListener(new MouseAdapter() {
    	    public void mouseReleased(MouseEvent evt) {
    	        if (tabPane.getTabCount() == 0) {
    	            return;
    	        }
    	        
    	        if (!evt.isPopupTrigger()) {
    	            IconProxy iconProxy = (IconProxy) tabPane.getIconAt(
    	            		tabPane.getSelectedIndex());
    	            
    	            if (iconProxy.contains(evt.getX(), evt.getY())) {
    	            	int removeIndex = names.indexOf(tabPane.getTitleAt(tabPane.getSelectedIndex()));
    	            	if(removeIndex != -1)
    	            	{
    	            		names.remove(removeIndex);
        	            	servers.remove(removeIndex);	
        	            	GUIBase.tabbedPeople.remove(removeIndex);
    	            	}
    	            	else
    	            	{
    	            		System.out.println("Not found");
    	            	}
    	            	numTabsOpen--;
    	            	tabPane.removeTabAt(tabPane.getSelectedIndex());
    	            	
    	            	if(numTabsOpen!=0)
    	            	{
    	            		pack();
    	            	}
    	            }
    	        }
    	    }
    	});
    	
    	tabPane.setTabPlacement(JTabbedPane.LEFT);
    	if(fields.size() != 0)
    	{
    		numTabsOpen++;
        	tabPane.addTab(names.get(0), new IconProxy(imageIcon), setUpPanel(fields, servers.get(0)));
    	}
    	
    	getContentPane().add(tabPane);
    	pack();
    	
    }
    
	private JScrollPane setUpPanel(ArrayList<Field> fields, String serverName) 
    {	
    	jScrollPane1 = new JScrollPane();
    	mainPane = new JPanel();
    	JLabel recordLabel = new JLabel();
    	ClassLoader cl = this.getClass().getClassLoader();

    	recordLabel.setBackground(new java.awt.Color(51, 255, 51));

    	URL imgURL2 = cl.getResource("images/greenDot.GIF");
    	recordLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL2)));
    	recordLabel.setText(serverName + " Record");
    	
    	ArrayList<JLabel> jLabels = new ArrayList<JLabel>();
    	ArrayList<JTextField> jTextBoxes = new ArrayList<JTextField>();

    	for(Field f : fields)
    	{
    		JLabel tempLabel = new JLabel(f.getName());
    		jLabels.add(tempLabel);
    		JTextField tempField = new JTextField(f.getValue());
    		tempField.setEditable(false);
    		jTextBoxes.add(tempField);
    	}

    	mainPane.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();

    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
    	c.insets = new Insets(10,10,10,0);  //padding
    	mainPane.add(recordLabel, c);

    	for(int i=0; i<jLabels.size();i++)
    	{
    		c.insets = new Insets(5, 28, 10, 20);
    		c.gridx = 0;
    		c.gridy = i+1;
    		c.anchor = GridBagConstraints.LINE_START;
    		mainPane.add(jLabels.get(i), c);

    		c.insets = new Insets(5,0,10,20);
    		c.weightx = 0.0;
    		c.anchor = GridBagConstraints.LINE_END;
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.weightx = 0.5;
    		c.gridx = 1;
    		c.gridy = i+1;
    		mainPane.add(jTextBoxes.get(i), c);
    	}

    	jScrollPane1.setViewportView(mainPane);
  
    	return jScrollPane1;
    }                  
	
	public void addNew(String title, String server, ArrayList<Field> fields)
	{
		numTabsOpen++;
		names.add(title);
		servers.add(server);
    	tabPane.addTab(names.get(numTabsOpen-1), new IconProxy(imageIcon), setUpPanel(fields, servers.get(numTabsOpen-1)));
    	pack();
	}
	
	public void setSelectedPerson(String title)
	{
		int count = tabPane.getTabCount();
		
		for(int i = 0;i<count;i++)
		{
			if(tabPane.getTitleAt(i).equals(title))
			{
				tabPane.setSelectedIndex(i);
			}
		}
	}
	
    private void formWindowClosing(java.awt.event.WindowEvent evt) 
    {
    	numWindowsOpen=0;
    	numTabsOpen=0;
    	names.clear();
    	servers.clear();
    	GUIBase.tabbedPeople.clear();
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	ArrayList<Field> tempFields = new ArrayList<Field>();
            	tempFields.add(new Field("First Name", "Nick"));
            	tempFields.add(new Field("Last Name", "Guertin"));
            	tempFields.add(new Field("Phone Number", "524-6367"));
            	tempFields.add(new Field("E-Mail", "n_guertin@yahoo.com"));
            	TabbedMoreInfo dmi = new TabbedMoreInfo("Test", "VTC", tempFields);
                dmi.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                dmi.setVisible(true);
                dmi.addNew("Test2", "VTC", tempFields);
            }
        });
    }   
    
    public class IconProxy implements Icon {
    	
        private int x;
        private int y;
        private int xMax;
        private int yMax;
        private int width;
        private int height;
        private Icon icon;
        
        public IconProxy(Icon icon) {
            this.icon = icon;
            width  = icon.getIconWidth();
            height = icon.getIconHeight();
        }
        
        public int getIconWidth() {
            return width;
        }
        
        public int getIconHeight() {
            return height;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            this.x = x;
            this.y = y;
            xMax = x + width;
            yMax = y + height;
            icon.paintIcon(c, g, x, y);
        }
        
        public boolean contains(int x, int y) {
            return x >= this.x && x <= xMax && y >= this.y && y <= yMax;
        }
    }

    public class CentredBackgroundBorder implements Border {
        private final BufferedImage image;
     
        public CentredBackgroundBorder(BufferedImage image) {
            this.image = image;
        }
     
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            int x0 = x + (width-image.getWidth());//*2;
            int y0 = y + (height-image.getHeight())/2;
            g. drawImage(image, x0, y0, null);
        }
     
        public Insets getBorderInsets(Component c) {
            return new Insets(0,0,0,0);
        }
     
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
