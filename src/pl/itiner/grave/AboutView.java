package pl.itiner.grave;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutView  extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);		
		setContentView(R.layout.about);
		w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
			    R.drawable.icon);
		
	
	}	

}
