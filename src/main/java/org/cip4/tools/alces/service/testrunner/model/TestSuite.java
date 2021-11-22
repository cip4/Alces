package org.cip4.tools.alces.service.testrunner.model;

import java.util.ArrayList;
import java.util.List;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult.Result;
import org.cip4.tools.alces.service.testrunner.tests.Test;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test suite model object.
 */
public class TestSuite  {

	private final List<TestSession> testSessions;

	/**
	 * Default constructor.
	 */
	public TestSuite() {
		testSessions = new ArrayList<>();
	}

	/**
	 * Getter for the TestSession list.
	 * @return List of active test sessions.
	 */
	public List<TestSession> getTestSessions() {
		return testSessions;
	}

}
