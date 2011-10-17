package pl.itiner.grave;

import java.util.ArrayList;
import java.util.List;

import pl.itiner.map.GraveMap;
import pl.itiner.models.Deathman;
import pl.itiner.nutiteq.HelloNutiteq;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultList extends ListActivity{
	
	MyBaseAdapter listAdapter;
	public static String [] cementeries;
	public List<String> adapterList = new ArrayList<String>();
	String [] deads;
	TextView list_item_name_surmane;
	
	public ListAdapter createAdapter()
	{
		adapterList = new ArrayList<String>();
		for (int i = 0; i < GeoJSON.dList.size(); i++)
		{
			Deathman d = GeoJSON.dList.get(i);
			double [] c = d.getCoordinates();
			adapterList.add(d.getSurname() +" "+ d.getName()+ "\n" +d.getDeath_date()+"\n("+ c[0]+","+c[1]+")" );			
		}
		listAdapter = new MyBaseAdapter(this);
		return listAdapter;
		
	}
	public String getCmName(int id)
	{
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
//		  lv.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
		  
		  ListAdapter la = createAdapter();
		  setListAdapter(la); //TODO set s
		  Intent i;
		  i = new Intent(getApplicationContext(), GraveMap.class);
		
		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		    	 Intent i;
				 i = new Intent(getApplicationContext(), HelloNutiteq.class);
				 i.putExtra("y",((Deathman)parent.getItemAtPosition(position)).getCoordinates()[0]);
				 i.putExtra("x",((Deathman)parent.getItemAtPosition(position)).getCoordinates()[1]);
				 i.putExtra("id",position);
				 startActivity(i);
		    }
		  });
		}
	public class MyBaseAdapter extends BaseAdapter {

		Context c;
		private LayoutInflater mInflater;
		public MyBaseAdapter(Context ctx)
		{
			c = ctx;	
			mInflater = LayoutInflater.from(c);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return GeoJSON.dList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return GeoJSON.dList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = (RelativeLayout) mInflater.inflate(R.layout.list_item, parent,false);
		
			
			Deathman dt = (Deathman) (getItem(position));
			((TextView) convertView.findViewById(R.id.surname_name)).setText(dt.getSurname() + " " + dt.getName());
			((TextView) convertView.findViewById(R.id.list_cementry)).setText(getCmName(Integer.parseInt(dt.getCm_id())));
			if(dt.getDate_birth() == "0001-01-01" || dt.getDate_birth() == null)
				((TextView) convertView.findViewById(R.id.list_value_dateBirth)).setText(" Brak danych ");
			else
				((TextView) convertView.findViewById(R.id.list_value_dateBirth)).setText(dt.getDate_birth());
			
			if(dt.getDeath_date() == "0001-01-01" || dt.getDeath_date() == null)
				((TextView) convertView.findViewById(R.id.list_value_dateDeath)).setText(" Brak danych ");
			else
				((TextView) convertView.findViewById(R.id.list_value_dateDeath)).setText(dt.getDeath_date());
			
			if(dt.getBurial_date()== "0001-01-01" || dt.getBurial_date() == null)
				((TextView) convertView.findViewById(R.id.list_value_dateBurial)).setText(" Brak danych ");
			else
				((TextView) convertView.findViewById(R.id.list_value_dateBurial)).setText(dt.getBurial_date());
			//((TextView) convertView.findViewById(R.id.list_value_dateDeath)).setText(dt.getDeath_date());
			return convertView;
		}

	}

}
