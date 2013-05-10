package pl.itiner.grave.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class NutiteqSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(NutiteqSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(NutiteqMapTest.class);
		//$JUnit-END$
		return suite;
	}

}
