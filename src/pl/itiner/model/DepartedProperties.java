package pl.itiner.model;

import java.util.Date;
import java.util.GregorianCalendar;

public class DepartedProperties {

	private String g_surname;
	private String g_name;
	private Date g_date_burial;
	private Date g_date_death;
	private Long cm_id;
	private String g_quater;
	private String g_place;
	private String g_row;
	private String g_family;
	private String g_field;
	private String g_size;
	private Date g_date_birth;

	private static String capitalizeFirstLetter(String str) {
		if (str != null && str.length() > 1)
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		return null;
	}

	private static final long missingDate = new GregorianCalendar(1,
			GregorianCalendar.JANUARY, 1).getTime().getTime(); // 0001-01-01

	private static Date checkMissingValues(Date date) {
		if (date.getTime() == missingDate) {
			return null;
		}
		return date;
	}

	protected DepartedProperties() {
	}

	public String getSurname() {
		return capitalizeFirstLetter(g_surname);
	}

	public String getName() {
		return capitalizeFirstLetter(g_name);
	}

	public Date getBurialDate() {
		return checkMissingValues(g_date_burial);
	}

	public Date getDeathDate() {
		return checkMissingValues(g_date_death);
	}

	public Long getCmId() {
		return cm_id;
	}

	public String getQuater() {
		return g_quater;
	}

	public String getPlace() {
		return g_place;
	}

	public String getRow() {
		return g_row;
	}

	public String getFamily() {
		return g_family;
	}

	public String getField() {
		return g_field;
	}

	public String getSize() {
		return g_size;
	}

	public Date getDateBirth() {
		return checkMissingValues(g_date_birth);
	}

}