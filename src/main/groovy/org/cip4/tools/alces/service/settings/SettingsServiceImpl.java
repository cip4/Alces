package org.cip4.tools.alces.service.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ComboBoxModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.util.AlcesPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Implementation of the settings service.
 */
@Service
public class SettingsServiceImpl implements SettingsService {

	private static Logger log = LoggerFactory.getLogger(SettingsServiceImpl.class);

	private static final String RES_ALCES_PROPS = "/org/cip4/tools/alces/conf/alces.properties";

	private static final Properties DEFAULT_PROPERTIES;

	private Properties _props = null;

	private Map<String, Boolean> _incomingTestsConfig = null;

	private Map<String, Boolean> _outgoingTestsConfig = null;

	private Map<String, Boolean> _jmfPreprocessorsConfig = null;

	private Map<String, Boolean> _jdfPreprocessorsConfig = null;

	private Preprocessor[] _jmfPreprocessors = new Preprocessor[0];

	private JDFPreprocessor[] _jdfPreprocessors = new JDFPreprocessor[0];

	private Map<String, String> _generalPrefs = null;

	// This are the defaultValues for the Properties
	public final static String SEND_DELAY = "alces.send.delay";

	public final static String SESSION_DURATION = "alces.session.duration";

	public final static String OUTPUT_DIR = "alces.output.dir";

	public final static String XSLT_REPORT_DIR = "alces.xslt.dir";

	public final static String HOST = "alces.host";

	public final static String PORT = "alces.port";

	public final static String CONTEXT_PATH = "alces.context.path";

	public final static String RESOURCE_BASE = "alces.resource.base";

	public final static String INCOMING_TESTS = "alces.incoming.tests";

	public final static String OUTGOING_TESTS = "alces.outgoing.tests";

	public final static String PROPERTIES_FILE = "alces.properties.path";

	public final static String WIN_WIDTH = "gui.window.width";

	public final static String WIN_HEIGHT = "gui.window.height";

	public final static String DEVICE_WIDTH = "gui.device.pane.width";

	public final static String TEST_WIDTH = "gui.test.pane.width";

	public final static String MAIN_HEIGHT = "gui.main.pane.height";

	public final static String ADRESS_HISTORY = "gui.address.history";

	public final static String JMF_PATH = "alces.context.jmf.path";

	public final static String JDF_PATH = "alces.context.jdf.path";

	public final static String LAST_DIR = "last.dir";

	public final static String PROXY_ENABLED = "proxy.enabled";

	public final static String PROXY_HOST = "proxy.host";

	public final static String PROXY_PORT = "proxy.port";

	public final static String PROXY_USER = "proxy.user";

	public final static String PROXY_PASSWORD = "proxy.password";

	public final static String OUTGOING_CONNECTIONS = "outgoing.connections";

	public final static String JMF_PREPROCESSORS = "alces.jmf.preprocessors";

	public final static String JDF_PREPROCESSORS = "alces.jdf.preprocessors";

	public final static String REPLACE_URLS_IN_JDF = "alces.replace.urls.in.jdf";

	public final static String REPLACE_URLS_IN_JDF_WITH_HTTP = "http";

	public final static String REPLACE_URLS_IN_JDF_WITH_FILE = "file";

	public final static String REPLACE_URLS_IN_JDF_DISABLED = "disabled";

	public final static String JMF_NOT_IMPLEMENTED_RETURN_CODE = "jmf.return.code.not.implemented";

	public final static String UPDATE_MESSAGEID = "update.Message-ID";

	public final static String UPDATE_ACKNOWLEDGEURL = "update.AcknowledgeURL";

	public final static String UPDATE_RETURNURL = "update.ReturnURL";

	public final static String UPDATE_RETURNJMF = "update.ReturnJMF";

	public final static String UPDATE_WATCHURL = "update.WatchURL";

	public final static String BATCHMODE_DELAYTONEXT_FILE = "batch.mode.DelayToNextFile";

	public final static String SHOW_CONNECT_MESSAGES = "show.connect.messages";

	public final static String USE_SPECIFIED_IP = "use.specified.ip";

	public final static String NO_CONTENT_TYPE = "no.content.type";

