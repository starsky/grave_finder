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

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.itiner.fetch.GeoJSON;
import pl.itiner.grave.R;
import pl.itiner.model.Departed;
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
	private Image gps;
	private WgsPoint userLocation;
	private Place userPlace;
	private LocationManager locManager;
	private LocationListener locListener;
	private Departed departed;
	private OnMapElementListener elemListener = new OnMapElementListener() {

		@Override
		public void elementLeft(OnMapElement arg0) {
		}

		@Override
		public void elementEntered(OnMapElement arg0) {
		}

		@Override
		public void elementClicked(OnMapElement mapElem) {
			BalloonLabel tmp = (BalloonLabel) mapElem.getLabel();
			if (tmp.name.equals(getResources().getString(R.string.grave))) {
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
		gps = new Image(BitmapFactory.decodeResource(getResources(),
				R.drawable.dot));
		// Get death person data
		int id = getIntent().getExtras().getInt("id");
		departed = GeoJSON.getResults().get(id);
		// fill data header
		fillHeaderWithData();
		// setup position listeners
		locListener = new NutiteqLocationListener();
		locManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener);
		// setup map
		mapComponent = (BasicMapComponent) getLastNonConfigurationInstance();
		if (mapComponent == null) {
			createMapComponent();
		}
		MapView mapView = (MapView) findViewById(R.id.nutiteq_mapview);
		mapView.setMapComponent(mapComponent);
		setupZoom();
	}

	private void createMapComponent() {
		final Resources res = getResources();
		final int initialZoom = res.getInteger(R.integer.initial_zoom);
		final String mapKey = res.getString(R.string.nutiteq_key);
		final WgsPoint center = new WgsPoint(Double.parseDouble(res
				.getString(R.string.poznan_centre_lon)), Double.parseDouble(res
				.getString(R.string.poznan_centre_lat)));
		final Image graveImg = new Image(BitmapFactory.decodeResource(
				getResources(), R.drawable.graveloc));
		final BalloonLabel graveLocationLabel = new BalloonLabel(
				res.getString(R.string.grave), res.getString(R.string.zoom_map));
		final BalloonLabel userLocationLabel = new BalloonLabel(
				res.getString(R.string.your_location), "");
		final WgsPoint graveLoc = new WgsPoint(departed.getLocation()
				.getLongitude(), departed.getLocation().getLatitude());
		final Place gravePlace = new Place(0, graveLocationLabel, graveImg,
				graveLoc);

		mapComponent = new BasicMapComponent(mapKey, new AppContext(this), 1,
				1, center, initialZoom);
		map = getMap();
		mapComponent.setMap(map);
		mapComponent.setSmoothZoom(true);
		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		mapComponent.startMapping();

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
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener);
	}

	private void fillHeaderWithData() {
		final TextView mapSurnameName;
		final TextView mapBirthDate;
		final TextView mapDeathDate;
		final TextView mapFunrealDate;
		final TextView mapCementry;
		final TextView mapRow;
		final TextView mapQuater;
		final TextView mapField;
		final String[] cementeries = getResources().getStringArray(
				R.array.necropolises);

		mapSurnameName = (TextView) findViewById(R.id.map_surname_name);
		mapSurnameName
				.setText(departed.getName() + " " + departed.getSurname());

		mapBirthDate = (TextView) findViewById(R.id.map_value_dateBirth);
		mapBirthDate.setText(formatDate(departed.getBirthDate()));

		mapDeathDate = (TextView) findViewById(R.id.map_value_dateDeath);
		mapDeathDate.setText(formatDate(departed.getDeathDate()));

		mapFunrealDate = (TextView) findViewById(R.id.map_value_dateFunreal);
		mapFunrealDate.setText(formatDate(departed.getBurialDate()));

		mapField = (TextView) findViewById(R.id.map_field_value);
		mapField.setText(departed.getField());

		mapRow = (TextView) findViewById(R.id.map_row_value);
		mapRow.setText(departed.getRow());

		mapQuater = (TextView) findViewById(R.id.map_quater_value);
		mapQuater.setText(departed.getQuater());

		mapCementry = (TextView) findViewById(R.id.map_value_cementry);
		String cm_name = cementeries[departed.getCmId().intValue()];
		mapCementry.setText(cm_name);
	}

	private static final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

	private static String formatDate(Date d) {
		if (d == null) {
			return null;
		}
		return f.format(d);
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

	private class NutiteqLocationListener implements LocationListener {
		private BalloonLabel userLocationLabel = new BalloonLabel(
				getResources().getString(R.string.your_location), "");

		@Override
		public void onLocationChanged(Location loc) {
			userLocation = new WgsPoint(loc.getLongitude(), loc.getLatitude());
			if (userLocation != null) {
				try {
					mapComponent.removePlace(userPlace);
				} catch (NullPointerException nue) { /* ignored */
				}
				userPlace = new Place(0, userLocationLabel, gps, userLocation);
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
