package util;

import util.mock.MockServerChannel;

/**
 * The ServerChannel is a base class for any channel who will communicate with the
 * server. It exists as a base class to provide the option for using "mock" channels
 * to communicate with a server for the sake of testing.
 */
public abstract class ServerChannel {
	private String host;
	private int port;
	protected long lastAction = 0L;
	protected long nextTimeout = 0L;
	
	/**
	 * Allow base classes to initialize the channel to target a specific host and port.
	 * @param host Host to contact.
	 * @param port Port number to contact through.
	 */
	protected ServerChannel(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Get the host that this channel communicates with.
	 * @return String which holds the host name/IP address of the target server.
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Set a new host to connect to for further actions.
	 * @param _host Host to connect to.
	 */
	public void setHost(String _host) {
		host = _host;
	}
	
	/**
	 * Get the port by which the socket is created.
	 * @return Port number which is between 0 and 65535.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set a new port to communicate through (between 0-65535).
	 * @param _port Port number (unsigned short) to set.
	 */
	public void setPort(int _port) {
		port = _port;
	}
	
	/*
	 * Abstract methods.
	 */
	public abstract HealthReport getHealthReport();
	public abstract boolean startServer();
	public abstract boolean stopServer();
	public abstract boolean restartServer();
	public abstract boolean login(String name, String password);
	public abstract boolean isConnected();
	public abstract boolean isLoggedIn();
	
	/**
	 * Create a server channel, which is used to communicate with a server.
	 * @param host Host name of the server.
	 * @param port Port number to communicate through.
	 * @return Initialized server channel.
	 */
	public static ServerChannel makeServerChannel(String host, int port) {
		return new MockServerChannel(host, port);
	}
}
