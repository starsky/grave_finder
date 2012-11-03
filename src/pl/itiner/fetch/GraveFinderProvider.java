package pl.itiner.fetch;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public final class GraveFinderProvider extends ContentProvider {

	public static final String SIMPLE_AUTHORITY = "pl.itiner.grave.GraveFinderProvider";
	public static final String BASE_PATH = "graves";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ SIMPLE_AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/graves";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/grave";

	// Used for the UriMacher
	private static final int GRAVES_URI_ID = 1;
	private static final int GRAVE_URI_ID = 2;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH, GRAVES_URI_ID);
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH + "/#", GRAVE_URI_ID);
	}

	private DepartedDB dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (match) {
		case GRAVES_URI_ID:
			count = db.delete(DepartedTableHelper.TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case GRAVE_URI_ID:
			long departedId = ContentUris.parseId(uri);
			count = db.delete(DepartedTableHelper.TABLE_NAME,
					DepartedTableHelper.COLUMN_ID + " = " + departedId, null);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case GRAVES_URI_ID:
			return CONTENT_TYPE;
		case GRAVE_URI_ID:
			return CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sURIMatcher.match(uri) == GRAVES_URI_ID) {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			long id = db.insert(DepartedTableHelper.TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(CONTENT_URI, "/" + id);
		} else {
			throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DepartedDB(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c;
		switch (match) {
		case GRAVES_URI_ID:
			c = db.query(DepartedTableHelper.TABLE_NAME, projection, selection,
					selectionArgs, null, null, null);
			c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return c;
		case GRAVE_URI_ID:
			long departedId = ContentUris.parseId(uri);
			c = db.query(DepartedTableHelper.TABLE_NAME, projection,
					DepartedTableHelper.COLUMN_ID
							+ " = "
							+ departedId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs,
					null, null, sortOrder);

			c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return c;
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (sURIMatcher.match(uri) == GRAVES_URI_ID) {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			getContext().getContentResolver().notifyChange(uri, null);
			return db.update(DepartedTableHelper.TABLE_NAME, values, selection,
					selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

}
