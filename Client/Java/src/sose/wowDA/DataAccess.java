package src.sose.wowDA;

import java.util.ArrayList;

import src.sose.wowBL.Field;
import src.sose.wowBL.Person;

public interface DataAccess {
	public String getName();
	public ArrayList<Person> query();
	public ArrayList<Person> query(Person subject);
	public ArrayList<Person> query(Field subject);
	public ArrayList<String[]> getUnformattedAll();
	public String update(Person subject);
	public String add(Person subject);
	public String delete(Person subject);
	public Address getAddress();
}
