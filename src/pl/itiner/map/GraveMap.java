package pl.itiner.map;

import java.util.List;

import pl.itiner.grave.GeoJSON;
import pl.itiner.grave.R;
import pl.itiner.grave.ResultList;
import pl.itiner.models.Deathman;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GraveMap extends MapActivity {
	MapView mapView;
	Deathman dtDeathman;
	TextView mapSurnameName;
	TextView mapBirthDate;
	TextView mapDeathDate;
	TextView mapCementry;
	TextView mapRow;
	TextView mapQuater;
	TextView mapField;
	MapController mc;
	GeoPoint p;

	/** Called when the activity is first created. */

	class MapOverlay extends com.google.android.maps.Overlay {
		String _start, _meta;
		double _x, _y;
		public MapOverlay(String start, String meta, double x, double y)
		{
			_x = x;
			_y = y;
			_start  = start;
			_meta = meta;
		}
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.pin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 61, null);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
		    Intent intent = new Intent("pl.itiner.ROUTER",Uri.parse("route://itiner.pl/52.402267/16.911813/"+_x+"/"+_y));
		     startActivity(intent);
//52.402267,16.911813
			return true;
			
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		
		Bundle b = getIntent().getExtras();
		if(b!= null)
		{
		double x = b.getDouble("x");
		double y = b.getDouble("y");
		int id = b.getInt("id");
		

		Deathman tmp = GeoJSON.dList.get(id);
		mapSurnameName = (TextView) findViewById(R.id.map_surname_name);
		mapSurnameName.setText(tmp.getName() + " " + tmp.getSurname());
		
		mapBirthDate = (TextView) findViewById(R.id.map_value_dateBirth);
		mapBirthDate.setText(tmp.getDate_birth());
		
		mapDeathDate = (TextView) findViewById(R.id.map_value_dateDeath);
		mapDeathDate.setText(tmp.getDeath_date());
		
		mapField = (TextView) findViewById(R.id.map_field_value);
		mapField.setText(tmp.getField());
		
		mapRow = (TextView) findViewById(R.id.map_row_value);
		mapRow.setText(tmp.getRow());
		
		mapQuater = (TextView) findViewById(R.id.map_quater_value);
		mapQuater.setText(tmp.getQuater());

		mapCementry = (TextView) findViewById(R.id.map_value_cementry);
		String cm_name = ResultList.cementeries[Integer
				.parseInt(tmp.getCm_id())];
		mapCementry.setText(cm_name);

		mapView = (MapView) findViewById(R.id.mapView);
		mc = mapView.getController();

		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
		View zoomView = mapView.getZoomControls();

		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mapView.displayZoomControls(true);

		double lat = x;
		double lng = y;

		p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		mc.animateTo(p);
		mc.setZoom(17);

		// ---Add a location marker---
		MapOverlay mapOverlay = new MapOverlay("START", "META", x, y);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
// 0fwLhF406wY8NWJfI8BBuiifYfUUnVvVo8g__hg
