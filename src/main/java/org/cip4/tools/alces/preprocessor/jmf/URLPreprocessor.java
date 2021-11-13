/*
 * Created on Feb 21, 2007
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.util.Map;
import java.util.Properties;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.service.setting.SettingsService;
import org.cip4.tools.alces.service.setting.SettingsServiceImpl;
import org.cip4.tools.alces.util.ApplicationContextUtil;

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

	public static final String URL_ATTR = "org.cip4.tools.alces.URLPreprocessor.URL";
	
    private String url = null;

    public URLPreprocessor(String url) {
        setURL(url);
    }

    public URLPreprocessor() {
    }

    public void setURL(String url) {
        final Map<Object, Object> xpathValuePairs = new Properties();
        SettingsService settingsService = ApplicationContextUtil.getBean(SettingsService.class);
        if (settingsService.getProp(SettingsServiceImpl.UPDATE_RETURNJMF).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@ReturnJMF", url);
        }        
        if (settingsService.getProp(SettingsServiceImpl.UPDATE_RETURNURL).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@ReturnURL", url);
        }        
        if (settingsService.getProp(SettingsServiceImpl.UPDATE_WATCHURL).equalsIgnoreCase("TRUE")) {
            xpathValuePairs.put("//@WatchURL", url);
        }        
        if (settingsService.getProp(SettingsServiceImpl.UPDATE_ACKNOWLEDGEURL).equalsIgnoreCase("TRUE")) {
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
	public AbstractJmfMessage preprocess(AbstractJmfMessage message, PreprocessorContext context) throws PreprocessorException {
    	if (context != null && context.getAttribute(URL_ATTR) != null) {
    		setURL((String) context.getAttribute(URL_ATTR));
    	}
    	return super.preprocess(message, context);
    }
}
