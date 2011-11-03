package pl.itiner.nutiteq;

import pl.itiner.grave.R;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ZoomControls;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.MapTile;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceLabel;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.MapTileOverlay;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;
import com.nutiteq.wrappers.Image;

public class NutiteqMap extends Activity {
	private BasicMapComponent mapComponent;
	private boolean onRetainCalled;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onRetainCalled = false;
		setContentView(R.layout.nutiteq);
		mapComponent = new BasicMapComponent(
				"API_KEY",
				new AppContext(this), 1, 1, new WgsPoint(16.910834, 52.403597),
				1);
		mapComponent.addPlace(new Place(0, new PlaceLabel("Centr"), new Image(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.graveloc)), 16.910834, 52.403597));
		final PoznanAPIMap map = getMap();
		map.addTileOverlay(new MapTileOverlay() {
			
			@Override
			public String getOverlayTileUrl(MapTile tile) {
				String baseUrl = map.createBaseUrl("http://www-bckp.city.poznan.pl/tilecache/tilecache.cgi?","poznan_cmentarze_2177"); 
				return map.buildPath(tile.getX(), tile.getY(), tile.getZoom(),baseUrl);
				
			}
		});
		mapComponent.setMap(map);

		mapComponent.setPanningStrategy(new ThreadDrivenPanning());
		mapComponent.startMapping();
		// get the mapview that was defined in main.xml
		MapView mapView = (MapView) findViewById(R.id.nutiteq_mapview);
		// mapview requires a mapcomponent
		mapView.setMapComponent(mapComponent);

		ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		// set zoomcontrols listeners to enable zooming
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
		PoznanAPIMap wms = new PoznanAPIMap(
				"http://www-bckp.city.poznan.pl/tilecache/tilecache.cgi?", 256,
				1, 11, "plan_2177", "image/png", "", "GetMap", "",
				new double[] { 50, 25, 12.5, 6.25, 3.125, 1.5625, 0.78125,
						0.390625, 0.1953125, 0.09765625, 0.048828125,
						0.0244140625 }, 6411860, 5795086);
		return wms;
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
