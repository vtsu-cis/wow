<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>List of Computers</title>
</head>
<body>
	<script type="text/javascript">
		function popupaddpackage(urlToOpen)
			{
				var window_width = (screen.availWidth/4)+20;
				var window_height = (screen.availHeight/2)-60;
				var window_left = (screen.availWidth/2)-(window_width/2);
				var window_top = (screen.availHeight/2)-(window_height/2);
				var winParms = "Status=yes" + ",scrollbars=1"+ ",resizable=yes" + ",height="+window_height+",width="+window_width + ",left="+window_left+",top="+	window_top;
				var newwindow = window.open(urlToOpen,'_blank',winParms);
				newwindow.focus()
			}
	</script>
	<%!
		class Computer {
			public Computer(String[] list) {
				building = list[0];
				location = list[1];
				serial = list[2];
				model = list[3];
				warrantyDate = list[4];
				macAddress = list[5];
				wallPort = list[6];
				person = list[7];
				laptop = list[8];
				notes = list[9];
			}
			
			public Computer(String bu, String lo, String se, String mo, String wa, String ma, String wap, String pe, String la, String no) {
				building = bu;
				location = lo;
				serial = se;
				model = mo;
				warrantyDate = wa;
				macAddress = ma;
				wallPort = wap;
				person = pe;
				laptop = la;
				notes = no;
			}
			
			public Computer(Computer c){
				building = c.building;
				location = c.location;;
				serial = c.serial;
				model = c.model;
				warrantyDate = c.warrantyDate;
				macAddress = c.macAddress;
				wallPort = c.wallPort;
				person = c.person;
				laptop = c.laptop;
				notes = c.notes;
			}
			
			public String building;
			public String location;
			public String serial;
			public String model;
			public String warrantyDate;
			public String macAddress;
			public String wallPort;
			public String person;
			public String laptop;
			public String notes;
		}
		
		public boolean isValidSession(JspWriter out, HttpServletResponse response, String key) throws IOException {
			boolean found = false;
			
			try{
				BufferedReader reader = null;
				Scanner in = null;
				try{
					reader = new BufferedReader(new FileReader(new File("users.dat")));
				}
				catch(FileNotFoundException f){
					new File("users.dat").createNewFile();
					reader = new BufferedReader(new FileReader(new File("users.dat")));
				}
				in = new Scanner(reader);
				
				
				if (key != null) {
					while (in.hasNextLine()) {
						String line = in.nextLine();
						if (line.startsWith("#")){
							continue;
						}
					
						String realkey = line.split("\\|")[2];
					
						if (key.equals(realkey)) {
							found = true;
							break;
						}
					}
				}
			
				in.close();
				reader.close();
			}
			catch(IOException e){
				out.println("There is a database error: " + e.getMessage() + ". " +
						"Try again later.");
			}
			catch(ArrayIndexOutOfBoundsException a){
				out.println("An error occured.  Please report this error: ");
				out.println("ArrayIndexOutOfBoundsException (" + a.getMessage() + ")<br />");
				out.println("(The user database may be corrupted.)");
			}
			catch(NullPointerException n){
				out.println("An error occured.");
				response.sendRedirect("../list/login.html?error=-1");
			}
			
			return found;
		}
		
		public ArrayList<Computer> loadComputerList(JspWriter out) throws IOException {
			ArrayList<Computer> temp = new ArrayList<Computer>();
			
			BufferedReader reader = null;
			Scanner in = null;
			try{
				reader = new BufferedReader(new FileReader(new File("computerlist.dat")));
			}
			catch(FileNotFoundException f){
				new File("computerlist.dat").createNewFile();
				reader = new BufferedReader(new FileReader("computerlist.dat"));
			}
			in = new Scanner(reader);
			
			while (in.hasNextLine()) {
				String line = in.nextLine();
				try{
					temp.add(new Computer(line.split("\\,")));
				}
				catch(ArrayIndexOutOfBoundsException a){
					out.println("Error loading entry (corrupted)");
				}
			}
			
			in.close();
			reader.close();
			
			return temp;
		}
		
		public boolean containedInFilter(String filter, String field) {
			boolean result = false;
			
			if (filter != null) {
				if (field.toLowerCase().contains(filter.toLowerCase())) {
					result = true;
				}
			}
			else {
				result = true;
			}
			
			return result;
		}
		
		/**
		*	@param out object to write
		*	@param filter what text to filter by
		*	@param t type of filter( 0 = serial, 1 = location, 2 = person)
		*/
		public void printList(JspWriter out, String filter, int t, String key) throws IOException  {
			ArrayList<Computer> unfilteredlist = loadComputerList(out);
			ArrayList<Computer> list = new ArrayList<Computer>();
			
			if (unfilteredlist.size() < 1) {
				return;
			}
			
			for (int c = 0; c < unfilteredlist.size(); c++) {
				String ftype = unfilteredlist.get(c).serial;
				if (t == 0) ftype = unfilteredlist.get(c).serial;
				if (t == 1) ftype = unfilteredlist.get(c).location;
				if (t == 2) ftype = unfilteredlist.get(c).person;
				
				if (containedInFilter(filter, ftype)) {
					list.add(new Computer(unfilteredlist.get(c)));
				}
			}
			
			//sort:
			boolean done = false;
			while (!done){
				done = true;
				for (int c = 0; c < list.size()-1; c++) {
					if (list.get(c).serial.compareTo(list.get(c+1).serial) > 0) {
						Computer temp = list.get(c+1);
						list.set(c+1,list.get(c));
						list.set(c, temp);
						done = false;
					}
				}
			}
			
			out.println("<h3>List of entries.</h3>");
			out.println("Enter search query here:");
			out.println("<form>");
			out.println("<input type=\"text\" name=\"filter\"/> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"0\">Search by serial</input> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"1\" checked=\"true\">Search by location</input> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"2\">Search by assigned person</input> <br />");
			out.println("<input type=\"hidden\" name=\"session\" value=\""+key+"\" />");
			out.println("<input type=\"submit\" value=\"Submit query\" /> <br /> <br />");
			
			if (list.size() > 10) {
				out.println("<a href=\"account.jsp?session="+key+"\">Back to Menu</a><br /><br />");
			}
			
			out.println(list.size() + " entries found.");
			
			out.println("<table width=\"100%\" border=\"1\" cellpadding=\"2\">\n");
			out.println("<tr>");
			out.println("\t<th>Serial #</th>");
			out.println("\t<th>Building</th>");
			out.println("\t<th>Location</th>");
			out.println("\t<th>Model</th>");
			out.println("\t<th>Mac Address</th>");
			out.println("\t<th>Wall Port</th>");
			out.println("\t<th>Person</th>");
			out.println("\t<th>Laptop?</th>");
			out.println("\t<th>Warranty Exp. Date</th>");
			out.println("\t<th>Notes</th>");
			out.println("<th>Modify</th>");
			out.println("<th>Delete</th>");
			out.println("</tr>");
			for (int c = 0; c < list.size(); c++) {
				Computer e = list.get(c);
				out.println("<tr bgcolor=\""+((c%2==0)?"FFFFFF":"EEEEEE")+"\">");
				out.println("\t<td>" + e.serial + "</td>");
				out.println("\t<td>" + e.building + "</td>");
				out.println("\t<td>" + e.location + "</td>");
				out.println("\t<td>" + e.model + "</td>");
				out.println("\t<td>" + e.macAddress + "</td>");
				out.println("\t<td>" + e.wallPort + "</td>");
				out.println("\t<td>" + e.person + "</td>");
				out.println("\t<td>" + e.laptop + "</td>");
				out.println("\t<td>" + e.warrantyDate + "</td>");
				out.println("\t<td>" + e.notes + "</td>");
				out.println("\t<td align=\"center\"><input type=\"button\" value=\"Modify\" onClick=\"popupaddpackage('compedit.jsp?session="+key+"&serial="+e.serial+"&building="+e.building+"&location="+e.location+"&model="+e.model+"&macaddress="+e.macAddress+"&wallport="+e.wallPort+"&laptop="+e.laptop+"&person="+e.person+"&warranty="+e.warrantyDate+"&notes="+e.notes+"')\" /></td>");
				out.println("\t<td align=\"center\"><input type=\"button\" value=\"Delete\" onClick=\"popupaddpackage('compdel.jsp?session="+key+"&serial="+e.serial+"&building="+e.building+"&location="+e.location+"&model="+e.model+"&macaddress="+e.macAddress+"&wallport="+e.wallPort+"&laptop="+e.laptop+"&person="+e.person+"&warranty="+e.warrantyDate+"&notes="+e.notes+"')\" /></td>");
				out.println("</tr>");
			}
			out.println("</table>");
		}
	%>
	<%
		String filter = new String("");
		int filterType = 0; //0 = serial, 1 = location, 2 = person
		String key = request.getParameter("session");
		
		try{
			filter = request.getParameter("filter");
		}
		catch(NullPointerException n){
			filter = new String("");
		}
		
		try{
			filterType = Integer.parseInt(request.getParameter("ftype"));
		}
		catch(NumberFormatException f){
			filterType = 0;
		}
		
		if (!isValidSession(out, response, key)) {
			response.sendRedirect("../list/login.html?error=0");
		}
		
		%>
			<h3>List of Computers</h3>
		<%
		
		printList(out, filter, filterType, key);
	%>
	<br />
	<a href="account.jsp?session=<%= key %>">Back to Menu</a> <br />
	<a href="login.html">Logout</a>
</body>
</html>