/*
 * Created on Jun 14, 2007
 */
package org.cip4.tools.alces.service.testrunner.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configurable <code>Test</code> that loads its configuration from a Java properties file <code><i>classname</i>.properties</code> on the class path.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public abstract class ConfigurableTest extends Test {

	protected static Logger log = LoggerFactory.getLogger(ConfigurableTest.class);

	private static final String PATH_PROPS = "/org/cip4/tools/alces/props/";

	public ConfigurableTest(String description) {
		super(description);
	}

	/**
	 * Loads this <code>Test</code>'s configuration from the Java properties file <code><i>classname</i>.properties</code> on the class path.
	 * 
	 * @return this classes configuration
	 */
	protected Properties loadConfiguration() {

		// String configFile = getSimpleName() + ".properties";
		// LOGGER.debug("Loading test configuration from file '" + configFile + "'...");
		// InputStream stream = this.getClass().getClassLoader().getResourceAsStream(configFile);

		String configFile = PATH_PROPS + getSimpleName() + ".properties";
		log.debug("Loading test configuration from resource '" + configFile + "'...");
		InputStream stream = ConfigurableTest.class.getResourceAsStream(configFile);

		if (stream == null) {
			throw new IllegalArgumentException("Could not load test configuration. Make sure the configuration resource '" + configFile + "' is on the classpath.");
		}
		Properties config = null;
		try {
			config = new Properties();
			config.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load test configuration. Make sure the configuration resource '" + configFile + "' is on the classpath.");
		}
		return config;
	}

	private String getSimpleName() {
		String name = getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}
}