	public final static String PATH_TO_SAVE = "path.to.save";

	public final static String MJM_MIME_FILE_PARSE = "mjm.file.parse.enabled";

	public final static String MIME_CONTENT_TYPE = "mime.package.content-type";

	public final static String MIME_INDENT = "mime.package.indent";

	public final static String MIME_LINE_WIDTH = "mime.package.line.width";

	static {

		DEFAULT_PROPERTIES = new Properties();

		DEFAULT_PROPERTIES.put(PROPERTIES_FILE, FilenameUtils.concat(AlcesPathUtil.ALCES_CONFIG_DIR, "alces.properties"));

		DEFAULT_PROPERTIES.put(PORT, "9090");

		DEFAULT_PROPERTIES.put(CONTEXT_PATH, "/alces/jmf");

		DEFAULT_PROPERTIES.put(OUTPUT_DIR, AlcesPathUtil.ALCES_OUTPUT_DIR + File.separator);

		DEFAULT_PROPERTIES.put(WIN_WIDTH, 1373 + "");
		DEFAULT_PROPERTIES.put(WIN_HEIGHT, 906 + "");
		DEFAULT_PROPERTIES.put(MAIN_HEIGHT, 564 + "");

		DEFAULT_PROPERTIES.put(DEVICE_WIDTH, 235 + "");

		DEFAULT_PROPERTIES.put(TEST_WIDTH, 432 + "");

		DEFAULT_PROPERTIES.put(RESOURCE_BASE, AlcesPathUtil.ALCES_TEST_DATA_DIR + File.separator + "testdata");

		DEFAULT_PROPERTIES.put(SEND_DELAY, "100");

		DEFAULT_PROPERTIES.put(SESSION_DURATION, "15000");

		DEFAULT_PROPERTIES.put(LAST_DIR, System.getProperty("user.dir"));

		DEFAULT_PROPERTIES.put(REPLACE_URLS_IN_JDF, "none");

		DEFAULT_PROPERTIES.put(JDF_PATH, AlcesPathUtil.ALCES_TEST_DATA_DIR + File.separator + "jdf" + File.separator + "");

		try {
			DEFAULT_PROPERTIES.put(HOST, (InetAddress.getLocalHost().getHostAddress()).toString());
			// instead of any previously entered name use localhost by default.
			// it will be overwritten later using alces.properties file, 'alces.host'.
			DEFAULT_PROPERTIES.put(HOST, "localhost");
		} catch (UnknownHostException e) {
			log.error("error", e);
		}

		DEFAULT_PROPERTIES.put(JMF_PATH, AlcesPathUtil.ALCES_TEST_DATA_DIR + File.separator + "elk-testdata" + File.separator + "");

		DEFAULT_PROPERTIES.put(PROXY_ENABLED, "false");
		DEFAULT_PROPERTIES.put(PROXY_HOST, "");
		DEFAULT_PROPERTIES.put(PROXY_PORT, "-1");
		DEFAULT_PROPERTIES.put(PROXY_USER, "");
		DEFAULT_PROPERTIES.put(PROXY_PASSWORD, "");
		DEFAULT_PROPERTIES.put(OUTGOING_CONNECTIONS, "5");

		DEFAULT_PROPERTIES.put(UPDATE_MESSAGEID, "true");
		DEFAULT_PROPERTIES.put(UPDATE_ACKNOWLEDGEURL, "true");
		DEFAULT_PROPERTIES.put(UPDATE_RETURNURL, "true");
		DEFAULT_PROPERTIES.put(UPDATE_RETURNJMF, "true");
		DEFAULT_PROPERTIES.put(UPDATE_WATCHURL, "false");
		DEFAULT_PROPERTIES.put(BATCHMODE_DELAYTONEXT_FILE, "3000");
		DEFAULT_PROPERTIES.put(SHOW_CONNECT_MESSAGES, "false");
		DEFAULT_PROPERTIES.put(USE_SPECIFIED_IP, "false");
		DEFAULT_PROPERTIES.put(NO_CONTENT_TYPE, "false");
		DEFAULT_PROPERTIES.put(PATH_TO_SAVE, AlcesPathUtil.ALCES_USER_FILES_DIR);
		DEFAULT_PROPERTIES.put(MJM_MIME_FILE_PARSE, "false");
		DEFAULT_PROPERTIES.put(MIME_CONTENT_TYPE, "binary");
		DEFAULT_PROPERTIES.put(MIME_INDENT, "4");
		DEFAULT_PROPERTIES.put(MIME_LINE_WIDTH, "1000");
	}

