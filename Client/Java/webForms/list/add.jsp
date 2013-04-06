<%@ page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Update Entry</title>
</head>
<body>
	<%!
		/**
		*	@return 0 if it doesn't exist, 1 if it exists because of first name/last name, 2 if it exists because of email,
		*	or 3 if it's first/last/email
		*/
		public int exists(JspWriter out, String firstName, String lastName,
					String email) throws IOException {
			String[] result = null;
			String data = new String("");
			try{
				BufferedReader in = null;
				try{
					in = new BufferedReader(new FileReader("list.dat"));
				}
				catch(FileNotFoundException f){
					new File("list.dat").createNewFile();
					in = new BufferedReader(new FileReader("list.dat"));
				}
				
				int buf = 0;
				while ((buf = in.read()) != -1) {
					data += (char)buf;
				}
				
				if (data.contains(";")){
					result = data.split("\\;");
				}
				else{
					return 0;
				}
				
				in.close();
				
				for (int c = 0; c < result.length; c++) {
					String[] person = result[c].split("\\|");
					if (firstName.equalsIgnoreCase(person[0]) && 
							lastName.equalsIgnoreCase(person[1])) {
						//entry exists
						return (email.equalsIgnoreCase(person[2]))?3:1;
					}
					
					if (email.equalsIgnoreCase(person[2])) {
						//entry exists
						return 2;
					}
				}
			}
			catch(IOException e){
				out.println("An error occured when attempting to edit the entry: ");
				out.println(e.getMessage());
			}
			catch(ArrayIndexOutOfBoundsException a){
				out.println("The database is corrupted. Please report this:<br />");
				out.println("Out of bounds (entry contains no information): " +
						a.getMessage());
			}
			
			return 0;
		}
	%>
	<%
		String firstName = "";
		String lastName = "";
		String email = "";
		String key = "0";
		try{
			firstName = request.getParameter("firstname").trim();
		}
		catch(NullPointerException n){}
		
		try{
			lastName = request.getParameter("lastname").trim();
		}
		catch(NullPointerException n){}
		
		try{
			email = request.getParameter("email").trim();
		}
		catch(NullPointerException n){}
		
		try{
			key = request.getParameter("session");
		}
		catch(NullPointerException n){
			key = "0";
		}
		
		if (firstName.equals("") || lastName.equals("") || email.equals("")) {
			out.println("One or more fields have not been filled out. <br />");
			out.println("Please go back and try again.");
			out.println("<br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"");
		}
		else if (!email.contains("@")) {
			out.println("The e-mail you entered is not a valid one.<br />");
			out.println("Please go back and try again.");
			out.println("<br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"");
		}
		else if (firstName.contains("|") 	|| 
				 firstName.contains(";") 	||
				 lastName.contains("|") 	|| 
				 lastName.contains(";") 	||
				 email.contains("|") 		|| 
				 email.contains(";")) {
			out.println("One or more fields contain a '|' or ';' character.<br />");
			out.println("Please go back and try again.");
			out.println("<br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"");
		}
		else { //valid
			int state = exists(out, firstName, lastName, email);
			
			if (state != 0) {
				switch (state) {
					case 1:
						out.println("The same first and last name exist already.");
						break;
					case 2:
						out.println("This e-mail exists already.");
						break;
					case 3:
						out.println("This user is already entered in the database.");
						break;
				}
				out.println("<br /><br /><input type=\"button\" value=\"Back\" " +
					"onClick=\"javascript:history.go(-1)\"");
			}
			else {
				BufferedWriter writer = null;
				try{
					writer = new BufferedWriter(new FileWriter
							(new File("list.dat"), true));
				}
				catch (FileNotFoundException f){
					new File("list.dat").createNewFile();
					writer = new BufferedWriter(new FileWriter
							("list.dat", true));
				}
				try{
					writer.write(firstName+"|"+lastName+"|"+email+";");
					writer.flush();
					writer.close();
					
					out.println(
						"You have successfully added " + firstName + " " + lastName +".");
					out.println("<p>Please choose a new action:</p>");
					out.println("<a href=\"add.html?session="+key+"\">Add another user.</a><br />");
					out.println("<a href=\"account.jsp?session="+key+"\">Account menu</a><br />");
					out.println("<a href=\"list.jsp?session="+key+"\">View or Modify Entries</a><br />");
					
				}
				catch(IOException e){
					out.println("An error occured while trying to record a new entry: " +
							e.getMessage());
				}
			}
		}
	%>
</body>
</html>