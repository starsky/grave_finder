package pl.itiner.fetch;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class DepartedTableHelper {
	public static final String TABLE_NAME = "departeds";

	public static final String COLUMN_ID = BaseColumns._ID;
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "lon";
	public static final String COLUMN_SURENAME = "surename";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DATE_BURIAL = "date_burial";
	public static final String COLUMN_DATE_DEATH = "date_death";
	public static final String COLUMN_DATE_BIRTH = "date_birth";
	public static final String COLUMN_CEMENTERY_ID = "cm_id";
	public static final String COLUMN_QUARTER = "quarter";
	public static final String COLUMN_PLACE = "palce";
	public static final String COLUMN_ROW = "row";
	public static final String COLUMN_FAMILY = "family";
	public static final String COLUMN_FIELD = "field";
	public static final String COLUMN_SIZE = "size";
	public static final String COLUMN_FETCHED_TIME = "fetched_time";
	

	private static final String TABLE_CREATE = "create table "
			+ TABLE_NAME + "("  
			+ COLUMN_ID + " INTEGER PRIMARY KEY, " 
			+ COLUMN_CEMENTERY_ID + " INTEGER NOT NULL, "
			+ COLUMN_DATE_BIRTH  + " INTEGER NULL, "
			+ COLUMN_DATE_BURIAL + " INTEGER NULL, "
			+ COLUMN_DATE_DEATH  + " INTEGER NULL, "
			+ COLUMN_FAMILY + " TEXT NULL, "
			+ COLUMN_FIELD + " TEXT NULL, "
			+ COLUMN_NAME + " TEXT NULL, "
			+ COLUMN_PLACE + " TEXT NULL, "
			+ COLUMN_QUARTER + " TEXT NULL, "
			+ COLUMN_ROW + " TEXT NULL, "
			+ COLUMN_SIZE + " TEXT NULL, "
			+ COLUMN_SURENAME + " TEXT NULL, "
			+ COLUMN_LAT + " DECIMAL(9,6) NOT NULL, "
			+ COLUMN_LON + " DECIMAL(9,6) NOT NULL, "
			+ COLUMN_FETCHED_TIME + " TIMESTAMP"
			+ ");";
	
	private DepartedTableHelper() {
	}
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL(TABLE_CREATE);
	}
}
