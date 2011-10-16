package pl.itiner.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * LocationListener
 * @author mihoo
 *
 */
public class MyLocationListener implements LocationListener {
	   
	Location mobileLocation;
	@Override
	public void onStatusChanged(String provider, int status,
			Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mobileLocation = location;
		
		Log.i("LOCATION","LONG: "+location.getLongitude()+ " LAT:"+location.getLatitude());
	}

}