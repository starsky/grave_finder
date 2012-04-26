/*
 * This file is part of the Lokalizator grobów project.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation v3; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package pl.itiner.models;

import android.location.Location;

public class Departed {

	private Location location;
	private String id;
	private DepartedProperties properties;
	
	public Departed(String surname, String name, String burial_date,
			String death_date, String cm_id, String place, String row,
			String family, String field, String size, String birth_date,
			String quarter, String id, double[] coordinates) {
		super();
	}

	protected Departed(DepartedProperties properties, String id, Location location) {
		this.properties = properties;
		this.location = location;
		this.properties = properties;
	}

	public String getSurname() {
		return properties.getSurname();
	}

	public String getName() {
		return properties.getName();
	}

	public String getBurial_date() {
		return properties.getBurial_date();
	}

	public String getDeath_date() {
		return properties.getDeath_date();
	}

	public String getCm_id() {
		return properties.getCm_id();
	}

	public String getQuater() {
		return properties.getQuater();
	}

	public String getPlace() {
		return properties.getPlace();
	}

	public String getRow() {
		return properties.getRow();
	}

	public String getFamily() {
		return properties.getFamily();
	}

	public String getField() {
		return properties.getField();
	}

	public String getSize() {
		return properties.getSize();
	}

	public String getDate_birth() {
		return properties.getDate_birth();
	}

	public double[] getLocation() {
		double[] ret = new double[2];
		ret[0] = 0;//location.getLatitude();
		ret[1] = 0;//location.getLongitude();
		return ret;
	}

	public String getId() {
		return id;
	}

}
