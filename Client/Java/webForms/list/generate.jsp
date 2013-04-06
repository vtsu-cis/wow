<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Generated Mailing List</title>
</head>
<body>
	<%!
		public String[] loadList(JspWriter out) throws IOException {
			String[] result = null;
			
			try{
				BufferedReader reader = new BufferedReader(new FileReader(
						new File("list.dat")));
				
				String data = new String("");
				int buf = 0;
				while ((buf = reader.read()) != -1) {
					data += (char)buf;
				}
				
				if (data.contains(";")){
					result = data.split("\\;");
				}
				else{
					result = new String[1];
					result[0] = "-";
				}
				
				reader.close();
			}
			catch(IOException e){
				out.println("An error occured when attempting to edit the entry: ");
				out.println(e.getMessage());
			}
			
			return result;
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
				
				//CHECKS FOR ADMIN
				if (key != null) {
					while (in.hasNextLine()) {
						String line = in.nextLine();
						if (line.startsWith("#")){
							continue;
						}
					
						String realkey = line.split("\\|")[2];
					
						if (key.equals(realkey) && line.split("\\|")[0].equalsIgnoreCase("administrator")) {
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
		}
		
		if (!isValidSession(out, response, key)) {
			response.sendRedirect("../list/login.html?error=0");
			return;
		}
		
		String[] list = loadList(out);
		if (list[0].equals("-")) {
			response.sendRedirect("../list/account.jsp?session="+key+"&generated=0");
		}
		
		PrintWriter output = null;
		String path = config.getServletContext().getRealPath("list");
		try{
			output = new PrintWriter(new File(path + "\\WLStudents.dat"));
		}
		catch(FileNotFoundException f){
			new File(path + "WLStudents.dat").createNewFile();
			output = new PrintWriter(path + "\\WLStudents.dat");
		}
		
		for (String person : list) {
			output.print(person.split("\\|")[2] + ";");
		}
		
		output.close();
		
		response.sendRedirect("../list/account.jsp?session="+key+"&generated=1");
	%>
</body>
</html>