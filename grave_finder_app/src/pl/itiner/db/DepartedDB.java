package pl.itiner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class DepartedDB extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "departed_db";
	public static final Integer DATABASE_VERSION = 2;
	public DepartedDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DepartedTableHelper.onCreate(db);
		NameHintTableHelper.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DepartedTableHelper.onUpgrade(db, oldVersion, newVersion);
		NameHintTableHelper.onUpgrade(db, oldVersion, newVersion);
	}

}
