<%@page import="java.util.*, java.io.*, java.net.*"%>
<html>
<head>
	<title>Generate XML Database</title>
</head>
<body>
	<%!
		class Person {
			public Person (String _first, String _last, String _phone, String _email, String _campus, String _role, String _dept, String _fax, String _office) {
				first = _first;
				last = _last;
				phone = _phone;
				email = _email;
				campus = _campus;
				role = _role;
				dept = _dept;
				fax = _fax;
				office = _office;
			}
			
			public Person (String[] person) {
				first = person[0];
				last = person[1];
				phone = person[2];
				email = person[3];
				campus = person[4];
				role = person[5];
				dept = person[6];
				fax = person[7];
				office = ((person.length > 8)?person[8]:"");
			}
			
			public Person (String line) {
				String[] person = line.split("\\|", -1);
				
				first = person[0];
				last = person[1];
				phone = person[2];
				email = person[3];
				campus = person[4];
				role = person[5];
				dept = person[6];
				fax = person[7];
				office = ((person.length > 8)?person[8]:"");
			}
			
			public String first;
			public String last;
			public String phone;
			public String email;
			public String campus;
			public String role;
			public String dept;
			public String fax;
			public String office;
		}
		
		class Department {
			public Department(String _name) {
				name = _name;
			}
			
			public String name;
		}
		/////////////////////////////////////////////////////////////////////////
		
		public Vector<Person> getAllPeople() throws IOException {
			Vector<Person> result = new Vector<Person>();
			
			final String wowIP = "155.42.234.35";
			final int wowPort = 5280;
			Socket socket = new Socket(wowIP, wowPort);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
			String message = "QRY: ||||||||";
			out.println(message);
			
			String line;
			
			while ((line = in.readLine()) != null) {
				if (line.equals("||||||||"))
					continue;
					
				line = line.replace("$", "");
				line = line.replace("&", "&amp;");
				result.add(new Person(line));
			}
			
			in.close();
			out.close();
			socket.close();
			
			return result;
		}
		
		public Vector<Department> getAllDepartments(String path) throws IOException {
			Vector<Department> result = new Vector<Department>();
			
			BufferedReader reader = new BufferedReader(new FileReader(path + "/dept-list.txt"));
			Scanner in = new Scanner(reader);
			
			while (in.hasNextLine()) {
				String line = in.nextLine();
				
				line = line.replace("&", "&amp;");
				result.add(new Department(line));
			}
			
			in.close();
			reader.close();
			
			return result;
		}
		
		public void printTreeStyle(Vector<Department> departments, Vector<Person> people, String path) throws IOException {
			PrintWriter printOut = new PrintWriter(new FileWriter(path + "/wow.xml"));
			printOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			printOut.println("<data-group>");
			
			printOut.println("\t<departments>");
			for (Department dept : departments) {
				printOut.println("\t\t<entry>");
				printOut.println("\t\t\t<name>" + dept.name + "</name>");
				//Future: Add fields
				printOut.println("\t\t</entry>");
			}
			printOut.println("\t</departments>");
			printOut.println("\t<people>");
			int uniqueID = 0;
			for (Person person : people) {
				printOut.println("\t\t<record>");
				printOut.println("\t\t\t<id>" + uniqueID + "</id>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>First Name</name>");
				printOut.println("\t\t\t\t<value>" + person.first + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Last Name</name>");
				printOut.println("\t\t\t\t<value>" + person.last + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Phone Number</name>");
				printOut.println("\t\t\t\t<value>" + person.phone.replace("(802)000-0000", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Email</name>");
				printOut.println("\t\t\t\t<value>" + person.email.replace("_@_._", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Campus</name>");
				printOut.println("\t\t\t\t<value>" + person.campus.replace("null", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Role</name>");
				printOut.println("\t\t\t\t<value>" + person.role.replace("null", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Department</name>");
				printOut.println("\t\t\t\t<value>" + person.dept.replace("null", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Fax</name>");
				printOut.println("\t\t\t\t<value>" + person.fax.replace("null", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t\t<field>");
				printOut.println("\t\t\t\t<name>Office</name>");
				printOut.println("\t\t\t\t<value>" + person.office.replace("null", "") + "</value>");
				printOut.println("\t\t\t</field>");
				printOut.println("\t\t</record>");
				uniqueID++;
			}
			printOut.println("\t</people>");
			
			printOut.println("</data-group>");
			
			printOut.close();
		}
		
		public void printAbridgedStyle(Vector<Department> departments, Vector<Person> people, String path) throws IOException {
			PrintWriter printOut = new PrintWriter(new FileWriter(path + "/wow.xml"));
			printOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			printOut.println("<data-group>");
			
			printOut.println("\t<departments>");
			for (Department dept : departments) {
				printOut.println("\t\t<entry>");
				printOut.println("\t\t\t<name>" + dept.name.replace("'", "&apos;") + "</name>");
				printOut.println("\t\t</entry>");
			}
			printOut.println("\t</departments>");
			printOut.println("\t<people>");
			int uniqueID = 0;
			for (Person person : people) {
				printOut.println("\t\t<record>");
				printOut.println("\t\t\t<id>" + uniqueID + "</id>");
				printOut.println("\t\t\t<field name=\"First Name\">" + person.first.replace("'", "&apos;") + "</field>");
				printOut.println("\t\t\t<field name=\"Last Name\">" + person.last.replace("'", "&apos;") + "</field>");
				printOut.println("\t\t\t<field name=\"Phone Number\">" + person.phone.replace("(802)000-0000", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Email\">" + person.email.replace("_@_._", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Campus\">" + person.campus.replace("null", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Role\">" + person.role.replace("null", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Department\">" + person.dept.replace("null", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Fax\">" + person.fax.replace("null", "") + "</field>");
				printOut.println("\t\t\t<field name=\"Office\">" + person.office.replace("null", "") + "</field>");
				printOut.println("\t\t</record>");
				uniqueID++;
			}
			printOut.println("\t</people>");
			
			printOut.println("</data-group>");
			
			printOut.close();
		}
	%>
	<%
		try {
			new File("wow.xml").createNewFile();
			
			String path = config.getServletContext().getRealPath(".");
			
			Vector<Person> people = getAllPeople();
			Vector<Department> departments = getAllDepartments(path);
			
			//printTreeStyle(departments, people, path);
			printAbridgedStyle(departments, people, path);
			
			out.println("Done. <a href=\"wow.xml\">Download</a>");
		}
		catch (IOException e) {
			out.println(e);
		}
	%>
</body>
</html>