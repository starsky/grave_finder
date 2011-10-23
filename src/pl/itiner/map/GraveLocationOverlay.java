package pl.itiner.map;

import pl.itiner.grave.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class GraveLocationOverlay extends ItemizedOverlay<OverlayItem>
{

	OverlayItem _graveLocation;
	Context _ctx;
	Drawable _grave_marker;
	MapView map;

	public GraveLocationOverlay(MapView map,Drawable defaultMarker, Context ctx, GeoPoint graveLocation) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
		_ctx = ctx;
		this.map=map;
		_graveLocation = new OverlayItem(
				graveLocation, 
						"Grób", 
						"Znaleziony"
				);
	    this._grave_marker = defaultMarker;	
		_grave_marker.setBounds(0, 0, _grave_marker.getIntrinsicHeight(), _grave_marker.getIntrinsicWidth());
		_graveLocation.setMarker(_grave_marker);
		boundCenter(_grave_marker);
		populate();
		onTap(0);
	}

	public boolean updateLocation(Location userLocation)
	{		
		return true;
	}
	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return _graveLocation;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// wyłączamy rysowanie cienia za markerem
		super.draw(canvas, mapView, false);
		boundCenterBottom(_grave_marker);

	}
	@Override
	protected boolean onTap(int index) {
		
//		Toast toast = Toast.makeText(_ctx, _graveLocation.getTitle(), Toast.LENGTH_LONG);
//		// zmieniamy grawitację toastu
//		toast.setGravity(android.view.Gravity.TOP, 0, 36);
//		toast.show();
		LayoutInflater ll = LayoutInflater.from(_ctx);
		View popup = (LinearLayout)ll.inflate(R.layout.popup, map, false);
		
		((TextView) popup.findViewById(R.id.popup_text)).setText("Położenie grobu");
		MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT,
                _graveLocation.getPoint(),
                0,
               -_grave_marker.getIntrinsicHeight(),
                MapView.LayoutParams.BOTTOM_CENTER);
		popup.setOnTouchListener(popupTouch);
	    map.addView(popup, mapParams);
		
		return true;
	}
	OnTouchListener popupTouch = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			map.getController().animateTo(_graveLocation.getPoint());
			map.getController().setZoom(16);
			return false;
		}
	};
	
	
}
