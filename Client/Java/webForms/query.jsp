<%@ page import="java.net.*, java.io.*, java.util.*" %>
<html>
	<head>
		<%
		out.println("<title>");
		String titleFirstName = request.getParameter("firstname");
		String titleLastName = request.getParameter("lastname");
		String title;
		if (titleFirstName.equals("") && titleLastName.equals("")) {
			title = "everyone";
		}
		else {
			title = titleFirstName + " " + titleLastName;
		}
		out.println("Information on " + title);
		out.println("</title>");
		%>
		<SCRIPT LANGUAGE="JavaScript">
			function popupaddpackage(urlToOpen)
			{
				var window_width = (screen.availWidth/4)+50;
				var window_height = (screen.availHeight/2)+50;
				var window_left = (screen.availWidth/2)-(window_width/2);
				var window_top = (screen.availHeight/2)-(window_height/2);
				var winParms = "Status=yes" + ",scrollbars=1"+ ",resizable=yes" + ",height="+window_height+",width="+window_width + ",left="+window_left+",top="+window_top;
				var newwindow = window.open(urlToOpen,'_blank',winParms);
				newwindow.focus()
			}
		</script>
	</head>
	<body>
	<%!
		public String printHeader() {
			return  "<tr>\n " +
						"\t<th>&nbsp;</th>\n" +
						"\t<th>First Name</th>\n" 	+
						"\t<th>Last Name</th>\n"  	+
						"\t<th>Phone Number</th>\n" +
						"\t<th>E-mail Address</th>\n"+
						"\t<th>Campus</th>\n" 		+
						"\t<th>Role</th>\n" 		+
						"\t<th>Department</th>\n" 	+
						"\t<th>Fax Number</th>\n" 	+
						"\t<th>Location</th>\n" 	+
					"</tr>\n";
		}
		
		public void printResults(JspWriter out, String result, String key) throws IOException {
			out.println("<!-- " + result + " -->");
			
			int resultCount = 0;
			
			for (int i = 0; i < result.length(); i++) {
				if (result.charAt(i) == '\n') {
					resultCount++;
				}
			}
			out.println("Found " + resultCount + " entries:<br />");
			
			if (resultCount > 0) {
				String[] person 		= new String[resultCount];
				String[] firstName 		= new String[resultCount];
				String[] lastName 		= new String[resultCount];
				String[] phoneNumber 	= new String[resultCount];
				String[] email 			= new String[resultCount];
				String[] campus 		= new String[resultCount];
				String[] role 			= new String[resultCount];
				String[] dept 			= new String[resultCount];
				String[] faxNumber 		= new String[resultCount];
				String[] location 		= new String[resultCount];
				
				for (int noNull = 0; noNull < resultCount; noNull++) {
					person[noNull]      = new String("");
					firstName[noNull] 	= new String("");
					lastName[noNull] 	= new String("&nbsp;");
					phoneNumber[noNull] = new String("&nbsp;");
					email[noNull] 		= new String("&nbsp;");
					campus[noNull] 		= new String("&nbsp;");
					role[noNull] 		= new String("&nbsp;");
					dept[noNull] 		= new String("&nbsp;");
					faxNumber[noNull] 	= new String("&nbsp;");
					location[noNull] 	= new String("&nbsp;");
				}
				int pos = 0;
				int j = 0;
				while (pos < person.length) {
					if (result.charAt(j) == '\n') {
						person[pos] += "$";
						pos++;
					}
					else {
						person[pos] += result.charAt(j);
					}
					j++;
				}
				
				
				
				int lineCount = 0;
				for (int i = 0; i < resultCount; i++) {
					for (int charPos = 0; charPos < person[i].length(); charPos++) {
						if (person[i].charAt(charPos) != '|') {
							if (lineCount == 0)
								firstName[i] += person[i].charAt(charPos);
							else if (lineCount == 1)
								lastName[i] += person[i].charAt(charPos);
							else if (lineCount == 2)
								phoneNumber[i] += person[i].charAt(charPos);
							else if (lineCount == 3)
								email[i] += person[i].charAt(charPos);
							else if (lineCount == 4)
								campus[i] += person[i].charAt(charPos);
							else if (lineCount == 5)
								role[i] += person[i].charAt(charPos);
							else if (lineCount == 6)
								dept[i] += person[i].charAt(charPos);
							else if (lineCount == 7)
								faxNumber[i] += person[i].charAt(charPos);
							else if (lineCount == 8) {
								if (person[i].charAt(charPos) != '$') {
									location[i] += person[i].charAt(charPos);
								}
								else {
									lineCount = 0;
								}
							}
						}
						else
							lineCount++;
					}
				}
				
				String link = new String();
				out.println("<table cellspacing=\"2\" cellpadding=\"2\" frame=\"box\" width=\"100%\" border=\"1\">");
				out.println(printHeader());
				for (int current = 0; current < resultCount; current++) {
					link = "\t<td><input type=button value=\"Edit\" onclick=\"javascript:popupaddpackage('editBox.jsp?firstname=" + firstName[current] + "&lastname=" + lastName[current] + "&phonenumber=" + phoneNumber[current].replace("(802)000-0000","") + "&email=" + email[current].replace("_@_._","") + "&campus=" + campus[current] + "&role=" + role[current] + "&dept=" + dept[current] + "&faxnumber=" + faxNumber[current] + "&location=" + location[current] + "&key=" + key + "')\"></td>";
					out.println("<tr " + ((current%2==0)?"bgcolor=\"EEEEEE\"":"bgcolor=\"FFFFFF\"") + ">");
					out.println(link.replace("&nbsp;",""));
					out.println("\t<td>" + firstName[current] + "</td>");
					out.println("\t<td>" + lastName[current] + "</td>");
					out.println("\t<td>" + phoneNumber[current].replace("(802)000-0000","") + "</td>");
					out.println("\t<td>" + email[current].replace("_@_._","") + "</td>");
					out.println("\t<td>" + campus[current] + "</td>");
					out.println("\t<td>" + role[current] + "</td>");
					out.println("\t<td>" + dept[current] + "</td>");
					out.println("\t<td>" + faxNumber[current] + "</td>");
					out.println("\t<td>" + location[current] + "</td>");
					out.println("</tr>");
				}
				out.println("</table>");
			}
			else { /*No results*/
			
			}
		}
	%>
	<%
		Socket socket = null;
		try{
			String wowIP = "155.42.234.35";
			
			socket = new Socket(wowIP, 5280);
			
			InputStream inSocket = socket.getInputStream();
			PrintWriter outSocket = new PrintWriter(socket.getOutputStream(), true);
			//OutputStream outSocket = socket.getOutputStream();
			
			String line = "|";
			String firstName = request.getParameter("firstname").replace("\"", "").trim();
			String lastName = request.getParameter("lastname").replace("\"", "").trim();
			String key = request.getParameter("key").replace("\"", "").trim();
			
			if (!(firstName != null)) {
				firstName = new String("");
			}
			if (!(lastName != null)) {
				lastName = new String("");
			}
			
			String query = "QRY: " + firstName + line + lastName +
				line + line + line + line + line + line + line;
			
			
			out.println("<h3>Please edit information pertaining to " + firstName + " " + lastName + ".</h3>");
			
			socket.setSoTimeout(15000);
			outSocket.println(query);
			
			int character;
			
			String result = new String("");
			
			while ((character = inSocket.read()) != -1) {
				result += (char)character;
			}
			printResults(out, result, key);
			
			out.println("\t<br />\n\t<br />");
			
			outSocket.close();
			inSocket.close();
			socket.close();
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
	%>
	</body>
</html>