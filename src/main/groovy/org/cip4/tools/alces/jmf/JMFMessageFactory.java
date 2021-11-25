/*
 * Created on Sep 2, 2004
 */
package org.cip4.tools.alces.jmf;

import java.io.InputStream;
import java.util.Properties;

import org.cip4.jdflib.jmf.JDFJMF;

/**
 * Defines a factory API that enables applications to obtain a factory for
 * creating JMF messages.
 * <p>
 * Usage examples:
 * </p>
 * 
 * <pre>
 * JMFMessageFactory factory = JMFMessageFactory.getInstance();
 * 
 * // Create a JMF element
 * JDFJMF jmf = factory.createJMF();
 * 
 * JDFJMF jmf = factory.createJMF(&quot;QueryKnownMessages&quot;);
 * 
 * </pre>
 * 
 * <p>
 * <em>This class is thread save.</em>
 * </p>
 * 
 * @todo Reimplement without using synchronization, but still guaranteeing
 *       thread safety.
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public abstract class JMFMessageFactory {

    private static final String JMFELEMENTFACTORY_KEY = "org.cip4.tools.alces.jmf.JMFMessageFactory";

    private static final String CONFIG_FILE = "org/cip4/tools/alces/jmf/JMFMessageFactory.properties";

    private static JMFMessageFactory instance;

    /**
     * Private constructor so that this class cannot be instantiated except from
     * its factory method.
     */
    protected JMFMessageFactory() {
    }

    /**
     * Obtain a new instance of a <code>JMFMessageFactory</code>. This method
     * uses the following ordered lookup to determine the
     * <code>JMFMessageFactory</code> class to load:
     * <ul>
     * <li>Uses the <code>org.cip4.tools.alces.jmf.JMFMessageFactory</code>
     * system property. The value of the system property is the fully qualified
     * name of the implementation class.</li>
     * <li>Uses the first properties file
     * <code>org/cip4/tools/alces/jmf/JMFMessageFactory.properties</code> found
     * in the classpath. This configuration file is in standard
     * <code>java.util.Properties</code> format and uses the same key as the
     * system property defined above. Again, the property's value is the fully
     * qualified name of the implementation class.</li>
     * </ul>
     * Once an application has obtained a reference to a
     * <code>JMFMessageFactory</code> implementation it can use the factory to
     * generate <code>JDFJMF</code>s.
     * 
     * @return a reference to a JMFMessageFactory
     * @throws JMFMessageFactoryLoaderException
     *             if the factory cannot be loaded
     */
    public static synchronized JMFMessageFactory getInstance() {
        // Creates an instance if it has not been instantiated yet
        if (instance == null) {
            loadInstance();
        }
        return instance;
    }

    /**
     * Loads the factory instance based on the system property or configuration
     * file.
     * 
     * @see #getInstance()
     */
    protected static void loadInstance() {
        try {
            // Checks for the system property
            String className = System.getProperty(JMFELEMENTFACTORY_KEY);
            if (className == null) {
                // Checks for the properties file in the classpath
                InputStream stream = JMFMessageFactory.class.getClassLoader()
                        .getResourceAsStream(CONFIG_FILE);
                // Loads the properties file
                Properties props = new Properties();
                props.load(stream);
                className = props.getProperty(JMFELEMENTFACTORY_KEY);
            }
            // Loads the class
            Class factoryClass = Class.forName(className);
            instance = (JMFMessageFactory) factoryClass.newInstance();
        } catch (Exception e) {
            throw new JMFMessageFactoryLoaderException(
                    "Could not load the JMFMessageFactory implementation. Verify that the system property '"
                            + JMFELEMENTFACTORY_KEY
                            + "' exists or that a property file '"
                            + CONFIG_FILE
                            + "' exists in the classpath. The property file must contain the same key as the system property. The value of the property should be the fully qualified name of the JMFMessageFactory implementation to be loaded.",
                    e);
        }
    }

    /**
     * Creates an empty JMF node.
     * 
     * @return a JMF node
     */
    public abstract JDFJMF createJMF();

    /**
     * Creates a JMF message based on the specified template name. An implementation
     * could for example use xsi:type as the template name.
     * 
     * @see JDF Specification 1.2, Table 5-2, xsi:type
     * @param templateName  an identifier for the type of message to create
     * @return
     */
    public abstract JDFJMF createJMF(String templateName);

}