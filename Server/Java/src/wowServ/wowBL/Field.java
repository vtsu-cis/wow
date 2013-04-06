package wowServ.wowBL;

/**
 * A Field contains any data about a person or thing.
 */
public class Field {
	/**
	 * Create a new field.
	 * 
	 * @param _name
	 *            Name of the field.
	 * @param data
	 *            Data contained in the field of any type.
	 */
	public Field(String _name, String _data) {
		setName(_name);
		setData(_data);
	}

	/**
	 * Set the name of this field.
	 */
	public void setName(String _name) {
		name = _name;
	}

	/**
	 * Get the name of this field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the data object stored in this field.
	 */
	public void setData(String _data) {
		data = _data;
	}

	/**
	 * Get the data stored in this field.
	 */
	public String getData() {
		return data;
	}

	private String name; // Name of the field.
	private String data; // Data that this field contains.
}
