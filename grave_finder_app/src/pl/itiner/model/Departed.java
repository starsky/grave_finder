package pl.itiner.model;

import java.util.Date;

import android.location.Location;

public interface Departed {
	public static final String GRAVE_LOCATION_PROVIDER = "GRAVE_LOCATION_PROVIDER";

	public abstract String getSurname();

	public abstract String getName();

	public abstract Date getBurialDate();

	public abstract Date getDeathDate();

	public abstract long getCmId();

	public abstract String getQuater();

	public abstract String getPlace();

	public abstract String getRow();

	public abstract String getFamily();

	public abstract String getField();

	public abstract String getSize();

	public abstract Date getBirthDate();

	public abstract Location getLocation();

	public abstract long getId();
	
	public abstract String getURL();
	
	public abstract void setURL(String url);

}