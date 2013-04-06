package src.sose.wowLibs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JEditorPane;

/**
 * Subclass of javax.swing.JEditorPane. Used for displaying General Info Window
 *
 */
public class NewEditorPane extends JEditorPane {

	public static void main(String[] args) {}

	/**
	 * Wraps the protected getStream method from JEditorPane in a public method
	 * 
	 * @param url URL to be displayed on the page
	 * 
	 * @return an InputStream pointing to URL passed in
	 */
	public InputStream grabStream(URL url)
	{
		try {
			return getStream(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
