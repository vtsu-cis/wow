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
		String oldPassword = null;
		String newPassword = null;
		String confirmNewPassword = null;
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
		
		try{
			oldPassword = request.getParameter("oldpassword");
			newPassword = request.getParameter("newpassword1");
			confirmNewPassword = request.getParameter("newpassword2");
			
			if (!newPassword.equals(confirmNewPassword)){
				out.println("Your new password does not equal your confirmed new password.  Go back and try again.");
				out.println(backButton());
				out.println("<br /> <br />");
				out.println("<a href=\"account.jsp\">Back to Menu</a>");
				return;
			}
			
			if (oldPassword.equals(newPassword)){
				out.println("You did not change your password.  Go back and try again.");
				out.println(backButton());
				out.println("<br /> <br />");
				out.println("<a href=\"account.jsp\">Back to Menu</a>");
				return;
			}
		}
		catch(NullPointerException n){
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("You are required to fill in every field to change your password.");
			return;
		}
		
		if (newPassword.contains("|")) {
			response.sendRedirect("../list/settings.jsp?session="+key);
			out.println("Your new password cannot contain the character '|'");
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
		accountInfo[1] = newPassword;
		
		in.close();
		reader.close();
		
		try{
			String[] data = loadUserList();
			PrintWriter writer = new PrintWriter(new File("users.dat"));
			
			for (int c = 0; c < data.length; c++) {
				if (!data[c].equalsIgnoreCase(accountInfo[0] +"|"+ oldPassword +"|"+ accountInfo[2] +"|"+ accountInfo[3])){
					writer.print(data[c] + ((data[c].contains("\n"))?"":"\n"));
				}
			}
			
			writer.println(accountInfo[0] + "|" + accountInfo[1] + "|" + accountInfo[2] + "|" + accountInfo[3]);
				
				%>
					<h3> Update complete. </h3>
					<p>Your password was successfully updated.</p>
					<br />
				<%
				
			writer.close();
			
			out.println(backButton());
			out.println("<br /> <br />");
			out.println("<a href=\"account.jsp\">Back to Menu</a>");
		}
		catch(IOException e){
			out.println("Problem updating account! (" + e.getMessage() + ")");
		}
	%>
	
</body>
</html>