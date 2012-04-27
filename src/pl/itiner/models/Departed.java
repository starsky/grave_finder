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

import java.util.Date;

import android.location.Location;

public class Departed {
	private Location location;
	private String id;
	private DepartedProperties properties;

	protected Departed(DepartedProperties properties, String id,
			Location location) {
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

	public Date getBurialDate() {
		return properties.getBurialDate();
	}

	public Date getDeathDate() {
		return properties.getDeathDate();
	}

	public Long getCmId() {
		return properties.getCmId();
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

	public Date getBirthDate() {
		return properties.getDateBirth();
	}

	public Location getLocation() {
		return location;
	}

	public String getId() {
		return id;
	}

}
