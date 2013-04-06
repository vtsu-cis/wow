<%@ page import="java.io.*, java.util.*, java.text.*" %>
<html>
<head>
	<title>File received</title>
</head>
<body>
	<%!
		class Person {
			/**
			*	Add a person.
			*/
			public Person(String fn, String ln, String em) {
				setFirstName(fn);
				setLastName(ln);
				setEmail(em);
			}
			
			// first name|last name|campus|email
			public Person(String[] data) {
				setFirstName(data[0]);
				setLastName(data[1]);
				setEmail(data[2]);
			}
			
			public String getFirstName() { return firstName; }
			public String getLastName() { return lastName; }
			public String getEmail() { return email; }
			
			public void setFirstName(String fn) { firstName = fn; }
			public void setLastName(String ln) { lastName = ln; }
			public void setEmail(String em) { email = em; }
			
			
			
			private String firstName;
			private String lastName;
			private String email;
		}
		
		/**
		* Appends a message to log.txt. Usually written to when an error occurs.
		* @param msg message to write
		*/
		public void log(ServletConfig config, String msg) {
			BufferedWriter out = null;
			try {
				String path = config.getServletContext().getRealPath("list/logs");
				out = new BufferedWriter(new FileWriter(
						new File(path + "\\uploadlog " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
								+ "-" + Calendar.getInstance().get(Calendar.MONTH) +
								"-" + Calendar.getInstance().get(Calendar.YEAR) +
								".txt"), true));
			
				out.write(	"("+ getFormattedDateTime() + ") " +	msg);
				out.newLine();
				
				out.close();
			}
			catch (IOException e) {}
		}
		
		public String getFormattedDateTime() {
			Date today;
			String result;
			SimpleDateFormat formatter;

			formatter = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm:ss");
			today = new Date();
			result = formatter.format(today);

			return result;
		}
		
		public String convertPersonToSpaceDelimitedString(Person p) {
			String result = "";
			
			result = p.getFirstName() + " " + p.getLastName() + " " + p.getEmail();
		
			return result;
		}
		
		public ArrayList<Person> writeToList(JspWriter out, ArrayList<Person> list) throws IOException {
			ArrayList<Person> exist = new ArrayList<Person>();
			String data = new String("");
			if (list.size() == 0) {
				return exist;
			}
			
			Hashtable<String, Person> result = null;
			BufferedWriter writer = null;
			try{
				try{
					writer = new BufferedWriter(new FileWriter
							(new File("list.dat"), true));
				}
				catch (FileNotFoundException f){
					new File("list.dat").createNewFile();
					writer = new BufferedWriter(new FileWriter
							("list.dat", true));
				}
				
				BufferedReader in = null;
				in = new BufferedReader(new FileReader("list.dat"));
				
				int buf = 0;
				while ((buf = in.read()) != -1) {
					data += (char)buf;
				}
				
				result = new Hashtable<String, Person>();
				if (data.contains(";")){
					String[] entries = data.split("\\;");
					for (int c = 0; c < entries.length; c++) {
						String[] person = entries[c].split("\\|");
						result.put(person[2].toLowerCase(), new Person(person));
					}
				}
				
				in.close();
			}
			catch(IOException e){
				out.println("Read error: " + e.getMessage());
				return exist;
			}
			
			try{
				//check for existing and write
				for (int c = 0; c < list.size(); c++) {
					if (!result.containsKey(list.get(c).getEmail().toLowerCase())) {
						writer.write(list.get(c).getFirstName()+"|"+list.get(c).getLastName()+"|"+list.get(c).getEmail()+";");
						result.put(list.get(c).getEmail().toLowerCase(), list.get(c));
					}
					else {
						exist.add(list.get(c));
					}
				}
				
				writer.flush();
				writer.close();
			}
			catch(IOException e){
				out.println("An error occured while trying to record new entries: " +
						e.getMessage());
			}
			
			return exist;
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
	%>
	<%
		/*
		*	Set the largest possible filesize.
		*/
		final int MAX_UPLOAD = 2097152; //2 megabytes
		
		String key = null;
		try{
			key = request.getParameter("session");
		}
		catch (NullPointerException n){
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		/*if (!isValidSession(out, response, key)){
			response.sendRedirect("../list/login.html?error=0");
			return;
		}*/
		
		String contentType = request.getContentType();
		if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
			DataInputStream in = new DataInputStream(request.getInputStream());
			int formDataLength = request.getContentLength();
			
			if (formDataLength > MAX_UPLOAD){
				response.sendRedirect("../list/add.html?session="+key);
				out.println("You cannot upload files that big.");
				return;
			}

			byte dataBytes[] = new byte[formDataLength];
			int byteRead = 0;
			int totalBytesRead = 0;
			while (totalBytesRead < formDataLength) {
				byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
				totalBytesRead += byteRead;
			}
			
			String path = config.getServletContext().getRealPath("list/receivedfiles");
			String file = new String(dataBytes);
			String saveFile = path + "\\lastupload.txt";
			
			File save = null;
			FileOutputStream fileOut = null;
			try{
				(save = new File(saveFile)).deleteOnExit();
				fileOut = new FileOutputStream(save);
			}
			catch(IOException e){
				out.println("Error: " + e.getMessage());
				return;
			}

			int lastIndex = contentType.lastIndexOf("=");
			String boundary = contentType.substring(lastIndex + 1,contentType.length());
			
			int pos;
			pos = file.indexOf("filename=\"");
			
			pos = file.indexOf("\n", pos) + 1;
			
			pos = file.indexOf("\n", pos) + 1;
			
			pos = file.indexOf("\n", pos) + 1;
			
			int boundaryLocation = file.indexOf(boundary, pos) - 4;
			int startPos = ((file.substring(0, pos)).getBytes()).length;
			int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
			
			if (fileOut == null){
				out.println("Error." + "( " + path + ")");
				return;
			}
			else {
				fileOut.write(dataBytes, startPos, (endPos - startPos));
				fileOut.flush();
				fileOut.close();
			}

			out.println("File successfully uploaded (" + save.getName() + "). Attempting to read it...");
			
			try{
				BufferedReader reader = new BufferedReader(new FileReader(new File(saveFile)));
				Scanner scan = new Scanner(reader);
				ArrayList<Person> entryList = new ArrayList<Person>();
				ArrayList<String> invalidList = new ArrayList<String>();
				
				boolean valid = true;
				int errcount = 0;
				while (scan.hasNextLine() && valid) {
					String line = scan.nextLine();
					if (!Character.isDigit(line.charAt(0))) {
						errcount++;
						if (errcount > 10) {
							valid = false;
							break;
						}
						continue;
					}
					
					String[] sep = line.split("\\,");
					if (!(sep.length > 2)){
						valid = false;
						break;
					}
					
					//Expected: person[0] = "mr" or "ms," person[1] = first name, person[2] = middle initial, person[3] = last name
					String[] person = sep[1].split("\\s");
					if (person.length != 4){
						invalidList.add(sep[1]);
					}
					else {
						entryList.add(new Person(person[1], person[3], (person[1].charAt(0) + person[3]).toLowerCase() + "@vtc.vsc.edu"));
					}
				}
				
				if (valid){
					ArrayList<Person> invalid = writeToList(out, entryList);
					
					out.println("<p><b>Your file was successfully added and it's entries have been added to the database. Thank you.</b>" +
					"<br />Please note that the e-mail addresses were guessed in this format:<br />"+
					"<i>First letter of the first name</i> + <i>last name</i> + \"@vtc.vsc.edu\" (e.g. Mr. Andrew P. Sibley = \"asibley@vtc.vsc.edu\")</p>");
					if (invalidList.size() > 0) {
						out.println("The following entries could not be added and must be added manually:<br />");
						out.println("<ul>");
						for (int c = 0; c < invalidList.size(); c++) {
							out.println("<li>" + invalidList.get(c) + "</li>");
						}
						out.println("</ul><br />");
					}
					
					if (invalid.size() > 0) {
						out.println("The following entries <b>cannot be added</b> because there is already an entry with the same e-mail. ");
						out.println("When manually adding, please change the e-mail:");
						
						out.println("<table border=\"1\" width=\"50%\">");
						
						out.println("<tr>");
						out.println("\t<th>First Name</th>");
						out.println("\t<th>Last Name</th>");
						out.println("\t<th>E-mail</th>");
						out.println("</tr>");
						
						for (int c = 0; c < invalid.size(); c++) {
							out.println("<tr "+((c%2==0)?"bgcolor=\"FFFFFF\"":"bgcolor=\"EEEEEE\"")+">");
							out.println("\t<td>"+invalid.get(c).getFirstName()+"</td>");
							out.println("\t<td>"+invalid.get(c).getLastName()+"</td>");
							out.println("\t<td>"+invalid.get(c).getEmail()+"</td>");
							out.println("</tr>");
						}
						out.println("</table>");
						
						log(config, "File " + save.getName() + " uploaded from " + request.getRemoteAddr() + ".");
					}
				}
				else{
					out.println("<p>You have uploaded an invalid file. <br />");
					out.println("A valid file has entries that starts with an ID number, is comma delimited (file.CSV, exported from Excel), and ");
					out.println("a person's full name and title (e.g. Mr. Andrew P. Sibley) is in the second cell. Try again.</p>");
					
					log(config, "File " + save.getName() + " uploaded from " + request.getRemoteAddr() + " (invalid file).");
				}
				
				reader.close();
				scan.close();
			}
			catch(Exception e){
				out.println("An error occured: " + e.getMessage() + ".");
			}
			
			out.println("<br /><br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"");
		}
	%>
</body>
</html>