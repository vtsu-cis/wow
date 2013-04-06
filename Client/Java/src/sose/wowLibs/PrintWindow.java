package src.sose.wowLibs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PrintWindow extends JFrame {
	/**
	 * Create a new window to ask the user what type of printing they would like to do.
	 * @param parent allow access to parent functions.
	 */
	public PrintWindow(src.sose.wowBasicSearch.MainClientGUIBasic parent) {
		this.parent = parent;
		initializeComponents();
		setVisible(true);
	}
	
	/**
	 * Initialize JFrame components.
	 */
	private void initializeComponents() {
		main = new JPanel();
		printButton = new JButton();
		cancelButton = new JButton();
		printAllByDept = new JRadioButton();
		printAllByLastName = new JRadioButton();
		printResults = new JRadioButton();
		group = new ButtonGroup();
		radioPanel = new JPanel();
		butPanel = new JPanel();
		
		printAllByLastName.setText("ALL sorted by Last Name");
		printAllByLastName.setSelected(true);
		
		printAllByDept.setText("ALL sorted by Department");
		
		printResults.setText("JUST current search results");
		
		group.add(printAllByLastName);
		group.add(printAllByDept);
		group.add(printResults);
		
		radioPanel.setLayout(new java.awt.GridLayout(0, 1)); 	
		radioPanel.add(printAllByLastName);
		radioPanel.add(printAllByDept);
		radioPanel.add(printResults);
		
		java.awt.GridLayout butgrid = new java.awt.GridLayout(1, 0);
		butgrid.setHgap(5);
		butPanel.setLayout(butgrid);
		butPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		butPanel.add(printButton);
		butPanel.add(cancelButton);
		
		
		printButton.setText("Print");
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printActionPerformed(e);
			}
		});
		
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		try {
			this.setIconImage(ImageIO.read(ClassLoader.getSystemResource("images/WoWsuperSMALL.gif")));
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
			System.err.println("(Unable to load images/WoWsuperSMALL.gif)");
		}
		
		main.setLayout(new java.awt.BorderLayout());
		
		main.add(radioPanel, java.awt.BorderLayout.LINE_START);
		main.add(butPanel, java.awt.BorderLayout.PAGE_END);
		
		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.add(main);
		
		pack();
		
		setTitle("Print");
		if (parent != null) {
			setLocationRelativeTo(parent);
		}
	}
	
	/**
	 * Print their items.
	 */
	private void printActionPerformed(ActionEvent e) {
		setVisible(false);
		if (printAllByDept.isSelected()) {
			parent.printAllRecordsByDept( );
		}
		else if (printAllByLastName.isSelected()) {
			parent.printAllRecords();
		}
		else if (printResults.isSelected()) {
			parent.printCurrentRecords();
		}
		
		dispose();
	}
	
	private JPanel main;
	private JButton printButton;
	private JButton cancelButton;
	private JRadioButton printAllByDept;
	private JRadioButton printAllByLastName;
	private JRadioButton printResults;
	private ButtonGroup group;
	private JPanel radioPanel;
	private JPanel butPanel;
	src.sose.wowBasicSearch.MainClientGUIBasic parent;
}