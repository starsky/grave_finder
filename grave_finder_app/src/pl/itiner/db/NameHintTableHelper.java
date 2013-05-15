package pl.itiner.db;

import static android.provider.BaseColumns._ID;
import static pl.itiner.db.NameHintProvider.Columns.COLUMN_HINT_TYPE;
import static pl.itiner.db.NameHintProvider.Columns.COLUMN_VALUE;
import android.database.sqlite.SQLiteDatabase;

public final class NameHintTableHelper {
	public static final String TABLE_NAME = "name_hints";

	private static final String TABLE_CREATE = "create table " + TABLE_NAME
			+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_HINT_TYPE + " TEXT NOT NULL, "
			+ COLUMN_VALUE + " TEXT NOT NULL " + ");";

	private NameHintTableHelper() {
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
