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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
	public static List<Departed> dead = new ArrayList<Departed>();
	public static final int PROGRESSBAR = 1;
	public static final int PROGRESSBAR_GONE = 2;
	public static final int TOAST = 3;
	Spinner necropolis;
	ConnectivityManager cm;
	ProgressBar progressBar;
	DatePicker datePicker;
	CheckBox checkBoxDate;
	EditText editTextSurname;
	EditText editTextName;
	TextView deathDate;
	TextView burialDate;
	TextView birthDate;
	LinearLayout ll_dataChooseHeader;
	RelativeLayout ll;
	int whichDate = 1; // 0 = deathDate was chosen, 1 = burialDate, 2 =
						// birthDate
	Button find;
	public static Drawable white;
	public static Drawable dark;

	OnCheckedChangeListener onCheckedDateVisiable = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				datePicker.setVisibility(View.VISIBLE);
				ll_dataChooseHeader.setVisibility(View.VISIBLE);
				checkBoxDate.setText("");
			} else if (!isChecked) {
				checkBoxDate.setText("Dodatkowe opcje wyszukiwania");
				datePicker.setVisibility(View.GONE);
				ll_dataChooseHeader.setVisibility(View.GONE);
			}
		}
	};

	OnClickListener onTextViewDateClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!checkBoxDate.isChecked())
				checkBoxDate.setChecked(true);
			if (v.getId() == deathDate.getId()) {

				whichDate = 0;
				deathDate.setBackgroundDrawable(white);
				deathDate.setTextColor(Color.BLACK);

				burialDate.setBackgroundDrawable(dark);
				burialDate.setTextColor(Color.WHITE);

				birthDate.setBackgroundDrawable(dark);
				birthDate.setTextColor(Color.WHITE);

				birthDate.invalidate();
				deathDate.invalidate();
				burialDate.invalidate();

			} else if (v.getId() == burialDate.getId()) {
				whichDate = 1;
				deathDate.setBackgroundDrawable(dark);
				deathDate.setTextColor(Color.WHITE);

				burialDate.setBackgroundDrawable(white);
				burialDate.setTextColor(Color.BLACK);

				birthDate.setBackgroundDrawable(dark);
				birthDate.setTextColor(Color.WHITE);

				birthDate.invalidate();
				deathDate.invalidate();
				burialDate.invalidate();
			} else if (v.getId() == birthDate.getId()) {
				whichDate = 2;
				deathDate.setBackgroundDrawable(dark);
				deathDate.setTextColor(Color.WHITE);

				burialDate.setBackgroundDrawable(dark);
				burialDate.setTextColor(Color.WHITE);

				birthDate.setBackgroundDrawable(white);
				birthDate.setTextColor(Color.BLACK);

				birthDate.invalidate();
				deathDate.invalidate();
				burialDate.invalidate();
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
					AboutView.class);
			startActivity(intentAboutView);
			break;
		}
		return true;
	}

	public void runQuery() {
		String tmpNecropolisId = "";
		if (necropolis.getSelectedItemId() != 0) {
			tmpNecropolisId = "" + necropolis.getSelectedItemId();
		}

		String tmpName = "";
		String tmpSurname = "";
		try {
			tmpName = URLEncoder.encode(editTextName.getText().toString()
					.toLowerCase().trim(), "UTF-8");
			tmpSurname = URLEncoder.encode(editTextSurname.getText().toString()
					.toLowerCase().trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tmpDate = "";
		GeoJSON gJSON;
		if (checkBoxDate.isChecked()) {
			if (datePicker.isFocused()) {
				datePicker.clearFocus();
			}
			tmpDate = datePicker.getYear() + "-" + (datePicker.getMonth() + 1)
					+ "-" + datePicker.getDayOfMonth();
			gJSON = new GeoJSON(tmpNecropolisId, tmpName, tmpSurname, tmpDate,
					whichDate);
		} else {
			gJSON = new GeoJSON(tmpNecropolisId, tmpName, tmpSurname, tmpDate,
					-1);
		}

		dead = gJSON.parseJSON(this, gJSON.getJSON(this));
	}

	public boolean isOnline() {

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	Runnable th_searchGraves = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message dbmsg = activityUIHandler.obtainMessage();

			dbmsg.what = PROGRESSBAR;
			activityUIHandler.sendMessage(dbmsg);
			runQuery();
			if (dead.size() != 0) {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		white = getApplicationContext().getResources().getDrawable(
				android.R.drawable.editbox_dropdown_light_frame);
		dark = getApplicationContext().getResources().getDrawable(
				android.R.drawable.editbox_dropdown_dark_frame);
		/**
		 * TITLEBAR PROGRESSBAR
		 */
		ll = (RelativeLayout) findViewById(R.id.all);
		progressBar = (ProgressBar) findViewById(R.id.progressbar_titlebar);

		ll_dataChooseHeader = (LinearLayout) findViewById(R.id.date_choose_header);

		/**
		 * FIND BUTTON
		 */

		find = (Button) findViewById(R.id.find_btn);
		find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				if (isOnline()) {
					new Thread(th_searchGraves).start();
				}
				if (!isOnline()) {
					Toast.makeText(
							getApplicationContext(),
							"Brak dostępu do Internetu\nSprawdź swoje połączenie",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		/**
		 * SPINNER
		 */
		necropolis = (Spinner) findViewById(R.id.necropolis_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.necropolises,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		necropolis.setAdapter(adapter);

		/**
		 * DATEPICKER
		 */
		datePicker = (DatePicker) findViewById(R.id.datepicker);

		/**
		 * CHECKBOX
		 */
		checkBoxDate = (CheckBox) findViewById(R.id.checkbox);
		checkBoxDate.setOnCheckedChangeListener(onCheckedDateVisiable);

		/**
		 * EDITEXT
		 * 
		 */
		editTextSurname = (EditText) findViewById(R.id.surname);
		editTextSurname.setSelected(false);

		editTextName = (EditText) findViewById(R.id.name);
		editTextName.setSelected(false);
		/**
		 * TEXTBOXES
		 */
		deathDate = (TextView) findViewById(R.id.death_date);

		burialDate = (TextView) findViewById(R.id.burial_date);
		birthDate = (TextView) findViewById(R.id.birth_date);

		deathDate.setOnClickListener(onTextViewDateClick);
		burialDate.setOnClickListener(onTextViewDateClick);
		birthDate.setOnClickListener(onTextViewDateClick);

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