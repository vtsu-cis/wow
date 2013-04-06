package src.sose.wowLibs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import src.sose.wowBL.Field;
/**
 *
 * @author  Boomer
 */
public class DynMoreInfo extends JFrame {
	
    protected static int numWindowsOpen=0;
    protected Rectangle dimension;
    
    private javax.swing.JLabel recordLabel;
    private JScrollPane jScrollPane1;
    private JPanel mainPane; 
    private String[] server;
    public DynMoreInfo() {
        initComponents(new ArrayList<Field>());
        numWindowsOpen++;
    }
    
    public DynMoreInfo(String title, ArrayList<Field> fields) {
    	this(fields);
        this.setTitle(title);
        server = (this.getTitle().split("@"));
        recordLabel.setText(server[server.length-1] + " Record");
    }
    
    public DynMoreInfo(ArrayList<Field> fields) 
    {
    	numWindowsOpen++;
        initComponents(fields);
    }
                             
    private void initComponents(ArrayList<Field> fields) 
    {	
    	
    	recordLabel = new javax.swing.JLabel();
    	jScrollPane1 = new JScrollPane();
    	//jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	mainPane = new JPanel();
    	//setResizable(false);
    	URL giIconURL = ClassLoader.getSystemResource("images/WoWsuperSMALL.gif");
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage(giIconURL));

    	recordLabel.setBackground(new java.awt.Color(51, 255, 51));

    	URL imgURL2 = ClassLoader.getSystemResource("images/greenDot.GIF");
    	recordLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL2)));
    	

    	setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    	addWindowListener(new java.awt.event.WindowAdapter() {
    		public void windowClosing(java.awt.event.WindowEvent evt) {
    			//formWindowClosing(evt);
    		}
    	});

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
    	getContentPane().add(jScrollPane1);

    	pack();
    	setSize(this.getWidth()+20, this.getHeight());
    }                    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	ArrayList<Field> tempFields = new ArrayList<Field>();
            	tempFields.add(new Field("First Name", "Nick"));
            	tempFields.add(new Field("Last Name", "Guertin"));
            	tempFields.add(new Field("Phone Number", "524-6367"));
            	tempFields.add(new Field("E-Mail", "n_guertin@yahoo.com"));
                DynMoreInfo dmi = new DynMoreInfo("Test", tempFields);
                dmi.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                dmi.setVisible(true);
            }
        });
    }                 
    
//    private void formWindowClosing(java.awt.event.WindowEvent evt) 
//    {
//    	GUIBase.removeMI(this);
//    }
//    
    public Rectangle getMILocation()
    {
    	dimension = new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    	return dimension;
    }
}

