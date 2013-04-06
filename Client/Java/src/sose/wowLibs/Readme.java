package src.sose.wowLibs;

import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Readme extends javax.swing.JFrame {
	
	byte[] infile = new byte[100000];
	String filestring;
	String fileName = "Readme.txt";
	
    public Readme() {
        initComponents();
    }
    
    public Readme(String s) {
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
    	String configPath = (new File(".").getAbsolutePath());
		File ffile = new File(configPath + "/" + fileName);
    	
		// read the file
		try {
			FileInputStream fis = new FileInputStream(ffile); 
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			try {
				int filelength = dis.read(infile);
				filestring = new String(infile, 0, filelength);
				//System.out.println("FILE CONTENT=" + filestring);
			} catch(IOException iox) {
				filestring = new String("File read error...");
				iox.printStackTrace();
			}
		} catch (FileNotFoundException fnf) {
			filestring = new String("File not found...");
			fnf.printStackTrace();
		}

     JTextPane textPane = new JTextPane();
     textPane.setFont(new Font("Courier New", Font.PLAIN, 12));
     StyledDocument doc = textPane.getStyledDocument();

//     Load the text pane with styled text.
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
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Readme().setVisible(true);
            }
        });
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
