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

public class Deathman {
	private static final String NO_DATA = " Brak danych ";
	
	private String surname; //surname
	private String name; //imi�
	private String burial_date;
	private String death_date;
	private String cm_id;
	private String quater;
	private String place;
	private String row;
	private String family; //type of grave
	private String field;
	private String size;
	private String date_birth;
	private String id;
	
	public Deathman(String surname, String name, String burial_date,
			String death_date, String cm_id, String place, String row, String family,
			String field, String size, String birth_date, String quarter, String id, double[] coordinates) {
		super();
		this.surname = surname;
		this.name = name;
		this.burial_date = burial_date;
		this.date_birth = birth_date;
		this.death_date = death_date;
		this.cm_id = cm_id;
		this.place = place;
		this.row = row;
		this.family = family;
		this.field = field;
		this.size = size;
		this.quater = quarter;
		this.coordinates = coordinates;
		this.id = id;
	}

	private double [] coordinates = new double[2];

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBurial_date() {
		if(burial_date.equals("0001-01-01"))
			return NO_DATA;
		return burial_date;
	}

	public void setBurial_date(String burial_date) {
		this.burial_date = burial_date;
	}

	public String getDeath_date() {
		if(death_date.equals("0001-01-01"))
			return NO_DATA;
		return death_date;
	}

	public void setDeath_date(String death_date) {
		this.death_date = death_date;
	}

	public String getCm_id() {
		return cm_id;
	}

	public void setCm_id(String cm_id) {
		this.cm_id = cm_id;
	}

	public String getQuater() {
		return quater;
	}

	public void setQuater(String quater) {
		this.quater = quater;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDate_birth() {
		if(date_birth.equals("0001-01-01"))
			return NO_DATA;
		return date_birth;
	}

	public void setDate_birth(String date_birth) {
		this.date_birth = date_birth;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
