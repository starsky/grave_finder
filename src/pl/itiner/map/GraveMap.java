package pl.itiner.map;

import java.util.List;

import pl.itiner.grave.GeoJSON;
import pl.itiner.grave.R;
import pl.itiner.grave.ResultList;
import pl.itiner.models.Deathman;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;

public class GraveMap extends MapActivity {
	MapView mapView;
	Deathman dtDeathman;
	TextView mapSurnameName;
	TextView mapBirthDate;
	TextView mapDeathDate;
	TextView mapCementry;
	MapController mc;
	GeoPoint p;

	/** Called when the activity is first created. */

	class MapOverlay extends com.google.android.maps.Overlay {
		
		private Deathman dt;
		private Context ctx;
		private int pin_x;
		private int pin_y;
		Point screenPt;
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.pin);
		public MapOverlay(Deathman dt, Context ctx)
		{
			this.ctx = ctx;
			this.dt = dt;
		}
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			// ---add the marker---
			screenPt = screenPts;
			pin_x = screenPts.x;
			pin_y = screenPts.y - 61;
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 61, null);
			return true;
		}

		
		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			// TODO Auto-generated method stub
			if (e.getAction() == MotionEvent.ACTION_MOVE && e.getAction()!= MotionEvent.ACTION_DOWN)
			{
			RectF rec = new RectF();
			rec.set(bmp.getWidth(),-bmp.getHeight(),bmp.getWidth(),0);
			rec.offset(pin_x, pin_y+61);
			if(rec.contains(e.getX(), e.getY()))
			{
			//double [] coord = dt.getCoordinates();
			//Toast.makeText(getApplicationContext(), coord[0] +" "+coord[1], Toast.LENGTH_LONG).show();
			AlertDialog.Builder alertBox = new AlertDialog.Builder(ctx);
			alertBox.setMessage("Nawiguj do celu");
			alertBox.setPositiveButton("Tak", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "TUTAJ BĘDZIE ROUTING", Toast.LENGTH_LONG).show();
					dialog.dismiss();
				}
			});
			
			alertBox.setNegativeButton("Nie teraz", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//Nothing to do
					dialog.dismiss();
				}
			});
			alertBox.show();
			}
			
			
		}
			return true;
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Bundle b = getIntent().getExtras();
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
		MapOverlay mapOverlay = new MapOverlay(tmp,this);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
// 0fwLhF406wY8NWJfI8BBuiifYfUUnVvVo8g__hg

