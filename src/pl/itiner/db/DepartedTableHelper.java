package pl.itiner.db;

import android.database.sqlite.SQLiteDatabase;
import static pl.itiner.db.GraveFinderProvider.Columns.*;

final class DepartedTableHelper {
	public static final String TABLE_NAME = "departeds";

	private static final String TABLE_CREATE = "create table "
			+ TABLE_NAME + "("  
			+ _ID + " INTEGER PRIMARY KEY, " 
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
