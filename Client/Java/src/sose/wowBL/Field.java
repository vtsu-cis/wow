package src.sose.wowBL;

public class Field {
	
	private String name = "";
	private String value = "";
	
	public Field()
	{}
	
	/**
	 * Initializes new field
	 * 
	 * @param na name to be set
	 * @param val value to be set
	 */
	public Field(String na, String val)
	{
		setName(na);
		setValue(val);
	}
	
	/**
	 * Get name of field
	 * 
	 * @return name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set name of field
	 * 
	 * @param na name to be set
	 */
	public void setName(String na)
	{
		name = na;
	}
	
	/**
	 * Get value of field
	 * 
	 * @return value
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Set value of field
	 * 
	 * @param val value to be set
	 */
	public void setValue(String val)
	{
		value = val;
	}
	
}	
