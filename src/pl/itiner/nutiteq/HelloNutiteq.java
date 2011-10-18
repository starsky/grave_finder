package pl.itiner.nutiteq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.itiner.grave.R;
import sun.misc.Regexp;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ZoomControls;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.android.MapView;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
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
		
//		MapPos pos = wms.wgsToMapPos(new Point(6424660,5807886), 15);
//	    String path = wms.buildPath(pos.getX(),pos.getY(),pos.getZoom());
//	    String [] patches = path.split("BBOX=[0-9]+(.[0-9]+)*");
//		   Pattern p = Pattern.compile("BBOX=[0-9]+(.[0-9]+)*");
//		   Matcher m = p.matcher(path);
//		   String match = null;
//		   while(m.find())
//		   {
//			   match = m.toMatchResult().group();
//		   }
//		   // String[] paths = path.("BBOX=[0-9]+(.[0-9]+)*");
//		   
////		    path = patches[0]+ match.replace(".","")+patches[1];
//	    wms = new SimpleWMSMap(
//	    		path,
//	    		256, 1, 10,"plan_2177", "image/png",
//	    		"", "GetMap", ""
//	    		);	
	    ExtendedSimpleWMSMap ewms = new ExtendedSimpleWMSMap("",
	    		256, 1, 10,"plan_2177", "image/png",
	    		"", "GetMap", ""
	    		);
	    ewms.toString();
		//String path = wms.buildPath(13600, 95000, 10); 
	 //   Regexp regexp = new Regexp("BBOX=[0-9]+(.[0-9]+)*");
	  
		mapComponent.setMap(ewms);
		mapComponent.setPanningStrategy(new ThreadDrivenPanning());	
		mapComponent.startMapping();
	    MapView mapView = (MapView)findViewById(R.id.nutiteq_mapview);
        mapView.setMapComponent(mapComponent);
		       
		}
	}
}
