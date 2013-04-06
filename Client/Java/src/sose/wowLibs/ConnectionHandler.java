package src.sose.wowLibs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * The ConnectionHandler's job is to provide a socket to the client. It is not the client's
 * responsibility to create or close sockets. This class is especially useful to the
 * administrative client, since it can keep track of a single socket through which it will
 * communicate with the server.
 */
public class ConnectionHandler {
	private static Socket socket = new Socket();
	private static String lastHost = "";
	private static int lastPort = 0;
	private static long lastActionTime = System.currentTimeMillis();
	public static long TIMEOUT = 300000; // 5 minutes.
	private static boolean pinged = false;
	private static boolean connected = false; // Track whether or not we are connected.
	
	private static Vector<String> messageBuffer = new Vector<String>(5);
	
	/**
	 * Attempt to connect to the given host through the given port.
	 * @param host Host name or Ip address of the target.
	 * @param port Port number to talk through.
	 * @throws IOException Thrown if a socket creation fails.
	 */
	public static void connectTo(String host, int port) throws IOException {
		lastHost = host;
		lastPort = port;
		
		if (isConnected()) {
			socket.close();
		}
		
		socket = new Socket(host, port);
		
		socket.setReuseAddress(true);
		System.out.println("[ConnectionHandler]: Connected to " + host + ":" + port);
		
		new ConnectionHandler().new PacketReader();
		
		refreshTimeout();
		
		setConnected(true);
	}
	
	/**
	 * Set (or unset) the "pinged" state in a synchronized fashion.
	 * @param state State to set.
	 */
	private static synchronized void setPinged(boolean state) {
		pinged = state;
	}
	
	/**
	 * Get whether or not the pinged state is true in a synchronized fashion.
	 * @return True if the "ping" flag is set.
	 */
	private static synchronized boolean isPinged() {
		return pinged;
	}
	
	/**
	 * Check if the socket is connected.
	 * @return True if the socket is in fact connected, otherwise false.
	 */
	public static synchronized boolean isConnected() {
		return connected;
	}
	
	/**
	 * Set whether or not the client is connected.
	 * @param state True if the client is connected.
	 */
	private static synchronized void setConnected(boolean state) {
		connected = state;
	}
	
	/**
	 * Create a temporary socket based on the last known host and port.
	 * @return Socket object pointing to a server.
	 */
	public static Socket createTemporarySocket() throws IOException {
		Socket tempSocket = new Socket(lastHost, lastPort);
		tempSocket.setReuseAddress(true);
		return tempSocket;
	}
	
	/**
	 * Check if the client timed out (5 minutes of no packet sending).
	 * @return True if the client timed out.
	 */
	public static boolean timedOut() {
		return System.currentTimeMillis() - lastActionTime >= TIMEOUT;
	}
	
	/**
	 * Allows the rest of the client to refresh the timeout timer so that it doesn't time out.
	 */
	public static void refreshTimeout() {
		lastActionTime = System.currentTimeMillis();
	}
	
	/**
	 * Send an (unmodified) message to the connected server. If the socket is not connected,
	 * this will throw an IOException.
	 * @param message
	 * @throws IOException Thrown when the socket is not connected or if an error occurs while
	 * attempting to send a message.
	 */
	public static void sendMessage(String message) throws IOException {
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		out.println(message.trim());
		
		System.out.println("Sending message: " + message);
		
		refreshTimeout();
	}
	
	/**
	 * Send a ping to the server if -- and only if -- the client is not waiting for a 
	 * response ping.
	 * 
	 * @throws IOException
	 * 				Thrown when the socket is not connected or if an error occurs
	 * while attempting to send a message.
	 */
	public static void sendPing() throws IOException {
		if (!isPinged()) {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("PING");
			
			setPinged(true);
		}
	}
	
	/**
	 * Read a message from the server.
	 * @return Read from a socket until a newline ('\n') is sent from the server.
	 * @throws IOException Thrown if an IO error occurs.
	 */
	public static String readLine() throws IOException {
		String line = "";
		while (messageBuffer.size() == 0) {
			// Wait until a packet enters.
			try {
				Thread.sleep(20);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		line = messageBuffer.get(0);
		messageBuffer.remove(0);
		
		System.out.println("Received message: " + line);
		
		refreshTimeout();
		
		return line;
	}
	
	/**
	 * The server will close the connection (if one exists) and use the last used host and port
	 * to reconnect to the server.
	 * @throws IOException Thrown if the socket creation fails.
	 */
	public static void reconnect() throws IOException {
		disconnect();
		
		connectTo(lastHost, lastPort);
	}
	
	/**
	 * Close the socket, shutting down communications with any connected servers.
	 * If the socket is not connected, this method does nothing.
	 */
	public static void disconnect() {
		setPinged(false);
		messageBuffer.clear();
		setConnected(false);
		
		try {
			socket.close();
		} catch (IOException e) {
			// Do nothing. It doesn't matter to us.
		}
		
		System.out.println("[ConnectionHandler]: Disconnected.");
	}
	
	/**
	 * The finalize method is called by the garbage collector when it has no more use for
	 * this object. It will close the socket if it is connected.
	 */
	@Override protected void finalize() throws Throwable {
		disconnect();
		
		socket = null;
		
		super.finalize();
	}
	
	/**
	 * Read in packets from the server and store them in a thread-safe buffer.
	 */
	class PacketReader extends Thread {
		public PacketReader() {
			super("PacketReader thread.");
			
			this.start();
		}
		
		/**
		 * Run method called by the Thread.start method.
		 */
		@Override public void run() {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while (isConnected()) {
					String line = in.readLine();
					if (line == null) {
						throw new IOException("Socket disconnected.");
					}
					else if (line.equalsIgnoreCase("PONG")) {
						// No need to buffer the PONG command.
						setPinged(false);
						continue;
					}
					
					messageBuffer.add(line);
				}
			}
			catch (IOException e) {
				disconnect();
			}
		}
		
	}
}
