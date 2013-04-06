<%@page import="java.io.*, java.util.*" %>
<html>
<head>
	<title>Recover Password</title>
</head>
<body>
	<%!
		public String sendEmail(String accountName, String email, String password) {
			String result = new String("");
			
			try {
				String line;
				Process p = Runtime.getRuntime().exec
					(new String[]{"cmd.exe", "/c", "recover.py " + accountName + " " + email + " " + password});
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					result += line + "\n";
				}
				input.close();
			}
			catch (Exception err) {
				result =  "A problem occured when attempting to send you an e-mail: " + err.getMessage() + ". Please contact the <a href=\"mailto:andysib@gmail.com\">Administrator</a>.";
		    }
			
			return result;
		}
		
		public String[] loadUserList() throws IOException {
			String[] result = null;
			
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
			
			ArrayList<String> list = new ArrayList<String>();
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.startsWith("#")){
					continue;
				}
			
				list.add(line);
			}
			
			result = (String[])list.toArray(new String[list.size()]);
			
			in.close();
			reader.close();
			
			return result;
		}
	%>
	<%
		String accountName = request.getParameter("accountname");
		String email = request.getParameter("email");
		String password = new String("");
		
		boolean sendByAccount = true;
		if (accountName == null && email == null) {
			response.sendRedirect("../list/recover.html");
		}
		
		if (accountName != null && email == null) {
			sendByAccount = true;
		}
		else if (accountName == null && email != null) {
			sendByAccount = false;
		}
		else {
			response.sendRedirect("../list/recover.html");
		}
		
		String[] userList = loadUserList();
		
		Hashtable<String, String[]> list = new Hashtable<String, String[]>();
		
		String[] accountInfo = null;
		if (sendByAccount) {
			for (int c = 0; c < userList.length; c++) {
				list.put(userList[c].split("\\|")[0], userList[c]);
			}
		}
		else {
			for (int c = 0; c < userList.length; c++) {
				list.put(userList[c].split("\\|")[2], userList[c]);
			}
		}
		
		if (list.containsKey(accountName) || list.containsKey(email)) {
			accountInfo = ((sendByAccount)?list.get(accountName):list.get(email)).split("\\|");
			
			String result = sendEmail(accountInfo[0], accountInfo[2], accountInfo[1]);
			
			out.println(result);
			out.println("<br /> <br />");
			out.println(
		}
		else {
			
		}
		
		
	%>
</body>
</html>