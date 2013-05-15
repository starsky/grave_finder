package pl.itiner.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NameHintProvider extends ContentProvider {

	public static class Columns implements BaseColumns {
		public static final String COLUMN_HINT_TYPE = "type";
		public static final String COLUMN_VALUE = "value";
	}
	
	public static enum QUERY_TYPES {
		NAME,SURNAME;
	}

	public static final String SIMPLE_AUTHORITY = "pl.itiner.grave.NameHintProvider";
	public static final String BASE_PATH = "name_hints";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ SIMPLE_AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/name_hints";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/name_hint";

	// Used for the UriMacher"AA"
	private static final int HINTS = 1;
	private static final int HINT_URI_ID = 2;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH, HINTS);
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH + "/#", HINT_URI_ID);
	}

	private DepartedDB dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sURIMatcher.match(uri);
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (match) {
		case HINTS:
			count = db.delete(NameHintTableHelper.TABLE_NAME, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case HINT_URI_ID:
			long id = ContentUris.parseId(uri);
			count = db.delete(NameHintTableHelper.TABLE_NAME, Columns._ID + "=?", new String[] { id + "" });
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case HINTS:
			return CONTENT_TYPE;
		case HINT_URI_ID:
			return CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sURIMatcher.match(uri) == HINTS) {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			long id = db.insert(NameHintTableHelper.TABLE_NAME, null, values);
			Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(insertUri, null);
			return insertUri;
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
		switch (match) {
		case HINTS:
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor c = db.query(NameHintTableHelper.TABLE_NAME, projection, selection,
					selectionArgs, null, null, Columns.COLUMN_VALUE + " ASC");
			c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return c;
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}

}
