package pl.itiner.grave;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class LicenseView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.about);
		TextView text = (TextView) findViewById(R.id.about_description);
		text.setText(getResources().getString(R.string.license_txt));
		w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
	}

}
