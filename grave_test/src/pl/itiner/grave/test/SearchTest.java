package pl.itiner.grave.test;

import java.util.Locale;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.db.NameHintProvider;
import pl.itiner.grave.SearchActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;

public class SearchTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	private static final String TEST_NAME = "Jan";
	private static final String TEST_SURNAME = "Nowak";
	private static final String TEST_CEMENTERY = "Miłostowo";

	public SearchTest() {
		super(SearchActivity.class);
	}

	private Solo solo;

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	private void helperTestSimpleName(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		startSearchTest(criteria, online);
	}

	private void helperTestSimpleSurname(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename(TEST_SURNAME);
		startSearchTest(criteria, online);
	}

	private void helperTestCapitalName(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME.toUpperCase(Locale.US));
		startSearchTest(criteria, online);
	}

	private void helperTestSpacedName(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName("  " + TEST_NAME + "  ");
		startSearchTest(criteria, online);
	}

	private void helperTestCapitalSurname(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename(TEST_SURNAME.toUpperCase(Locale.US));
		startSearchTest(criteria, online);
	}

	private void helperTestSpacedSurname(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename("  " + TEST_SURNAME + "  ");
		startSearchTest(criteria, online);
	}

	private void helperTestCementerySelect(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setCementery(TEST_CEMENTERY);
		startSearchTest(criteria, online);
	}

	private void helperTestAllBasicCategories(boolean online) {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		criteria.setSurename(TEST_SURNAME);
		criteria.setCementery(TEST_CEMENTERY);
		startSearchTest(criteria, online);
	}

	private void startSearchTest(SearchCriteria criteria, boolean online) {
		criteria.apply(solo);
		solo.clickOnButton(solo.getString(pl.itiner.grave.R.string.search));
		assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
		assertTrue(solo.waitForView(ProgressBar.class));
		assertTrue(solo.waitForView(ListView.class));
		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				View view = solo.getView(android.R.id.empty);
				return view != null && view.getVisibility() == View.GONE;
			}
		}, 5000);
		criteria.verifyResult((ListView) solo.getView(android.R.id.list), solo);
		if (online) {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.GONE);
		} else {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.VISIBLE);
		}
	}

	private void clearResultsCache() {
		getActivity().getContentResolver().delete(
				GraveFinderProvider.CONTENT_URI, null, null);

	}

	private void clearHintsCache() {
		getActivity().getContentResolver().delete(NameHintProvider.CONTENT_URI,
				null, null);

	}

	public void testSimpleNameNetworkNoCache() {
		clearResultsCache();
		helperTestSimpleName(true);
	}

	public void testSimpleSurnameNetworkNoCache() {
		clearResultsCache();
		helperTestSimpleSurname(true);
	}

	public void testSimpleCementerySelectNetworkNoCache() {
		clearResultsCache();
		helperTestCementerySelect(true);
	}

	public void testAllBasicCategoriesNetworkNoCache() {
		clearResultsCache();
		helperTestAllBasicCategories(true);
	}

	public void testCapitalSurnameNetworkNoCache() {
		clearResultsCache();
		helperTestCapitalSurname(true);
	}

	public void testCapitalNameNetworkNoCache() {
		clearResultsCache();
		helperTestCapitalName(true);
	}

	public void testSpacedSurnameNetworkNoCache() {
		clearResultsCache();
		helperTestSpacedSurname(true);
	}

	public void testSpacedNameNetworkNoCache() {
		clearResultsCache();
		helperTestSpacedName(true);
	}

	public void testHintForSurname() {
		clearResultsCache();
		clearHintsCache();
		// Assure that there will be something in hint
		EditText field = (EditText) solo.getView(pl.itiner.grave.R.id.surname);
		helperTestSimpleSurname(true);
		solo.goBack();
		assertTrue(
				"Did not back to search fragment",
				solo.waitForFragmentById(pl.itiner.grave.R.id.search_form_fragment));
		assertTrue(solo.waitForView(field));
		solo.clearEditText(field);
		solo.typeText(field,
				TEST_SURNAME.toCharArray()[0] + "".toLowerCase(Locale.US));
		assertTrue(
				"Hint list did not contain expected surname " + TEST_SURNAME,
				solo.waitForText(TEST_SURNAME));
		solo.clickLongOnText(TEST_SURNAME);
		assertEquals(TEST_SURNAME, field.getText().toString());
	}

	public void testHintForName() {
		clearResultsCache();
		clearHintsCache();
		// Assure that there will be something in hint
		helperTestSimpleName(true);
		solo.goBack();
		assertTrue(
				"Did not back to search fragment",
				solo.waitForFragmentById(pl.itiner.grave.R.id.search_form_fragment));
		EditText field = (EditText) solo.getView(pl.itiner.grave.R.id.name);
		solo.clearEditText(field);
		String name = TEST_NAME;
		solo.typeText(field, name.toCharArray()[0] + "".toLowerCase(Locale.US));
		assertTrue("Hint list did not contain expected surname " + name,
				solo.waitForText(name));
		solo.clickLongOnText(name);
		assertEquals(name, field.getText().toString());
	}

	public void testScreenRotationAfterCriteria() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		criteria.setSurename(TEST_SURNAME);
		criteria.setCementery(TEST_CEMENTERY);
		criteria.apply(solo);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		criteria.verifySearchView(solo);
	}

	public void testScreenRotationAfterSearchHit() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(TEST_NAME);
		criteria.setSurename(TEST_SURNAME);
		criteria.setCementery(TEST_CEMENTERY);
		criteria.apply(solo);
		solo.clickOnButton(solo.getString(pl.itiner.grave.R.string.search));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.waitForDialogToOpen(1000);
		solo.waitForDialogToClose(1000);
		assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
		solo.waitForView(ListView.class);
		criteria.verifyResult((ListView) solo.getView(android.R.id.list), solo);
	}

	/*
	 * TODO As I could not find the way how to disable networking in testing
	 * code, the tests which includes fetching data from cache also download
	 * data from network. Which actually should be disabled when want to test
	 * cache.
	 * 
	 * The above test will be introduced after making some changes to
	 * architecture allowing to mock some of classes to emulate no network.
	 */

	/*
	 * TODO Add test for extra criteria set (dates)
	 */

}
