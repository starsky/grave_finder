package pl.itiner.grave.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.Locale;

import org.apache.commons.net.telnet.TelnetClient;

import junit.framework.TestCase;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;

public class SearchCriteriaHelper {

	protected static final String TEST_NAME = "Jan";
	protected static final String TEST_SURNAME = "Nowak";
	protected static final String TEST_CEMENTERY = "Miłostowo";

	private Solo solo;

	public SearchCriteriaHelper(Solo solo) {
		this.solo = solo;
	}

	protected void helperTestSimpleName(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestSimpleSurname(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename(TEST_SURNAME);
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestCapitalName(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME.toUpperCase(Locale.US));
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestSpacedName(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName("  " + TEST_NAME + "  ");
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestCapitalSurname(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename(TEST_SURNAME.toUpperCase(Locale.US));
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestSpacedSurname(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename("  " + TEST_SURNAME + "  ");
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestCementerySelect(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setCementery(TEST_CEMENTERY);
		startSearchTest(criteria, isOnline);
	}

	protected void helperTestAllBasicCategories(boolean isOnline) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		criteria.setSurename(TEST_SURNAME);
		criteria.setCementery(TEST_CEMENTERY);
		startSearchTest(criteria, isOnline);
	}

	protected void startSearchTest(SearchCriteria criteria, boolean isOnline) {
		criteria.apply(solo);
		solo.clickOnButton(solo.getString(pl.itiner.grave.R.string.search));
		TestCase.assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
		TestCase.assertTrue(solo.waitForView(ListView.class));
		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				View view = solo.getView(android.R.id.empty);
				return view != null && view.getVisibility() == View.GONE;
			}
		}, 5000);
		criteria.verifyResult((ListView) solo.getView(android.R.id.list), solo);
		if (isOnline) {
			TestCase.assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.GONE);
		} else {
			TestCase.assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.VISIBLE);
		}
	}

	protected void clearScr() {
		solo.clearEditText((EditText) solo.getView(pl.itiner.grave.R.id.name));
		solo.clearEditText((EditText) solo
				.getView(pl.itiner.grave.R.id.surname));
		solo.pressSpinnerItem(0, -2);
	}

	public static final String GSM_DATA_OFF = "gsm data off\n";
	public static final String GSM_DATA_ON = "gsm data on\n";

	
	public void runTelnetCommand(String cmd) throws SocketException,
			IOException, Exception {
		TelnetClient client = null;
		try {
			client = new TelnetClient();
			client.connect("10.0.2.2", 5554);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String result = in.readLine();
			result = in.readLine();
			if (!result.equals("OK")) {
				throw new Exception("Telnet failed: " + result);
			}
			PrintStream out = new PrintStream(client.getOutputStream());
			out.print(cmd);
			out.flush();
			result = in.readLine();
			if (!result.equals("OK")) {
				throw new Exception("Telnet failed: " + result);
			}
		} finally {
			if (null != client) {
				client.disconnect();
			}
		}
	}

}
