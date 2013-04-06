package src.sose.wowLibs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import src.sose.wowBL.Email;
import src.sose.wowBL.Field;
import src.sose.wowBL.Person;
import src.sose.wowBL.Phone;
import src.sose.wowDA.DataAccess;

/**
 * A superclass containing common methods used in all WoW clients
 *
 * @author Nick Guertin and David Ransom
 */
public class GUIBase extends javax.swing.JFrame implements Printable {

	public static int columnCount = 5;
	public static int defaultSortColumn = 0;
	public static boolean[] ascend = {true, false, true, true, false};//used for table sorting
	DefaultTableModel model;
	//static ArrayList<DynMoreInfo> MIWindows = new ArrayList<DynMoreInfo>();
	//DynMoreInfo firstMIWindow;
	//private static int rowSelected = -1;//used for right click menu
	protected int MISecondRowCount = 0;
	//boolean isNewSearch = fla;
	public static String [] columnNames = new String [] {
			"Last Name", "First Name", "Phone Num.", "Email Address", "Serv"
	};
	static ImageIcon ascendIcon;
	static ImageIcon descendIcon;
	
	public static Object[][] blankRows = new Object [][] {
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, null, null, null}
	};
	public static String[][] noResults = {
			{"No Results Found", null, null, null, null},
			{null, null, null, null, null}};
	public static double[] columnPercents = {.22, .2, .2, .32, .06}; //used for sizing columns
	public static double[] columnPercentsPrint = {.26, .22, .16, .30, .06}; //used for sizing columns
	public static ArrayList<Person> searchResults;
	public static ArrayList<String[]> tableData;
	public static TabbedMoreInfo tmi;
	public static ArrayList<Person> tabbedPeople = new ArrayList<Person>();
	public static ArrayList<Person> listPeople = new ArrayList<Person>();
	public JTable printingTable;
	 private final static int POINTS_PER_INCH = 72;
	 public static int numGIWindowsOpen = 0;
	 public static int numFBWindowsOpen = 0;
	 public static int numFLWindowsOpen = 0;
	 
	 /**
	  * Searches an ArrayList of DataAccess objects using the criteria specified in 
	  * the search string. Returns an ArrayList of string arrays that will be used 
	  * by printTable directly for display in a JTable. 
	  *
	  * @param criteria  string to search on
	  * @param das       collection of known servers to search in
	  * 
	  * @return          all matching records. empty if no records were found. 
	  * @see             #printTable(java.util.ArrayList, javax.swing.JTable)
	  */
	public static ArrayList<String[]> search(String criteria, ArrayList<DataAccess> das)
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		if(criteria.startsWith("@"))
		{
			Field search = new Field();
			String[] entry = criteria.split(" ", 2); //  Splits the records into the keyword 
													 //    and the rest of the results
			
			if (entry.length == 1) {
				String temp = entry[0];
				entry = new String[] {temp, " "};
			}
			
			if(entry[0].equalsIgnoreCase("@dept") || entry[0].equalsIgnoreCase("@department"))
			{
				search.setName("Department");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@email")) 
			{
				search.setName("Email");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@office"))
			{
				search.setName("Office");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@role"))
			{
				search.setName("Role");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@campus"))
			{
				search.setName("Campus");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@phone"))
			{
				search.setName("Phone");
				search.setValue(entry[1]);
			}
			else if(entry[0].equalsIgnoreCase("@fax")) {
				search.setName("Fax");
				search.setValue(entry[1]);
			}
			else 
			{
				search.setName("");
				search.setValue("");
			}
			
			ArrayList<Person> results = new ArrayList<Person>();
			
			for(DataAccess da : das)
			{
				results.addAll(da.query(search));
			}
			
			searchResults = results;
		}
		else if(criteria.equalsIgnoreCase("dean") 
				|| criteria.equalsIgnoreCase("bookstore") 
				|| criteria.equalsIgnoreCase("book store")
				//add special search terms as necessary
				)
		{
			Field search = new Field();
			
			if(criteria.equalsIgnoreCase("dean") )
			{
				search.setName("Department");
				search.setValue(criteria);
			}
			else if(criteria.equalsIgnoreCase("bookstore") )
			{
				search.setName("Department");
				search.setValue("bookstore");
			}
			else if(criteria.equalsIgnoreCase("book store"))
			{
				search.setName("Department");
				search.setValue("bookstore");
			}
			else 
			{
				search.setName("");
				search.setValue("");
			}
			
			ArrayList<Person> results = new ArrayList<Person>();
			
			ArrayList<String> usedDAs = new ArrayList<String>(das.size());
			for(DataAccess da : das)
			{
				boolean doAdd = true;
				for (String used : usedDAs) {
					if (used.equalsIgnoreCase(da.getName())) {
						doAdd = false;
						break;
					}
				}
				
				if (doAdd) {
					results.addAll(da.query(search));
					usedDAs.add(da.getName());
				}
			}
			
			searchResults = results;
		}
		else
		{
			Person searchSubject = new Person();

			String firstNameOnly = criteria;
			boolean isFirst = firstNameOnly.startsWith(" ");

			int validSearch=0;

			if(isFirst)
			{
				String test = firstNameOnly.trim();
				validSearch = searchSubject.setFirstName(test);
			}
			else 
			{
				String[] test = criteria.split(" ");
				if(test.length == 2)
				{
					validSearch = searchSubject.setLastName(test[0]);
					if(validSearch == 0){validSearch = searchSubject.setFirstName(test[1]);}
				}
				else
				{
					validSearch = searchSubject.setLastName(test[0]);
				}
			}

			

			if(validSearch==1)
			{
				return result;
			}

			searchResults = searchSubject.query(das);
			
		}
		
		if(searchResults.size() != 0)
		{
			for(Person searchResult : searchResults) 
			{
				String[] data = {searchResult.getLastName(), searchResult.getFirstName(), 
						searchResult.getPhoneNumber(), searchResult.getEmail(), 
						searchResult.getServer(), searchResult.getCampus(), 
						searchResult.getFields().get(4).getValue()};
				result.add(data);
			}
		}
		tableData = result;
		return result;
	}
	
	 /**
	  * Modifies a JTable to use a specific table header. 
	  *
	  * @param column  default column to use for rendering ascend/descend indicator
	  * @param ascend  <code>true</code>  : use icon indicating sorting in ascending order<br>
	  *                &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
	  *                <code>false</code> : use icon indicating sorting in descending order
	  * @param jt      table to be rendered
	  * 
	  * @throws IOException if icon files cannot be read. 
	  */
	public static void createTable(int column, boolean ascend, JTable jt){

		String[] headerNames = columnNames;

		jt.getTableHeader().getColumnModel().getColumn(column).setHeaderRenderer(iconHeaderRenderer);
		Icon headerIcon;
		try {
			ascendIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/ascend.gif")));
			descendIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/descend.gif")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if(ascend)
		{
			headerIcon = descendIcon;
		}
		else
		{
			headerIcon = ascendIcon;
		}
		
		jt.getColumnModel().getColumn(column).setHeaderValue(
				new TextAndIcon(headerNames[column], headerIcon));

	}
	
	/**
	 * Resizes table based on values in private member {@link #columnPercents}  
	 * 
	 * @param jt table to be resized
	 */
	public static void resizeTable(JTable jt)
	{  
	    for(int i=0;i<jt.getColumnCount();i++)
	    {
		    TableColumn col = jt.getColumnModel().getColumn(i);
		    int width = (int)(jt.getWidth()*columnPercents[i]);
		    col.setPreferredWidth(width);
	    }
	}
	
	/**
	 * Prints a the collection of the string arrays to the table. Sets the table header renderer and
	 * resizes the table. 
	 * 
	 * @param info information to be printed. Usually provided by {@link #search(String, ArrayList)} method
	 * @param table table to be printed to
	 */
	public static void printTable(ArrayList<String[]> info, JTable table)
	{
		String[][] results2;

		if(info.size() != 0)
		{

			Object[] results = info.toArray();
			results2 = new String[results.length][columnCount];

			for(int i=0 ; i<results.length;i++)
			{
				for(int j=0; j<columnCount;j++)
				{
					results2[i] = (String[])(results[i]);
				}
			}
			
			if(info.get(0)[0].equals("No Results Found"))
			{
				results2 = new String[][]{
						{"No Results Found", null, null, null, null},
						{null, null, null, null, null}
				};
			}
		}
		else
		{
			results2 = new String[][]{
					{"No Results Found", null, null, null, null},
					{null, null, null, null, null}
			};
		}

		TableModel model = new javax.swing.table.DefaultTableModel(
				results2, columnNames){
			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}};


			table.setModel(model) ;
			setTableHeaderRenderer(table);
			resizeTable(table);
	}
	
	/** 
	 * Sets all columns of the table to have the table header renderer {@link #iconHeaderRenderer}
	 *   
	 * @param jt - JTable to be modified
	 */
	public static void setTableHeaderRenderer(JTable jt)
	{
		for(int i=0;i<jt.getColumnCount();i++)
		{
			jt.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(iconHeaderRenderer);
		}
	}
	
	/** 
	 * Sorts the given JTable by which column indicated by colIndex. Sorts in ascending or descending order 
	 * based on value in the given index in ascend. 
	 * 
	 * @param colIndex  index of column to be sorted
	 * @param jt  table to display data
	 * @param ascend array of boolean values indicating sorting preference
	 * @param isNewSearch - used to determine whether or not to reverse order of sorting
	 * @param model - model for the above JTable
	 */
	public static void sortTable(int colIndex, JTable jt, boolean[] ascend, boolean isNewSearch, DefaultTableModel model)
	{
		//javax.swing.table.DefaultTableModel model;
		
		ArrayList<String[]> info = new ArrayList<String[]>();
		if(model != null)
		{
			ArrayList<Vector> infoV = new ArrayList<Vector>();

			for(int i=0;i<(model.getDataVector().size());i++)
			{
				infoV.add((Vector)model.getDataVector().elementAt(i));
			}


			for(int i=0;i<infoV.size();i++)
			{
				String[] temp = new String[columnCount];
				for(int j=0;j<columnCount;j++)
				{
					temp[j] = (String)infoV.get(i).get(j);
				}
				info.add(temp);
			}

			if(!isNewSearch)
			{
				if(ascend[colIndex] == true)
				{
					ascend[colIndex] = false;
				}
				else
				{
					ascend[colIndex] = true;
				}
			}
			else
			{
				ascend[colIndex] = true;
			}

			Collections.sort(info, new ColumnSorter(colIndex, ascend[colIndex]));
			Collections.sort(tableData, new ColumnSorter(colIndex, ascend[colIndex]));
			printTable(info, jt);
			createTable(colIndex, ascend[colIndex], jt);
		}

	}
	
	/**
	 * Extracts and returns a DefaultTableModel from any JTable
	 * 
	 * @param table table to get the model from
	 * 
	 * @return model of that table
	 */
	public static DefaultTableModel getDefaultTableModel(JTable table)
	{
		DefaultTableModel dtm;
		Object[][] tempArray = new Object[table.getModel().getRowCount()][table.getModel().getColumnCount()];
			
		for(int i=0;i<table.getModel().getRowCount();i++)
		{
			for(int j=0;j<table.getModel().getColumnCount();j++)
			{
				tempArray[i][j] = table.getModel().getValueAt(i, j);
			}
		}
	
		dtm = new DefaultTableModel(tempArray, columnNames);
		return dtm;
	}
	
	/** 
	 * Sets a JTable to have blank results and a fresh header
	 * 
	 * @param jt JTable to be modified
	 */
	public static void clearTable(JTable jt)
	{
		jt.setModel(new javax.swing.table.DefaultTableModel(
	            blankRows, columnNames
	        ));
	}
	
	/** 
	 * Refreshes the current table to a brand new search. This includes searching again, and then
	 * printing and sorting. 
	 *  
	 * @param jt table to be printed to
	 * @param jtf search text box
	 * @param ln search criteria, usually the last name of a person
	 * @param aLDA collection of servers to search
	 */
	public static void refreshTable(JTable jt, JTextField jtf, String ln, ArrayList<DataAccess> aLDA )
	{
		jtf.setText(ln);
		printTable(search(jtf.getText(), aLDA), jt);
		sortTable(defaultSortColumn, jt, src.sose.wowAdminGui.MainAdminWindow.ascend, true, getDefaultTableModel(jt));
	}
	
	/**
	 * Returns the person object of the record being displayed in the JTable at the specified row
	 * 
	 * @param rowIndex row at which to retrieve the person
	 * @param jt  table being displayed currently
	 * 
	 * @return person being displayed at rowIndex in jt
	 */
	public static Person getPersonAtRow(int rowIndex, JTable jt)
	 {
		 int index = 0;
		 ArrayList<Field> fields = new ArrayList<Field>();
		 fields.add(new Field("Campus", ""));
		 Person tempPerson = Person.newPerson(tableData.get(rowIndex)[1], tableData.get(rowIndex)[0], "", tableData.get(rowIndex)[3], fields);
			tempPerson.setServer(tableData.get(rowIndex)[4]);
			tempPerson.setCampus(tableData.get(rowIndex)[5]);
			
			for(Person p : searchResults)
			{
				if(p.getLastName().equals(tempPerson.getLastName()))
				{
					if(p.getFirstName().equals(tempPerson.getFirstName()))
					{
						if(p.getServer().equals(tempPerson.getServer()))
						{
							if(p.getEmail().equals(tempPerson.getEmail()))
							{
								if(p.getCampus().equals(tempPerson.getCampus()))
								{
									index = searchResults.indexOf(p);
								}
							}
						}
					}
				}
			}
			tempPerson = searchResults.get(index);
		
			 return tempPerson;
	 }
	
	/**
	 * Takes a Person and strips out all the Field objects in its list that are blank. 
	 * Usually used to optimize the viewing of all of a Person's record
	 * 
	 * @param person Person object that needs to be cleaned
	 * 
	 * @return a new person object with no empty fields
	 */
	public static Person clearEmptyFields(Person person)
	{
		Person tempPerson = person;
		ArrayList<Phone> newPhones = new ArrayList<Phone>();
		ArrayList<Email> newEmails = new ArrayList<Email>();
		
		ArrayList<Phone> tempPhones = tempPerson.getPhoneList();
		for(Phone p : tempPhones)
		{
			if(!(p.getType().equals("") || p.getNumber().equals("")))
			{
				newPhones.add(p);
			}
		}
		tempPerson.setPhoneNumbers(newPhones);
		
		ArrayList<Email> tempEmails = tempPerson.getEmailList();
		for(Email e : tempEmails)
		{
			if(!(e.getType().equals("") || e.getAddress().equals("")))
			{
				newEmails.add(e);
			}
		}
		tempPerson.setEmailAddresses(newEmails);
		
		
		ArrayList<Field> tempFields = new ArrayList<Field>();
		ArrayList<Field> fields = person.getFields();
		
		for(int i=0;i<fields.size();i++)
		{
			if(!fields.get(i).getName().equals(""))
			{
				if(!fields.get(i).getValue().equals(""))
				{
					tempFields.add(new Field(fields.get(i).getName(), fields.get(i).getValue()));
				}
			}
		}
		tempPerson.setFields(tempFields);
		
		return tempPerson;
	}
	//******************************************************************************************************
	//******************************************************************************************************

	/**
	 *	Main controlling method for controlling the location of MoreInfo Windows
	 *	Has two different ways of spawning windows, based on extended state of window (Maximized or not)
	 *
	 *	@param title name to appear in the titlebar of new tab
	 *	@param server name of the server which will appear next to the name to identify person easier
	 *	@param fields collection of Fields to be displayed in tab window
	 * 
	 */
	public void openNewMIWindow(String title, String server, ArrayList<Field> fields)
	{
		if(fields == null || title.equals("null, null"))
		{
			return;
		}

		if(TabbedMoreInfo.numWindowsOpen == 0)
		{
			tmi = new TabbedMoreInfo();
		}
		
		tmi.addNew(title, server, fields);
		
		Point position;
		
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			position = new Point((this.getX()+this.getWidth()), this.getY());
			tmi.setAlwaysOnTop(false);
		}
		else //window is maximized
		{
			position = new Point((this.getX()+this.getWidth()-tmi.getWidth()), this.getY()+this.getHeight()-tmi.getHeight());
			tmi.setAlwaysOnTop(true);
		}
		
		tmi.setLocation(position);
		tmi.setVisible(true);
	}
	
	//******************************End working with MoreInfo Windows*********************************
	
	/**
	 * Expands a record from the table and displays in it a MoreInfo window
	 * 
	 * @param jt table that info is taken out of to display
	 * @param rowIndex row index in table that record is currently displayed
	 */
	public void expandInfo(JTable jt, int rowIndex)
	{
		Person tempPerson = clearEmptyFields(getPersonAtRow(rowIndex, jt));

		if(!tabbedPeople.contains(tempPerson))
		{
			tabbedPeople.add(tempPerson);
			ArrayList<Field> tempArrayList = new ArrayList<Field>();

			tempArrayList.add(new Field("First Name", tempPerson.getFirstName()));
			tempArrayList.add(new Field("Last Name", tempPerson.getLastName()));

			int count = 1;
			for(int i=0;i<tempPerson.getPhoneList().size();i++)
			{
				String phone = "Phone";

				if(tempPerson.getPhoneList().get(i).getType().equals(""))
				{
					phone = phone + Integer.toString(count);
				}
				else
				{
					phone = tempPerson.getPhoneList().get(i).getType();
					count++;
				}
				tempArrayList.add(new Field(phone, tempPerson.getPhoneList().get(i).getNumber()));
			}

			count = 1;
			for(int i=0;i<tempPerson.getEmailList().size();i++)
			{
				String email = "Email";

				if(tempPerson.getEmailList().get(i).getType().equals(""))
				{
					email = email + Integer.toString(i+1);
				}
				else
				{
					email = tempPerson.getEmailList().get(i).getType();
					count++;
				}
				tempArrayList.add(new Field(email, tempPerson.getEmailList().get(i).getAddress()));
			}

			tempArrayList.addAll(tempPerson.getFields());

			openNewMIWindow(tempPerson.getLastName() + ", " + tempPerson.getFirstName()+ " - " + getCampusAbbrev(tempPerson.getCampus()), tempPerson.getServer(), tempArrayList);
			tmi.setSelectedPerson(tempPerson.getLastName() + ", " + tempPerson.getFirstName() + " - " + getCampusAbbrev(tempPerson.getCampus()));
		}
		else
		{
			tmi.setSelectedPerson(tempPerson.getLastName() + ", " + tempPerson.getFirstName() + " - " + getCampusAbbrev(tempPerson.getCampus()));
		}
	}
	
	/**
	 * Helper method for printing that returns the three letter abbreviation of a campus
	 * so that it will fit on the printout better
	 * 
	 * @param fullCampus the full campus name. For example "Randolph" or "Williston"
	 * 
	 * @return the abbrevation of the campus name. For example "RAN" for "Randolph"
	 */
	public String getCampusAbbrev(String fullCampus)
	{
		String campus = "OTH";
		
		if(fullCampus.equals("Bennington"))
		{
			campus = "BEN";
		}
		else if(fullCampus.equals("Brattleboro"))
		{
			campus = "BRA";
		}
		else if(fullCampus.equals("Randolph"))
		{
			campus = "RAN";
		}
		else if(fullCampus.equals("Williston"))
		{
			campus = "WIL";
		}
		else if(fullCampus.equals("Windsor"))
		{
			campus = "WIN";
		}

		return campus;
	}
	//************************************************************************************************
	//************************************Helper Classes**********************************************
	//************************************************************************************************
	
	/**
	 *  This class is used to hold the text and icon values used by the renderer that renders both text and icons
	 */
	static class TextAndIcon {
		TextAndIcon(String text, Icon icon) {
			this.text = text;
			this.icon = icon;
		}
		String text;
		Icon icon;
	}
	
//	borrowed from: http://www.exampledepot.com/egs/javax.swing.table/Sorter.html
	/**
	 * Class used for determining how to sort the records in a column of a table
	 */
	public static class ColumnSorter implements Comparator<Object> {
		int colIndex;
		boolean ascending;
		ColumnSorter(int colIndex, boolean ascending) {
			this.colIndex = colIndex;
			this.ascending = ascending;
		}
		public int compare(Object a, Object b) {
			String[] v1 = (String[])a;
			String[] v2 = (String[])b;
			Object o1 = v1[colIndex];
			Object o2 = v2[colIndex];

			// Treat empty strings like nulls
			if (o1 instanceof String && ((String)o1).length() == 0) {
				o1 = null;
			}
			if (o2 instanceof String && ((String)o2).length() == 0) {
				o2 = null;
			}

			// Sort nulls so they appear last, regardless
			// of sort order
			if (o1 == null && o2 == null) 
			{
				return 0;
			} 
			else if (o1 == null) 
			{
				return 1;
			} 
			else if (o2 == null) 
			{
				return -1;
			} 
			else
			{
				if (ascending) 
				{
					return (o1.toString()).compareTo(o2.toString());
				} 
				else 
				{
					return (o2.toString()).compareTo(o1.toString());
				}
			}
		}
	}
//	************************************************************************************************
	//************************************Custom Renderers********************************************
	//************************************************************************************************

	/**
	 * This customized renderer can render objects of the type TextandIcon. Used in {@link TabbedMoreInfo} to 
	 * display record name and close icon in the same tab.
	 */
	public static TableCellRenderer iconHeaderRenderer = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			// Inherit the colors and font from the header component
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (value instanceof TextAndIcon) {
				setIcon(((TextAndIcon)value).icon);
				setText(((TextAndIcon)value).text);
				setHorizontalTextPosition(JLabel.LEFT);

				int i = table.getFontMetrics(table.getFont()).stringWidth(((TextAndIcon)value).text);
				int j = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
				int k = ((TextAndIcon)value).icon.getIconWidth();

				setIconTextGap(j-i-k-5);

			} else {
				setText((value == null) ? "" : value.toString());
				setIcon(null);
			}
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(JLabel.LEFT);
			return this;
		}
	};
	
	//************************************************************************************************
	//**********************************End Custom Renderers******************************************
	//************************************************************************************************
	
//	borrowed from: http://www.exampledepot.com/egs/javax.swing.table/ColHeadEvent.html
	/**
	 * Mouse Listener that detects a click on the header of a JTable. This indicates a desire to reverse how 
	 * the data is currently sorted in that column (ascending to descending and vice versa). So this listener
	 * will detect which column has been clicked on and sort the table according to that column and the state 
	 * in which it is currently being sorted. 
	 */
	public class ColumnHeaderListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			JTable table = ((JTableHeader)evt.getSource()).getTable();
			TableColumnModel colModel = table.getColumnModel();

			// The index of the column whose header was clicked
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			int mColIndex = table.convertColumnIndexToModel(vColIndex);

			// Return if not clicked on any column header
			if (vColIndex == -1) {
				return;
			}
			DefaultTableModel dtm = getDefaultTableModel(table);
			
			sortTable(mColIndex, table, ascend, false, dtm);
		}

	}
	
	/**
	 * Static class that will guide the focus transfer from one component to another.
	 *
	 */
	public static class MyOwnFocusTraversalPolicy
	 extends FocusTraversalPolicy
	 {
		 Vector<Component> order;

		 public MyOwnFocusTraversalPolicy(Vector<Component> order) {
			 this.order = new Vector<Component>(order.size());
			 this.order.addAll(order);
		 }
		 public Component getComponentAfter(Container focusCycleRoot,
				 Component aComponent)
		 {
			 int idx = (order.indexOf(aComponent) + 1) % order.size();
			 return order.get(idx);
		 }

		 public Component getComponentBefore(Container focusCycleRoot,
				 Component aComponent)
		 {
			 int idx = order.indexOf(aComponent) - 1;
			 if (idx < 0) {
				 idx = order.size() - 1;
			 }
			 return order.get(idx);
		 }

		 public Component getDefaultComponent(Container focusCycleRoot) {
			 return order.get(0);
		 }

		 public Component getLastComponent(Container focusCycleRoot) {
			 return order.lastElement();
		 }

		 public Component getFirstComponent(Container focusCycleRoot) {
			 return order.get(0);
		 }
	 }

	/**
	 * Class used to color code the results of a JTable based on the value of the 
	 * Campus Field in the Person object currently being displayed. For example any
	 * records that are from the Williston Campus will display as red, Randolph in 
	 * green, etc. 
	 * 
	 */
	public class AttributiveCellRenderer extends JLabel  implements
	 TableCellRenderer {

	   public AttributiveCellRenderer() {
	     setOpaque(true);
	   }

	   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
			                                             boolean hasFocus, int row, int column) 
	   {
		   
		   if(table.getModel().getValueAt(row, 4) != null)
		   {
			   if((tableData.get(row)[5]).equals("Williston"))
			   {
				   this.setBackground(new Color(252, 146, 146, 100)); // light red
				   this.setForeground(Color.black);
				   this.setText((String)value);
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   }
			   else if((tableData.get(row)[5]).equals("Randolph"))
			   {
				   this.setBackground(new Color(197, 252, 177, 100));//light green
				   //this.setBackground(new Color(232, 242, 254, 255));//light blue
				   this.setForeground(Color.black);
				   this.setText((String)value);
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));  
			   }
			   else if(((String)(table.getModel().getValueAt(row, 4))).equals("LOC"))
			   {
				   this.setBackground(new Color(232, 242, 254, 255)); // white
				   this.setForeground(Color.black);
				   this.setText((String)value);
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   }
			   else
			   {
				   this.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f));//white
				   this.setForeground(Color.black);
				   this.setText((String)value);
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   }

			   if(isSelected)
			   {
				   this.setBackground(new Color(184, 207, 229, 255));//standard windows selection - slightly darker light blue
				   this.setForeground(Color.black);
				   this.setText((String)(table.getModel().getValueAt(row, column)));
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   }
		   }
		   else //table is empty
		   {
			   this.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f)); // white
			   this.setForeground(Color.black);
			   this.setText((String)value);
			   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   
			   if(isSelected)
			   {
				   this.setBackground(new Color(184, 207, 229, 255));//standard windows selection - slightly darker light blue
				   this.setForeground(Color.black);
				   this.setText((String)(table.getModel().getValueAt(row, column)));
				   this.setFont(this.getFont().deriveFont(Font.PLAIN));
			   }
		   }
		   
		 
	     return this;
	   }
	 }
	
	/**
	 * Class that "washes" a JTable clean to be suitable for printing. Takes all background color out
	 * and changes the font. 
	 */
	public class BlankCellRenderer extends JLabel  implements TableCellRenderer 
	{

		public BlankCellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) 
		{

			this.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f)); // white
			this.setForeground(Color.black);
			this.setText((String)value);
			//this.setFont(this.getFont().deriveFont(Font.PLAIN));
			this.setFont(new Font("Bookman Old Style", Font.PLAIN, 9));
			
			if(isSelected)
			{
				this.setBackground(new Color(184, 207, 229, 255));//standard windows selection - slightly darker light blue
				this.setForeground(Color.black);
				this.setText((String)(table.getModel().getValueAt(row, column)));
				this.setFont(this.getFont().deriveFont(Font.PLAIN));
				
			}

			return this;
		}
	}
	
	/**
	 * Prints all the records passed to it
	 * 
	 * @param data  data to be printed
	 * @param table table to help render the table properly for printing 
	 * @param isNewSearch helps with rendering the table properly
	 */
	public void printReport(ArrayList<String[]> data, JTable table, boolean isNewSearch)
	{
		boolean confirm = printConfirm(data.size());
		
		if(!confirm)
		{
			return;
		}
		
		printTable(data, table);
		isNewSearch = true;
		sortTable(defaultSortColumn, table, ascend, isNewSearch, getDefaultTableModel(table));
		isNewSearch = false;
		
		printingTable = table;
		GUIBase gu = new GUIBase();
		printingTable.setDefaultRenderer(Object.class, gu.new BlankCellRenderer());
	   
		printingTable.getColumnModel().getColumn(4).setHeaderValue(" LOC");
		printingTable.getColumnModel().getColumn(1).setHeaderValue(" Office");
		printingTable.getColumnModel().getColumn(0).setHeaderValue(" Name");
		printingTable.getColumnModel().getColumn(2).setHeaderValue(" " + printingTable.getColumnModel().getColumn(2).getHeaderValue());
		printingTable.getColumnModel().getColumn(3).setHeaderValue(" " + printingTable.getColumnModel().getColumn(3).getHeaderValue());
		 
		for(int i=0;i<printingTable.getColumnCount();i++)
		{
			TableColumn col = printingTable.getColumnModel().getColumn(i);
			int width = (int)(printingTable.getWidth()*columnPercentsPrint[i]);
			col.setPreferredWidth(width);
		}
		
          PrinterJob pj=PrinterJob.getPrinterJob();
          pj.setPrintable(this);
          if(pj.printDialog())
          {
        	  try{ 
        		  pj.print();
        	  }catch (Exception PrintException) {}

          }
          else
          {
        	  table.setModel(new javax.swing.table.DefaultTableModel(
        			  blankRows, columnNames));

        	  resizeTable(table);
          }
	}
	
	/**
	 * Prints every record that the server passes to it.
	 * 
	 * @param table table to help render the records for printing
	 * @param dataAccess collection of servers to get records from
	 * @param isNewSearch helps in rendering the table correctly
	 */
	public void printAllRecords(JTable table, ArrayList<DataAccess> dataAccess, boolean isNewSearch)
	{
		ArrayList<String[]> temp = WowHelper.getAllPeople(dataAccess);
		
		boolean confirm = printConfirm(temp.size());
		
		if(!confirm)
		{
			return;
		}
		
		printTable(temp, table);
		isNewSearch = true;
		sortTable(defaultSortColumn, table, ascend, isNewSearch, getDefaultTableModel(table));
		isNewSearch = false;
		
		printingTable = table;
		GUIBase gu = new GUIBase();
		printingTable.setDefaultRenderer(Object.class, gu.new BlankCellRenderer());
	   
		//printingTable.getColumnModel().getColumn(4).setHeaderValue(" LOC");
		printingTable.removeColumn(printingTable.getColumnModel().getColumn(4));
		printingTable.getColumnModel().getColumn(1).setHeaderValue(" Office");
		printingTable.getColumnModel().getColumn(0).setHeaderValue(" Name");
		printingTable.getColumnModel().getColumn(2).setHeaderValue(" " + printingTable.getColumnModel().getColumn(2).getHeaderValue());
		printingTable.getColumnModel().getColumn(3).setHeaderValue(" " + printingTable.getColumnModel().getColumn(3).getHeaderValue());
		 
		for(int i=0;i<printingTable.getColumnCount();i++)
		{
			TableColumn col = printingTable.getColumnModel().getColumn(i);
			int width = (int)(printingTable.getWidth()*columnPercentsPrint[i]);
			col.setPreferredWidth(width);
		}
		
          PrinterJob pj=PrinterJob.getPrinterJob();
          pj.setPrintable(this);
          if(pj.printDialog())
          {
        	  try{ 
        		  pj.print();
        	  }catch (Exception PrintException) {}

          }
          else
          {
        	  table.setModel(new javax.swing.table.DefaultTableModel(
        			  blankRows, columnNames));

        	  resizeTable(table);
          }
	}
	
	/**
	 * Prints every record that the server passes to it sorted by department.
	 * 
	 * @param dataAccess collection of servers to get records from
	 */
	public void printAllRecordsByDept(ArrayList<DataAccess> dataAccess)
	{
		ArrayList<String[]> temp = new ArrayList<String[]>();
		for (DataAccess da : dataAccess) {
			temp = da.getUnformattedAll();
			if (temp.size() > 0) //query worked
				break;
		}
		
		boolean confirm = printConfirm(temp.size());
		
		if(!confirm)
		{
			return;
		}
		
		int DEPT_FIELD = 6;
		/*//sort:
		boolean done = false;
		while (!done) {
			done = true;
			for (int c = 0; c < temp.size()-1; c++) {
				String dept = temp.get(c)[DEPT_FIELD];
				String n_dept = temp.get(c+1)[DEPT_FIELD];
				if (dept.compareTo(n_dept) > 0) {
					String[] t = temp.get(c);
					temp.set(c, temp.get(c+1));
					temp.set(c+1, t);
					
					done = false;
				}
			}
		}*/
		
		JEditorPane editorPane = new JEditorPane();
		DocumentRenderer print = new DocumentRenderer();
		
		java.io.PrintStream p = null;
		try {
			ArrayList<String> dept = new ArrayList<String>();
	    	
	    	java.io.File f = new java.io.File("dept-list.txt");
			try {
				java.io.BufferedReader reader = new java.io.BufferedReader(
						new java.io.FileReader(f));
				java.util.Scanner scan = new java.util.Scanner(reader);
				
				while (scan.hasNextLine()) {
					dept.add(scan.nextLine());
				}
				
				scan.close();
				reader.close();
			}
			catch (java.io.FileNotFoundException sadface) {
				System.err.println(sadface);
				JOptionPane.showMessageDialog(null, "The file 'dept-list.txt' was not found.\n" + 
						"Please reinstall the WOW client.", "File not found.", JOptionPane.ERROR_MESSAGE);
			}
			
			p = new java.io.PrintStream("temp.htm");
			p.println("<html>\n<head>\n<title>WOW Department List</title>\n</head>\n" +
					"<body>" +
					"<h3>WOW Full Department List</h3>" + 
					//"<style>" +
					//"table { font-family: courier new; font-size: 13px } " +
					//"p { font-family: courier new; font-size: 13px }" +
					//"</style>" +
					"<table border=\"0\" width=\"800\">");
			
			for (String department : dept) {
				p.println("<p>" + department + "</p>");
				p.println("<table width=\"750\">");
				for (int c = 0; c < temp.size(); c++) {
					if (temp.get(c)[DEPT_FIELD].equals(department)) {
						p.println("\t<tr>");
						p.println("\t\t<td width=\"50\">&nbsp;</td>");
						p.println("\t\t<td width=\"180\">" + temp.get(c)[0] +
								" " + temp.get(c)[1] +"</td>");
						p.println("\t\t<td width=\"140\">" + temp.get(c)[5] + "</td>");
						p.println("\t\t<td width=\"220\">" + 
								(temp.get(c)[3].equals("_@_._")?"&nbsp;":temp.get(c)[3]) + "</td>");
						p.println("\t\t<td width=\"140\">"+ 
								(temp.get(c)[2].equals("(802)000-0000")?"&nbsp;":temp.get(c)[2]) +"</td>\n" +
								"\n\t</tr>");
					}
				}
				
				p.println("\t\t<tr><td>&nbsp;</td></tr>\n</table>");
			}
			p.println("<p>Total record count: " + temp.size() + "</p>\n</table>\n" +
					"</body>\n" +
					"</html>");
		}
		catch (java.io.IOException e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(null, "An error occured while creating temp file: " + 
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		finally {
			p.close();
		}
		
		try {
			String configPath = (new File(".").getAbsolutePath());
			File config = new File(configPath + "/temp.htm");
			editorPane.setPage(new URL("file:///"+config.getAbsolutePath()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		print.print(editorPane);
	}
	

	
	
	/**
	 * Implements abstract method print from the Printable Interface. Prints a full WoW report
	 * 
	 * @param g graphics object to render print job on
	 * @param pageFormat format of print job
	 * @param pageIndex page currently being printed
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
     	Graphics2D  g2 = (Graphics2D) g;
     	g2.setColor(Color.black);

     	Image image = new ImageIcon().getImage();
     	ClassLoader cl = this.getClass().getClassLoader();
	
     	try {
			image = ImageIO.read(cl.getResource("images/wowContactListHeader.JPG"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int headerImageHeight = image.getHeight(this);
     	int headerImageWidth  = image.getWidth(this)-275;     	
     	int tableWidth = printingTable.getColumnModel().getTotalColumnWidth();
     	int headerHeightOnPage = printingTable.getTableHeader().getHeight();
     	int tableWidthOnPage = tableWidth;
     	int oneRowHeight = (printingTable.getRowHeight() + printingTable.getRowMargin());
     	int numRowsOnAPage = 32;
     	int pageHeightForTable=oneRowHeight*numRowsOnAPage;
     	int totalNumPages= (printingTable.getRowCount()/numRowsOnAPage)+1;
     	
     	if(pageIndex>=totalNumPages) 
     	{
     		return NO_SUCH_PAGE;
     	}

     	g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
     	
     	// Paint the page number at the bottom center
     	g2.drawString("Page: "+(pageIndex+1), (tableWidthOnPage/2)-35, pageHeightForTable+headerImageHeight-8);
     	//***********************************************************

     	//--- Render the headerImage on the sheet
     	g2.drawImage (image,
     			0,
     			0,
     			headerImageWidth,
     			(1 * POINTS_PER_INCH),
     			this);
     	//******************************************************8
     	g2.translate(0f,headerHeightOnPage+72);
     	g2.translate(0f,-pageIndex*pageHeightForTable);
    	
     	//If this piece of the table is smaller 
     	//than the size available,
     	//clip to the appropriate bounds.
     	if (pageIndex + 1 == totalNumPages) {
           int lastRowPrinted = numRowsOnAPage * pageIndex;
           int numRowsLeft = printingTable.getRowCount() - lastRowPrinted;
           g2.setClip(0, 
             pageHeightForTable * pageIndex,
             tableWidthOnPage+2,
             oneRowHeight * numRowsLeft);
     	}
     	//else clip to the entire area available.
     	else{    
             g2.setClip(0, 
             pageHeightForTable*pageIndex, 
             tableWidthOnPage+2,
             pageHeightForTable);        
     	}

     	
     	printingTable.paint(g2);
     	
     	g2.translate(0f,pageIndex*pageHeightForTable);
     	g2.translate(0f, -headerHeightOnPage);
     	g2.setClip(0, 0,
     	  tableWidthOnPage, 
          headerHeightOnPage);
     
     	printingTable.getTableHeader().paint(g2);
     	//paint header at top

     	return Printable.PAGE_EXISTS;
}

	/**
	 * Opens a confirmation dialog asking for confirmation for printing
	 * 
	 * @param numPeople total number of people that are going to be printed
	 * @return <code>true</code> if printing is confirmed <br> <code>false</code> if printing is not confirmed
	 */
	public boolean printConfirm(int numPeople)
	{
		int pages = (numPeople/32)+1;
		String temp = "";
		if(pages == 1)
		{
			temp = pages + " page?";
		}
		else
		{
			temp = pages + " pages?";
		}
		
		int n = JOptionPane.showConfirmDialog(
			    this,
			    "Are you sure you would like to print " + temp,
			    "Confirmation",
			    JOptionPane.YES_NO_OPTION);
		
		if(n==JOptionPane.YES_OPTION)
		{
			return true;
		}

		return false;
	}
}
