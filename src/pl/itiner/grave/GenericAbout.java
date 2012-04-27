package pl.itiner.grave;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class GenericAbout extends Activity {

	public static final String DESC_ID = "DESC_ID";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int contentId = getIntent().getExtras().getInt(DESC_ID);
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.about);
		TextView text = (TextView) findViewById(contentId);
		text.setText(getResources().getString(R.string.description));
		w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
	}

}
