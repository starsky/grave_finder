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

package pl.itiner.grave;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import pl.itiner.fetch.GeoJSON;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class GForm extends Activity {
	/** Called when the activity is first created. */
	private static final int PROGRESSBAR = 1;
	private static final int PROGRESSBAR_GONE = 2;
	private static final int TOAST = 3;
	private static final int RESULTS_RECEIVED = 0;

	private static final int NONE_DATE = 3;
	private static final int BURIAL_DATE = 1;
	private static final int BIRTH_DATE = 2;
	private static final int DEATH_DATE = 0;

	private Spinner necropolis;
	private ConnectivityManager cm;
	private ProgressBar progressBar;
	private DatePicker datePicker;
	private CheckBox checkBoxDate;
	private EditText editTextSurname;
	private EditText editTextName;
	private int whichDate = NONE_DATE;
	private Button find;
	private RadioGroup dateGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		progressBar = (ProgressBar) findViewById(R.id.progressbar_titlebar);

		find = (Button) findViewById(R.id.find_btn);
		setSearchClickListener();

		necropolis = (Spinner) findViewById(R.id.necropolis_spinner);
		necropolis.setAdapter(getNecropolisSpinnerAdapter());

		datePicker = (DatePicker) findViewById(R.id.datepicker);

		checkBoxDate = (CheckBox) findViewById(R.id.checkbox);
		checkBoxDate.setOnCheckedChangeListener(onCheckedDateVisiable);

		editTextSurname = (EditText) findViewById(R.id.surname);
		editTextSurname.setSelected(false);
		editTextName = (EditText) findViewById(R.id.name);
		editTextName.setSelected(false);

		dateGroup = (RadioGroup) findViewById(R.id.dates_group);
		dateGroup.setOnCheckedChangeListener(onCheckDateType);
	}

	private OnCheckedChangeListener onCheckedDateVisiable = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				checkBoxDate.setText("");
				dateGroup.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.VISIBLE);
			} else {
				checkBoxDate.setText(R.string.additional_query_params);
				dateGroup.setVisibility(View.GONE);
				datePicker.setVisibility(View.GONE);
				whichDate = NONE_DATE;
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener onCheckDateType = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.birth_date:
				whichDate = BIRTH_DATE;
				break;
			case R.id.death_date:
				whichDate = DEATH_DATE;
				break;
			case R.id.burial_date:
				whichDate = BURIAL_DATE;
				break;
			}
		}
	};

	private Handler activityUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case RESULTS_RECEIVED:
				Intent i;
				i = new Intent(GForm.this, ResultList.class);
				startActivity(i);
				break;
			case PROGRESSBAR:
				progressBar.setVisibility(View.VISIBLE);
				break;
			case PROGRESSBAR_GONE:
				progressBar.setVisibility(View.GONE);
				break;
			case TOAST:
				Toast.makeText(getApplicationContext(), "\nBrak wyników\n",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	private Runnable th_searchGraves = new Runnable() {

		@Override
		public void run() {
			{
				Message dbmsg = Message.obtain();
				dbmsg.what = PROGRESSBAR;
				activityUIHandler.sendMessage(dbmsg);
			}
			runQuery();
			{
				Message dbmsg = Message.obtain();
				dbmsg.what = PROGRESSBAR_GONE;
				activityUIHandler.sendMessage(dbmsg);
			}
			if (GeoJSON.getResults().size() != 0) {
				Message dbmsg = Message.obtain();
				dbmsg.what = RESULTS_RECEIVED;
				activityUIHandler.sendMessage(dbmsg);
			} else {
				Message dbmsg = Message.obtain();
				dbmsg.what = TOAST;
				activityUIHandler.sendMessage(dbmsg);
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAbout:
			Intent intentAboutView = new Intent(this.getApplicationContext(),
					About.class);
			intentAboutView.putExtra(GenericAbout.DESC_ID,R.string.description);
			startActivity(intentAboutView);
			break;
		}
		return true;
	}

	private void runQuery() {
		Long tmpNecropolisId = necropolis.getSelectedItemId() != 0 ? necropolis
				.getSelectedItemId() : null;
		Date deathDate = null;
		Date burialDate = null;
		Date birthDate = null;

		if (datePicker.isFocused()) {
			datePicker.clearFocus();
		}

		Date tmpDate = new GregorianCalendar(datePicker.getYear(),
				datePicker.getMonth(), datePicker.getDayOfMonth()).getTime();
		switch (whichDate) {
		case DEATH_DATE:
			deathDate = tmpDate;
			break;
		case BIRTH_DATE:
			birthDate = tmpDate;
		case BURIAL_DATE:
			burialDate = tmpDate;
		}
		try {
			GeoJSON.executeQuery(tmpNecropolisId, editTextName.getText()
					.toString(), editTextSurname.getText().toString(),
					deathDate, birthDate, burialDate);
		} catch (IOException e) {
			Toast.makeText(this, R.string.query_io_err, Toast.LENGTH_LONG);
			Log.e("GForm", "IO Err", e);
		}
	}

	private boolean isOnline() {

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private ArrayAdapter<CharSequence> getNecropolisSpinnerAdapter() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.necropolises,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	private void setSearchClickListener() {
		find.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				if (isOnline()) {
					new Thread(th_searchGraves).start();
				}
				if (!isOnline()) {
					Toast.makeText(getApplicationContext(),
							R.string.check_conn, Toast.LENGTH_SHORT).show();
				}

			}
		});
	}
}