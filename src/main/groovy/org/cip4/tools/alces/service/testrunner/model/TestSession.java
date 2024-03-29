package org.cip4.tools.alces.service.testrunner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Test session model object.
 */
public class TestSession {

	private final String targetUrl;

	private final List<OutgoingJmfMessage> outgoingJmfMessages;

	private final List<IncomingJmfMessage> incomingJmfMessages;

	private final AbstractJmfMessage initializingJmfMessage;

	/**
	 * Custom constructor. Accepting a target url for initializing.
	 * @param targetUrl The jmf target url.
	 */
	public TestSession(String targetUrl, AbstractJmfMessage initializingJmfMessage) {
		this.targetUrl = targetUrl;
		this.initializingJmfMessage = initializingJmfMessage;
		this.outgoingJmfMessages = new ArrayList<>();
		this.incomingJmfMessages = new ArrayList<>();
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public List<OutgoingJmfMessage> getOutgoingJmfMessages() {
		return outgoingJmfMessages;
	}

	public List<IncomingJmfMessage> getIncomingJmfMessages() {
		return incomingJmfMessages;
	}

	public AbstractJmfMessage getInitializingJmfMessage() {
		return initializingJmfMessage;
	}

	/**
	 * Checks if all tests has passed.
	 * @return True in case all tests has passed. Otherwise, false.
	 */
	public boolean hasPassedAllTests() {
		boolean passed;

		for (IncomingJmfMessage incomingJmfMessage : getIncomingJmfMessages()) {
			passed = incomingJmfMessage.hasPassedAllTests();
			if (!passed) {
				return false;
			}
		}

		for (OutgoingJmfMessage outgoingJmfMessage : getOutgoingJmfMessages()) {
			passed = outgoingJmfMessage.hasPassedAllTests();
			if (!passed) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "TestSession[ targetUrl=" + targetUrl + " ]";
	}
}