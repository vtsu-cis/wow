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
		
		public String deleteButton(String value, String email, String key){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:load('delconfirm.jsp?remove="+email+"&session="+key+"')\" />";
		}
	%>
	<%
		String key = request.getParameter("session");
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String email = request.getParameter("email");
		
		if (key == null || firstName == null || lastName == null || email == null) {
			out.println("Error: Invalid deletion attempt. Try again.");
			out.println(closeButton("Close"));
		}
		
		out.println("Do you really want to delete this entry?<br />");
		out.println("<b>Name</b>: " + firstName + " " + lastName + "<br />");
		out.println("<b>E-mail</b>: " + email + "<br /> <br />");
		out.println(deleteButton("Delete", email, key) + " &nbsp; " + closeButton("Cancel"));
	%>
</body>
</html>