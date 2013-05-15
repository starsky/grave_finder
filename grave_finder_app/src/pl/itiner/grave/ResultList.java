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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.itiner.commons.Commons;
import pl.itiner.db.GraveFinderProvider;
import pl.itiner.fetch.JsonFetchService;
import pl.itiner.fetch.QueryParams;
import pl.itiner.nutiteq.NutiteqMap;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ResultList extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	public static final String TAG = "ResultList";
	private static final SearchHandler HANDLER = new SearchHandler();

	private static final int GRAVE_DATA_LOADER_ID = 0;
	private static final String CONTENT_PROVIDER_URI = "CONTENT_PROVIDER_URI";
	public static final String ALERT_FRAGMENT_TAG = "ALERT_FRAGMENT_TAG";

	private static String[] cementeries;
	private SearchActivity activity;
	private SimpleCursorAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SearchActivity) {
			this.activity = (SearchActivity) activity;
		} else {
			throw new IllegalArgumentException("Activity is not instance of "
					+ SearchActivity.class.getSimpleName());
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.activity = null;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cementeries = getResources().getStringArray(R.array.necropolises);
		adapter = createAdapter();
		setListAdapter(adapter);
		if (null != savedInstanceState) {
			if (getLoaderManager().getLoader(GRAVE_DATA_LOADER_ID) != null) {
				getLoaderManager().initLoader(GRAVE_DATA_LOADER_ID, null, this);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		HANDLER.setActivity(this);
	}

	@Override
	public void onPause() {
		HANDLER.clearActivity();
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.list, container,
				false);
		return root;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NutiteqMap.class);
		i.putExtra(NutiteqMap.DEPARTED_ID_BUND, id);
		startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader loader = new CursorLoader(getActivity());
		if (bundle != null)
			loader.setUri(Uri.parse(bundle.getString(CONTENT_PROVIDER_URI)));
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		adapter.swapCursor(c);
		if (adapter.getCount() == 0 && !activity.isConnectionAvailable()) {
			HANDLER.sendEmptyMessage(SearchHandler.NO_CONNECTION);
		} else if (!activity.isConnectionAvailable()) {
			setupOfflineDataWarningHeader();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	private SimpleCursorAdapter createAdapter() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
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

	private static String getCmName(Long id) {
		return cementeries[id.intValue()];
	}

	final static class ResultListViewBinder implements
			SimpleCursorAdapter.ViewBinder {

		private static SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd.MM.yyyy", Locale.getDefault());

		@Override
		public boolean setViewValue(View view, Cursor c, int columnIndex) {
			final String columnName = c.getColumnName(columnIndex);
			if (columnName.equals(COLUMN_CEMENTERY_ID)) {
				TextView textView = (TextView) view;
				textView.setText(getCmName(c.getLong(columnIndex)));
				return true;
			}
			if (columnName.equals(COLUMN_DATE_BIRTH)
					|| columnName.equals(COLUMN_DATE_BURIAL)
					|| columnName.equals(COLUMN_DATE_DEATH)) {
				TextView textView = (TextView) view;
				if (!c.isNull(columnIndex))
					textView.setText(dateFormat.format(new Date((c
							.getLong(columnIndex)))));
				else
					textView.setText(R.string.no_data);
				return true;
			}
			if (columnName.equals(COLUMN_NAME)
					|| columnName.equals(COLUMN_SURENAME)) {
				TextView textView = (TextView) view;
				textView.setText(Commons.capitalizeFirstLetter(c
						.getString(columnIndex)));
				return true;
			}
			return false;
		}
	}

	private boolean hasData() {
		return getListAdapter() != null && getListAdapter().getCount() > 0;
	}

	public void search(QueryParams params) {
		getView().findViewById(R.id.list_offline_warninig_view).setVisibility(
				View.GONE);
		Bundle b = new Bundle();
		String queryUriStr = GraveFinderProvider.createUri(params).toString();
		b.putString(CONTENT_PROVIDER_URI, queryUriStr);
		b.putParcelable(JsonFetchService.QUERY_PARAMS_BUNDLE, params);
		getLoaderManager().destroyLoader(GRAVE_DATA_LOADER_ID);
		getLoaderManager().initLoader(GRAVE_DATA_LOADER_ID, b, this);
		if (activity.isConnectionAvailable()) {
			Intent i = new Intent(getActivity(), JsonFetchService.class);
			i.putExtra(JsonFetchService.MESSENGER_BUNDLE,
					new Messenger(HANDLER));
			i.putExtras(b);
			getActivity().startService(i);
		}
	}

	private void setupOfflineDataWarningHeader() {
		getView().findViewById(R.id.list_offline_warninig_view).setVisibility(
				activity.isConnectionAvailable() ? View.GONE : View.VISIBLE);
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case SearchHandler.NO_CONNECTION:
			if (!hasData()) {
				AlertDialogFragment.create(R.string.no_conn,
						R.string.turn_on_conn).show(getFragmentManager(),
						ALERT_FRAGMENT_TAG);
			} else {
				setupOfflineDataWarningHeader();
			}
			break;
		case SearchHandler.DOWNLOAD_FAILED:
			if (!hasData()) {
				AlertDialogFragment.create(R.string.no_conn,
						R.string.check_conn).show(getFragmentManager(),
						ALERT_FRAGMENT_TAG);
				getView().findViewById(R.id.list_offline_warninig_view)
						.setVisibility(View.VISIBLE);
			} else {
				setupOfflineDataWarningHeader();
			}
			break;
		case SearchHandler.NO_ONLINE_RESULTS:
			if (!hasData()) {
				AlertDialogFragment.create(R.string.no_data,
						R.string.no_data_desc).show(getFragmentManager(),
						ALERT_FRAGMENT_TAG);
				getView().findViewById(R.id.list_offline_warninig_view)
						.setVisibility(View.VISIBLE);
			} else {
				setupOfflineDataWarningHeader();
			}
			break;
		case SearchHandler.UNEXPECTED_SEVER_ANSWER:
			if (!hasData()) {
				AlertDialogFragment.create(R.string.no_conn,
						R.string.unexpected_server_err).show(
						getFragmentManager(), ALERT_FRAGMENT_TAG);
				getView().findViewById(R.id.list_offline_warninig_view)
						.setVisibility(View.VISIBLE);
			} else {
				setupOfflineDataWarningHeader();
			}
			break;
		}
	}

	public static class SearchHandler extends Handler {

		public static final int UNEXPECTED_SEVER_ANSWER = 4;
		public static final int NO_ONLINE_RESULTS = 3;
		public static final int DOWNLOAD_FAILED = 2;
		public static final int NO_CONNECTION = 1;
		public static final int LOCAL_DATA_AVAILABLE = 0;

		private ResultList fragmentRef;

		private SearchHandler() {
		}

		public void setActivity(ResultList activity) {
			this.fragmentRef = activity;
		}

		public void clearActivity() {
			this.fragmentRef = null;
		}

		@Override
		public void handleMessage(Message msg) {
			if (null != fragmentRef) {
				fragmentRef.handleMessage(msg);
			}
		}
	}

}
