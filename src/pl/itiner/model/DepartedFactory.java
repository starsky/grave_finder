package pl.itiner.model;

import java.lang.reflect.Type;
import java.util.List;

import pl.itiner.db.DepartedTableHelper;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class DepartedFactory {
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final Type COLLECTION_TYPE = new TypeToken<List<DepartedImpl>>() {
	}.getType();

	private static GsonBuilder g;

	static {
		g = new GsonBuilder();
		g.setDateFormat(DATE_FORMAT);
		g.registerTypeAdapter(DepartedImpl.class, new DepartedDeserializer());
		g.registerTypeAdapter(COLLECTION_TYPE, new DepartedListDeserializer());
	}

	private DepartedFactory() {
	}

	public static List<? extends Departed> parseJson(String json) {
		Gson gson = g.create();
		return gson.fromJson(json, COLLECTION_TYPE);
	}

	public static ContentValues asContentValues(Departed departed) {
		ContentValues values = new ContentValues();
		values.put(DepartedTableHelper.COLUMN_ID, departed.getId());
		values.put(DepartedTableHelper.COLUMN_DATE_BIRTH, departed
				.getBirthDate() != null ? departed.getBirthDate().getTime()
				: null);
		values.put(DepartedTableHelper.COLUMN_DATE_BURIAL, departed
				.getBurialDate() != null ? departed.getBurialDate().getTime()
				: null);
		values.put(DepartedTableHelper.COLUMN_DATE_DEATH, departed
				.getDeathDate() != null ? departed.getDeathDate().getTime()
				: null);
		values.put(DepartedTableHelper.COLUMN_FAMILY, departed.getFamily());
		values.put(DepartedTableHelper.COLUMN_FIELD, departed.getField());
		values.put(DepartedTableHelper.COLUMN_LAT, departed.getLocation()
				.getLatitude());
		values.put(DepartedTableHelper.COLUMN_LON, departed.getLocation()
				.getLongitude());
		values.put(DepartedTableHelper.COLUMN_NAME, departed.getName());
		values.put(DepartedTableHelper.COLUMN_PLACE, departed.getPlace());
		values.put(DepartedTableHelper.COLUMN_QUARTER, departed.getQuater());
		values.put(DepartedTableHelper.COLUMN_ROW, departed.getRow());
		values.put(DepartedTableHelper.COLUMN_SIZE, departed.getSize());
		values.put(DepartedTableHelper.COLUMN_SURENAME, departed.getSurname());
		values.put(DepartedTableHelper.COLUMN_CEMENTERY_ID, departed.getCmId());
		return values;

	}

}
