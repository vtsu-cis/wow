<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Menu</title>
</head>
<body>
	<%
		String key = new String("");
		String name = new String("");
		BufferedReader reader = null;
		Scanner in = null;
		try{
			key = request.getParameter("session");
		}
		catch(NullPointerException n){
			response.sendRedirect("../list/login.html");
		}
		try{
			try{
				reader = new BufferedReader(new FileReader(new File("users.dat")));
			}
			catch(FileNotFoundException f){
				new File("users.dat").createNewFile();
				reader = new BufferedReader(new FileReader(new File("users.dat")));
			}
			in = new Scanner(reader);
			
			boolean found = false;
			boolean adminKey = false;
			if (key != null) {
				while (in.hasNextLine()) {
					String line = in.nextLine();
					if (line.startsWith("#")){
						continue;
					}
					
					String realkey = line.split("\\|")[2];
					
					if (key.equals(realkey)) {
						name = line.split("\\|")[0];
						found = true;
						if (line.split("\\|")[0].equalsIgnoreCase("Administrator"))
							adminKey = true;
						break;
					}
				}
			}
			
			in.close();
			reader.close();
			
			if (!found) {
				response.sendRedirect("../list/login.html?error=0");
			}
			else {
				out.println("<h3>You're logged in as " + name + ".</h3>");
				out.println("(If you're not " + name +
						", <a href=\"login.html\">click here</a>.)");
				out.println("<br /><br /><br /><b>Williston Student Mailing List</b><br />");
				out.println("<a href=\"add.html?session="+key+"\">Add entries</a><br />");
				out.println("<a href=\"list.jsp?session="+key+"\">View or modify entries</a><br />");
				out.println("<br /><b>VTC Williston's List of Computers</b><br />");
				out.println("<a href=\"comp.jsp?session="+key+"\">Add Computer</a><br />");
				out.println("<a href=\"complist.jsp?session="+key+"\">View or modify school's computers</a><br />");
				out.println("<br /><b>Account</b><br />");
				out.println("<a href=\"settings.jsp?session="+key+"\">Account Settings</a><br /><br />");
				out.println("<br /> <br /> <br />");
				if (adminKey) {
					out.println("<b>Administrator actions:</b><br />");
					out.println("<a href=\"create.jsp?session="+key+"\">Add an account</a><br />");
					out.println("<a href=\"remove.jsp?session="+key+"\">Remove an account</a><br />");
					out.println("<a href=\"generate.jsp?session="+key+"\">Generate WLStudents.dat</a><br />");
					if (request.getParameter("generated") != null) {
						if (request.getParameter("generated").equals("1")) {
							out.println("The file was successfully generated with the latest information.<br />");
						}
						else {
							out.println("The file was <b>NOT</b> successfully generated.<br />");
						}
					}
					out.println("<a href=\"compgenerate.jsp?session="+key+"\">Generate comma-delimited list of computers</a ><br />");
					out.println("<a href=\"WLStudents.dat\">Download WLStudents.dat</a><br />");
				}
			}
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
	%>
</body>
</html>