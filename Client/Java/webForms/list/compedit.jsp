<%@ page import="java.io.*, java.util.*"%>
<html>
<head>
	<title>Modify entry</title>
</head>
<body>
	<script lang="javascript">
	function loadinparent(url, closeSelf){
		self.opener.location = url;
		if(closeSelf) self.close();
	}
	
	function gup( name )
	{
		name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
		var regexS = "[\\?&]"+name+"=([^&#]*)";
		var regex = new RegExp( regexS );
		var results = regex.exec( window.location.href );
		if( results == null )
			return "";
		else
			return results[1];
	}
	
	</script>
	<%!
		public String closeButton(String value){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:self.close()\"";
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
	<h3>Edit an entry:</h3>
	
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
		String key = request.getParameter("session");
		
		if (!isValidSession(out, response, key)) {
			out.println("You do not have permission to view this page.<br /><br />");
			out.println(closeButton("Close"));
			return;
		}
		
		out.println(
			"<table>\n"+
			"<form name=\"editform\" method=\"post\" action=\"compedit2.jsp\">" +
			"<tr>\n" +
				"\t<td>Serial</td>\n" +
				"\t<td><input type=\"text\" name=\"serial\" value=\""+serial+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Building</td>\n" +
				"\t<td><input type=\"text\" name=\"building\" value=\""+building+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Location</td>\n" +
				"\t<td><input type=\"text\" name=\"location\" value=\""+location+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Model</td>\n" +
				"\t<td><input type=\"text\" name=\"model\" value=\""+model+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Mac Address</td>\n" +
				"\t<td><input type=\"text\" name=\"macaddress\" value=\""+macAddress+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Wall Port</td>\n" +
				"\t<td><input type=\"text\" name=\"wallport\" value=\""+wallPort+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Person</td>\n" +
				"\t<td><input type=\"text\" name=\"person\" value=\""+person+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Laptop?</td>\n" +
				"\t<td><select name=\"laptop\"><option value=\"Yes\">Yes</option><option value=\"No\">No</option></select></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Warranty Expiration Date</td>\n" +
				"\t<td><input type=\"text\" name=\"warranty\" value=\""+warrantyDate+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td colspan=\"2\">Notes:</td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td colspan=\"2\"><textarea name=\"notes\" cols=\"35\"></textarea></td>\n" +
			"</tr>\n" +
			"<tr>\n"+
			"\t<td><input type=\"hidden\" name=\"session\" value=\""+key+"\" />\n" +
			"\t<input type=\"hidden\" name=\"serial_old\" value=\""+serial+"\" />\n" +
			"<input type=\"submit\" value=\"Modify\" />\n</td>" +
			"\t<td align=\"right\">" + closeButton("Cancel") + "</td>\n" +
			"</form>\n" +
			"</table>\n");
			
		
	%>
</body>
</html>