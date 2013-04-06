package util.real;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.HealthReport;
import util.ServerChannel;

public class RealServerChannel extends ServerChannel {
	private Socket socket;
	
	/**
	 * Create a real channel, which is used to communicate with the server.
	 * @param host Host to connect to.
	 * @param port Port number that the target server listens on.
	 */
	public RealServerChannel(String host, int port) {
		super(host, port);
	}
	
	/**
	 * Convenience method for opening a connection to the server. This will automatically
	 * disconnect the socket first.
	 * @throws IOException Thrown if the server cannot be connected to.
	 */
	private void connect() throws IOException {
		disconnect();
		
		socket.connect(new InetSocketAddress(getHost(), getPort()), 2000);
	}
	
	/**
	 * Convenience method for closing the connection to the server. Guaranteed
	 * not to throw a IOException if the close fails for any reason.
	 */
	private void disconnect() {
		try {
			if (socket != null)
				socket.close();
		}
		catch (IOException e) {
		}
		
		socket = new Socket();
	}

	/**
	 * Connect to the server and request a health report.
	 * @return Health report object, or null if the server could not be contacted.
	 */
	@Override public HealthReport getHealthReport() {
		throw new NotImplementedException();
	}
	
	/**
	 * Restart the server. A new health report should be retrieved afterwards.
	 * @return True if the server was restarted without issue, false if not.
	 */
	@Override public boolean restartServer() {
		throw new NotImplementedException();
	}

	/**
	 * Attempt to log into the server.
	 * @param name Administrator account name.
	 * @param password Password to identify the account.
	 * @return True if the login succeeded, false if an error occurred.
	 */
	@Override public boolean login(String name, String password) {
		throw new NotImplementedException();
	}

	/**
	 * A user is guaranteed to be connected to the server while the socket to the server
	 * remains open. If the user never logs in, the socket is disconnected after a query.
	 * @return True if the user is connected to the server. This is generally the same result of
	 * isLoggedIn().
	 */
	@Override public boolean isConnected() {
		return socket.isConnected();
	}

	/**
	 * Ask if the client is logged into the server.
	 * @return True if the client is logged into the server, false if not.
	 */
	@Override public boolean isLoggedIn() {
		return isConnected();
	}
	
	/**
	 * Start the server.
	 */
	@Override public boolean startServer() {
		throw new NotImplementedException();
	}

	/**
	 * Stop the server.
	 */
	@Override public boolean stopServer() {
		throw new NotImplementedException();
	}

}
