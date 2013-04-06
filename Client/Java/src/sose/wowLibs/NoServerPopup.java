package src.sose.wowLibs;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Notification popup informing users whether or not certain servers could be reached
 *
 */
public class NoServerPopup extends JFrame {

	private String output;
	
	public NoServerPopup(String serverlist) {
		output = "The following servers could not be contacted\n" + serverlist;
		initComponents();
	}

	private void initComponents() {
		jButton1 = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();

		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	setTitle("Servers Unavailable");
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
		jTextArea1.setText(output);
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

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new About().setVisible(true);
			}
		});
	}

	private javax.swing.JButton jButton1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextArea1; 
	
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) 
	{  	
		setVisible(false);
	}
}

