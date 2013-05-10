package pl.itiner.grave.test;

import java.util.Date;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.jayway.android.robotium.solo.Solo;

public class SearchCriteria {

	public static enum DateMode {
		BIRTH, DEATH, BURIAL
	}

	private String name;
	private String surname;
	private String cementery;
	private Date date;
	private DateMode dateMode;
	private Boolean searchExtra;

	public void apply(Solo solo) {
		if (name != null) {
			solo.enterText((EditText) solo.getView(pl.itiner.grave.R.id.name),
					name);
		}
		if (surname != null) {
			solo.enterText(
					(EditText) solo.getView(pl.itiner.grave.R.id.surname),
					surname);
		}
		if (cementery != null) {
			solo.pressSpinnerItem(0, findCementeryItemIndex(cementery, solo));
			ActivityInstrumentationTestCase2.assertTrue(
					"Faild to select cementary",
					solo.isSpinnerTextSelected(0, cementery));
		}
		if (searchExtra != null) {
			// TODO setup apply for extra criteria
		}
	}

	public void verifySearchView(Solo solo) {
		if (name != null) {
			TextView field = (TextView) solo.getView(pl.itiner.grave.R.id.name);
			TestCase.assertTrue(field.getText().toString().equals(name));
		}
		if (surname != null) {
			TextView field = (TextView) solo
					.getView(pl.itiner.grave.R.id.surname);
			TestCase.assertTrue(field.getText().toString().equals(surname));
		}
		if (cementery != null) {
			ActivityInstrumentationTestCase2.assertTrue(
					"Faild to select cementary",
					solo.isSpinnerTextSelected(0, cementery));
		}
		if (searchExtra != null) {
			// TODO setup apply for extra criteria
		}
	}

	public void verifyResult(ListView resultView, Solo solo) {
		TestCase.assertTrue("ListView has no results.",
				resultView.getChildCount() > 0);
		for (int i = 0; i < resultView.getChildCount(); i++) {
			View v = resultView.getChildAt(i);
			verifyResultField(v, pl.itiner.grave.R.id.list_value_name, name);
			verifyResultField(v, pl.itiner.grave.R.id.list_value_surname,
					surname);
			verifyResultField(v, pl.itiner.grave.R.id.list_value_cementry,
					cementery);
		}
	}

	private void verifyResultField(View parent, int id, String expectedValue) {
		if (!Strings.isNullOrEmpty(expectedValue)) {
			TextView fieldView = (TextView) parent.findViewById(id);
			String fieldText = fieldView.getText().toString();
			expectedValue = expectedValue.trim();
			TestCase.assertTrue(fieldText.equalsIgnoreCase(expectedValue));
		}
	}

	private int findCementeryItemIndex(String cementery, Solo solo) {
		String[] cementeries = solo.getCurrentActivity().getResources()
				.getStringArray(pl.itiner.grave.R.array.necropolises);
		int index = ArrayUtils.indexOf(cementeries, cementery);
		if (index == ArrayUtils.INDEX_NOT_FOUND) {
			throw new NoSuchElementException("No such cementery " + cementery);
		}
		return index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurename() {
		return surname;
	}

	public void setSurename(String surename) {
		this.surname = surename;
	}

	public String getCementery() {
		return cementery;
	}

	public void setCementery(String cementery) {
		this.cementery = cementery;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DateMode getDateMode() {
		return dateMode;
	}

	public void setDateMode(DateMode dateMode) {
		this.dateMode = dateMode;
	}

	public boolean isSearchExtra() {
		return searchExtra;
	}

	public void setSearchExtra(boolean searchExtra) {
		this.searchExtra = searchExtra;
	}

}
