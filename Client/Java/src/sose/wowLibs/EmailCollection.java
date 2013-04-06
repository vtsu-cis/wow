package src.sose.wowLibs;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class EmailCollection extends JFrame implements ClipboardOwner{

	private javax.swing.JButton clearButton;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton copyButton;
	private javax.swing.JTextField emailTxtBox;
	private javax.swing.JLabel jLabel1; 
	private ArrayList<String> emails = new ArrayList<String>();
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public EmailCollection() {
		initComponents();

	}

	private void initComponents() {

		this.setTitle("Email Collection Window");
		
		try {			
			ClassLoader cl = this.getClass().getClassLoader();
			this.setIconImage(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif"))); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jLabel1 = new javax.swing.JLabel();
		emailTxtBox = new javax.swing.JTextField();
		copyButton = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jLabel1.setText("E-Mail Address Collection");

		emailTxtBox.setText("");

		copyButton.setText("Copy");
		copyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyButtonActionPerformed(evt);
			}
		});

		clearButton.setText("Clear");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});


		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(emailTxtBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
								.add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
										.add(copyButton)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 182, Short.MAX_VALUE)
										.add(clearButton)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(closeButton))
										.add(jLabel1))
										.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(jLabel1)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(emailTxtBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
								.add(closeButton)
								.add(clearButton)
								.add(copyButton))
								.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		pack();
	}                      

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				EmailCollection ecw = new EmailCollection();
				ecw.setVisible(true);
				ecw.setDefaultCloseOperation(EXIT_ON_CLOSE);
			}
		});
	}

	public void addEmail(String s)
	{
		emails.add(s);
		emailTxtBox.setText(formatEmails(emails));
	}

	public void copyEmailsToClipBoard()
	{
		StringSelection stringSelection = new StringSelection(emailTxtBox.getText());
		clipboard.setContents( stringSelection, this );
	}

	public void clearAllEmails()
	{
		emailTxtBox.setText("");
		GUIBase.listPeople.clear();
		emails.clear();
	}

	public String formatEmails(ArrayList<String> email)
	{
		String tempString = "";

		for(String s : email)
		{
			tempString = tempString + s + "; ";
		}

		return tempString;
	}

	private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		copyEmailsToClipBoard();
	}

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		clearAllEmails();
	}

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		this.setVisible(false);	
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub

	}

}

