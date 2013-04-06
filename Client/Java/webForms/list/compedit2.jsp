<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Entry Modified</title>
</head>
<body>
	<script type="text/javascript">
		<!--
		function refreshParent() {
			window.opener.location.href = window.opener.location.href;

			if (window.opener.progressWindow)
			{
				window.opener.progressWindow.close()
			}
			window.close();
		}
		//-->
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
		
		public String closeButton(String value){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:self.close()\"";
		}
		
		public String backButton(){
			return "<br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"";
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
		
		public Hashtable<String, Computer> loadComputerList(JspWriter out) throws IOException {
			Hashtable<String, Computer> temp = new Hashtable<String, Computer>();
			
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
					temp.put(line.split("\\,")[2], new Computer(line.split("\\,")));
				}
				catch(ArrayIndexOutOfBoundsException a){
					out.println("Error loading entry (corrupted)");
				}
			}
			
			in.close();
			reader.close();
			
			return temp;
		}
		
		public String computerToString(Computer c) {
			String result = new String();
			
			if (c.building.equals("")) c.building = "N/A";
			if (c.location.equals("")) c.location = "N/A";
			if (c.serial.equals("")) c.serial = "N/A";
			if (c.model.equals("")) c.model = "N/A";
			if (c.warrantyDate.equals("")) c.warrantyDate = "N/A";
			if (c.macAddress.equals("")) c.macAddress = "N/A";
			if (c.wallPort.equals("")) c.wallPort = "N/A";
			if (c.person.equals("")) c.person = "N/A";
			if (c.laptop.equals("")) c.laptop = "No";
			if (c.notes.equals("")) c.notes = "None";
			
			
			result = c.building +","+ c.location +","+ c.serial +","+ c.model +","+ c.warrantyDate +","+ c.macAddress +","+ c.wallPort +","+ c.person +","+ c.laptop +","+ c.notes;
			
			return result;
		}
		
		public void save(Hashtable<String, Computer> list) throws IOException {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("computerlist.dat")));
			
			Enumeration e = list.keys();
			while (e.hasMoreElements()) {
				Computer c = list.get(e.nextElement());
				String data = computerToString(c);
				writer.println(data);
			}
			
			writer.close();
		}
	%>
	<%
		String key = request.getParameter("session");
		
		if (!isValidSession(out, response, key)) {
			out.println("You do not have permission to modify this entry.<br /><br />");
			out.println(closeButton("Close"));
			return;
		}
		
		String serial = request.getParameter("serial");
		String serial_old = request.getParameter("serial_old");
		String building = request.getParameter("building");
		String location = request.getParameter("location");
		String model = request.getParameter("model");
		String macAddress = request.getParameter("macaddress");
		String wallPort = request.getParameter("wallport");
		String person = request.getParameter("person");
		String laptop = request.getParameter("laptop");
		String warrantyDate = request.getParameter("warranty");
		String notes = request.getParameter("notes");
		
		if (serial.equals("") || building.equals("") || location.equals("") || model.equals("") || macAddress.equals("") || wallPort.equals("") || person.equals("") || laptop.equals("") || warrantyDate.equals("")) {
			out.println("One or more fields are not filled out.  Please fill every one out.<br /><br />");
			out.println(backButton());
			return;
		}
		
		if (building.contains(",") || location.contains(",") || serial.contains(",") || model.contains(",") || warrantyDate.contains(",") ||
				macAddress.contains(",") || wallPort.contains(",") || person.contains(",") || laptop.contains(",") || notes.contains(",")) {
			out.println("Because the report is comma-delimited and I am too lazy to change it, commas are not allowed.<br />");
			out.println("Please go back and try again.<br />");
			out.println("<br /><a href=\"javascript:history.go(-1)\">Go Back</a>");
			return;
		}
		
		Hashtable<String, Computer> list = loadComputerList(out);
		list.remove(serial_old);
		list.put(serial, new Computer(building, location, serial, model, warrantyDate, macAddress, wallPort, person, laptop, notes));
		save(list);
	%>
</body>
</html>