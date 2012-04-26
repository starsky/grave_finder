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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pl.itiner.models.Departed;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TODO Zasięg widoczności zmiennych On clickView - skrócić
 * 
 */
public class GForm extends Activity {
	/** Called when the activity is first created. */
	public static final int PROGRESSBAR = 1;
	public static final int PROGRESSBAR_GONE = 2;
	public static final int TOAST = 3;

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
	private Button deathDate;
	private Button burialDate;
	private Button birthDate;
	private LinearLayout dataChooseHeader;
	private int whichDate = DEATH_DATE;
	private Button find;
	public static Drawable white;
	public static Drawable dark;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		progressBar = (ProgressBar) findViewById(R.id.progressbar_titlebar);
		dataChooseHeader = (LinearLayout) findViewById(R.id.date_choose_header);

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

		deathDate = (Button) findViewById(R.id.death_date);
		burialDate = (Button) findViewById(R.id.burial_date);
		birthDate = (Button) findViewById(R.id.birth_date);
		deathDate.setOnClickListener(onBtnDateClick);
		burialDate.setOnClickListener(onBtnDateClick);
		birthDate.setOnClickListener(onBtnDateClick);

	}

	private OnCheckedChangeListener onCheckedDateVisiable = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				dataChooseHeader.setVisibility(View.VISIBLE);
				checkBoxDate.setText("");
			} else {
				checkBoxDate.setText(R.string.additional_query_params);
				dataChooseHeader.setVisibility(View.GONE);
			}
		}
	};

	private OnClickListener onBtnDateClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == deathDate.getId()) {
				whichDate = DEATH_DATE;
			} else if (v.getId() == burialDate.getId()) {
				whichDate = BURIAL_DATE;
			} else if (v.getId() == birthDate.getId()) {
				whichDate = BIRTH_DATE;
			}
			birthDate.invalidate();
			deathDate.invalidate();
			burialDate.invalidate();
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
					AboutView.class);
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

		if (checkBoxDate.isChecked()) {
			if (datePicker.isFocused()) {
				datePicker.clearFocus();
			}
			Date tmpDate;
			tmpDate = new GregorianCalendar(datePicker.getYear(),
					datePicker.getMonth(), datePicker.getDayOfMonth())
					.getTime();
			switch (whichDate) {
			case DEATH_DATE:
				deathDate = tmpDate;
				break;
			case BIRTH_DATE:
				birthDate = tmpDate;
			case BURIAL_DATE:
				burialDate = tmpDate;
			}
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
		return true; // TODO Poprawić
	}

	private Runnable th_searchGraves = new Runnable() {

		@Override
		public void run() {
			Message dbmsg = activityUIHandler.obtainMessage();

			dbmsg.what = PROGRESSBAR;
			activityUIHandler.sendMessage(dbmsg);
			runQuery();
			if (GeoJSON.getResults().size() != 0) {
				Intent i;
				i = new Intent(getApplicationContext(), ResultList.class);
				startActivity(i);
			} else {
				dbmsg = activityUIHandler.obtainMessage();

				dbmsg.what = TOAST;
				activityUIHandler.sendMessage(dbmsg);
			}
			dbmsg = activityUIHandler.obtainMessage();

			dbmsg.what = PROGRESSBAR_GONE;
			activityUIHandler.sendMessage(dbmsg);

		}
	};

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

	Handler activityUIHandler = new Handler() {
		// this method will handle the calls from other threads.
		public void handleMessage(Message msg) {
			// Bundle b = msg.getData();
			switch (msg.what) {

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

}