package pl.itiner.nutiteq;

import pl.itiner.conversions.CoordinateConversion;
import pl.itiner.grave.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ZoomControls;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.maps.SimpleWMSMap;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.AppContext;

public class HelloNutiteq extends Activity{
	private BasicMapComponent mapComponent;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nutiteq);
		Bundle b = getIntent().getExtras();
		if(b!= null)
		{
			double x = b.getDouble("x");
			double y = b.getDouble("y");
			/**
			 * http://www.poznan.pl/tilecache/
			 * tilecache.cgi?service=wms&version=1.1.1&
			 * request=GetCapabilities&tiled=true
			 */
			ZoomControls zoomControls = (ZoomControls)findViewById(R.id.zoomcontrols);
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
		mapComponent = new BasicMapComponent(
				"TYPE_YOUR_KEY", 
				new AppContext(this), //52.414579,16.997545
				1, 1, new WgsPoint(y,x), 10
		);
		SimpleWMSMap wms = new SimpleWMSMap(
		"http://www.poznan.pl/tilecache/tilecache.cgi?",
		256, 1, 10,"plan_2177", "image/png",
		"", "GetMap", ""
		);	

		CoordinateConversion cc = new CoordinateConversion();
		String [] afterConv = cc.latLon2UTM(x, y).split(" ");
		int conv_x, conv_y;
		conv_x = Integer.parseInt(afterConv[2],10);
		conv_y = Integer.parseInt(afterConv[3],10);
	    ExtendedSimpleWMSMap ewms = new ExtendedSimpleWMSMap("",
	    		256, 1, 10,"plan_2177", "image/png",
	    		"", "GetMap", "", conv_x, conv_y
	    		);
	    ewms.toString();
	  
		mapComponent.setMap(ewms);
		mapComponent.setPanningStrategy(new ThreadDrivenPanning());	
		mapComponent.startMapping();
	    MapView mapView = (MapView)findViewById(R.id.nutiteq_mapview);
        mapView.setMapComponent(mapComponent);
		       
		}
	}
}
