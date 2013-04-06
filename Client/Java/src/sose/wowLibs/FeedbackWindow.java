package src.sose.wowLibs;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class FeedbackWindow extends JFrame {
	
	JScrollPane editorPane = null;
	 
	 public FeedbackWindow() {
		 editorPane = createEditorPane();
		 
		 if (editorPane == null) {
			 dispose();
			 return;
		 }
		 
		 editorPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		 editorPane.setPreferredSize(new Dimension(250, 145));
		 editorPane.setMinimumSize(new Dimension(10, 10));
		this.setTitle("Feedback Page");
		
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			this.setIconImage(ImageIO.read(cl.getResource("images/WoWsuperSMALL.gif")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        
		this.add(editorPane);
		this.setSize(600, 380);
		GUIBase.numFBWindowsOpen++;
		setVisible(true);
	}
	 
	public static void main(String[] args) 
	{
		new FeedbackWindow();
	}
		
	private JScrollPane createEditorPane() 
	{
			        
	    final NewEditorPane jt = new NewEditorPane();
	    // make read-only
	    jt.setEditable(false);
	    // follow links
	    jt.addHyperlinkListener(new HyperlinkListener () {
	      public void hyperlinkUpdate(
	          final HyperlinkEvent e) {
	        if (e.getEventType() == 
	            HyperlinkEvent.EventType.ACTIVATED) {
	          SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	              // Save original
	              Document doc = jt.getDocument();
	              try {
	                URL url = e.getURL();
	                jt.setPage(url);
	              } catch (IOException io) {
	                JOptionPane.showMessageDialog (
	                  FeedbackWindow.this, "Can't follow link", 
	                  "Invalid Input", 
	                   JOptionPane.ERROR_MESSAGE);
	                jt.setDocument (doc);
	              }
	            }
	          });
	        }
	      }
	    });
	    
		java.net.URL helpURL;
		try 
		{
			helpURL = new java.net.URL("http://quicksilver.ecet.vtc.edu/validate/feedback.html");
			if(helpURL != null)
			{
				Socket  s = new Socket();
				boolean flag = false;
				try
				{
					s.connect(new InetSocketAddress(helpURL.getHost(), 80), 800);
					flag = true;
					s.close();

				}
				catch(IOException ex)
				{
					displayFileinBrowser(jt);
				}

				if(flag)
				{
					try {
						jt.setPage(helpURL);
						getPageOffOfBrowser(jt);
					} catch (IOException e1) {
						System.out.println(e1.toString());
						JOptionPane.showMessageDialog (
				                  FeedbackWindow.this, 
				                  "The feedback page cannot be displayed right now.\n" +
				                  "Make sure you're using a VTC computer and try again.", 
				                  "Feedback Page Cannot Be Displayed", 
				                   JOptionPane.ERROR_MESSAGE);
						
						return null;
					}
				}
			}
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
			System.out.println("URL not formatted correctly");
            JOptionPane.showMessageDialog (
	                  FeedbackWindow.this, "Can't Open Feedback Page", 
	                  "Invalid Web Address", 
	                   JOptionPane.ERROR_MESSAGE);
		}
		
	    JScrollPane pane = new JScrollPane();
	    pane.setBorder (
	      BorderFactory.createLoweredBevelBorder());
	    pane.getViewport().add(jt);
	    
		return pane;
	}
	
	public void getPageOffOfBrowser(NewEditorPane jt)
	{
		ArrayList<String> temp = new ArrayList<String>();

		//read
		try
		{
			InputStreamReader ISReader = new InputStreamReader(jt.grabStream(jt.getPage()));
			BufferedReader reader = new BufferedReader(ISReader);
			String line = "";

			while ((line = reader.readLine()) != null)
			{
				temp.add(line);
			}
			reader.close();
			ISReader.close();
		}
		catch(Exception ex)
		{
			System.out.println("File I/O Error");
			ex.printStackTrace();
		}

		//write
		try
		{
			String configPath = (new File(".").getAbsolutePath());
			File config = new File(configPath + "/FB.htm");

			FileWriter fileWriter = new FileWriter(config);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			String line = "";

			System.out.println(line);
			for(String word : temp)
			{
				writer.write(word);
				writer.newLine();
			}

			writer.close();
			fileWriter.close();
		}
		catch(Exception ex)
		{
			System.out.println("File I/O Error");
			ex.printStackTrace();
		}


	}

	public void displayFileinBrowser(NewEditorPane jt)
	{

		String configPath = (new File(".").getAbsolutePath());
		File config = new File(configPath + "/FB.htm");
		try {
			jt.setPage(new URL("file:///"+config.getAbsolutePath()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
    private void formWindowClosing(java.awt.event.WindowEvent evt) 
    {
    	GUIBase.numFBWindowsOpen--;
    }
}
