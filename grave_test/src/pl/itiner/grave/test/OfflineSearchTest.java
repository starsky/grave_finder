package pl.itiner.grave.test;


public class OfflineSearchTest extends SearchTest {


	private static boolean isDataDownloaded = false;

	protected void setUp() throws Exception {
		super.setUp();
		if (!isDataDownloaded) {
			helper.helperTestAllBasicCategories(true);
			solo.goBack();
			helper.clearScr();
			isDataDownloaded = true;
		}
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_OFF);
	}


	protected void tearDown() throws Exception {
		helper.runTelnetCommand(SearchCriteriaHelper.GSM_DATA_ON);
		super.tearDown();
	}

	@Override
	protected boolean isOnline() {
		return false;
	}
}
