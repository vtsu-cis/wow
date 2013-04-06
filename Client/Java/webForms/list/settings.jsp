<%@page import="java.io.*, java.util.*, java.text.*" %>
<html>
<head>
	<title>Account Settings</title>
</head>
<body>
	<%!
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
		String key = request.getParameter("session");
		if (!isValidSession(out, response, key)) {
			response.sendRedirect("../list/login.html?error=0");
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
			
		in.close();
		reader.close();
		
		
	%>
	<h3>Account Settings</h3>
	<form method="post" action="settings2.jsp" name="settingsform">
		<table cellspacing="3">
			<tr>
				<td>Account Name</td>
				<td><input type="text" name="accountname" value=<%= accountInfo[0] %> /></td>
			</tr>
			<tr>
				<td>E-mail Address</td>
				<td><input type="text" name="email" value=<%= accountInfo[3] %> /></td>
			</tr>
			<tr>
				<input type="hidden" name="session" value=<%= key %> />
				<td><input type="submit" value="Submit" /></td>
				<td>&nbsp;</td>
			</tr>
	</form>
	<form method="post" action="changepw.jsp" name="pwform">
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr colspan="2">
				<td><b>Change Password</b></td>
			</tr>
			<tr>
				<td>Old Password</td>
				<td><input type="password" name="oldpassword" /></td>
			</tr>
			<tr>
				<td>New Password</td>
				<td><input type="password" name="newpassword1" /></td>
			</tr>
			<tr>
				<td>Confirm Password</td>
				<td><input type="password" name="newpassword2" /></td>
			</tr>
			<tr colspan="2">
				<input type="hidden" name="session" value=<%= key %> />
				<td><input type="submit" value="Change Password" /></td>
			</tr>
	</form>
		</table>
	
	<br />
	<br />
	<% out.println("<a href=\"account.jsp?session="+key+">Back to Menu</a>"); %>
	
</body>
</html>