package src.sose.wowBL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phone {
	
	private String type = "";
	private String number = "";
	
	public Phone()
	{}
	
	/**
	 * Initializes new type and number
	 * 
	 * @param ty sets type
	 * @param num sets number
	 */
	public Phone(String ty, String num)
	{
		setType(ty);
		setNumber(num);
	}
	
	/**
	 * Gets phone type
	 * 
	 * @return type
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Sets new type
	 * 
	 * @param na sets type
	 */
	public void setType(String na)
	{
		type = na;
	}
	
	
	/**
	 * Gets number
	 * 
	 * @return number
	 */
	public String getNumber()
	{
		return number;
	}
	
	/**
	 * Sets new number
	 * 
	 * @param pn phone number to be set
	 * @return 0 if successful<br>
	 * 		   1 if input is invalid
	 */
	public int setNumber(String pn)
	{
		Pattern pattern = Pattern.compile("\\(\\d{3}\\)\\d{3}\\-\\d{4}");

		Matcher matcher = pattern.matcher(pn);

		if(matcher.matches())
		{
			number = pn;
			return 0;
		}
		
		pattern = Pattern.compile("\\d{3}\\-\\d{3}\\-\\d{4}");
		
		matcher = pattern.matcher(pn);

		if(matcher.matches())
		{
			number = "(" + pn.substring(0, 3) + ")" + pn.substring(4);
			return 0;
		}

		pattern = Pattern.compile("\\d{3}\\-\\d{4}");
		
		matcher = pattern.matcher(pn);

		if(matcher.matches())
		{
			number = "(802)" + pn;
			return 0;
		}
		
		pattern = Pattern.compile("\\d{7}");
		
		matcher = pattern.matcher(pn);

		if(matcher.matches())
		{
			number = "(802)" + pn.substring(0, 3) + "-" + pn.substring(3);
			return 0;
		}
		
		pattern = Pattern.compile("\\d{10}");
		
		matcher = pattern.matcher(pn);

		if(matcher.matches())
		{
			number = "(" + pn.substring(0, 3) + ")" + pn.substring(3, 6) + "-" + pn.substring(6);
			return 0;
		}
		
		return 1;
	}
	
}	