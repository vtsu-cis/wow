package src.sose.wowLibs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Notification popup that will display text passed to it. Has option of keeping focus on itself until closed.
 *
 */
public class PopupMessage extends javax.swing.JFrame implements WindowListener,
WindowFocusListener,
WindowStateListener{
	
	javax.swing.JButton jButton1 = new javax.swing.JButton();
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    javax.swing.JLabel  jLabel2 = new javax.swing.JLabel();
    javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    
    boolean alwaysInFocus = true;
    
    public PopupMessage() {
        initComponents();
    }
    
    public PopupMessage(String s, boolean alwaysFocus)
    {
    	initComponents();
    	jLabel1.setText(s);
    	alwaysInFocus = alwaysFocus;
    }
    private void initComponents() {
        
        addWindowListener(this);
        addWindowFocusListener(this);
        addWindowStateListener(this);
        
		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	
        jLabel1.setText("Sorry, feature not available yet");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchButtonActionPerformed(evt);
			}
		});
        
        URL imgURL2 = ClassLoader.getSystemResource("images/WoWlittle.gif");
        jLabel1.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL2)));       
        jPanel1.add(jLabel1);
        
        javax.swing.JPanel jPanel2 = new  javax.swing.JPanel();
        
        jPanel2.setLayout(new GridLayout(1,3));
        jPanel2.add(new javax.swing.JLabel());
        jPanel2.add(jButton1);
        jPanel2.add(new javax.swing.JLabel());
        
        jPanel1.add(BorderLayout.SOUTH, jPanel2);
       
        getContentPane().add(BorderLayout.CENTER, jPanel1);
        setSize(275,125);
        setResizable(false);
        setTitle(":)");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PopupMessage().setVisible(true);
            }
        });
    }
    
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
    	this.setVisible(false);
	}
    

    public void windowClosing(WindowEvent e) {
    	
    }
    
    public void windowClosed(WindowEvent e) {
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
    	if(alwaysInFocus)
    	{
    		requestFocus();
    	}
    }

    public void windowGainedFocus(WindowEvent e) {
    }

    public void windowLostFocus(WindowEvent e) {
    	if(alwaysInFocus)
    	{
    		requestFocus();
    	}
    }

    public void windowStateChanged(WindowEvent e) {
    }
    
}