	/**
	 * DefaultConstructor for the ConfigFacade is called from the getInstance() Method
	 * 
	 */
	public SettingsServiceImpl() {
		_props = new Properties(DEFAULT_PROPERTIES);
	}

	/**
	 * Event is called after applications start up.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void onStartUp() {
		loadConfiguration(getProp(PROPERTIES_FILE));
	}

	/**
	 * Loads Alces configuration from file.
	 * 
	 * @param propsFile the Java Properties File to load the configuration from
	 * @return the loaded Properties
	 */
	private synchronized Properties loadConfiguration(String propsFile) {
		return loadConfiguration(new File(propsFile));
	}

	/**
	 * Loads Alces' configuration
	 * 
	 * @param propsFile the Java Properties File to load the configuration from
	 * @return the loaded Properties
	 */
	private synchronized Properties loadConfiguration(File propsFile) {
		log.info("Loading configuration from '" + propsFile.getAbsolutePath() + "'...");
		Properties props = new Properties(_props);

		// check if properties file exists
		if (!propsFile.exists()) {
			InputStream is = SettingsServiceImpl.class.getResourceAsStream(RES_ALCES_PROPS);

			try {
				FileOutputStream fos = new FileOutputStream(propsFile);
				IOUtils.copy(is, fos);
				fos.close();
				is.close();

			} catch (Exception e) {
				log.error("Error occured during creating alces.properties file.", e);
			}
		}

		try {
			props.load(new FileInputStream(propsFile));

		} catch (FileNotFoundException fnfe) {
			log.error("Could not find the properties file '" + propsFile.getAbsolutePath() + "'. Defaults will be used. No error while using Automated-Alces");
		} catch (IOException ioe) {
			log.error("Could not load the properties file '" + propsFile.getAbsolutePath() + "'. Defaults will be used. No error while using Automated-Alces");
		}
		log.info("Configuration loaded.");
		_props = props;

		loadAndInitPreprocessors();
		_generalPrefs = new HashMap<String, String>();
		_generalPrefs.put(JDF_PATH, getProp(JDF_PATH));
		_generalPrefs.put(JMF_PATH, getProp(JMF_PATH));

		return props;
	}

	/**
	 * Returns the URL used for receiving JMF messages. The URL is built from the hostname, port, and context properties.
	 * 
	 * @return the server URL, for example http://localhost:9090/alces/jmf
	 */
	@Override
	public String getServerJmfUrl() {
		return "http://" + getServerHost() + ":" + getServerPort() + getServerJmfContextPath();
	}

	/**
	 * @return the test server's port
	 */
	private int getServerPort() {
		int port;
		try {
			port = Integer.parseInt(getProp(SettingsServiceImpl.PORT));
		} catch (Exception e) {
			port = 9090;
			log.error("Port was not a number. Using Port: " + port, e);
		}
		return port;
	}

	/**
	 * @return the test server's hostname/IP.
	 */
	private String getServerHost() {
		return getProp(SettingsServiceImpl.HOST);
	}

	/**
	 * @return the server's JMF context path.
	 */
	private String getServerJmfContextPath() {
		return getProp(SettingsServiceImpl.CONTEXT_PATH);
	}

