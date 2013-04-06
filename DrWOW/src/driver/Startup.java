package driver;

import javax.swing.JOptionPane;

import util.*;

/**
 * Startup class for the Dr. WOW program. This is where options are interpreted and
 * the GUI is set up and displayed.
 */
public class Startup {
	/**
	 * Entry point for the program.
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			Config.readConfigFile();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
				"Unable to read the configuration file \"wow.conf\".\n" +
				"Error details: " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			
			System.exit(1);
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new gui.StatusViewerWindow();
            }
        });
	}
}
