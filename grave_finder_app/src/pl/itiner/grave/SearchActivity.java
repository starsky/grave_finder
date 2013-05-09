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

import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_CEMENTERY_ID;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BIRTH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BURIAL;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_DEATH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_NAME;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_SURENAME;
import static pl.itiner.grave.SearchActivity.SearchActivityHandler.DOWNLOAD_FAILED;
import static pl.itiner.grave.SearchActivity.SearchActivityHandler.LOCAL_DATA_AVAILABLE;
import static pl.itiner.grave.SearchActivity.SearchActivityHandler.NO_CONNECTION;
import pl.itiner.db.GraveFinderProvider;
import pl.itiner.db.NameHintProvider;
import pl.itiner.fetch.JsonFetchService;
import pl.itiner.fetch.QueryParams;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockFragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final int GRAVE_DATA_LOADER_ID = 0;
	private static final String CONTENT_PROVIDER_URI = "CONTENT_PROVIDER_URI";
	private static final String ALERT_FRAGMENT_TAG = "ALERT_FRAGMENT_TAG";
	private static final String PROGRESS_FRAGMENT_TAG = "PROGRESS_FRAGMENT_TAG";

	private static final SearchActivityHandler HANDLER = new SearchActivityHandler();

	private FragmentManager fragmentMgr;
	private SimpleCursorAdapter adapter;
	private ResultList listFragment;
	private GFormFragment formFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		HANDLER.setActivity(this);
		adapter = createAdapter();
		fragmentMgr = getSupportFragmentManager();
		listFragment = (ResultList) fragmentMgr
				.findFragmentById(R.id.result_list_fragment);
		formFragment = (GFormFragment) fragmentMgr
				.findFragmentById(R.id.search_form_fragment);
		if (null == savedInstanceState) {
			FragmentTransaction transaction = fragmentMgr.beginTransaction();
			transaction.hide(listFragment);
			transaction.commit();
		}
		listFragment.setListAdapter(adapter);
		if (null != savedInstanceState) {
			if (getSupportLoaderManager().getLoader(GRAVE_DATA_LOADER_ID) != null) {
				getSupportLoaderManager().initLoader(GRAVE_DATA_LOADER_ID,
						null, this);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HANDLER.clearActivity();
	}
	
	public void search(QueryParams params) {
		new ProgressFragment().show(fragmentMgr, PROGRESS_FRAGMENT_TAG);
		Bundle b = new Bundle();
		String queryUriStr = GraveFinderProvider.createUri(params).toString();
		b.putString(CONTENT_PROVIDER_URI, queryUriStr);
		b.putParcelable(JsonFetchService.QUERY_PARAMS_BUNDLE, params);
		getSupportLoaderManager().destroyLoader(GRAVE_DATA_LOADER_ID);
		getSupportLoaderManager().initLoader(GRAVE_DATA_LOADER_ID, b, this);
		if (isConnectionAvailable()) {
			Intent i = new Intent(this, JsonFetchService.class);
			i.putExtra(JsonFetchService.MESSENGER_BUNDLE,
					new Messenger(HANDLER));
			i.putExtras(b);
			startService(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intentAboutView = new Intent(this.getApplicationContext(),
					About.class);
			startActivity(intentAboutView);
			return true;
		case R.id.menu_clear_cache:
			getContentResolver().delete(GraveFinderProvider.CONTENT_URI, null,
					null);
			return true;
		case R.id.menu_clear_hints:
			getContentResolver().delete(NameHintProvider.CONTENT_URI, null,
					null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader loader = new CursorLoader(this);
		if (bundle != null)
			loader.setUri(Uri.parse(bundle.getString(CONTENT_PROVIDER_URI)));
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		adapter.swapCursor(c);
		if (null != c && c.getCount() > 0) {
			HANDLER.sendEmptyMessage(SearchActivityHandler.LOCAL_DATA_AVAILABLE);
		} else {
			if (!isConnectionAvailable()) {
				HANDLER.sendEmptyMessage(SearchActivityHandler.NO_CONNECTION);
			}
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	public boolean isConnectionAvailable() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		return i != null && i.isConnected();
	}

	private SimpleCursorAdapter createAdapter() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.list_item, null, new String[] { COLUMN_NAME,
						COLUMN_SURENAME, COLUMN_CEMENTERY_ID,
						COLUMN_DATE_BIRTH, COLUMN_DATE_DEATH,
						COLUMN_DATE_BURIAL }, new int[] { R.id.list_value_name,
						R.id.list_value_surname, R.id.list_value_cementry,
						R.id.list_value_dateBirth, R.id.list_value_dateDeath,
						R.id.list_value_dateBurial },
				SimpleCursorAdapter.NO_SELECTION);
		adapter.setViewBinder(new ResultList.ResultListViewBinder());
		return adapter;
	}

	public void handleMessage(Message msg) {
		ProgressFragment progressFragment = (ProgressFragment) fragmentMgr
				.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
		if (null != progressFragment) {
			progressFragment.dismiss();
		}
		switch (msg.what) {
		case LOCAL_DATA_AVAILABLE:
			gotoList();
			break;
		case NO_CONNECTION:
			if (!listFragment.hasData()) {
				AlertDialogFragment.create(R.string.no_conn,
						R.string.turn_on_conn).show(fragmentMgr,
						ALERT_FRAGMENT_TAG);
			}
			break;
		case DOWNLOAD_FAILED:
			if (!listFragment.hasData()) {
				AlertDialogFragment.create(R.string.no_conn,
						R.string.check_conn).show(fragmentMgr,
						ALERT_FRAGMENT_TAG);
			}
			break;
		}
	}

	private void gotoList() {
		if (!listFragment.isVisible()) {
			FragmentTransaction transaction = fragmentMgr.beginTransaction();
			transaction.hide(formFragment);
			transaction.show(listFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public static class SearchActivityHandler extends Handler {

		public static final int DOWNLOAD_FAILED = 2;
		public static final int NO_CONNECTION = 1;
		public static final int LOCAL_DATA_AVAILABLE = 0;

		private SearchActivity activityRef;

		public SearchActivityHandler() {
		}

		public void setActivity(SearchActivity activity) {
			this.activityRef = activity;
		}

		public void clearActivity() {
			this.activityRef = null;
		}

		@Override
		public void handleMessage(Message msg) {
			if (activityRef != null) {
				activityRef.handleMessage(msg);
				activityRef.formFragment.handleMessage(msg);
				activityRef.listFragment.handleMessage(msg);
			}
		}
	}
}