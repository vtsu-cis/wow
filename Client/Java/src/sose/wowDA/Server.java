package src.sose.wowDA;

import java.util.ArrayList;
import java.util.Collections;



public class Server {

	private String name = new String();
	private ArrayList<Address> addresses = new ArrayList<Address>();
	
	/**
	 * Constructor for the server. Sets the essential information of the server.
	 * 
	 * @param na name of the server
	 * @param addr address to be added
	 * @param port port number to listen on
	 * @param prio priority to be set
	 */
	public Server(String na, String addr, int port, int prio)
	{
		SetName(na);
		AddAddress(addr, port, prio);
	}
	
	/**
	 * Sets name of the server
	 * 
	 * @param na name to be set
	 */
	public void SetName(String na)
	{
		name = na;
	}
	
	/**
	 * Gets the name of the server
	 * 
	 * @return name
	 */
	public String GetName()
	{
		return name;
	}
	
	/**
	 * Adds and sorts the address, port and priority to the list of addresses
	 * 
	 * @param addr address to be added
	 * @param po port number to list on
	 * @param pr priority of the server
	 */
	public void AddAddress(String addr, int po, int pr)
	{
		addresses.add(new Address(addr, po, pr));
		Collections.sort(addresses);
	}
	
	/**
	 * Gets the list of addresses
	 * 
	 */
	public ArrayList<Address> GetAddresses()
	{
		return addresses;
	}
}
