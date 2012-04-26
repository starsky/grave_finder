package pl.itiner.models;

public class DepartedProperties {

	static final String NO_DATA = " Brak danych ";

	private String g_surname;
	private String g_name;
	private String g_date_burial;
	private String g_date_death;
	private String cm_id;
	private String g_quater;
	private String g_place;
	private String g_row;
	private String g_family;
	private String g_field;
	private String g_size;
	private String g_date_birth;

	protected DepartedProperties() {
	}

	public String getSurname() {
		return g_surname;
	}

	public String getName() {
		return g_name;
	}

	public String getBurial_date() {
		if (g_date_burial.equals("0001-01-01"))
			return NO_DATA;
		return g_date_burial;
	}

	public String getDeath_date() {
		if (g_date_death.equals("0001-01-01"))
			return NO_DATA;
		return g_date_death;
	}

	public String getCm_id() {
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

	public String getDate_birth() {
		if (g_date_birth.equals("0001-01-01"))
			return NO_DATA;
		return g_date_birth;
	}

}