package src.sose.wowDA;


public class Address implements Comparable<Address>{

	private String address = new String();
	private Integer port;
	private Integer priority;
	
	/**
	 * Compares the priority of Address addr and this priority
	 * 
	 * @param addr address to be compared
	 *  
	 * @return 0 if addr is equal to this member's priority,<br>
	 * 		   a value less than zero if addr is less than this priority,<br>
	 * 		   or a value greater than zero if addr is greater than this priority.
	 */
	public int compareTo(Address addr)
	{		
		return this.priority.compareTo(addr.priority);
	}
	
	/**
	 * Constructor for the class Address.<br>
	 * Accepts Address, Port number, and Priority level as arguments.
	 * 
	 * @param addr address
	 * @param por port number
	 * @param prio priority
	 */
	public Address(String addr, int por, int prio)
	{
		SetAddress(addr);
		SetPort(por);
		SetPriority(prio);
	}
	
	/**
	 * Sets a new address
	 * 
	 * @param addr address to be set
	 */
	public void SetAddress(String addr)
	{
		address = addr;
	}
	
	/**
	 * Gets the address of type String
	 * 
	 * @return address
	 */
	public String GetAddress()
	{
		return address;
	}
	
	/**
	 * Sets the port
	 * 
	 * @param por port to be set
	 */
	public void SetPort(int por)
	{
		port = por;
	}
	
	/**
	 * Gets the port of type Integer
	 * 
	 * @return port
	 */
	public Integer getPort()
	{
		return port;
	}
	
	/**
	 * Set new priority
	 * 
	 * @param pr priority to be set
	 */
	public void SetPriority(int pr)
	{
		priority = new Integer(pr);
	}
	
	/**
	 * Gets priority of type Integer
	 * 
	 * @return priority
	 */
	public Integer GetPriority()
	{
		return priority;
	}
}