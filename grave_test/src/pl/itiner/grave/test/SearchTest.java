package pl.itiner.grave.test;

import java.util.Locale;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.db.NameHintProvider;
import pl.itiner.grave.SearchActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;

public abstract class SearchTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	protected static final String TEST_NAME = "Jan";
	protected static final String TEST_SURNAME = "Nowak";
	protected static final String TEST_CEMENTERY = "Miłostowo";

	public SearchTest() {
		super(SearchActivity.class);
	}

	protected Solo solo;

	protected abstract boolean isOnline();

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
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
		assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
//		assertTrue(solo.waitForView(ProgressBar.class));
		assertTrue(solo.waitForView(ListView.class));
		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				View view = solo.getView(android.R.id.empty);
				return view != null && view.getVisibility() == View.GONE;
			}
		}, 5000);
		criteria.verifyResult((ListView) solo.getView(android.R.id.list), solo);
		if (isOnline) {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.GONE);
		} else {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.VISIBLE);
		}
	}
	
	protected void clearScr() {
		solo.clearEditText((EditText) solo.getView(pl.itiner.grave.R.id.name));
		solo.clearEditText((EditText) solo.getView(pl.itiner.grave.R.id.surname));
		solo.pressSpinnerItem(0, -2);
	}

	protected void clearResultsCache() {
		getActivity().getContentResolver().delete(
				GraveFinderProvider.CONTENT_URI, null, null);

	}

	protected void clearHintsCache() {
		getActivity().getContentResolver().delete(NameHintProvider.CONTENT_URI,
				null, null);

	}

	public void testSimpleNameNetworkNoCache() {
		helperTestSimpleName(isOnline());
	}

	public void testSimpleSurnameNetworkNoCache() {
		helperTestSimpleSurname(isOnline());
	}

	public void testSimpleCementerySelectNetworkNoCache() {
		helperTestCementerySelect(isOnline());
	}

	public void testAllBasicCategoriesNetworkNoCache() {
		helperTestAllBasicCategories(isOnline());
	}

	public void testCapitalSurnameNetworkNoCache() {
		helperTestCapitalSurname(isOnline());
	}

	public void testCapitalNameNetworkNoCache() {
		helperTestCapitalName(isOnline());
	}

	public void testSpacedSurnameNetworkNoCache() {
		helperTestSpacedSurname(isOnline());
	}

	public void testSpacedNameNetworkNoCache() {
		helperTestSpacedName(isOnline());
	}

	public void testHintForSurname() {
		clearHintsCache();
		// Assure that there will be something in hint
		EditText field = (EditText) solo.getView(pl.itiner.grave.R.id.surname);
		helperTestSimpleSurname(isOnline());
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
		clearHintsCache();
		// Assure that there will be something in hint
		helperTestSimpleName(isOnline());
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
	 * TODO Add test for extra criteria set (dates)
	 */

}
