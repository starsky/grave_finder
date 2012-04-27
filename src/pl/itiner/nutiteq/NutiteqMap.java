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

package pl.itiner.nutiteq;

import pl.itiner.grave.GeoJSON;
import pl.itiner.grave.Log;
import pl.itiner.grave.R;
import pl.itiner.grave.ResultList;
import pl.itiner.models.Departed;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.mgmaps.utils.Tools;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.MapTile;
import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTileOverlay;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class NutiteqMap extends Activity {
	private BasicMapComponent mapComponent;
	private GeoMap map;
	private boolean onRetainCalled;
	public boolean locationSet = false;
	public Image gps; 
	public Image grave;
	public WgsPoint userLocation;
	public Place userPlace;
	public Place gravePlace;
	public Location mobileLocation;
	public LocationManager locManager;
	public LocationListener locListener;
	int initialZoom;
	String mapKey;
	WgsPoint center;
	OnMapElementListener elemListener = new OnMapElementListener() {
		
		@Override
		public void elementLeft(OnMapElement arg0) {
		}
		
		@Override
		public void elementEntered(OnMapElement arg0) {
		}
		
		@Override
		public void elementClicked(OnMapElement mapElem) {
			BalloonLabel tmp = (BalloonLabel) mapElem.getLabel();
			if(tmp.name.equals("Grób")){
				mapComponent.setMiddlePoint(mapElem.getPoints()[0]);
				mapComponent.zoomIn();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onRetainCalled = false;
		setContentView(R.layout.nutiteq);
		Resources res = getResources();
		initialZoom = res.getInteger(R.integer.initial_zoom);
		mapKey = res.getString(R.string.nutiteq_key);
		center = new WgsPoint(Double.parseDouble(res
				.getString(R.string.poznan_centre_lon)), Double.parseDouble(res
				.getString(R.string.poznan_centre_lat)));
		gps = new Image(BitmapFactory.decodeResource(getResources(), R.drawable.dot));
		grave = new Image(BitmapFactory.decodeResource(getResources(), R.drawable.graveloc));
		Bundle b = getIntent().getExtras();
		WgsPoint graveLoc = null;
		if (b != null) {
			double x = b.getDouble("x");
			double y = b.getDouble("y");
			int id = b.getInt("id");
			graveLoc = new WgsPoint(y, x);
			fillHeaderWithData(id);
		}
		locListener = new NutiteqLocationListener();
		locManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locListener);
		mapComponent = (BasicMapComponent) getLastNonConfigurationInstance();
		if (mapComponent == null) {
			createMapComponent(graveLoc);
		}
		MapView mapView = (MapView) findViewById(R.id.nutiteq_mapview);
		mapView.setMapComponent(mapComponent);
		setupZoom();
	}

	private void createMapComponent(WgsPoint graveLoc) {
		mapComponent = new BasicMapComponent(mapKey, new AppContext(this),
				1, 1, center, initialZoom);
		map = getMap();
		mapComponent.setMap(map);
		mapComponent.setSmoothZoom(true);
		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		mapComponent.startMapping();

		BalloonLabel graveLocationLabel = new BalloonLabel("Grób",
				"Przybliż mapę");
		BalloonLabel userLocationLabel = new BalloonLabel("Twoja pozycja",
				"");
		gravePlace = new Place(0, graveLocationLabel, grave, graveLoc);
		mapComponent.addPlace(gravePlace);

		userPlace = new Place(0, userLocationLabel, gps, userLocation);
		mapComponent.setOnMapElementListener(elemListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		locManager.removeUpdates(locListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
	}

	private void fillHeaderWithData(int id) {
		final TextView mapSurnameName;
		final TextView mapBirthDate;
		final TextView mapDeathDate;
		final TextView mapFunrealDate;
		final TextView mapCementry;
		final TextView mapRow;
		final TextView mapQuater;
		final TextView mapField;

		Departed tmp = GeoJSON.getResults().get(id);
		mapSurnameName = (TextView) findViewById(R.id.map_surname_name);
		mapSurnameName.setText(tmp.getName() + " " + tmp.getSurname());

		mapBirthDate = (TextView) findViewById(R.id.map_value_dateBirth);

		mapBirthDate.setText(tmp.getDate_birth());

		mapDeathDate = (TextView) findViewById(R.id.map_value_dateDeath);
		mapDeathDate.setText(tmp.getDeath_date());

		mapFunrealDate = (TextView) findViewById(R.id.map_value_dateFunreal);
		mapFunrealDate.setText(tmp.getBurial_date());

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
	}

	private void setupZoom() {
		ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomIn();
			}
		});
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				mapComponent.zoomOut();
			}
		});
	}

	private PoznanAPIMap getMap() {
		Resources res = getResources();
		final String baseUrl = res.getString(R.string.base_url);
		final int tileSize = res.getInteger(R.integer.tile_size);
		final double[] resolutions = createResolutionsArray(res
				.getStringArray(R.array.resolutions));
		final int minZoom = res.getInteger(R.integer.min_zoom);
		final int maxZoom = minZoom + resolutions.length - 1;
		final String layerName = res.getString(R.string.base_layer_name);
		final String gravesLayer = res.getString(R.string.graves_layer_name);
		final String imageType = res.getString(R.string.png_type);
		final String getString = res.getString(R.string.map_get_string);
		final String copyrightTxt = res.getString(R.string.map_copyright_txt);
		final int minEpsgX = res.getInteger(R.integer.epsg_min_x);
		final int minEpsgY = res.getInteger(R.integer.epsg_min_y);

		PoznanAPIMap mainMap = new PoznanAPIMap(baseUrl, tileSize, minZoom,
				maxZoom, layerName, imageType, "", getString, copyrightTxt,
				resolutions, minEpsgX, minEpsgY);
		mainMap.addTileOverlay(new MapTileOverlay() {
			@Override
			public String getOverlayTileUrl(MapTile tile) {
				String url = tile.getIDString();
				url = url.replaceFirst(Tools.urlEncode(layerName),
						Tools.urlEncode(gravesLayer));
				return url;
			}
		});

		return mainMap;
	}

	private static double[] createResolutionsArray(String[] stringArray) {
		double[] arr = new double[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			arr[i] = Double.parseDouble(stringArray[i]);
		}
		return arr;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		onRetainCalled = true;
		return mapComponent;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!onRetainCalled) {
			mapComponent.stopMapping();
		}
	}
	public class NutiteqLocationListener implements LocationListener
	{
		BalloonLabel userLocationLabel = new BalloonLabel("Twoja pozycja","");
		@Override
		public void onLocationChanged(Location loc) {
			Log.i("userLocation","LAT:"+loc.getLatitude()+" LONG:"+loc.getLongitude());
			userLocation = new WgsPoint(loc.getLongitude(), loc.getLatitude());
			if(userLocation != null)
			{
					try {
					mapComponent.removePlace(userPlace);
					}catch(NullPointerException nue)
					{ /* ignored*/}					
					 userPlace = new Place(0, userLocationLabel, gps,
								userLocation);
					mapComponent.addPlace(userPlace);
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
		
	}
}
