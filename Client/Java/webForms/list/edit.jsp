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
	%>
	<h3>Edit an entry:</h3>
	
	<%
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String email = request.getParameter("email");
		String key = request.getParameter("session");
		String error = request.getParameter("error");
		if (error != null) {
			if (error.equals("1")) {
				out.println("Make sure there are no unusual characters in the edit boxes.");
			}
		}
		
		out.println(
			"<table>\n"+
			"<form name=\"editform\" method=\"get\" action=\"modify.jsp\">" +
			"<tr>\n" +
				"\t<td>First Name</td>\n" +
				"\t<td><input type=\"text\" name=\"firstname\" value=\""+firstname+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>Last Name</td>\n" +
				"\t<td><input type=\"text\" name=\"lastname\" value=\""+lastname+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n" +
				"\t<td>E-mail</td>\n" +
				"\t<td><input type=\"text\" name=\"email\" value=\""+email+"\" /></td>\n" +
			"</tr>\n" +
			"<tr>\n"+
			"\t<td><input type=\"hidden\" name=\"session\" value=\""+key+"\" />\n" +
			"\t<input type=\"hidden\" name=\"firstname_old\" value=\""+firstname+"\" />\n" +
			"\t<input type=\"hidden\" name=\"lastname_old\" value=\""+lastname+"\" />\n" +
			"\t<input type=\"hidden\" name=\"email_old\" value=\""+email+"\" />\n" +
			"<input type=\"submit\" value=\"Modify\" />\n</td>" +
			"\t<td align=\"right\">" + closeButton("Cancel") + "</td>\n" +
			"</form>\n" +
			"</table>\n");
			
		
	%>
</body>
</html>