package pl.itiner.grave.test;

import java.io.IOException;
import java.net.SocketException;

import pl.itiner.grave.SearchActivity;
import pl.itiner.nutiteq.NutiteqMap;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class NutiteqMapTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	private Solo solo;
	private SearchCriteriaHelper helper;

	public NutiteqMapTest() {
		super(SearchActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		helper = new SearchCriteriaHelper(solo);
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testMapRotationOnline() {
		mapRotation(true);
	}
	
	public void testStressMapOnline() {
		stressMap(true);
	}

	public void testMapRotationOffline() throws SocketException, IOException, Exception {
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_OFF);
		mapRotation(false);
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_ON);
	}
	
	public void testStressMapOffline() throws SocketException, IOException, Exception {
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_OFF);
		stressMap(false);
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_ON);
	}

	private void mapRotation(boolean online) {
		helper.helperTestAllBasicCategories(online);
		solo.clickInList(0);
		assertTrue(solo.waitForActivity(NutiteqMap.class,5000));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue(solo.waitForActivity(NutiteqMap.class,5000));
		solo.setActivityOrientation(Solo.PORTRAIT);
	}

	private void stressMap(boolean online) {
		int height = getActivity().getResources().getDisplayMetrics().heightPixels;
		int width = getActivity().getResources().getDisplayMetrics().widthPixels;

		helper.helperTestAllBasicCategories(online);
		solo.clickInList(0);
		assertTrue(solo.waitForActivity(NutiteqMap.class,5000));

		solo.clickOnScreen(width / 2, height / 2);
		solo.clickOnScreen(width / 2, height / 2);

		solo.clickOnScreen(width / 2, height / 2);
		solo.clickOnScreen(width / 2, height / 2);

		solo.drag(width / 2, height / 2, width / 2 + 50, height / 2 + 50, 5);
	}

}
