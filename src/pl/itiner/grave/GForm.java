package pl.itiner.grave;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.itiner.models.Deathman;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GForm extends Activity {
    /** Called when the activity is first created. */
	public static ArrayList <Deathman> dead = new ArrayList<Deathman>();
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
	int whichDate = 1; //0 = deathDate was chosen, 1 = burialDate, 2 = birthDate
	Button find;
	OnCheckedChangeListener onCheckedDateVisiable = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked)
			{
				datePicker.setVisibility(View.VISIBLE);
				checkBoxDate.setText("");
			}
			else if(!isChecked)
			{	checkBoxDate.setText("Pokaż wybór daty");
				datePicker.setVisibility(View.GONE);
			}
		}
	};
	
	OnClickListener onTextViewDateClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == deathDate.getId())
			{
				whichDate = 0;
				deathDate.setBackgroundColor(Color.WHITE);
				deathDate.setTextColor(Color.BLACK);
				
				burialDate.setBackgroundColor(Color.BLACK);
				burialDate.setTextColor(Color.WHITE);
				
				birthDate.setBackgroundColor(Color.BLACK);
				birthDate.setTextColor(Color.WHITE);
				
			}
			else if(v.getId() == burialDate.getId())
			{
				whichDate =1;
				deathDate.setBackgroundColor(Color.BLACK);
				deathDate.setTextColor(Color.WHITE);
				
				burialDate.setBackgroundColor(Color.WHITE);
				burialDate.setTextColor(Color.BLACK);
				
				birthDate.setBackgroundColor(Color.BLACK);
				birthDate.setTextColor(Color.WHITE);
				
			}
			else if(v.getId() == birthDate.getId())
			{
				whichDate = 2;
				deathDate.setBackgroundColor(Color.BLACK);
				deathDate.setTextColor(Color.WHITE);
				
				burialDate.setBackgroundColor(Color.BLACK);
				burialDate.setTextColor(Color.WHITE);
			
				birthDate.setBackgroundColor(Color.WHITE);
				birthDate.setTextColor(Color.BLACK);
			}
			
		}
	};
	
	public void runQuery()
	{
		String tmpNecropolisId = "";
		if (necropolis.getSelectedItemId() != 0)
		{
		tmpNecropolisId = ""+necropolis.getSelectedItemId();
		}
		
		String tmpName = "";
		String tmpSurname = "";
		try {
			tmpName = URLEncoder.encode(editTextName.getText().toString().toLowerCase(),"UTF-8");
			tmpSurname = URLEncoder.encode(editTextSurname.getText().toString().toLowerCase(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tmpDate ="";
		GeoJSON gJSON;
		if(checkBoxDate.isChecked())
		{		
			datePicker.clearFocus();
			tmpDate = datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
			gJSON = new GeoJSON(tmpNecropolisId, tmpName, tmpSurname, tmpDate, whichDate);
		}
		else
		{
			gJSON = new GeoJSON(tmpNecropolisId, tmpName, tmpSurname, tmpDate, -1);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        /**
         * TITLEBAR PROGRESSBAR
         */
        
        progressBar = (ProgressBar) findViewById(R.id.progressbar_titlebar);
        
        /**
         * FIND BUTTON
         */
        
        find = (Button) findViewById(R.id.find_btn);
        find.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				if (isOnline())
				{
				progressBar.setVisibility(View.VISIBLE);
				
				runQuery();
				progressBar.setVisibility(View.GONE);
				Intent i;
				i = new Intent(getApplicationContext(),ResultList.class);
				startActivity(i);
				}
				else if (!isOnline())
				{					
					Toast.makeText(getApplicationContext(), 
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
                this, R.array.necropolises, android.R.layout.simple_spinner_item);
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
        editTextName = (EditText) findViewById(R.id.name);
        
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
    
}