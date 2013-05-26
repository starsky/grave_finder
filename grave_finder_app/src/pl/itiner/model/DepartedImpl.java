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

import android.location.Location;

class DepartedImpl implements Departed {
	private Location location;
	private long id;
	private DepartedProperties properties;

	public DepartedImpl(DepartedProperties properties, long id, Location location) {
		this.id = id;
		this.location = location;
		this.properties = properties;
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getSurname()
	 */
	@Override
	public String getSurname() {
		return properties.getSurname();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getName()
	 */
	@Override
	public String getName() {
		return properties.getName();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getBurialDate()
	 */
	@Override
	public Date getBurialDate() {
		return properties.getBurialDate();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getDeathDate()
	 */
	@Override
	public Date getDeathDate() {
		return properties.getDeathDate();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getCmId()
	 */
	@Override
	public long getCmId() {
		return properties.getCmId();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getQuater()
	 */
	@Override
	public String getQuater() {
		return properties.getQuater();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getPlace()
	 */
	@Override
	public String getPlace() {
		return properties.getPlace();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getRow()
	 */
	@Override
	public String getRow() {
		return properties.getRow();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getFamily()
	 */
	@Override
	public String getFamily() {
		return properties.getFamily();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getField()
	 */
	@Override
	public String getField() {
		return properties.getField();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getSize()
	 */
	@Override
	public String getSize() {
		return properties.getSize();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getBirthDate()
	 */
	@Override
	public Date getBirthDate() {
		return properties.getDateBirth();
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getLocation()
	 */
	@Override
	public Location getLocation() {
		return location;
	}

	/* (non-Javadoc)
	 * @see pl.itiner.model.Departed#getId()
	 */
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return properties.getW_url();
	}

	@Override
	public void setURL(String url) {
		// TODO Auto-generated method stub
		properties.setW_url(url);
		
	}

}
