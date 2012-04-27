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
import java.util.List;

import pl.itiner.models.Departed;
import pl.itiner.nutiteq.NutiteqMap;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultList extends ListActivity {

	private static String[] cementeries;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		cementeries = getResources().getStringArray(R.array.necropolises);
		ListView lv = getListView();
		setListAdapter(createAdapter());

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(getApplicationContext(), NutiteqMap.class);
				i.putExtra("id", position);
				startActivity(i);
			}
		});
	}

	public ListAdapter createAdapter() {
		return new MyBaseAdapter(this, GeoJSON.getResults());
	}

	private static String getCmName(Long id) {
		return cementeries[id.intValue()];
	}

	public class MyBaseAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<Departed> list;

		public MyBaseAdapter(Context ctx, List<Departed> list) {
			mInflater = LayoutInflater.from(ctx);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			final Departed dt = (Departed) (getItem(position));

			((TextView) convertView.findViewById(R.id.surname_name)).setText(dt
					.getSurname() + " " + dt.getName());
			((TextView) convertView.findViewById(R.id.list_cementry))
					.setText(getCmName(dt.getCmId()));
			((TextView) convertView.findViewById(R.id.list_value_dateBirth))
					.setText(f.format(dt.getBirthDate()));
			((TextView) convertView.findViewById(R.id.list_value_dateDeath))
					.setText(f.format(dt.getDeathDate()));
			((TextView) convertView.findViewById(R.id.list_value_dateBurial))
					.setText(f.format(dt.getBurialDate()));
			return convertView;
		}

	}

}
