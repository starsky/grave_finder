package pl.itiner.nutiteq;

import pl.itiner.grave.GeoJSON;
import pl.itiner.grave.R;
import pl.itiner.grave.ResultList;
import pl.itiner.models.Deathman;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.mgmaps.utils.Tools;
import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.MapTile;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceIcon;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.location.LocationMarker;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.NutiteqLocationMarker;
import com.nutiteq.location.providers.AndroidGPSProvider;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTileOverlay;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class NutiteqMap extends Activity {
	private BasicMapComponent mapComponent;
	private GeoMap map;
	private boolean onRetainCalled;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onRetainCalled = false;
		setContentView(R.layout.nutiteq);
		Resources res = getResources();
		final int initialZoom = res.getInteger(R.integer.initial_zoom);
		final String mapKey = res.getString(R.string.nutiteq_key);
		final WgsPoint center = new WgsPoint(Double.parseDouble(res
				.getString(R.string.poznan_centre_lon)), Double.parseDouble(res
				.getString(R.string.poznan_centre_lat)));

		mapComponent = new BasicMapComponent(mapKey, new AppContext(this), 1,
				1, center, initialZoom);
		map = getMap();
		mapComponent.setMap(map);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			double x = b.getDouble("x");
			double y = b.getDouble("y");
			int id = b.getInt("id");
			WgsPoint graveLoc = new WgsPoint(y, x);
			mapComponent.addPlace(new Place(0, new PlaceLabel("Grób",
					PlaceLabel.DISPLAY_TOP), new Image(BitmapFactory
					.decodeResource(getResources(), R.drawable.graveloc)),
					graveLoc));
			fillHeaderWithData(id);
		}

		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		mapComponent.startMapping();
		MapView mapView = (MapView) findViewById(R.id.nutiteq_mapview);
		mapView.setMapComponent(mapComponent);

		setupZoom();
		setupUserLocation();

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

		Deathman tmp = GeoJSON.dList.get(id);
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

	private void setupUserLocation() {
		final LocationSource locationSource = new AndroidGPSProvider(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				1000L);
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.gps_marker);
		final LocationMarker marker = new NutiteqLocationMarker(new PlaceIcon(
				Image.createImage(icon), icon.getWidth(), icon.getHeight()),
				3000, true);
		locationSource.setLocationMarker(marker);
		mapComponent.setLocationSource(locationSource);
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
			mapComponent = null;
		}
	}
}
