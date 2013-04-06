<%@ page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>List of users</title>
</head>
<body>
	<script type="text/javascript">
		function popupaddpackage(urlToOpen)
			{
				var window_width = (screen.availWidth/4)+10;
				var window_height = (screen.availHeight/8)+60;
				var window_left = (screen.availWidth/2)-(window_width/2);
				var window_top = (screen.availHeight/2)-(window_height/2);
				var winParms = "Status=yes" + ",scrollbars=1"+ ",resizable=yes" + ",height="+window_height+",width="+window_width + ",left="+window_left+",top="+window_top;
				var newwindow = window.open(urlToOpen,'_blank',winParms);
				newwindow.focus()
			}
	</script>
	<%!
		class Person {
			/**
			*	Add a person.
			*/
			public Person(String fn, String ln, String em) {
				setFirstName(fn);
				setLastName(ln);
				setEmail(em);
			}
			
			// first name|last name|campus|email
			public Person(String[] data) {
				setFirstName(data[0]);
				setLastName(data[1]);
				setEmail(data[2]);
			}
			
			public String getFirstName() { return firstName; }
			public String getLastName() { return lastName; }
			public String getEmail() { return email; }
			
			public void setFirstName(String fn) { firstName = fn; }
			public void setLastName(String ln) { lastName = ln; }
			public void setEmail(String em) { email = em; }
			
			private String firstName;
			private String lastName;
			private String email;
		}
		
		public boolean containedInFilter(String filter, String field) {
			boolean result = false;
			
			if (filter != null) {
				if (field.toLowerCase().contains(filter.toLowerCase())) {
					result = true;
				}
			}
			else {
				result = true;
			}
			
			return result;
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
		
		/**
		*	@param out object to write
		*	@param type sort type
		*	@param order sort order (0 = descending, 1 = ascending)
		*	@param filter what text to filter by
		*	@param t type of filter (0 = first name, 1 = last name, 2 = e-mail)
		*/
		public void printList(JspWriter out, String type, int order, String filter, int t, String key) throws IOException  {
			ArrayList<Person> list = new ArrayList<Person>();
			String[] data = loadList(out);
			
			if (data == null) {
				return;
			}
			if (data[0].equals("-")) {
				return;
			}
			
			int blankEntries = 0;
			for (int c = 0; c < data.length; c++) {
				if (containedInFilter(filter, data[c].split("\\|")[t])) {
					if (!(data[c].split("\\|").length < 2)) {
						list.add(new Person(data[c].split("\\|")));
					}
					else
						blankEntries++;
				}
			}
			
			//sort:
			boolean done = false;
			while (!done){
				done = true;
				for (int c = 0; c < list.size()-1; c++) {
					switch(order) {
						case 0:
							if (type.equals("email")) {
								if (list.get(c).getEmail().
										compareTo(list.get(c+1).getEmail()) > 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
							else if (type.equals("firstname")) {
								if (list.get(c).getFirstName().
										compareTo(list.get(c+1).getFirstName()) > 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
							else {
								if (list.get(c).getLastName().
										compareTo(list.get(c+1).getLastName()) > 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
							
							break;
							
						default:
							if (type.equals("email")) {
								if (list.get(c).getEmail().
										compareTo(list.get(c+1).getEmail()) < 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
							else if (type.equals("firstname")) {
								if (list.get(c).getFirstName().
										compareTo(list.get(c+1).getFirstName()) < 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
							else {
								if (list.get(c).getLastName().
										compareTo(list.get(c+1).getLastName()) < 0) {
									Person temp = list.get(c+1);
									list.set(c+1,list.get(c));
									list.set(c, temp);
									done = false;
								}
							}
					}
					
				}
			}
			
			out.println("<h3>List of entries.</h3>");
			out.println("Enter search query here:");
			out.println("<form>");
			out.println("<input type=\"text\" name=\"filter\"/> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"0\">Search by first name</input> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"1\" checked=\"true\">Search by last name</input> <br />");
			out.println("<input type=\"radio\" name=\"ftype\" value=\"2\">Search by e-mail</input> <br />");
			
			out.println("<input type=\"submit\" value=\"Submit query\" /> <br /> <br />");
			
			if (list.size() > 10) {
				out.println("<a href=\"account.jsp?session="+key+"\">Back to Menu</a><br /><br />");
			}
			
			out.println(list.size() + " entries found.");
			
			out.println("<table width=\"60%\" border=\"1\" cellpadding=\"2\">\n");
			out.println("<tr>");
			out.println("\t<th><a href=\"list.jsp?type=firstname&order="+(order==0?"1":"0")+"&session="+key+((filter!=null)?"&filter="+filter+"&ftype="+t:"")+"\">First Name</a> " +
					((type.equals("firstname"))
					?"<img src=\""+ ((order==0)?"images/arrowdown.bmp":"images/arrowup.bmp") + "\" />"
					:"&nbsp;") +
					"</th>");
			out.println("\t<th><a href=\"list.jsp?type=lastname&order="+(order==0?"1":"0")+"&session="+key+((filter!=null)?"&filter="+filter+"&ftype="+t:"")+"\">Last Name</a> " +
					((type.equals("lastname"))
					?"<img src=\""+ ((order==0)?"images/arrowdown.bmp":"images/arrowup.bmp") + "\" />"
					:"&nbsp;") +
					"</th>");
			out.println("\t<th><a href=\"list.jsp?type=email&order="+(order==0?"1":"0")+"&session="+key+((filter!=null)?"&filter="+filter+"&ftype="+t:"")+"\">E-mail</a> " +
					((type.equals("email"))
					?"<img src=\""+ ((order==0)?"images/arrowdown.bmp":"images/arrowup.bmp") + "\" />"
					:"&nbsp;") +
					"</th>");
			
			out.println("<th>Modify</th>");
			out.println("<th>Delete</th>");
			out.println("</tr>");
			for (int c = 0; c < list.size(); c++) {
				out.println("<tr bgcolor=\""+((c%2==0)?"FFFFFF":"EEEEEE")+"\">");
				out.println("\t<td>" + list.get(c).getFirstName() + "</td>");
				out.println("\t<td>" + list.get(c).getLastName() + "</td>");
				out.println("\t<td>" + list.get(c).getEmail() + "</td>");
				out.println("\t<td align=\"center\"><input type=\"button\" value=\"Modify\" onClick=\"popupaddpackage('edit.jsp?session="+key+"&firstname="+list.get(c).getFirstName()+"&lastname="+list.get(c).getLastName()+"&email="+list.get(c).getEmail()+"')\" /></td>");
				out.println("\t<td align=\"center\"><input type=\"button\" value=\"Delete\" onClick=\"javascript:popupaddpackage('del.jsp?session="+key+"&firstname="+list.get(c).getFirstName()+"&lastname="+list.get(c).getLastName()+"&email="+list.get(c).getEmail()+"')\" /></td>");
				out.println("</tr>");
			}
			out.println("</table>");
			
			if (blankEntries > 0){
				out.println("(Found " + blankEntries + " blank entries.)");
			}
		}
	%>
	<%
		String sortType = "lastname"; //'firstname', 'lastname', 'email'
		int sortOrder = 0; //0 = descending, 1 = ascending
		String filter = new String("");
		int filterType = 0; //0 = first name, 1 = last name, 2 = e-mail
		String key = new String("");
		try{
			key = request.getParameter("session");
		}
		catch(NullPointerException n){
			key = "0";
		}
		
		try{
			filter = request.getParameter("filter");
		}
		catch(NullPointerException n){
			filter = new String("");
		}
		
		try{
			filterType = Integer.parseInt(request.getParameter("ftype"));
		}
		catch(NumberFormatException f){
			filterType = 0;
		}
		
		try{
			sortType = request.getParameter("type").trim();
			if (sortType.equals("") || (!sortType.equals("lastname") && !sortType.equals("firstname") && !sortType.equals("email"))) {
				sortType = "lastname";
			}
			sortOrder = Integer.parseInt(request.getParameter("order").trim());
			if (sortOrder < 0 && sortOrder > 1) {
				sortOrder = 0;
			}
		}
		catch(NumberFormatException n){
			sortOrder = 0;
		}
		catch(Exception e){}
		
		printList(out, sortType, sortOrder, filter, filterType, key);
		
		out.println("<input type=\"hidden\" name=\"type\" value=\""+sortType+"\" />");
		out.println("<input type=\"hidden\" name=\"order\" value=\""+sortOrder+"\" />");
		out.println("<input type=\"hidden\" name=\"session\" value=\""+key+"\" />");
		out.println("</form>");
		
		out.println("<br />");
		out.println("<a href=\"account.jsp?session=" + key + ">Back to Menu</a>");
	%>
</body>
</html>