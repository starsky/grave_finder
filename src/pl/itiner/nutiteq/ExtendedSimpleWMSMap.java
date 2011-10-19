package pl.itiner.nutiteq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.itiner.conversions.CoordinateConversion;

import android.util.Log;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.maps.SimpleWMSMap;

public class ExtendedSimpleWMSMap extends SimpleWMSMap {

	private double x,y;
	int conv_x, conv_y;
	public ExtendedSimpleWMSMap(String arg0, int arg1, int arg2, int arg3,
			String arg4, String arg5, String arg6, String arg7, String string, int x, int y) {
		super(another(x,y), arg1, arg2, arg3, arg4, arg5, arg6, arg7, string);
		// TODO Auto-generated constructor stubb
		this.x = x;
		this.y =y;
		
		//conv_y = Double.parseDouble(afterConv[1]);
	}
	
	
	public static String another(int conv_x,  int conv_y)
	{
		SimpleWMSMap tmpMap = new SimpleWMSMap("http://www.poznan.pl/tilecache/tilecache.cgi?",
		256, 1, 10,"plan_2177", "image/png",
		"", "GetMap", ""
		);	
		
		
		MapPos mpos = tmpMap.wgsToMapPos(new Point(conv_x,conv_y), 15);
		String path = tmpMap.buildPath(mpos.getX(), mpos.getY(), mpos.getZoom());
		 String [] patches = path.split("BBOX=[0-9]+(.[0-9]+)*");
		   Pattern p = Pattern.compile("BBOX=[0-9]+(.[0-9]+)*");
		   Matcher m = p.matcher(path);
		   String match = null;
		   while(m.find())
		   {
			   match = m.toMatchResult().group();
		   }
		   // String[] paths = path.("BBOX=[0-9]+(.[0-9]+)*");
		   
		    path = patches[0]+ match.replace(".","")+patches[1];
		    Log.i("",path);
		    return path;
	}

}
//SimpleWMSMap wms = new SimpleWMSMap(
//		"http://www.poznan.pl/tilecache/tilecache.cgi?",
//		256, 1, 10,"plan_2177", "image/png",
//		"", "GetMap", ""
//		);	