	/**
	 * Saves Alces configuration to the properties file. Including Window settings, incoming and outgoing <code>Test</code>s and JDF- and JMF-Path settings
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @param devicePaneWidth
	 * @param testPaneWidth
	 * @param mainPaneHeight
	 */
	@Override
	public void saveConfiguration(int windowWidth, int windowHeight, int devicePaneWidth, int testPaneWidth, int mainPaneHeight) {
		log.info("Saving configuration to '" + _props.getProperty(PROPERTIES_FILE).toString() + "'...");

		// Saving Window settings
		_props.put(WIN_WIDTH, windowWidth + "");
		_props.put(WIN_HEIGHT, windowHeight + "");
		_props.put(DEVICE_WIDTH, devicePaneWidth + "");
		_props.put(TEST_WIDTH, testPaneWidth + "");
		_props.put(MAIN_HEIGHT, mainPaneHeight + "");

		// Saving Path settings for JDF- and JMF-Files
		_props.put(JMF_PATH, _generalPrefs.get("alces.context.jmf.path").toString());
		_props.put(JDF_PATH, _generalPrefs.get("alces.context.jdf.path").toString());

		// Saving Properties to File
		try {
			_props.store(new FileOutputStream(_props.getProperty(PROPERTIES_FILE).toString()), "");
			log.debug("Saved configuration.");
		} catch (IOException ioe) {
			log.error("Could not save configuration.", ioe);
		}
	}

	/**
	 * Returns an array of the currently enabled <code>Preprocessor</code>s.
	 * 
	 * @return a map of <code>Preprocessor</code>s
	 */
	@Override
	public Preprocessor[] getJMFPreprocessors() {
		return _jmfPreprocessors;
	}

	/**
	 * Returns an array of the currently enabled <code>JDFPreprocessor</code>s.
	 * 
	 * @return a map of <code>Preprocessor</code>s
	 */
	@Override
	public JDFPreprocessor[] getJDFPreprocessors() {
		return _jdfPreprocessors;
	}


	private void loadAndInitPreprocessors() {
		_jmfPreprocessorsConfig = parseConfigProperty(getProp(JMF_PREPROCESSORS));
		_jdfPreprocessorsConfig = parseConfigProperty(getProp(JDF_PREPROCESSORS));
		initJMFPreprocessors();
		initJDFPreprocessors();
	}

	private void initJMFPreprocessors() {
		// Instantiate and cache preprocessors
		List<Preprocessor> pps = new ArrayList<Preprocessor>();
		for (String ppName : _jmfPreprocessorsConfig.keySet()) {
			final boolean ppEnabled = Boolean.valueOf(_jmfPreprocessorsConfig.get(ppName).toString()).booleanValue();
			if (ppEnabled) {
				try {
					Preprocessor pp = (Preprocessor) Class.forName(ppName).newInstance();
					// // Special configuration for known Preprocessors
					// if (pp instanceof SenderIDPreprocessor) {
					// ((SenderIDPreprocessor)
					// pp).setSenderID(getProp(SENDER_ID));
					// } else if (pp instanceof URLPreprocessor) {
					// ((URLPreprocessor) pp).setURL(getServerJmfUrl());
					// }
					pps.add(pp);
					log.debug("Configured Preprocessor: " + ppName);
				} catch (ClassNotFoundException cnfe) {
					log.error("Could not find Preprocessor class: " + ppName, cnfe);
				} catch (IllegalAccessException iae) {
					log.error("Could not instantiate Preprocessor class: " + ppName, iae);
				} catch (InstantiationException ie) {
					log.error("Could not instantiate Preprocessor class: " + ppName, ie);
				} catch (ClassCastException cce) {
					log.error("Could not add Preprocessor class because it was of incorrect type: " + ppName, cce);
				}
			}
		}
		// Set cached preprocessors
		_jmfPreprocessors = pps.toArray(new Preprocessor[pps.size()]);
	}

	private void initJDFPreprocessors() {
		// Instantiate and cache preprocessors
		List<JDFPreprocessor> pps = new ArrayList<JDFPreprocessor>();
		for (String ppName : _jdfPreprocessorsConfig.keySet()) {
			final boolean ppEnabled = Boolean.valueOf(_jdfPreprocessorsConfig.get(ppName).toString()).booleanValue();
			if (ppEnabled) {
				try {
					JDFPreprocessor pp = (JDFPreprocessor) Class.forName(ppName).newInstance();
					pps.add(pp);
					log.debug("Configured JDFPreprocessor: " + ppName);
				} catch (ClassNotFoundException cnfe) {
					log.error("Could not find JDFPreprocessor class: " + ppName, cnfe);
				} catch (IllegalAccessException iae) {
					log.error("Could not instantiate JDFPreprocessor class: " + ppName, iae);
				} catch (InstantiationException ie) {
					log.error("Could not instantiate JDFPreprocessor class: " + ppName, ie);
				} catch (ClassCastException cce) {
					log.error("Could not add JDFPreprocessor class because it was of incorrect type: " + ppName, cce);
				}
			}
		}
		// Set cached preprocessors
		_jdfPreprocessors = pps.toArray(new JDFPreprocessor[pps.size()]);
	}

