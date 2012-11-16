package pl.itiner.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static pl.itiner.db.GraveFinderProvider.Columns.*;

import pl.itiner.fetch.QueryParams;
import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public final class GraveFinderProvider extends ContentProvider {

	@SuppressWarnings("unused")
	private static final String TAG = "GraveFinderProvider";
	public static final String NAME_QUERY_PARAM = "name";
	public static final String SURENAME_QUERY_PARAM = "surename";
	public static final String CEMENTARY_ID_QUERY_PARAM = "cm_id";
	public static final String BIRTH_DATE_QUERY_PARAM = "birth_date";
	public static final String DEATH_DATE_QUERY_PARAM = "death_date";
	public static final String BURIAL_DATE_QUERY_PARAM = "burial_date";

	public static final String SIMPLE_AUTHORITY = "pl.itiner.grave.GraveFinderProvider";
	public static final String BASE_PATH = "graves";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ SIMPLE_AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/graves";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/grave";

	// Used for the UriMacher"AA"
	private static final int GRAVES = 1;
	private static final int GRAVE_URI_ID = 2;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH, GRAVES);
		sURIMatcher.addURI(SIMPLE_AUTHORITY, BASE_PATH + "/#", GRAVE_URI_ID);
	}

	private DepartedDB dbHelper;
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd-MM-yyyy");

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (match) {
		case GRAVES:
			StringBuilder builder = new StringBuilder();
			String[] whereArgs = createWhere(uri, builder);
			count = db.delete(DepartedTableHelper.TABLE_NAME,
					builder.toString(), whereArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case GRAVE_URI_ID:
			long departedId = ContentUris.parseId(uri);
			count = db.delete(DepartedTableHelper.TABLE_NAME, _ID + " = "
					+ departedId, null);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case GRAVES:
			return CONTENT_TYPE;
		case GRAVE_URI_ID:
			return CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sURIMatcher.match(uri) == GRAVES) {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			long id = db.insert(DepartedTableHelper.TABLE_NAME, null, values);
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
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c;
		switch (match) {
		case GRAVES:
			return handleQuery(uri, projection, db);
		case GRAVE_URI_ID:
			long departedId = ContentUris.parseId(uri);
			c = db.query(DepartedTableHelper.TABLE_NAME, projection, _ID
					+ " = "
					+ departedId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs, null, null, sortOrder);

			c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return c;
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}
	}

	private Cursor handleQuery(Uri uri, String[] projection, SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();
		String[] whereArgs = createWhere(uri, builder);
		Cursor c = db.query(DepartedTableHelper.TABLE_NAME, projection,
				builder.toString(), whereArgs, null, null, null);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	private static String[] createWhere(Uri uri, StringBuilder builder) {
		StringBuilder delim = new StringBuilder();
		ArrayList<String> whereArgs = new ArrayList<String>();
		putDateToWhere(builder, COLUMN_DATE_BIRTH, delim, whereArgs,
				uri.getQueryParameter(BIRTH_DATE_QUERY_PARAM));
		putDateToWhere(builder, COLUMN_DATE_BURIAL, delim, whereArgs,
				uri.getQueryParameter(BURIAL_DATE_QUERY_PARAM));
		putDateToWhere(builder, COLUMN_DATE_DEATH, delim, whereArgs,
				uri.getQueryParameter(DEATH_DATE_QUERY_PARAM));
		putStringToWhere(builder, COLUMN_SURENAME, delim, whereArgs,
				uri.getQueryParameter(SURENAME_QUERY_PARAM));
		putStringToWhere(builder, COLUMN_NAME, delim, whereArgs,
				uri.getQueryParameter(NAME_QUERY_PARAM));
		putStringToWhere(builder, COLUMN_CEMENTERY_ID, delim, whereArgs,
				uri.getQueryParameter(CEMENTARY_ID_QUERY_PARAM));
		return whereArgs.toArray(new String[whereArgs.size()]);
	}

	private static void putStringToWhere(StringBuilder builder, String column,
			StringBuilder delim, List<String> whereArgs, Object paramValue) {
		final String AND_DELIM = " AND ";
		if (paramValue != null) {
			builder.append(delim.toString()).append(column).append("=?");
			delim.delete(0, delim.length());
			delim.append(AND_DELIM);
			whereArgs.add(paramValue.toString());
		}
	}

	private static void putDateToWhere(StringBuilder builder, String column,
			StringBuilder delim, List<String> whereArgs, String paramValue) {
		final String AND_DELIM = " AND ";
		Date date;
		try {
			if (paramValue != null) {
				date = dateFormat.parse(paramValue);
				builder.append(delim.toString()).append(column).append("=?");
				whereArgs.add(date.getTime() + "");
				delim.delete(0, delim.length());
				delim.append(AND_DELIM);
			}
		} catch (ParseException e) {
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (sURIMatcher.match(uri) == GRAVES) {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			getContext().getContentResolver().notifyChange(uri, null);
			return db.update(DepartedTableHelper.TABLE_NAME, values, selection,
					selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	// @Override
	// public synchronized void handleResponse(List<Departed> data) {
	// for (Departed d : data) {
	// getContext().getContentResolver().insert(CONTENT_URI, values);
	// }
	// }

	public static Uri createUri(QueryParams params) {
		Uri.Builder builder = CONTENT_URI.buildUpon();
		if (params.isFilledName())
			builder.appendQueryParameter(GraveFinderProvider.NAME_QUERY_PARAM,
					params.getName());
		if (params.isFilledSurename())
			builder.appendQueryParameter(
					GraveFinderProvider.SURENAME_QUERY_PARAM,
					params.getSurename());
		if (params.isFilledCmId())
			builder.appendQueryParameter(
					GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM,
					params.getCmId() + "");
		if (params.isFilledBirthDate())
			builder.appendQueryParameter(
					GraveFinderProvider.BIRTH_DATE_QUERY_PARAM,
					dateFormat.format(params.getBirthDate()));
		if (params.isFilledBurialDate())
			builder.appendQueryParameter(
					GraveFinderProvider.BURIAL_DATE_QUERY_PARAM,
					dateFormat.format(params.getBurialDate()));
		if (params.isFilledDeathDate())
			builder.appendQueryParameter(
					GraveFinderProvider.DEATH_DATE_QUERY_PARAM,
					dateFormat.format(params.getDeathDate()));
		return builder.build();
	}

	public static class Columns implements BaseColumns {
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
	}

}
