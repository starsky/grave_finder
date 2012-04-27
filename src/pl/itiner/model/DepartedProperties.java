package pl.itiner.model;

import java.util.Date;

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

	protected DepartedProperties() {
	}

	public String getSurname() {
		return g_surname;
	}

	public String getName() {
		return g_name;
	}

	public Date getBurialDate() {
		return g_date_burial;
	}

	public Date getDeathDate() {
		return g_date_death;
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
		return g_date_birth;
	}

}