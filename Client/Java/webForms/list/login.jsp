<%@ page import="java.io.*, java.util.*"%>
<html>
<head>
	<title>Logging in...</title>
	<%
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	%>
</head>
<body>
	<%
		String accountName = new String("");
		String password = new String("");
		try{
			accountName = request.getParameter("username");
			password = request.getParameter("password");
		}
		catch(NullPointerException n){
			response.sendRedirect("../list/login.html?error=2");
		}
		
		BufferedReader reader = null;
		Scanner in = null;
		String key = "";
		try{
			try {
				reader = new BufferedReader(new FileReader("users.dat"));
			}
			catch(FileNotFoundException f){
				new File("users.dat").createNewFile();
				reader = new BufferedReader(new FileReader(new File("users.dat")));
			}
			in = new Scanner(reader);
			
			boolean found = false;
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.startsWith("#"))
					continue;
				String user = line.split("\\|")[0];
				String pass = line.split("\\|")[1];
				key = line.split("\\|")[2];
				
				if (accountName.equalsIgnoreCase(user) && password.equals(pass)) {
					found = true;
					break;
				}
			}
			
			in.close();
			reader.close();
			
			if (!found){
				response.sendRedirect("../list/login.html?error=1");
			}
			else {
				response.sendRedirect("../list/account.jsp?session="+key);
			}
		}
		catch(IOException e){
			out.println("There is a database error: " + e.getMessage() + ". " +
					"Try again later.");
		}
		catch(ArrayIndexOutOfBoundsException a){
			out.println("A problem has occured and the database may be corrupted.<br />"+
					"Please report this error:<br />" + 
					"Array out of bounds (" + a.getMessage() + ").");
		}
		catch(NullPointerException n){
			out.println("There may be a bug preventing you from logging in.\n<br />" +
					"Please report this error:<br />" +
					"Null received (invalid parameter: " + n.getMessage() + ")");
		}
	%>
</body>
</html>