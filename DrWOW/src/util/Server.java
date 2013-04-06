package util;

/**
 * Store properties about a server.
 */
public class Server {
	private String host;
	private int port;
	
	/**
	 * Instantiate a new Server object.
	 * @param _host Host name.
	 * @param _port Port number.
	 */
	public Server(String _host, int _port) {
		host = _host;
		port = _port;
	}
	
	/**
	 * Get the host name.
	 * @return String containing the host name.
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Set a new host name for this server.
	 * @param _host Host name to set.
	 */
	public void setHost(String _host) {
		host = _host;
	}
	
	/**
	 * Get the port number.
	 * @return Port number of the server.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set a new port number.
	 * @param _port Port number to set.
	 */
	public void setPort(int _port) {
		port = _port;
	}
}
