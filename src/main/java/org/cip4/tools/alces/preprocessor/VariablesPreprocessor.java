package org.cip4.tools.alces.preprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.util.ConfigurationHandler;

/**
 * A preprocessor that replaces variables in the JDF and JMF. See
 * {@linkplain http
 * ://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text
 * /StrSubstitutor.html} for details on the the variables name-value map and the
 * syntax in the JDF/JMF template.
 * 
 * <p>
 * Default variables are first loaded from
 * {@link ConfigurationHandler#getPropFile()} and then from a Java Property file
 * named <code>VariablesPreprocessor.properties</code> if it exists at the root
 * of the classpath. The property names in the properties files can be used as
 * variable names and will be replace by their property values.
 * </p>
 * 
 * @author Claes Buckwalter
 * @since 0.9.9.3
 */
public class VariablesPreprocessor implements Preprocessor, JDFPreprocessor {
	private static final Log log = LogFactory
			.getLog(VariablesPreprocessor.class);

	public static final String VARIABLES_MAP = "org.cip4.elk.alces.VariablesPreprocessor.VARIABLES_MAP";

	public static final String VARIABLES_FILE = "VariablesPreprocessor.properties";

	public Map<String, String> variablesMap = new HashMap<String, String>();

	/**
	 * @param map
	 *            A map containing variable name-value pairs. If any variables
	 *            have the same names as the defaults, then the default values
	 *            are overwritten.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/
	 *      StrSubstitutor.html
	 */
	public VariablesPreprocessor(Map<String, String> map) {
		// Loads defaults
		loadVariablesMap();
		// Overrides defaults
		if (map != null) {
			setVariablesMap(map);
		}
	}

	public VariablesPreprocessor() {
		this(null);
	}

	/**
	 * @param map
	 *            A map to merge with the existing variables map. If any
	 *            variables already exist there values are overwritten by the
	 *            new ones.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/
	 *      StrSubstitutor.html
	 */
	public void setVariablesMap(Map<String, String> map) {
		variablesMap.putAll(map);
	}

	/**
	 * Copies the variable mappings from a <code>Properties</code> object.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/
	 *      StrSubstitutor.html
	 */
	private void setVariablesMap(Properties props) {
		final Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			map.put(key, props.getProperty(key));
		}
		setVariablesMap(map);
	}

	/**
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/
	 *      StrSubstitutor.html
	 */
	public Map<String, String> getVariablesMap() {
		return variablesMap;
	}

	@SuppressWarnings("unchecked")
	public Message preprocess(final Message message,
			final PreprocessorContext context) throws PreprocessorException {
		if (context != null && context.getAttribute(VARIABLES_MAP) != null) {
			setVariablesMap((Map<String, String>) context
					.getAttribute(VARIABLES_MAP));
		}
		final String jmf = message.getBody();
		final StrSubstitutor substitutor = new StrSubstitutor(getVariablesMap());
		final String newJmf = substitutor.replace(jmf);
		message.setBody(newJmf);
		return message;
	}

	public Message preprocess(final Message message)
			throws PreprocessorException {
		return preprocess(message, null);
	}

	/**
	 * Loads the variable mappings. First loads the variable mappings from
	 * Alces's configuration file. Then overrides them if they can be loaded
	 * from the file <code>VariablesPreprocessor.properties</code> found at the
	 * root of the classpath.
	 */
	private void loadVariablesMap() {
		final Properties props = ConfigurationHandler.getInstance()
				.getPropFile();
		setVariablesMap(props);
		final InputStream input = VariablesPreprocessor.class.getClassLoader()
				.getResourceAsStream(VARIABLES_FILE);
		if (input == null) {
			log
					.warn(String
							.format(
									"Could not load variables from Java Properties file %s because it could not be found.",
									VARIABLES_FILE));
			return;
		}
		try {
			props.load(input);
			setVariablesMap(props);
		} catch (IOException e) {
			log.error("Could not load variables from Java Properties file.", e);
		}
	}

	public JDFNode preprocess(JDFNode jdf) throws PreprocessorException {
		return preprocess(jdf, null);
	}

	/**
	 * Converts a root JDF node to its XML source, replaces all variables in the
	 * source with the values specified in the {@link PreprocessorContext}'s map
	 * with key {@link #VARIABLES_MAP}, then parses the XML source and returns
	 * the root JDF node.
	 * 
	 * <p>
	 * If the JDF node is not a root node, the context is null, or the context
	 * does not contain a value for the key {@link #VARIABLES_MAP}, then no
	 * preprocessing is performed and the original JDF is returned.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public JDFNode preprocess(JDFNode jdf, PreprocessorContext context)
			throws PreprocessorException {
		if (context != null && context.getAttribute(VARIABLES_MAP) != null) {
			setVariablesMap((Map<String, String>) context
					.getAttribute(VARIABLES_MAP));
		} else {
			log
					.warn("No PreprocessorContext or variables map specified. Returning original JDF.");
			return jdf;
		}
		if (!jdf.isJDFRoot()) {
			log
					.warn("JDF node is not root node. This Preprocessor only processes root JDF nodes.");
			return jdf;
		}
		String src = jdf.toXML();
		StrSubstitutor substitutor = new StrSubstitutor(getVariablesMap());
		src = substitutor.replace(src);
		JDFDoc jdfDoc = new JDFParser().parseString(src);
		if (jdfDoc == null) {
			log.warn("Could not preprocess JDF. Returning original JDF.");
			return jdf;
		}
		return jdfDoc.getJDFRoot();
	}
}
