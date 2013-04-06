<%@ page import="java.net.*, java.io.*, java.util.*" %>
<html>
<head>
	<title>Information Submitted</title>
</head>
<body onload="window.opener.document.location.reload()">
	<%
	if (!request.getParameter("acc").equals("accurate")) {
		Socket socket = null;
		
		try{
			String wowIP = "155.42.234.35";
			
			String filename = getServletContext().getRealPath("validate/updates.txt");
			FileWriter writer = new FileWriter(filename, true);
			
			/*socket = new Socket(wowIP, 5280);
			
			InputStream inSocket = socket.getInputStream();
			PrintWriter outSocket = new PrintWriter(socket.getOutputStream(), true);*/
			
			String line 		= "|";
			String firstName 	= request.getParameter("firstname");
			String lastName 	= request.getParameter("lastname");
			String phoneNumber 	= request.getParameter("phonenumber");
			String email 		= request.getParameter("email");
			String campus 		= request.getParameter("campus");
			String role 		= request.getParameter("role");
			String dept 		= request.getParameter("dept");
			String faxNumber 	= request.getParameter("faxnumber");
			String location 	= request.getParameter("location");
			
			if (phoneNumber.equals("")) {
					phoneNumber = "(802)000-0000";
			}
			
			if (email.equals("")) {
				email = "_@_._";
			}
			
			if (!(firstName != null)) {
				firstName = new String("");
			}
			if (!(lastName != null)) {
				lastName = new String("");
			}
			
			String update = new String();
			if (request.getParameter("acc").equals("delete")) {
				out.println("Deleting entry.");
				update = "DEL: " + firstName + line + lastName +
					line + phoneNumber + line + email + line + campus + line + role + line + dept + line + faxNumber + line + location;
			}
			else {
				out.println("Updating entry.");
				update = "UPD: " + firstName + line + lastName +
					line + phoneNumber + line + email + line + campus + line + role + line + dept + line + faxNumber + line + location;
			}
			
			try {
				writer.write(update + "\n");
				
				out.println("Your update was received, but it may take a while to be validated and entered into the database.");
			}
			catch (IOException e) {
				out.println("Unable to update: " + e.getMessage());
			}
			/*socket.setSoTimeout(15000);
			outSocket.println(update);
			
			int character;
			
			String result = new String("");
			
			while ((character = inSocket.read()) != -1) {
				result += (char)character;
			}
			
			out.println("\t<br />\n\tResult: "+result+"\n\t<br />");
			
			outSocket.close();
			inSocket.close();
			socket.close();*/
			
			writer.close();
		}
		catch (SocketTimeoutException e){
			%> The server is not responding.<br><%
			out.println(e.getMessage());
		}
		catch (ConnectException e){
			%> Cannot connect to server.<br> <%
			out.println(e.getMessage());
		} 
		catch (IOException e){
			%> There was an error connecting to the server.<br> <%
			out.println(e.getMessage());
		}
		
		}
		else if (request.getParameter("acc").equals("accurate")) {
			//Send message saying "accurate"
		}
		
		out.println("<h4>Thank you for your time.</h4>\nIf you have any questions or comments, e-mail<br />");
		out.println("<a href=\"mailto:asibley@vtc.vsc.edu\">Andrew Sibley</a>, <a href=\"mailto:nguertin@vtc.vsc.edu\">Nick Guertin</a>, <a " +
						"href=\"mailto:dransom@vtc.vsc.edu\">Boomer Ransom</a> or <a href=\"mailto:cbeattie@vtc.vsc.edu\">Chris Beattie</a>");
		%>
</body>
</html>