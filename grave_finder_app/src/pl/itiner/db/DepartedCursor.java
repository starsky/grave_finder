package pl.itiner.db;

import static pl.itiner.db.GraveFinderProvider.Columns.*;

import java.util.Date;

import pl.itiner.model.Departed;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;

public final class DepartedCursor extends CursorWrapper implements Departed {

	public DepartedCursor(Cursor cursor) {
		super(cursor);
	}

	@Override
	public String getSurname() {
		int idx = getColumnIndexOrThrow(COLUMN_SURENAME);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getName() {
		int idx = getColumnIndexOrThrow(COLUMN_NAME);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public Date getBurialDate() {
		int idx = getColumnIndexOrThrow(COLUMN_DATE_BURIAL);
		return isNull(idx) ? null : new Date(getLong(idx));
	}

	@Override
	public Date getDeathDate() {
		int idx = getColumnIndexOrThrow(COLUMN_DATE_DEATH);
		return isNull(idx) ? null : new Date(getLong(idx));
	}

	@Override
	public long getCmId() {
		int idx = getColumnIndexOrThrow(COLUMN_CEMENTERY_ID);
		return getLong(idx);
	}

	@Override
	public String getQuater() {
		int idx = getColumnIndexOrThrow(COLUMN_QUARTER);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getPlace() {
		int idx = getColumnIndexOrThrow(COLUMN_PLACE);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getRow() {
		int idx = getColumnIndexOrThrow(COLUMN_ROW);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getFamily() {
		int idx = getColumnIndexOrThrow(COLUMN_FAMILY);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getField() {
		int idx = getColumnIndexOrThrow(COLUMN_FIELD);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public String getSize() {
		int idx = getColumnIndexOrThrow(COLUMN_SIZE);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public Date getBirthDate() {
		int idx = getColumnIndexOrThrow(COLUMN_DATE_BIRTH);
		return isNull(idx) ? null : new Date(getLong(idx));
	}

	@Override
	public Location getLocation() {
		int latIdx = getColumnIndexOrThrow(COLUMN_LAT);
		int lonIdx = getColumnIndexOrThrow(COLUMN_LON);
		if (!isNull(latIdx) && !isNull(lonIdx)) {
			Location location = new Location(Departed.GRAVE_LOCATION_PROVIDER);
			location.setLatitude(getDouble(latIdx));
			location.setLongitude(getDouble(lonIdx));
			return location;
		}
		return null;
	}

	@Override
	public long getId() {
		int idx = getColumnIndexOrThrow(_ID);
		return getLong(idx);
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		int idx = getColumnIndexOrThrow(COLUMN_URL);
		return isNull(idx) ? "" : getString(idx);
	}

	@Override
	public void setURL(String url) {
		// TODO Auto-generated method stub
		
	}

}