	/**
	 * Parses a configuration string of comma-separated elements on the format: CLASSNAME;ENABLED
	 */
	private Map<String, Boolean> parseConfigProperty(final String propValue) {
		final Map<String, Boolean> testConfig = new HashMap<String, Boolean>();
		if (propValue != null && propValue.length() != 0) {
			try {
				final String[] tests = propValue.split(",");
				for (int i = 0; i < tests.length; i++) {
					String[] splitProp = tests[i].split(";");
					testConfig.put(splitProp[0], Boolean.valueOf(splitProp[1]));
				}
			} catch (Exception e) {
				log.error("Could not parse configuration property: " + propValue, e);
			}
		}
		return testConfig;
	}

	/**
	 * Sets the <code>Preprocessor</code> configuration.
	 * 
	 * @param ppConfig A map where keys are the fully qualified classnames of the configured <code>Preprocessor</code> classes; values are the
	 * <code>String</code> "true" if the <code>Preprocessor</code> is enabled, "false" if it is disabled.
	 */
	private void setJMFPreprocessorConfig(Map<String, Boolean> ppConfig) {
		_jmfPreprocessorsConfig = ppConfig;
		initJMFPreprocessors();
	}

	/**
	 * Sets the <code>JDFPreprocessor</code> configuration.
	 * 
	 * @param ppConfig A map where keys are the fully qualified classnames of the configured <code>JDFPreprocessor</code> classes; values are the
	 * <code>String</code> "true" if the <code>JDFPreprocessor</code> is enabled, "false" if it is disabled.
	 */
	private void setJDFPreprocessorConfig(Map<String, Boolean> ppConfig) {
		_jdfPreprocessorsConfig = ppConfig;
		initJDFPreprocessors();
	}

	@Override
	public void setPreferences(Map<String, String> generalPrefs) {
		_generalPrefs = generalPrefs;
	}

	/**
	 * Loads the already used addresses from the properties
	 * 
	 * @return
	 */
	@Override
	public String[] loadHistory() {
		log.debug("Loading history...");
		String addressHistory = getProp(ADRESS_HISTORY);

		return addressHistory == null ? new String[0] : addressHistory.split("\\|");

	}

	/**
	 * Saves the address history and finds duplicate HTTP-Adresses and remove them before saving
	 */
	@Override
	public void saveHistory(ComboBoxModel adresses) {
		log.debug("Saving history...");

		StringBuffer addressHistory = new StringBuffer();

		String[] cache = new String[adresses.getSize()];

		for (int i = 0; i < adresses.getSize(); i++) {

			cache[i] = adresses.getElementAt(i).toString();

		}
		for (int i = 0; i < cache.length; i++) {
			inner: for (int z = i + 1; z < cache.length; z++) {

				if (cache[i].equals(cache[z]) && (cache[i] != null)) {
					cache[i] = null;
					break inner;
				}
			}
		}

		for (int i = 0; i < cache.length && i < 10; i++) {

			if (cache[i] != null) {

				addressHistory.append(cache[i]).append("|");
			}
		}

		_props.put(ADRESS_HISTORY, addressHistory.toString().trim());

		log.debug("Saved history: " + addressHistory);
	}

	/**
	 * If no propertie is available, a defaultValue for this <code>key</code> is returned.
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String getProp(String key) {
		log.debug("Looking up property with key: " + key);

		return _props.getProperty(key);
	}

	/**
	 * Storing of altered properties
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void putProp(String key, String value) {
		_props.put(key, value);

	}

	/**
	 * Is used to get automated alces started
	 * 
	 * @return
	 */
	@Override
	public Properties getPropFile() {
		return _props;

	}

	/**
	 * Returns the General Preferences
	 * 
	 * @return
	 */
	@Override
	public Map<String, String> getGeneralPrefs() {

		return _generalPrefs;
	}

}