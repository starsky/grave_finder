package pl.itiner.nutiteq;

import static android.provider.BaseColumns._ID;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_CEMENTERY_ID;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BIRTH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BURIAL;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_DEATH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_FIELD;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_LAT;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_LON;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_NAME;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_PLACE;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_QUARTER;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_ROW;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_SURENAME;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.itiner.commons.Commons;
import pl.itiner.db.DepartedCursor;
import pl.itiner.db.GraveFinderProvider;
import pl.itiner.grave.R;
import pl.itiner.model.Departed;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class WroclawResultView extends SherlockFragmentActivity implements
		LoaderCallbacks<Cursor> {

	public static final String DEPARTED_ID_BUND = "DEPARTED_ID_BUND";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.wroclaw);
		getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle b) {
		long id = b.getLong(DEPARTED_ID_BUND);
		final Uri uri = ContentUris.withAppendedId(
				GraveFinderProvider.CONTENT_URI, id);
		return new CursorLoader(this, uri, new String[] { COLUMN_CEMENTERY_ID,
				COLUMN_DATE_BIRTH, COLUMN_DATE_BURIAL, COLUMN_DATE_DEATH,
				COLUMN_NAME, COLUMN_SURENAME, COLUMN_FIELD, COLUMN_LAT,
				COLUMN_LON, _ID, COLUMN_PLACE, COLUMN_QUARTER, COLUMN_ROW, COLUMN_URL },
				null, null, null);
	}

	private void fillHeaderWithData(Departed departed) {
		final TextView mapName;
		final TextView mapSurname;
		final TextView mapBirthDate;
		final TextView mapDeathDate;
		final TextView mapFunrealDate;
		final TextView mapCementry;
		final TextView mapRow;
		final TextView mapQuater;
		final TextView mapField;

		final WebView gifMap;
		final String[] cementeries = getResources().getStringArray(
				R.array.necropolises);

		mapName = (TextView) findViewById(R.id.map_name);
		mapName.setText(Commons.capitalizeFirstLetter(departed.getName()));

		mapSurname = (TextView) findViewById(R.id.map_surname);
		mapSurname
				.setText(Commons.capitalizeFirstLetter(departed.getSurname()));

		mapBirthDate = (TextView) findViewById(R.id.map_value_dateBirth);
		mapBirthDate.setText(formatDate(departed.getBirthDate()));

		mapDeathDate = (TextView) findViewById(R.id.map_value_dateDeath);
		mapDeathDate.setText(formatDate(departed.getDeathDate()));

		mapFunrealDate = (TextView) findViewById(R.id.map_value_dateFunreal);
		mapFunrealDate.setText(formatDate(departed.getBurialDate()));

		mapField = (TextView) findViewById(R.id.map_field_value);
		mapField.setText(departed.getField());

		mapRow = (TextView) findViewById(R.id.map_row_value);
		mapRow.setText(departed.getRow());

		mapQuater = (TextView) findViewById(R.id.map_quater_value);
		mapQuater.setText(departed.getQuater());

		mapCementry = (TextView) findViewById(R.id.map_value_cementry);
		String cm_name = cementeries[(int) departed.getCmId()];
		mapCementry.setText(cm_name);

		gifMap = (WebView) findViewById(R.id.wroclaw_gif_map);
		String url = "http://iwroclaw.pl/"+ departed.getURL();
		gifMap.loadUrl(url);
	}

	private static final SimpleDateFormat f = new SimpleDateFormat(
			"dd.MM.yyyy", Locale.getDefault());

	private static String formatDate(Date d) {
		if (d == null) {
			return null;
		}
		return f.format(d);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.getCount() == 0) {
			final CursorLoader l = (CursorLoader) loader;
			throw new RuntimeException(
					"Cursor has 0 results for loader with URI: " + l.getUri());
		}
		Departed d = new DepartedCursor(cursor);
		cursor.moveToFirst();
		fillHeaderWithData(d);
		// placeGravePin(d);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
