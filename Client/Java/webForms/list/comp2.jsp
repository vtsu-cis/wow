<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Adding...</title>
</head>
<body>
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
		if (key == null) {
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		if (!isValidSession(out, response, key)) {
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		String building = request.getParameter("building");
		String location = request.getParameter("location");
		String serial = request.getParameter("serial");
		String model = request.getParameter("model");
		String warrantyExpirationDate = request.getParameter("warranty");
		String macAddress = request.getParameter("mac");
		String wallPort = request.getParameter("wallport");
		String person = request.getParameter("person");
		String laptop = request.getParameter("laptop");
		String notes = request.getParameter("notes");
		
		if (building == null || location == null || serial == null || model == null || macAddress == null) {//necessary
			out.println("One or more vital fields have not been filled out.<br />");
			out.println("<br /><a href=\"javascript:history.go(-1)\">Go Back</a>");
			return;
		}
		
		if (building.equals("") || location.equals("") || serial.equals("") || model.equals("") || macAddress.equals("")) {
			out.println("One or more vital fields have not been filled out.<br />");
			out.println("<br /><a href=\"javascript:history.go(-1)\">Go Back</a>");
			return;
		}
		
		if (building.contains(",") || location.contains(",") || serial.contains(",") || model.contains(",") || warrantyExpirationDate.contains(",") ||
				macAddress.contains(",") || wallPort.contains(",") || person.contains(",") || laptop.contains(",") || notes.contains(",")) {
			out.println("Because the report is comma-delimited and I am too lazy to change it, commas are not allowed.<br />");
			out.println("Please go back and try again.<br />");
			out.println("<br /><a href=\"javascript:history.go(-1)\">Go Back</a>");
			return;
		}
		
		Hashtable<String, Computer> computerList = loadComputerList(out);
		if (!computerList.containsKey(serial)){
			computerList.put(serial, new Computer(building, location, serial, model, warrantyExpirationDate, macAddress, wallPort, person, laptop, notes));
		}
		else {
			response.sendRedirect("../list/comp.jsp?session="+key+"&success=0&exists=1");
			return;
		}
		
		save(computerList);
		
		response.sendRedirect("../list/comp.jsp?session="+key+"&success=1");
	%>
</body>
</html>