package wowServ.wowBL;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class is set up to handle any number of fields. Every record, however,
 * has some restrictions:<br>
 * - For data integrity, every ID must be unique. This isn't error-checked in
 * the Record class.<br>
 * - Every record must contain a first and last name.<br>
 * - A fields vector must not be null. If it is passed as null, it returns null.
 */
public class Record {
	public Record() {
	}

	/**
	 * Construct a record. The assumption is that an element will be set
	 * eventually.
	 */
	public Record(int _ID, ArrayList<Field> _fields) {
		setFields(_fields);
		setID(_ID);
	}

	/**
	 * Make a new record. This method requires that every record have an ID >=
	 * 0, a first name, and a last name.
	 * 
	 * @param _ID
	 *            Unique identification number.
	 * @param _fields
	 *            Set list of fields.
	 * @return New record on success, null on failure.
	 */
	public static Record newRecord(int _ID, ArrayList<Field> _fields,
			org.w3c.dom.Element e) {
		Record r = new Record();

		if (_fields == null) {
			// A field must contain a first name and last name at least.
			return null;
		}
		r.setFields(_fields);
		r.setID(_ID);
		r.setElement(e);

		// Check required fields:
		if (r.getField("First Name") == null || r.getField("Last Name") == null
				|| _ID < 0) {
			return null;
		}

		return r;
	}

	/**
	 * Set the element that belongs to this record.
	 * 
	 * @param e
	 *            Element to set.
	 */
	public void setElement(org.w3c.dom.Element e) {
		element = e;
	}

	/**
	 * Return the element that belongs to this record.
	 */
	public org.w3c.dom.Element getElement() {
		return element;
	}

	/**
	 * Set the fields for this record.
	 * 
	 * @param _fields
	 *            List of fields to set.
	 */
	public void setFields(ArrayList<Field> _fields) {
		fields = _fields;
	}

	/**
	 * Change every field with the name "<code>_name</code>" to the new given
	 * value.
	 * 
	 * @param _name
	 *            Name of the field(s).
	 * @param _data
	 *            New data to insert into the field(s).
	 */
	public void changeField(String _name, String _data) {
		for (Field f : fields) {
			if (f.getName().equalsIgnoreCase(_name)) {
				f.setData(_data);
			}
		}
	}

	/**
	 * Change every field (within the limit) with the name "<code>_name</code>"
	 * to the new given value. It will change a field according to first come
	 * first serve.
	 * 
	 * @param _name
	 *            Name of the field(s).
	 * @param _data
	 *            New data to insert into the field(s).
	 * @param limit
	 *            Number of fields to change.
	 */
	public void changeField(String _name, String _data, int limit) {
		if (limit < 1)
			return;

		int changed = 0;
		for (Field f : fields) {
			if (changed >= limit)
				break;

			if (f.getName().equalsIgnoreCase(_name)) {
				changed++;
				f.setData(_data);
			}
		}
	}

	/**
	 * Rather than changing all or the first occurrence of a field, this method
	 * can pick out a field by checking it's old value.
	 * 
	 * @param _name
	 *            Name of the field that should be changed.
	 * @param _oldData
	 *            Data that currently exists in the field.
	 * @param _newData
	 *            New data to be inserted into the field.
	 */
	public boolean changeSpecificField(String _name, String _oldData,
			String _newData) {
		for (Field f : fields) {
			if (f.getName().equalsIgnoreCase(_name)
					&& f.getData().equalsIgnoreCase(_oldData)) {
				f.setData(_newData);
				return true;
			}
		}

		return false;
	}

	/**
	 * Attempts to add a new field to this record. Requires data to be unique
	 * for it's field.
	 * 
	 * @param _f
	 *            Field to add.
	 * @return <code>true</code> if successful, <code>false</code> if this data
	 *         already exists.
	 */
	public boolean addField(Field _f) {
		for (Field f : fields) {
			if (f.getName().equalsIgnoreCase(_f.getName())
					&& f.getData().equalsIgnoreCase(_f.getData())) {
				return false;
			}
		}

		fields.add(_f);

		return true;
	}

	/**
	 * Removes <em>all</em> fields from the record (by name). Does nothing if
	 * the field doesn't exist.
	 * 
	 * @param name
	 *            Name of the field to delete.
	 */
	public boolean deleteField(String _name) {
		for (int c = 0; c < fields.size(); c++) {
			if (fields.get(c).getName().equalsIgnoreCase(_name)) {
				fields.remove(c);
				return true;
			}
		}

		return false;
	}

	/**
	 * Removes fields from the record (by name), up to the user-specified
	 * <code>limit</code>. Does nothing if the field doesn't exist.
	 * 
	 * @param _name
	 *            Name of the field.
	 * @param limit
	 *            Number of fields of this name to delete.
	 */
	public void deleteField(String _name, int limit) {
		if (limit < 1)
			return;

		int changed = 0;
		for (int c = 0; c < fields.size(); c++) {
			if (changed >= limit) {
				break;
			}

			if (fields.get(c).getName().equalsIgnoreCase(_name)) {
				fields.remove(c);
				changed++;
			}
		}
	}

	/**
	 * Set the identification number for this record.
	 * 
	 * @param _ID
	 *            Number to set (must be unique).
	 */
	public void setID(int _ID) {
		ID = _ID;
	}

	/**
	 * Get the identification number for this record.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Convenience method to get the first name of this record.
	 */
	public String getFirstName() {
		return getField("First Name").get(0).getData();
	}

	/**
	 * Convenience method to get the last name of this record.
	 */
	public String getLastName() {
		return getField("Last Name").get(0).getData();
	}

	/**
	 * Get every field that belongs to this record.
	 */
	public ArrayList<Field> getFields() {
		return fields;
	}

	/**
	 * Return the Field(s) of this record by name.
	 * 
	 * @param _name
	 *            Name of the field(s) <em>(not the value)</em>.
	 * @return Field(s) if it exists, otherwise <code>null</code>.
	 */
	public ArrayList<Field> getField(String _name) {
		ArrayList<Field> field = new ArrayList<Field>();

		for (Field f : fields) {
			if (_name.equalsIgnoreCase(f.getName())) {
				// Field found.
				field.add(f);
			}
		}

		if (field.size() == 0) {
			field.add(new Field(_name, ""));
		}

		return field;
	}

	/**
	 * Return the Field(s) of this record by the data contained in the field(s).
	 * 
	 * @param _data
	 *            Data in the field(s) <em>(not the name of the field)</em>.
	 * @return Field(s) if it exists, otherwise <code>null</code>.
	 */
	public Vector<Field> getFieldByValue(String _data) {
		Vector<Field> field = new Vector<Field>();

		for (Field f : fields) {
			if (_data.equalsIgnoreCase(f.getData())) {
				// Field found.
				field.add(f);
			}
		}

		return field;
	}

	/**
	 * This works as a convenience method. It searches the fields by name and
	 * instead of returning the field itself, only returns the value inside the
	 * field(s).
	 * 
	 * @param _fieldName
	 *            Name of the field(s) <em>(not the value in the field)</em>.
	 * @return Empty string on failure, data on success.
	 */
	public Vector<String> getData(String _fieldName) {
		Vector<String> data = new Vector<String>();

		for (Field f : fields) {
			if (_fieldName.equalsIgnoreCase(f.getName())) {
				// Field found.
				data.add(f.getData());
			}
		}

		if (data.size() == 0) {
			data.add("");
		}

		return data;
	}

	private int ID;
	private ArrayList<Field> fields;
	private org.w3c.dom.Element element;
}
