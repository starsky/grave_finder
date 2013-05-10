package pl.itiner.grave.test;


public class OnlineSearchTest extends SearchTest {

	protected void setUp() throws Exception {
		super.setUp();
		clearResultsCache();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected boolean isOnline() {
		return true;
	}

}
