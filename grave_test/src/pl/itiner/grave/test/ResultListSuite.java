package pl.itiner.grave.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ResultListSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(ResultListSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(ResultListTest.class);
		//$JUnit-END$
		return suite;
	}

}
