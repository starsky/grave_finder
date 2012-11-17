package pl.itiner.grave;

import java.util.Date;
import java.util.GregorianCalendar;

import pl.itiner.fetch.QueryParams;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.actionbarsherlock.app.SherlockFragment;

public class GFormFragment extends SherlockFragment {
	public static final String TAG = "GFormFragment";
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

	private SearchActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SearchActivity) {
			this.activity = (SearchActivity) activity;
		} else {
			throw new IllegalArgumentException("Activity is not instance of "
					+ SearchActivity.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.search_form, container, false);
		progressBar = (ProgressBar) root
				.findViewById(R.id.progressbar_titlebar);

		find = (Button) root.findViewById(R.id.find_btn);
		setSearchClickListener();

		necropolis = (Spinner) root.findViewById(R.id.necropolis_spinner);
		necropolis.setAdapter(getNecropolisSpinnerAdapter());

		datePicker = (DatePicker) root.findViewById(R.id.datepicker);

		checkBoxDate = (CheckBox) root.findViewById(R.id.checkbox);
		checkBoxDate.setOnCheckedChangeListener(onCheckedDateVisiable);

		editTextSurname = (EditText) root.findViewById(R.id.surname);
		editTextSurname.setSelected(false);
		editTextName = (EditText) root.findViewById(R.id.name);
		editTextName.setSelected(false);

		dateGroup = (RadioGroup) root.findViewById(R.id.dates_group);
		dateGroup.setOnCheckedChangeListener(onCheckDateType);
		return root;

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

	private ArrayAdapter<CharSequence> getNecropolisSpinnerAdapter() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.necropolises,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	private void setSearchClickListener() {
		find.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextName.requestFocus();
				Long tmpNecropolisId = necropolis.getSelectedItemId() != 0 ? necropolis
						.getSelectedItemId() : null;
				Date deathDate = null;
				Date burialDate = null;
				Date birthDate = null;

				Date tmpDate = new GregorianCalendar(datePicker.getYear(),
						datePicker.getMonth(), datePicker.getDayOfMonth())
						.getTime();
				switch (whichDate) {
				case DEATH_DATE:
					deathDate = tmpDate;
					break;
				case BIRTH_DATE:
					birthDate = tmpDate;
					break;
				case BURIAL_DATE:
					burialDate = tmpDate;
					break;
				}
				final QueryParams params = new QueryParams(editTextName
						.getText().toString(), editTextSurname.getText()
						.toString(), tmpNecropolisId, birthDate, burialDate,
						deathDate);
				activity.search(params);
				// cm = (ConnectivityManager) getActivity().getSystemService(
				// Context.CONNECTIVITY_SERVICE);
				// if (isOnline()) {
				// new Thread(th_searchGraves).start();
				// }
				// if (!isOnline()) {
				// Toast.makeText(getApplicationContext(),
				// R.string.check_conn, Toast.LENGTH_SHORT).show();
				// }
				//
			}
		});
	}

	private boolean isOnline() {

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
