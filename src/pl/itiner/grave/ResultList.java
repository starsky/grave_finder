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

import java.util.ArrayList;
import java.util.List;

import pl.itiner.models.Departed;
import pl.itiner.models.DepartedProperties;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultList extends ListActivity {

	MyBaseAdapter listAdapter;
	public static String[] cementeries; // wyrzucić
	public List<String> adapterList = new ArrayList<String>();
	String[] deads;
	TextView list_item_name_surmane;

	// TODO Nie robić tego String`a!!
	public ListAdapter createAdapter() {
		adapterList = new ArrayList<String>();
		for (int i = 0; i < GeoJSON.getResults().size(); i++) {
			Departed d = GeoJSON.getResults().get(i);
			double[] c = d.getLocation();
			adapterList.add(d.getSurname() + " " + d.getName() + "\n"
					+ d.getDeath_date() + "\n(" + c[0] + "," + c[1] + ")");
		}
		listAdapter = new MyBaseAdapter(this);
		return listAdapter;

	}

	public String getCmName(int id) {
		return cementeries[id];
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cementeries = getResources().getStringArray(R.array.necropolises);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setDividerHeight(2);
		lv.setFastScrollEnabled(true);
		lv.setFadingEdgeLength(2);

		ListAdapter la = createAdapter();
		setListAdapter(la); // TODO set s

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				// Intent i;
				// i = new Intent(getApplicationContext(), NutiteqMap.class);
				Intent i;
				i = new Intent(getApplicationContext(), NutiteqMap.class);
				i.putExtra("y", ((Departed) parent.getItemAtPosition(position))
						.getLocation()[0]);
				i.putExtra("x", ((Departed) parent.getItemAtPosition(position))
						.getLocation()[1]);
				i.putExtra("id", position);
				startActivity(i);
			}
		});
	}

	public class MyBaseAdapter extends BaseAdapter {

		Context c;
		private LayoutInflater mInflater;

		public MyBaseAdapter(Context ctx) {
			c = ctx;
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return GeoJSON.getResults().size();
		}

		@Override
		public Object getItem(int position) {
			return GeoJSON.getResults().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = (RelativeLayout) mInflater.inflate(
					R.layout.list_item, parent, false);

			Departed dt = (Departed) (getItem(position));
			((TextView) convertView.findViewById(R.id.surname_name)).setText(dt
					.getSurname() + " " + dt.getName());
			((TextView) convertView.findViewById(R.id.list_cementry))
					.setText(getCmName(Integer.parseInt(dt.getCm_id())));
			((TextView) convertView.findViewById(R.id.list_value_dateBirth))
					.setText(dt.getDate_birth());
			((TextView) convertView.findViewById(R.id.list_value_dateDeath))
					.setText(dt.getDeath_date());
			((TextView) convertView.findViewById(R.id.list_value_dateBurial))
					.setText(dt.getBurial_date());
			return convertView;
		}

	}

}
