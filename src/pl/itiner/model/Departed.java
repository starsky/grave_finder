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

package pl.itiner.model;

import java.util.Date;

import pl.itiner.db.DepartedTableHelper;
import android.content.ContentValues;
import android.location.Location;

public class Departed {
	private Location location;
	private String id;
	private DepartedProperties properties;

	public Departed(DepartedProperties properties, String id, Location location) {
		this.id = id;
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

	public ContentValues asContentValues() {
		ContentValues values = new ContentValues();
		values.put(DepartedTableHelper.COLUMN_ID, getId());
		values.put(DepartedTableHelper.COLUMN_DATE_BIRTH,
				getBirthDate() != null ? getBirthDate().getTime() : null);
		values.put(DepartedTableHelper.COLUMN_DATE_BURIAL,
				getBurialDate() != null ? getBurialDate().getTime() : null);
		values.put(DepartedTableHelper.COLUMN_DATE_DEATH,
				getDeathDate() != null ? getDeathDate().getTime() : null);
		values.put(DepartedTableHelper.COLUMN_FAMILY, getFamily());
		values.put(DepartedTableHelper.COLUMN_FIELD, getField());
		values.put(DepartedTableHelper.COLUMN_LAT, getLocation().getLatitude());
		values.put(DepartedTableHelper.COLUMN_LON, getLocation().getLongitude());
		values.put(DepartedTableHelper.COLUMN_NAME, getName());
		values.put(DepartedTableHelper.COLUMN_PLACE, getPlace());
		values.put(DepartedTableHelper.COLUMN_QUARTER, getQuater());
		values.put(DepartedTableHelper.COLUMN_ROW, getRow());
		values.put(DepartedTableHelper.COLUMN_SIZE, getSize());
		values.put(DepartedTableHelper.COLUMN_SURENAME, getSurname());
		values.put(DepartedTableHelper.COLUMN_CEMENTERY_ID, getCmId());
		return values;
	}

}
