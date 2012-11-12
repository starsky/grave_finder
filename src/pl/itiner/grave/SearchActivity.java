/*
 * This file is part of the Lokalizator grobów project.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation v3; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package pl.itiner.grave;

import pl.itiner.fetch.GraveFinderProvider;
import pl.itiner.fetch.QueryParams;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SearchActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final String CONTENT_PROVIDER_URI = "CONTENT_PROVIDER_URI";
	private FragmentManager fragmentMgr;
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		fragmentMgr = getSupportFragmentManager();
		FragmentTransaction transation = fragmentMgr.beginTransaction();
		transation.add(R.id.content_fragment_placeholder, new GFormFragment(),
				"CONTENT_FRAGMENT");
		transation.commit();
	}

	public void search(QueryParams params) {
		Bundle b = new Bundle();
		b.putString(CONTENT_PROVIDER_URI,
				GraveFinderProvider.createUri(params).toString());
		getSupportLoaderManager().initLoader(0, b, this);
	}

	// private Handler activityUIHandler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	//
	// case RESULTS_RECEIVED:
	// Intent i;
	// i = new Intent(SearchActivity.this, ResultList.class);
	// startActivity(i);
	// break;
	// case PROGRESSBAR:
	// progressBar.setVisibility(View.VISIBLE);
	// break;
	// case PROGRESSBAR_GONE:
	// progressBar.setVisibility(View.GONE);
	// break;
	// case TOAST:
	// Toast.makeText(getApplicationContext(), "\nBrak wyników\n",
	// Toast.LENGTH_SHORT).show();
	// break;
	// }
	// }
	//
	// };

	// private Runnable th_searchGraves = new Runnable() {
	//
	// @Override
	// public void run() {
	// {
	// Message dbmsg = Message.obtain();
	// dbmsg.what = PROGRESSBAR;
	// activityUIHandler.sendMessage(dbmsg);
	// }
	// runQuery();
	// {
	// Message dbmsg = Message.obtain();
	// dbmsg.what = PROGRESSBAR_GONE;
	// activityUIHandler.sendMessage(dbmsg);
	// }
	// // if (PoznanGeoJSONHandler.getResults().size() != 0) {
	// // Message dbmsg = Message.obtain();
	// // dbmsg.what = RESULTS_RECEIVED;
	// // activityUIHandler.sendMessage(dbmsg);
	// // } else {
	// // Message dbmsg = Message.obtain();
	// // dbmsg.what = TOAST;
	// // activityUIHandler.sendMessage(dbmsg);
	// // }
	//
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAbout:
			Intent intentAboutView = new Intent(this.getApplicationContext(),
					About.class);
			intentAboutView
					.putExtra(GenericAbout.DESC_ID, R.string.description);
			startActivity(intentAboutView);
			break;
		}
		return true;
	}

	// private void runQuery() {
	// Long tmpNecropolisId = necropolis.getSelectedItemId() != 0 ? necropolis
	// .getSelectedItemId() : null;
	// Date deathDate = null;
	// Date burialDate = null;
	// Date birthDate = null;
	//
	// Date tmpDate = new GregorianCalendar(datePicker.getYear(),
	// datePicker.getMonth(), datePicker.getDayOfMonth()).getTime();
	// switch (whichDate) {
	// case DEATH_DATE:
	// deathDate = tmpDate;
	// break;
	// case BIRTH_DATE:
	// birthDate = tmpDate;
	// break;
	// case BURIAL_DATE:
	// burialDate = tmpDate;
	// break;
	// }
	// // try {
	// // PoznanGeoJSONHandler.executeQuery(tmpNecropolisId,
	// editTextName.getText()
	// // .toString(), editTextSurname.getText().toString(),
	// // deathDate, birthDate, burialDate);
	// // } catch (IOException e) {
	// // Toast.makeText(this, R.string.query_io_err, Toast.LENGTH_LONG);
	// // Log.e("GForm", "IO Err", e);
	// // }
	// }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader loader = new CursorLoader(this);
		loader.setUri(Uri.parse(bundle.getString(CONTENT_PROVIDER_URI)));
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		cursor = c;
		goToList();

	}

	private void goToList() {
		if (null != cursor && cursor.getCount() > 0) {
			FragmentTransaction transaction = fragmentMgr.beginTransaction();
			transaction.replace(R.id.content_fragment_placeholder,
					new ResultList(), "CONTENT_FRAGMENT");
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}
}