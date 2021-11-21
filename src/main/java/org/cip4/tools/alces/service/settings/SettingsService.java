package org.cip4.tools.alces.service.settings;

import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.service.testrunner.model.TestSession;

import javax.swing.*;
import java.util.Map;
import java.util.Properties;

/**
 * Business interface for applications settings.
 */
public interface SettingsService {

    /**
     * Returns the property value for a specific key.
     * In case no property is set, a default value will be return.
     *
     * @param key The properties key.
     * @return The properties value.
     */
    String getProp(String key);

    /**
     * Configures Tests that will be run on all incoming messages during a test session.
     *
     * @param session the TestSession to add the incoming Tests to.
     */
    void configureIncomingTests(TestSession session);

    /**
     * Configures Tests that will be run on all outgoing messages during a test session.
     *
     * @param session the TestSession to add the outgoing Tests to
     */
    void configureOutgoingTests(TestSession session);

    /**
     * Returns the URL used for receiving JMF messages. The URL is built from the hostname, port, and context properties.
     *
     * @return the server URL, for example http://localhost:9090/alces/jmf
     */
    String getServerJmfUrl();

    /**
     * Returns an array of the currently enabled <code>Preprocessor</code>s.
     *
     * @return A array of <code>Preprocessor</code>s
     */
    Preprocessor[] getJMFPreprocessors();

    /**
     * Returns an array of the currently enabled <code>JDFPreprocessor</code>s.
     *
     * @return a map of <code>Preprocessor</code>s
     */
    JDFPreprocessor[] getJDFPreprocessors();

    /**
     * Is used to get automated alces started
     *
     * @return The properties.
     */
    Properties getPropFile();

    /**
     * Returns a map of the configured incoming <code>Test</code>s.
     *
     * @return A map where keys are the fully qualified classnames of the configured <code>Test</code> classes; values are the <code>String</code> "true" if the
     * <code>Test</code> is enabled, "false" if it is disabled.
     */
    Map<String, Boolean> getIncomingTestConfig();

    /**
     * Returns a map of the configured outgoing <code>Test</code>s.
     *
     * @return A map where keys are the fully qualified classnames of the configured <code>Test</code> classes; values are the <code>String</code> "true" if the
     * <code>Test</code> is enabled, "false" if it is disabled.
     */
    Map<String, Boolean> getOutgoingTestConfig();

    /**
     * Storing of altered properties
     *
     * @param key
     * @param value
     */
    void putProp(String key, String value);

    /**
     * Returns the General Preferences
     *
     * @return
     */
    Map<String, String> getGeneralPrefs();

    /**
     * Sets the incoming <code>Test</code> configuration.
     *
     * @param testConfig A map where keys are the fully qualified classnames of the configured <code>Test</code> classes; values are the <code>String</code>
     * "true" if the <code>Test</code> is enabled, "false" if it is disabled.
     */
    void setIncomingTestConfig(Map<String, Boolean> testConfig);

    /**
     * Sets the outgoing <code>Test</code> configuration.
     *
     * @param testConfig A map where keys are the fully qualified classnames of the configured <code>Test</code> classes; values are the <code>String</code>
     * "true" if the <code>Test</code> is enabled, "false" if it is disabled.
     */
    void setOutgoingTestConfig(Map<String, Boolean> testConfig);

    void setPreferences(Map<String, String> generalPrefs);

    /**
     * Saves the address history and finds duplicate HTTP-Adresses and remove them before saving
     */
    void saveHistory(ComboBoxModel adresses);

    void saveConfiguration(int windowWidth, int windowHeight, int devicePaneWidth, int testPaneWidth, int mainPaneHeight);

    /**
     * Loads the already used addresses from the properties
     *
     * @return
     */
    String[] loadHistory();
}
