package pl.itiner.grave.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.itiner.grave.SearchActivity;
import pl.itiner.nutiteq.NutiteqMap;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class ResultListTest extends
		ActivityInstrumentationTestCase2<SearchActivity> {

	private Solo solo;
	private SearchCriteriaHelper helper;

	public ResultListTest() {
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

	public void testListRotation() {
		helper.helperTestAllBasicCategories(true);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue((solo
				.waitForFragmentById(pl.itiner.grave.R.id.result_list_fragment)));
		assertTrue(solo.getView(android.R.id.list).getVisibility() == View.VISIBLE);
		assertTrue(((ListView) solo.getView(android.R.id.list)).getChildCount() > 0);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue((solo
				.waitForFragmentById(pl.itiner.grave.R.id.result_list_fragment)));
		assertTrue(solo.getView(android.R.id.list).getVisibility() == View.VISIBLE);
		assertTrue(((ListView) solo.getView(android.R.id.list)).getChildCount() > 0);
	}

	public void testGoToMap() {
		helper.helperTestAllBasicCategories(true);
		List<TextView> textViews = solo.clickInList(0);
		Map<Integer, String> map = createMap(textViews);
		assertTrue(solo.waitForActivity(NutiteqMap.class));
		assertNotNull(solo.getText(
				map.get(pl.itiner.grave.R.id.list_value_name), true));
		assertNotNull(solo.getText(
				map.get(pl.itiner.grave.R.id.list_value_surname), true));
		assertNotNull(solo.getText(
				map.get(pl.itiner.grave.R.id.list_value_cementry), true));
	}

	@SuppressLint("UseSparseArrays")
	private Map<Integer, String> createMap(List<TextView> list) {
		HashMap<Integer, String> map = new HashMap<Integer, String>(list.size());
		for (TextView v : list) {
			map.put(v.getId(), v.getText().toString());
		}
		return map;
	}
}
