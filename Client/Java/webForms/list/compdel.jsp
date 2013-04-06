<%@ page import="java.io.*, java.util.*"%>
<html>
<head>
	<title>Confirm Delete</title>
</head>
<body>
	<script lang="javascript">
	function loadinparent(url, closeSelf){
		self.opener.location = url;
		if(closeSelf) self.close();
	}
	function load(url){
		window.location = url;
	}
	</script>
	<%!
		public String closeButton(String value){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:self.close()\" />";
		}
		
		public String deleteButton(String value, String serial, String key){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:load('compdelconfirm.jsp?remove="+serial+"&session="+key+"')\" />";
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
		String serial = request.getParameter("serial");
		String old_serial = request.getParameter("serial");
		String building = request.getParameter("building");
		String location = request.getParameter("location");
		String model = request.getParameter("model");
		String macAddress = request.getParameter("macaddress");
		String wallPort = request.getParameter("wallport");
		String person = request.getParameter("person");
		String laptop = request.getParameter("laptop");
		String warrantyDate = request.getParameter("warranty");
		String notes = request.getParameter("notes");
		String key = request.getParameter("session");
		
		if (!isValidSession(out, response, key)) {
			out.println("You do not have permission to view this page.<br /><br />");
			out.println(closeButton("Close"));
			return;
		}
		
		out.println("Do you really want to delete this entry?<br />");
		out.println(
			"<table>\n"+
			"<form name=\"editform\" method=\"post\" action=\"compedit2.jsp\">" +
			"<tr>\n" +
				"\t<td>Serial</td>\n" +
				"\t<td><input type=\"text\" name=\"serial\" value=\""+serial+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Building</td>\n" +
				"\t<td><input type=\"text\" name=\"building\" value=\""+building+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Location</td>\n" +
				"\t<td><input type=\"text\" name=\"location\" value=\""+location+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Model</td>\n" +
				"\t<td><input type=\"text\" name=\"model\" value=\""+model+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Mac Address</td>\n" +
				"\t<td><input type=\"text\" name=\"macaddress\" value=\""+macAddress+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Wall Port</td>\n" +
				"\t<td><input type=\"text\" name=\"wallport\" value=\""+wallPort+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Person</td>\n" +
				"\t<td><input type=\"text\" name=\"person\" value=\""+person+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Laptop?</td>\n" +
				"\t<td><select name=\"laptop\" readonly=\"readonly\" ><option value=\""+laptop+"\">Yes</option><option value=\"No\">No</option></select></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Warranty Expiration Date</td>\n" +
				"\t<td><input type=\"text\" name=\"warranty\" value=\""+warrantyDate+"\" readonly=\"readonly\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td colspan=\"2\">Notes:</td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td colspan=\"2\"><textarea name=\"notes\" cols=\"35\" readonly=\"readonly\" >"+notes+"</textarea></td>\n" +
			"</tr>\n" +
			"</table>\n");
		out.println(deleteButton("Delete", serial, key) + " &nbsp; " + closeButton("Cancel"));
	%>
</body>
</html>