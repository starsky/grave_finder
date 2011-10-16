package pl.itiner.map;

import pl.itiner.grave.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class UserLocationOverlay extends ItemizedOverlay<OverlayItem>
{


	OverlayItem _userLocation;
	Context _ctx;
	Drawable _user_marker;
	
	public UserLocationOverlay(Drawable defaultMarker, Context ctx, GeoPoint userLocation) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
		_ctx = ctx;
		//boundCenter(defaultMarker);
		_userLocation = new OverlayItem(
				userLocation, 
						"Ja", 
						"Jestem tutaj"
				);
		
		
//		_grave_marker = _ctx.getResources().getDrawable(R.drawable.pin);
//		
//		_grave_marker.setBounds(0, 0, _grave_marker.getIntrinsicHeight(), _grave_marker.getIntrinsicWidth());
//		//boundCenter(_grave_marker);
		
//		_graveLocation.setMarker(_grave_marker);
//	
	_user_marker = _ctx.getResources().getDrawable(R.drawable.user_location);
	_user_marker.setBounds(0, 0, _user_marker.getIntrinsicHeight(), _user_marker.getIntrinsicWidth());
	_userLocation.setMarker(_user_marker);
//		boundCenter(_user_marker);
//		}
//		catch(NullPointerException e)
//		{
//			Toast.makeText(_ctx.getApplicationContext(), "Nie mogę ustalić Twojej lokalizacji", Toast.LENGTH_SHORT).show();
////			boundCenter(_grave_marker);
//		}
		populate();
	}

	public boolean updateLocation(Location userLocation)
	{		
		return true;
	}
	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return _userLocation;
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

//		// ---translate the GeoPoint to screen pixels---
		Point screenPts = new Point();		mapView.getProjection().toPixels(_userLocation.getPoint(), screenPts);
//
//		// ---add the marker---
		Bitmap bmp = BitmapFactory.decodeResource(_ctx.getResources(),
				R.drawable.user_location);
//		
		canvas.drawBitmap(bmp, screenPts.x, screenPts.y -bmp.getHeight(), null);
//		if(_userLocation.getPoint() != null)
//		{
//			Bitmap bmp1 = BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.user_location);
//			mapView.getProjection().toPixels(_userLocation.getPoint(), screenPts);
//			canvas.drawBitmap(bmp1, screenPts.x, screenPts.y - bmp1.getHeight(), null);
//		}
		
	}
	@Override
	protected boolean onTap(int index) {
		
		Toast toast = Toast.makeText(_ctx, _userLocation.getTitle(), Toast.LENGTH_LONG);
		// zmieniamy grawitację toastu
		toast.setGravity(android.view.Gravity.TOP, 0, 36);
		toast.show();
		
		return true;
	}
	
	
}
