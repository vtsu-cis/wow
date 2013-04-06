package wowServLibs;

import java.net.InetAddress;
import java.util.Calendar;

/**
 * Tracks a hit with the type, IP address and the time
 * 
 * @author Isaac Parenteau
 * 
 */
public class Hit {

	public static enum Type {
		QUERY, ADD, DELETE, UPDATE
	};

	private InetAddress ipAddress;
	private Calendar now;
	private Type type;

	/**
	 * Constructor to set the type ipAddress and time for a query
	 * 
	 * @param _type
	 *            the type of Query
	 * @param _ipAddress
	 *            The IP Address from the query
	 */
	public Hit(Type _type, InetAddress _ipAddress) {
		type = _type;
		ipAddress = _ipAddress;
		now = Calendar.getInstance();
	}

	/**
	 * Gets the IP Address of a specific query
	 * 
	 * @return IpAddress
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/**
	 * Gets the time of the specific query
	 * 
	 * @return the time of the specific query
	 */
	public Calendar getTime() {
		return now;
	}

	public Type getType() {
		return type;
	}
}
