<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Entry deleted<title>
</head>
<body onLoad="javascript:window.opener.location.href = window.opener.location.href;">
	<script type="text/javascript">
		function loadinparent(url, closeSelf){
			self.opener.location = url;
			if(closeSelf) self.close();
		}
	</script>
	<%!
		public String closeButton(String value){
			return "<input type=\"button\" value=\""+value+"\" onClick=\"javascript:self.close()\" />";
		}
		
		public void log(ServletConfig config, String msg) {
			BufferedWriter out = null;
			try {
				String path = config.getServletContext().getRealPath("list/logs");
				out = new BufferedWriter(new FileWriter(
						new File(path + "\\uploadlog " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
								+ "-" + Calendar.getInstance().get(Calendar.MONTH) +
								"-" + Calendar.getInstance().get(Calendar.YEAR) +
								".txt"), true));
			
				out.write(	"("+ getFormattedDateTime() + ") " +	msg);
				out.newLine();
				
				out.close();
			}
			catch (IOException e) {}
		}
		
		public String getFormattedDateTime() {
			Date today;
			String result;
			java.text.SimpleDateFormat formatter;

			formatter = new java.text.SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm:ss");
			today = new Date();
			result = formatter.format(today);

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
	%>
	<%
		String key = request.getParameter("session");
		String del = request.getParameter("remove");
		String name = null;
		
		if (key == null || del == null) {
			out.println("Invalid deletion attempt. <br />");
		}
		
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
			
			boolean found = false;
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
						break;
					}
				}
			}
			
			in.close();
			reader.close();
			
			if (!found) {
				out.println("You are either not logged on or are trying to access this page without permission.<br />");
				out.println("This incident has been logged.<br /><br />");
				out.println(closeButton("Close"));
				
				log(config, "Invalid deletion attempt from " + key + " (" + request.getRemoteAddr() + ") (accessed page without proper permission)");
			}
			else {
				String[] data = loadList(out);
				PrintWriter writer = new PrintWriter(new FileOutputStream("list.dat"));
				
				for (int c = 0; c < data.length; c++) {
					String info[] = data[c].split("\\|");
					if (!info[2].equalsIgnoreCase(del)) {
						writer.print(info[0]+"|"+info[1]+"|"+info[2]+";");
					}
				}
				
				writer.close();
				
				out.println("The entry was deleted. <br /> <br />");
				out.println(closeButton("Close"));
				
				log(config, "Entry (" + del + ") was deleted by " + name + " (" + key + ") from " + request.getRemoteAddr() + ".");
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