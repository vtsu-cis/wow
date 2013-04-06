package src.sose.wowBL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {
	
	private String type = "";
	private String address = "";
	
	/**
	 * Initializes new e-mail
	 */
	public Email()
	{}
	
	/**
	 * Initializes new e-mail
	 * 
	 * @param ty type to be set
	 * @param addr e-mail address
	 */
	public Email(String ty, String addr)
	{
		setType(ty);
		setAddress(addr);
	}
	
	/**
	 * Gets e-mail type
	 * 
	 * @return type
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Sets e-mail type
	 * 
	 * @param ty type to be set
	 */
	public void setType(String ty)
	{
		type = ty;
	}
	
	
	/**
	 * Gets e-mail address
	 * 
	 * @return address
	 */
	public String getAddress()
	{
		return address;
	}
	
	/**
	 * Sets e-mail address<br>
	 * Note: E-mail address format is based on IETF's RFC 2822 format section 3.4
	 * 
	 * @param addr e-mail address to be set
	 * @return 0 if e-mail address is correctly formatted<br>
	 * 		   1 if e-mail address is not correctly formatted
	 */
	public int setAddress(String addr)
	{	
		Pattern pattern = Pattern.compile(".+@.+\\..+");

		Matcher matcher = pattern.matcher(addr);

		if(matcher.matches())
		{
			address = addr;
			return 0;
		}

		return 1;

	}
	
}	