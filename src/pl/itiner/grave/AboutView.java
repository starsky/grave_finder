package pl.itiner.grave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class AboutView extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.about);
		TextView text = (TextView) findViewById(R.id.about_description);
		text.setText(getResources().getString(R.string.description));
		w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.license:
				Intent intentLicenseView = new Intent(this.getApplicationContext(),LicenseView.class);
				startActivity(intentLicenseView);
				return true;
			case R.id.libraries:
				Intent intentLibrariesView = new Intent(this.getApplicationContext(),LibrariesView.class);
				startActivity(intentLibrariesView);
				return true;
		}
		return false;
	}

}
