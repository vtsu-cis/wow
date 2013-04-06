<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Add a computer</title>
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
		if (key == null) {
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		if (!isValidSession(out, response, key)) {
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
	%>
	<h3>Add a computer</h3>
	<form method="get" action="comp2.jsp">
		<table border="0" cellspacing="2" cellpadding="2" width="25%">
			<tr>
				<td>Building</td>
				<td align="right"><input type="text" name="building" /></td>
			</tr>
			<tr>
				<td>Location (Room)</td>
				<td align="right"><input type="text" name="location" /></td>
			</tr>
			<tr>
				<td>Serial Number</td>
				<td align="right"><input type="text" name="serial" /></td>
			</tr>
			<tr>
				<td>Model</td>
				<td align="right"><input type="text" name="model" /></td>
			</tr>
			<tr>
				<td>Warranty Expiration Date</td>
				<td align="right"><input type="text" name="warranty" /></td>
			</tr>
			<tr>
				<td>Mac Address</td>
				<td align="right"><input type="text" name="mac" /></td>
			</tr>
			<tr>
				<td>Wall Port</td>
				<td align="right"><input type="text" name="wallport" /></td>
			</tr>
			<tr>
				<td>Person Assigned</td>
				<td align="right"><input type="text" name="person" /></td>
			</tr>
			<tr>
				<td>Laptop?</td>
				<td align="right">
					<select name="laptop">
						<option value="Yes">Yes</option>
						<option value="No" selected="true">No</option>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="left">Notes:</td>
			</tr>
			<tr>
				<td colspan="2"><textarea cols="35" rows="4" name="notes"></textarea></td>
			</tr>
			<input type="hidden" name="session" value="<%= key %>" />
			<tr>
				<td><input type="submit" value="Submit" /></td>
				<td align="right"><input type="reset" value="Clear" /></td>
			</tr>
		</table>
	</form>
	<br />
	<%
		boolean isNull = true;
		boolean successful = false;
		if (request.getParameter("success") != null) {
			if (request.getParameter("success").equals("1")) {
				successful = true;
			}
			isNull = false;
		}
		
		if (!isNull && successful) {
			out.println("Computer was successfully added!<br />");
			
			out.println("<br />");
		}
		else if (!isNull && !successful) {
			if (request.getParameter("exists") != null) {
				out.println("This computer already exists. Try again.");
			}
			else {
				out.println("An error occured while trying to record this computer. Try again.");
			}
			out.println("<br />");
		}
	%>
	<br />
	<a href="account.jsp?session=<%= key %>">Back to Menu</a><br />
	<a href="login.html">Logout</a>
</body>
</html>