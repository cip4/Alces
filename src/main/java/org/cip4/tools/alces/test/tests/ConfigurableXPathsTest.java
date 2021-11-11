/*
 * Created on May 30, 2005
 */
package org.cip4.tools.alces.test.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.util.JmfUtil;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class ConfigurableXPathsTest extends Test {

	public static final String DEFAULT_CONFIG_FILE = "xpaths-test.properties";

	private static final String DEFAULT_NS_PREFIX = "jdf";

	private String _defaultNsPrefix = DEFAULT_NS_PREFIX;

	private Properties _xpaths = null;

	private static Logger log = LoggerFactory.getLogger(ConfigurableXPathsTest.class);

	public ConfigurableXPathsTest() throws IOException {
		this(DEFAULT_CONFIG_FILE, DEFAULT_NS_PREFIX);
	}

	public ConfigurableXPathsTest(String configFile, String defaultNsPrefix) throws IOException {
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		_xpaths = new Properties();
		_xpaths.load(stream);
		_defaultNsPrefix = defaultNsPrefix;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.test.tests.Test#runTest(org.cip4.tools.alces.message.Message)
	 */
	@Override
	public TestResult runTest(AbstractJmfMessage message) {
		boolean passedTest = true;
		String logMsg = "";
		try {
			long t0 = System.currentTimeMillis();
			Document doc = JmfUtil.getBodyAsJDOM(message);

			for (Iterator i = _xpaths.keySet().iterator(); i.hasNext();) {
				// Get XPath test
				String xpath = (String) i.next();
				log.debug("XPath test: " + xpath);
				// TODO String expectedResult = (String) _xpaths.get(xpath);
				XPath xp = XPath.newInstance(xpath);
				// Add root element's namespace to XPath
				Namespace n = doc.getRootElement().getNamespace();
				if (n.getPrefix().length() == 0) {
					// Replace empty prefix with default prefix
					xp.addNamespace(_defaultNsPrefix, n.getURI());
					log.debug("Assigned namespace prefix '" + _defaultNsPrefix + "' to default namespace URI '" + n.getURI() + "'.");
				} else {
					xp.addNamespace(n);
				}
				// Add other namespaces defined on root element to XPath
				List nsList = doc.getRootElement().getAdditionalNamespaces();
				for (Iterator j = nsList.iterator(); j.hasNext();) {
					Namespace ns = (Namespace) j.next();
					if (ns.getPrefix().length() == 0) {
						// Replace empty prefix with default prefix
						xp.addNamespace(_defaultNsPrefix, ns.getURI());
						log.debug("Assigned namespace prefix '" + _defaultNsPrefix + "' to default namespace URI '" + ns.getURI() + "'.");
					} else {
						xp.addNamespace(ns);
					}
				}
				// Evaluate XPath test
				List nodes = xp.selectNodes(doc);
				log.debug("Matching nodes: " + nodes.size());
				log.debug(nodes.toString());

				/*
				 * for (Iterator j = nodes.iterator(); j.hasNext();) { Object o = j.next(); LOGGER.debug(o); if (o instanceof String) { passedTest = passedTest
				 * && o.equals("true"); } else { passedTest = false; } }
				 */
			}

			long t1 = System.currentTimeMillis();
			if (passedTest) {
				logMsg += "Passed test.\n";
			}
			logMsg += "\nValidated JDF/JMF in " + (t1 - t0) + " ms.";
		} catch (Exception e) {
			logMsg = "An error occurred while validating the message body. Maybe the" + " message body does not contain a JDF instance or a JMF message?";
		}
		TestResult result = new TestResultImpl(this, message, Result.getPassed(passedTest), logMsg);
		return result;
	}

}
