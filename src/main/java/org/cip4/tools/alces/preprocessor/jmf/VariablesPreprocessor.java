/*
 * Created on Feb 21, 2007
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.ConfigurationHandler;

/**
 * A preprocessor that replaces variables in the JMF. See {@linkplain http ://commons .apache.org/lang/api-2.3/org/apache/commons/lang/text/StrSubstitutor.html}
 * for details on the the variables name-value map and the syntax in the JMF template.
 * 
 * <p>
 * Default variables are first loaded from {@link ConfigurationHandler#getPropFile()} and then from a Java Property file named
 * <code>VariablesPreprocessor.properties</code> if it exists at the root of the classpath. The property names in the properties files can be used as variable
 * names and will be replace by their property values.
 * </p>
 * 
 * @author Claes Buckwalter
 * @deprecated Use org.cip4.tools.alces.preprocessor.VariablesPreprocessor instead
 */
@Deprecated
public class VariablesPreprocessor implements Preprocessor {
	private static final Logger LOGGER = Logger.getLogger(VariablesPreprocessor.class);

	public static final String VARIABLES_MAP = "org.cip4.tools.alces.jmf.VariablesPreprocessor.VARIABLES_MAP";

	public static final String VARIABLES_FILE = "VariablesPreprocessor.properties";

	public Map<String, String> variablesMap = new HashMap<String, String>();

	/**
	 * @param map A map containing variable name-value pairs. If any variables have the same names as the defaults, then the default values are overwritten.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/ StrSubstitutor.html
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
	 * @param map A map to merge with the existing variables map. If any variables already exist there values are overwritten by the new ones.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/ StrSubstitutor.html
	 */
	public void setVariablesMap(Map<String, String> map) {
		variablesMap.putAll(map);
	}

	/**
	 * Copies the variable mappings from a <code>Properties</code> object.
	 * 
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/ StrSubstitutor.html
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
	 * @see http://commons.apache.org/lang/api-2.3/org/apache/commons/lang/text/ StrSubstitutor.html
	 */
	public Map<String, String> getVariablesMap() {
		return variablesMap;
	}

	public Message preprocess(final Message message, final PreprocessorContext context) throws PreprocessorException {
		if (context != null && context.getAttribute(VARIABLES_MAP) != null) {
			setVariablesMap((Map<String, String>) context.getAttribute(VARIABLES_MAP));
		}
		final String jmf = message.getBody();
		final StrSubstitutor substitutor = new StrSubstitutor(getVariablesMap());
		final String newJmf = substitutor.replace(jmf);
		message.setBody(newJmf);
		return message;
	}

	public Message preprocess(final Message message) throws PreprocessorException {
		return preprocess(message, null);
	}

	/**
	 * Loads the variable mappings. First loads the variable mappings from Alces's configuration file. Then overrides them if they can be loaded from the file
	 * <code>VariablesPreprocessor.properties</code> found at the root of the classpath.
	 */
	private void loadVariablesMap() {
		final Properties props = ConfigurationHandler.getInstance().getPropFile();
		setVariablesMap(props);
		final InputStream input = VariablesPreprocessor.class.getClassLoader().getResourceAsStream(VARIABLES_FILE);
		if (input == null) {
			LOGGER.warn(String.format("Could not load variables from Java Properties file %s because it could not be found.", VARIABLES_FILE));
			return;
		}
		try {
			props.load(input);
			setVariablesMap(props);
		} catch (IOException e) {
			LOGGER.error("Could not load variables from Java Properties file.", e);
		}
	}
}
