<%@page import="java.io.*, java.util.*, java.text.*" %>
<html>
<head>
	<title>Update Settings</title>
</head>
<body>
	<%!
		public String[] loadUserList() throws IOException {
			String[] result = null;
			
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
			
			ArrayList<String> list = new ArrayList<String>();
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.startsWith("#")){
					continue;
				}
			
				list.add(line);
			}
			
			result = (String[])list.toArray(new String[list.size()]);
			
			in.close();
			reader.close();
			
			return result;
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
	%>
	<%
		String key = null;
		String oldAccount = null;
		String newAccount = null;
		String oldEmail = null;
		String newEmail = null;
		try{
			if (!isValidSession(out, response, (key = request.getParameter("session")))) {
				response.sendRedirect("../list/login.html?error=0");
				return;
			}
		}
		catch(NullPointerException n){
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		newAccount = request.getParameter("accountname");
		newEmail = request.getParameter("email");
		if (newAccount == null || newEmail == null){
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("You must have an account name and e-mail address.");
			return;
		}
		
		if (newAccount.contains(";") || newAccount.contains("|") || newEmail.contains(";") || newEmail.contains("|")) {
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("Check your account and e-mail for illegal characters and try again.");
			return;
		}
		
		if (!newEmail.contains("@")) {
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("Invalid e-mail address.");
			return;
		}
		
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
		
		String line = null;
		while (in.hasNextLine()) {
			line = in.nextLine();
			if (line.startsWith("#")){
				continue;
			}
		
			if (line.contains(key)){
				break;
			}
		}
			
		//Expected: accountInfo[0] = name / [1] = password / [2] = session key / [3] = e-mail address
		String[] accountInfo = line.split("\\|");
		oldAccount = accountInfo[0];
		oldEmail = accountInfo[3];
		
		in.close();
		reader.close();
		
		boolean accountChanged = false;
		boolean emailChanged = false;
		if (!oldAccount.equalsIgnoreCase(newAccount)) {
			accountChanged = true;
		}
		
		if (!oldEmail.equalsIgnoreCase(newEmail)) {
			emailChanged = true;
		}
		
		if (accountChanged || emailChanged) {
			accountInfo[0] = newAccount;
			accountInfo[3] = newEmail;
			
			try{
				String[] data = loadUserList();
				PrintWriter writer = new PrintWriter(new File("users.dat"));
				
				Hashtable<String, String> check = new Hashtable<String, String>();
				for (int c = 0; c < data.length; c++) {
					if (!data[c].equalsIgnoreCase(oldAccount + "|" + accountInfo[1] + "|" + key + "|" + oldEmail)) {
						writer.print(data[c] + ((data[c].contains("\n"))?"":"\n"));
						check.put(data[c].split("\\|")[0], data[c]);
					}
				}
				
				if (check.containsKey(accountInfo[0])) {
					out.println("This account name is already in use.");
				}
				else {
					writer.println(accountInfo[0] + "|" + accountInfo[1] + "|" + accountInfo[2] + "|" + accountInfo[3]);
				
				%>
					<h3> Update complete. </h3>
					<p>Your account information was successfully updated.</p>
					<b>Account name: </b><%= accountInfo[0] %><br />
					<b>E-mail address: </b><%= accountInfo[3] %><br />
					<br />
				<%
				}
				writer.close();
				
				out.println(backButton());
				out.println("<br /> <br />");
				out.println("<a href=\"account.jsp?session="+key+"\">Back to Menu</a>");
			}
			catch(IOException e){
				out.println("Problem updating account! (" + e.getMessage() + ")");
			}
		}
		else {
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("Nothing was updated.");
			return;
		}
	%>
	
</body>
</html>