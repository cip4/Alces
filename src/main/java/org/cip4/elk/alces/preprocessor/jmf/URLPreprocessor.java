/*
 * Created on Feb 21, 2007
 */
package org.cip4.elk.alces.preprocessor.jmf;

import java.util.Map;
import java.util.Properties;

import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.preprocessor.PreprocessorContext;
import org.cip4.elk.alces.preprocessor.PreprocessorException;
import org.cip4.elk.alces.util.ConfigurationHandler;

/**
 * A preprocessor that replaces the following attributes with URL values with a
 * specified URL:
 * <ul>
 * <li>//@ReturnJMF</li>
 * <li>//@ReturnURL</li>
 * <li>//@AcknowledgeURL</li>
 * <li>//jdf:Subscription/@URL</li>
 * <li>//jdf:StopPersChParams/@URL</li>
 * </ul>
 * 
 * @see {@link #URLPreprocessor(String)}
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class URLPreprocessor extends XPathPreprocessor {

	public static final String URL_ATTR = "org.cip4.elk.alces.URLPreprocessor.URL";
	
    private String url = null;

    public URLPreprocessor(String url) {
        setURL(url);
    }

    public URLPreprocessor() {
    }

    public void setURL(String url) {
        final Map<Object, Object> xpathValuePairs = new Properties();
        ConfigurationHandler confHand = ConfigurationHandler.getInstance();        
        if (confHand.getProp(ConfigurationHandler.UPDATE_RETURNJMF).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@ReturnJMF", url);
        }        
        if (confHand.getProp(ConfigurationHandler.UPDATE_RETURNURL).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@ReturnURL", url);
        }        
        if (confHand.getProp(ConfigurationHandler.UPDATE_WATCHURL).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@WatchURL", url);
        }        
        if (confHand.getProp(ConfigurationHandler.UPDATE_ACKNOWLEDGEURL).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@AcknowledgeURL", url);
        }
        xpathValuePairs.put("//jdf:Subscription/@URL", url);
        xpathValuePairs.put("//jdf:StopPersChParams/@URL", url);
        setXpathValuePairs(xpathValuePairs);
        setDefaultNsPrefix("jdf");
    }

    public String getURL() {
        return url;
    }

    @Override
	public Message preprocess(Message message, PreprocessorContext context) throws PreprocessorException {
    	if (context != null && context.getAttribute(URL_ATTR) != null) {
    		setURL((String) context.getAttribute(URL_ATTR));
    	}
    	return super.preprocess(message, context);
    }
}
