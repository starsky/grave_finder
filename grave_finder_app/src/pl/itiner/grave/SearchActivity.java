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

import java.lang.ref.WeakReference;

import pl.itiner.db.GraveFinderProvider;
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
import android.support.v4.app.ListFragment;
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
	public static final String CONTENT_FRAGMENT_TAG = "CONTENT_FRAGMENT";
	private static final String CONTENT_PROVIDER_URI = "CONTENT_PROVIDER_URI";
	private FragmentManager fragmentMgr;
	private SimpleCursorAdapter adapter;

	private Handler handler = new SearchActivityHandler(this);
	private ListFragment listFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		adapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item,
				null,
				new String[] { COLUMN_NAME, COLUMN_SURENAME,
						COLUMN_CEMENTERY_ID, COLUMN_DATE_BIRTH,
						COLUMN_DATE_DEATH, COLUMN_DATE_BURIAL },
				new int[] { R.id.list_value_name, R.id.list_value_surname,
						R.id.list_value_cementry, R.id.list_value_dateBirth,
						R.id.list_value_dateDeath, R.id.list_value_dateBurial },
				SimpleCursorAdapter.NO_SELECTION);
		adapter.setViewBinder(new ResultList.ResultListViewBinder());

		fragmentMgr = getSupportFragmentManager();
		if (savedInstanceState == null) {
			FragmentTransaction transation = fragmentMgr.beginTransaction();
			transation.add(R.id.content_fragment_placeholder,
					new GFormFragment(), CONTENT_FRAGMENT_TAG);
			transation.commit();
			listFragment = new ResultList();
		} else {
			if (fragmentMgr.findFragmentByTag(CONTENT_FRAGMENT_TAG) instanceof ListFragment) {
				listFragment = (ListFragment) fragmentMgr
						.findFragmentByTag(CONTENT_FRAGMENT_TAG);
				getSupportLoaderManager().initLoader(GRAVE_DATA_LOADER_ID,
						null, this);
			} else {
				listFragment = new ResultList();
			}
		}
		listFragment.setListAdapter(adapter);
	}

	public ListFragment getListFragment() {
		return listFragment;
	}

	public void search(QueryParams params) {
		Bundle b = new Bundle();
		String queryUriStr = GraveFinderProvider.createUri(params).toString();
		b.putString(CONTENT_PROVIDER_URI, queryUriStr);
		b.putParcelable(JsonFetchService.QUERY_PARAMS_BUNDLE, params);
		getSupportLoaderManager().destroyLoader(GRAVE_DATA_LOADER_ID);
		getSupportLoaderManager().initLoader(GRAVE_DATA_LOADER_ID, b, this);
		if (isConnectionAvailable()) {
			Intent i = new Intent(this, JsonFetchService.class);
			i.putExtra(JsonFetchService.MESSENGER_BUNDLE,
					new Messenger(handler));
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
			intentAboutView
					.putExtra(GenericAbout.DESC_ID, R.string.description);
			startActivity(intentAboutView);
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
			handler.sendEmptyMessage(SearchActivityHandler.LOCAL_DATA_AVAILABLE);
		} else {
			if (!isConnectionAvailable()) {
				handler.sendEmptyMessage(SearchActivityHandler.NO_CONNECTION);
			}
		}

	}

	public boolean isConnectionAvailable() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		return i != null && i.isConnected();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	public static class SearchActivityHandler extends Handler {

		public static final int DOWNLOAD_FAILED = 2;
		public static final int NO_CONNECTION = 1;
		public static final int LOCAL_DATA_AVAILABLE = 0;

		private WeakReference<SearchActivity> activityRef;

		public SearchActivityHandler(SearchActivity activity) {
			this.activityRef = new WeakReference<SearchActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final SearchActivity activity = activityRef.get();
			if (activity != null) {
				SearchActivityFragment fragment = (SearchActivityFragment) activity.fragmentMgr
						.findFragmentByTag(CONTENT_FRAGMENT_TAG);
				fragment.handleMessage(msg);
			}
		}
	}
}