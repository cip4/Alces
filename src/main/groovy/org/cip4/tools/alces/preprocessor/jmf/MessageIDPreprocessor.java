package org.cip4.tools.alces.preprocessor.jmf;

import java.util.Iterator;
import java.util.List;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.jmf.AlcesMessageIDFactory;
import org.cip4.tools.alces.jmf.MessageIDFactory;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.util.ApplicationContextUtil;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocesses a JMF message by replacing message IDs (<em>/JMF/Message/@ID</em>) with unique IDs. See {@link AlcesMessageIDFactory} for details on the format
 * of the generated message IDs.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class MessageIDPreprocessor implements Preprocessor {

	private MessageIDFactory factory;

	private static Logger log = LoggerFactory.getLogger(MessageIDPreprocessor.class);

	public MessageIDPreprocessor() {
		factory = new AlcesMessageIDFactory();
	}

	/**
	 * Preprocesses a JMF message by replacing message IDs
	 */
	public AbstractJmfMessage preprocess(AbstractJmfMessage message, PreprocessorContext context) {
		if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			log.warn("Message not preprocessed because it did not contain JMF. Content-type was: " + message.getContentType());
			return message;
		}
		if (log.isDebugEnabled()) {
			log.debug("Preprocessor input: " + message.toString());
		}

		SettingsService settingsService = ApplicationContextUtil.getBean(SettingsService.class);

		JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
		// update ID only if required
		if (settingsService.getProp(SettingsServiceImpl.UPDATE_MESSAGEID).equalsIgnoreCase("TRUE")) {
			List messages = jmf.getMessageVector(null, null);
			for (Iterator i = messages.iterator(); i.hasNext();) {
				JDFMessage m = (JDFMessage) i.next();
				m.setID(factory.newMessageID());
			}
		}
		message.setBody(jmf.toXML());

		if (log.isDebugEnabled()) {
			log.debug("Preprocessor output: " + message);
		}
		return message;
	}

	public AbstractJmfMessage preprocess(final AbstractJmfMessage message) {
		return preprocess(message, null);
	}

}