package util;

import java.io.*;
import java.util.ArrayList;

public final class Config {
	/** Configuration filename. */
	public final static String CONFIG_FILE = "wow.conf";
	
	private final static ArrayList<Server> serverList = new ArrayList<Server>();
	
	/**
	 * Parse the config file (defined in Config.CONFIG_FILE), reading in options.
	 * @throws IOException Thrown if the file cannot be read or a read error occurs.
	 */
	public static void readConfigFile() throws Exception {
		final File configFile = new File(CONFIG_FILE);
		if (!configFile.exists())
			configFile.createNewFile();
		
		final BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				// Do not consider commented lines.
				continue;
			}
			
			// Remove comments after the server name.
			if (line.contains("#")) {
				line = line.substring(0, line.indexOf("#"));
			}
			
			final String[] data = line.split("\\:");
			serverList.add(new Server(data[0], Integer.parseInt(data[1])));
		}
		
		reader.close();
	}
	
	/**
	 * Add a server to the configuration file.
	 * @param server Server to add.
	 */
	public static void addServer(Server server) {
		serverList.add(server);
	}
	
	/**
	 * Remove a server from the configuration file.
	 * @param server Server object to remove.
	 * @return True if the server was found and removed, otherwise false.
	 */
	public static boolean removeServer(Server server) {
		return removeServer(server.getHost(), server.getPort());
	}
	
	/**
	 * Remove a server from the configuration file.
	 * @param host Host name of the server.
	 * @param port Port number that the server listens on.
	 * @return True if the server was found and removed, otherwise false.
	 */
	public static boolean removeServer(String host, int port) {
		for (int i = 0; i < serverList.size(); i++) {
			if (serverList.get(i).getHost().equalsIgnoreCase(host) &&
					serverList.get(i).getPort() == port) {
				serverList.remove(i);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get a server by it's host name.
	 * @param host Host name of the server.
	 * @return Server object, or null.
	 */
	public static Server getServer(String host) {
		for (int i = 0; i < serverList.size(); i++) {
			if (serverList.get(i).getHost().equalsIgnoreCase(host)) {
				return serverList.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Get the list of servers.
	 * @return Array list of servers.
	 */
	public static ArrayList<Server> getServers() {
		return serverList;
	}
}
