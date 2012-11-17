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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.itiner.commons.Commons;
import static pl.itiner.db.GraveFinderProvider.Columns.*;
import pl.itiner.nutiteq.NutiteqMap;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ResultList extends ListFragment {

	private static String[] cementeries;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cementeries = getResources().getStringArray(R.array.necropolises);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), NutiteqMap.class);
		i.putExtra(NutiteqMap.DEPARTED_ID_BUND, id);
		startActivity(i);
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

}
