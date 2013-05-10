package pl.itiner.grave.test;

import java.util.Locale;

import pl.itiner.db.GraveFinderProvider;
import pl.itiner.db.NameHintProvider;
import pl.itiner.grave.ResultList;
import pl.itiner.grave.SearchActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;

public abstract class SearchTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	private static final String NO_SUCH_SURNAME = "NO_SUCH_SURNAME";
	
	protected SearchCriteriaHelper helper;
	
	public SearchTest() {
		super(SearchActivity.class);
	}

	protected Solo solo;

	protected abstract boolean isOnline();

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		helper = new SearchCriteriaHelper(solo);
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}


	protected void clearResultsCache() {
		getActivity().getContentResolver().delete(
				GraveFinderProvider.CONTENT_URI, null, null);

	}

	protected void clearHintsCache() {
		getActivity().getContentResolver().delete(NameHintProvider.CONTENT_URI,
				null, null);

	}

	public void testSimpleName() {
		helper.helperTestSimpleName(isOnline());
	}

	public void testSimpleSurname() {
		helper.helperTestSimpleSurname(isOnline());
	}

	public void testSimpleCementerySelect() {
		helper.helperTestCementerySelect(isOnline());
	}

	public void testAllBasicCategories() {
		helper.helperTestAllBasicCategories(isOnline());
	}

	public void testCapitalSurname() {
		helper.helperTestCapitalSurname(isOnline());
	}

	public void testCapitalName() {
		helper.helperTestCapitalName(isOnline());
	}

	public void testSpacedSurname() {
		helper.helperTestSpacedSurname(isOnline());
	}

	public void testSpacedName() {
		helper.helperTestSpacedName(isOnline());
	}

	public void testHintForSurname() {
		clearHintsCache();
		// Assure that there will be something in hint
		EditText field = (EditText) solo.getView(pl.itiner.grave.R.id.surname);
		helper.helperTestSimpleSurname(isOnline());
		solo.goBack();
		assertTrue(
				"Did not back to search fragment",
				solo.waitForFragmentById(pl.itiner.grave.R.id.search_form_fragment));
		assertTrue(solo.waitForView(field));
		solo.clearEditText(field);
		solo.typeText(field,
				SearchCriteriaHelper.TEST_SURNAME.toCharArray()[0] + "".toLowerCase(Locale.US));
		assertTrue(
				"Hint list did not contain expected surname " + SearchCriteriaHelper.TEST_SURNAME,
				solo.waitForText(SearchCriteriaHelper.TEST_SURNAME));
		solo.clickLongOnText(SearchCriteriaHelper.TEST_SURNAME);
		assertEquals(SearchCriteriaHelper.TEST_SURNAME, field.getText().toString());
	}

	public void testHintForName() {
		clearHintsCache();
		// Assure that there will be something in hint
		helper.helperTestSimpleName(isOnline());
		solo.goBack();
		assertTrue(
				"Did not back to search fragment",
				solo.waitForFragmentById(pl.itiner.grave.R.id.search_form_fragment));
		EditText field = (EditText) solo.getView(pl.itiner.grave.R.id.name);
		solo.clearEditText(field);
		String name = SearchCriteriaHelper.TEST_NAME;
		solo.typeText(field, name.toCharArray()[0] + "".toLowerCase(Locale.US));
		assertTrue("Hint list did not contain expected surname " + name,
				solo.waitForText(name));
		solo.clickLongOnText(name);
		assertEquals(name, field.getText().toString());
	}

	public void testScreenRotationAfterCriteria() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(SearchCriteriaHelper.TEST_NAME);
		criteria.setSurename(SearchCriteriaHelper.TEST_SURNAME);
		criteria.setCementery(SearchCriteriaHelper.TEST_CEMENTERY);
		criteria.apply(solo);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		criteria.verifySearchView(solo);
	}

	public void testScreenRotationAfterSearchHit() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setName(SearchCriteriaHelper.TEST_NAME);
		criteria.setSurename(SearchCriteriaHelper.TEST_SURNAME);
		criteria.setCementery(SearchCriteriaHelper.TEST_CEMENTERY);
		criteria.apply(solo);

		solo.clickOnButton(solo.getString(pl.itiner.grave.R.string.search));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
		assertTrue(solo.waitForView(ListView.class));
		solo.waitForCondition(new Condition() {

			@Override
			public boolean isSatisfied() {
				View view = solo.getView(android.R.id.empty);
				return view != null && view.getVisibility() == View.GONE;
			}
		}, 5000);
		criteria.verifyResult((ListView) solo.getView(android.R.id.list), solo);
		if (isOnline()) {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.GONE);
		} else {
			assertTrue(solo.getView(
					pl.itiner.grave.R.id.list_offline_warninig_view)
					.getVisibility() == View.VISIBLE);
		}
	}

	public void testANoSuchSurname() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSurename(NO_SUCH_SURNAME);
		criteria.apply(solo);

		solo.clickOnButton(solo.getString(pl.itiner.grave.R.string.search));
		assertTrue(solo.waitForFragmentById(
				pl.itiner.grave.R.id.result_list_fragment, 5000));
		assertTrue(solo.waitForFragmentByTag(ResultList.ALERT_FRAGMENT_TAG));
		solo.clickOnText(solo.getString(pl.itiner.grave.R.string.ok));
		solo.waitForFragmentById(pl.itiner.grave.R.id.search_form_fragment,
				5000);
	}

	/*
	 * TODO Add test for extra criteria set (dates)
	 */

}
