package wowServ.wowBL;

import java.util.ArrayList;

public class Department {
	/**
	 * Construct an empty record.
	 */
	public Department() {
	}

	/**
	 * Create a new department. This assumes the programmer will eventually set
	 * an element.
	 * 
	 * @param _name
	 *            Name of the department.
	 * @param _fields
	 *            Fields describing this department.
	 */
	public Department(String _name, ArrayList<Field> _fields) {
		setName(_name);
		setFields(_fields);
	}

	/**
	 * Creates a department.
	 * 
	 * @param _name
	 *            Name of the department.
	 * @param _fields
	 *            Fields describing this department.
	 * @param e
	 *            XML Element describing the physical node representing this
	 *            department.
	 * @return A completed department.
	 */
	public static Department makeDepartment(String _name,
			ArrayList<Field> _fields, org.w3c.dom.Element e) {
		Department dept = new Department();

		dept.setName(_name);
		dept.setFields(_fields);
		dept.setElement(e);

		return dept;
	}

	/**
	 * Set the name of the department.
	 * 
	 * @param _name
	 *            Name to set.
	 */
	public void setName(String _name) {
		name = _name;
	}

	/**
	 * Get the name of the department.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the element that this department belongs to.
	 */
	public void setElement(org.w3c.dom.Element e) {
		element = e;
	}

	/**
	 * Get the element that this department belongs to.
	 */
	public org.w3c.dom.Element getElement() {
		return element;
	}

	/**
	 * Set the fields of this department.
	 * 
	 * @param _fields
	 *            Fields to set.
	 */
	public void setFields(ArrayList<Field> _fields) {
		fields = _fields;
	}

	/**
	 * Get the fields for this department.
	 */
	public ArrayList<Field> getFields() {
		return fields;
	}

	private String name;
	private ArrayList<Field> fields;
	private org.w3c.dom.Element element;
}
