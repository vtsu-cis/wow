<%@ page import="java.io.*, java.util.*" %>
<html>
	<head>
		<title><% out.println(request.getParameter("firstname") + " " + request.getParameter("lastname")); %></title>
	</head>
	<body>
		<%!
			public void createHTMLForms(JspWriter out, String firstName, String lastName, String phoneNumber, String email, String campus, String role, String dept, String faxNumber, String location) throws IOException {
				out.println("<form name=edit action=\"submit.jsp\" method=\"get\">");
				out.println("<table cellspacing=\"2\" cellpadding=\"2\">");
				out.println("<tr>\n" +
				"<td>First Name</td>\n" +
				"<td><input type=\"text\" name=\"firstname\" value=\"" + firstName + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Last Name</td>\n" +
				"<td><input type=\"text\" name=\"lastname\" value=\"" + lastName + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Phone Number</td>\n" +
				"<td><input type=\"text\" name=\"phonenumber\" value=\"" + phoneNumber + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>E-mail</td>\n" +
				"<td><input type=\"text\" name=\"email\" value=\"" + email + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Campus</td>\n" +
				"<td><input type=\"text\" name=\"campus\" value=\"" + campus + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Role</td>\n" +
				"<td><input type=\"text\" name=\"role\" value=\"" + role + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Department</td>\n" +
				"<td><input type=\"text\" name=\"dept\" value=\"" + dept + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Fax Number</td>\n" +
				"<td><input type=\"text\" name=\"faxnumber\" value=\"" + ((faxNumber!=null)?faxNumber:"") + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr>\n" +
				"<td>Location</td>\n" +
				"<td><input type=\"text\" name=\"location\" value=\"" + location + "\" onChange=\"document.edit.acc[1].checked = true;\">\n" +
				"</tr>");
				out.println("<tr colspan=\"2\" width=\"100%\">");
				out.println("<td>Data is accurate;<br /><b>No changes</b>\n" +
							"<input type=\"radio\" name=\"acc\" value=\"accurate\" checked=\"checked\" /></td>\n</tr>");
				out.println("<tr colspan=\"2\" width=\"100%\">");
				out.println("<td>Data is not accurate;<br /><b>Submit changes</b>\n" +
							"<input type=\"radio\" name=\"acc\" value=\"notaccurate\" /></td>\n</tr>");
				out.println("<tr colspan=\"2\"  >");
				out.println("<td>Information is obsolete;<br /><b>Delete entry</b>\n" +
							"<input type=\"radio\" name=\"acc\" value=\"delete\" /></td>\n</tr>");
				out.println("</table>");
				out.println("<input type=\"submit\" value=\"Submit\" onClick=\"\"/><br />");
				out.println("</form>");
			}
		%>
		<%
			String firstName 	= request.getParameter("firstname");
			String lastName 	= request.getParameter("lastname");
			String phoneNumber 	= request.getParameter("phonenumber");
			String email 		= request.getParameter("email");
			String campus 		= request.getParameter("campus");
			String role 		= request.getParameter("role");
			String dept 		= request.getParameter("dept");
			String faxNumber 	= request.getParameter("faxNumber");
			String location 	= request.getParameter("location");
			String key			= request.getParameter("key");
			
			boolean validKey = false;
			try {
				String filename = getServletContext().getRealPath("validate/profiles.dat");
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				Scanner in = new Scanner(reader);
				
				while (in.hasNextLine()) {
					String[] line = in.nextLine().split("\\|");
					if (key.equalsIgnoreCase(line[3]) && firstName.equalsIgnoreCase(line[0]) && lastName.equalsIgnoreCase(line[1])) {
						validKey = true;
						break;
					}
				}
				
			in.close();
			}
			catch (IOException e) {
				out.println("Could not validate key: " + e.getMessage());
			}
			
			
			
			if (validKey) {
				out.println("Information on <b>" + firstName + " " + lastName + "</b>:");
			
				try{
					createHTMLForms(out, firstName,lastName,phoneNumber,email,campus,role,dept,faxNumber,location);
				}
				catch (IOException e){
					out.println("Cannot create forms:<br />" + e.getMessage());
				}
			}
			else {
				out.println("You do not have permission to modify this entry.");
			}
			
		%>
	</body>
</html>