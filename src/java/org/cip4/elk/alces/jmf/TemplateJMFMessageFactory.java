/*
 * Created on Sep 3, 2004
 */
package org.cip4.elk.alces.jmf;

import java.io.InputStream;

import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;

/**
 * An implementation of <code>JMFMessageFactory</code> that uses template XML
 * files to create JDF elements. To be recognized by this factory the template
 * XML files must be in a package "jmf_templates" located in the classpath.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TemplateJMFMessageFactory extends DefaultJMFMessageFactory {

    protected TemplateJMFMessageFactory() {
        super();
    }

    /**
     * Creates a new JMF message from a template file. If no template file is
     * found with the specified name then the super class's implementation of 
     * this method is used, see
     * {@link DefaultJMFMessageFactory#createJDFElement(String) DefaultJMFMessageFactory.createJMF(String)}.
     * <p>
     * When creating a JMF message the path to the template file used is
     * constructed from the specified element name using the following pattern:
     * </p>
     * <p>
     * <code>jmf_templates/<em>templateName</em>.xml</code>
     * </p>
     * 
     * @see DefaultJMFMessageFactory#createJMF(String)
     */
    public JDFJMF createJMF(String templateName) {
        // Build the template path and load the template from the classpath
        String templateFile = "jmf_templates/" + templateName + ".xml";
        InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(templateFile);
        // If no template exists, use the default implementation
        if (stream == null) {
            return super.createJMF(templateName);
        }
        return new JDFParser().parseStream(stream).getJMFRoot();
    }

}