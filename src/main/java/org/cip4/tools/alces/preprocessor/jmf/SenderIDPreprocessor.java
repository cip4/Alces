/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocesses a JMF message by replacing JMF/@SenderID.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class SenderIDPreprocessor implements Preprocessor {

	public static final String SENDERID_ATTR = "org.cip4.tools.alces.SenderIDPreprocessor.SenderID";

	private String senderIDValue;

	private static Logger log = LoggerFactory.getLogger(SenderIDPreprocessor.class);

	public SenderIDPreprocessor() {
		this(null);
	}

	public SenderIDPreprocessor(String senderId) {
		setSenderID(senderId);
	}

	public void setSenderID(String senderId) {
		senderIDValue = senderId;
	}

	public String getSenderID() {
		return senderIDValue;
	}

	/**
	 * Preprocesses a JMF message by replacing SenderID.
	 */
	public AbstractJmfMessage preprocess(final AbstractJmfMessage message) {
		return preprocess(message, null);
	}

	public AbstractJmfMessage preprocess(AbstractJmfMessage message, PreprocessorContext context) {
		if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			log.debug("Message not preprocessed because it did not contain JMF. Content-type was: " + message.getContentType());
			return message;
		}
		// Get SenderID
		final String senderId;
		if (context != null && context.getAttribute(SENDERID_ATTR) != null) {
			senderId = (String) context.getAttribute(SENDERID_ATTR);
		} else if (getSenderID() != null) {
			senderId = getSenderID();
		} else {
			senderId = "Alces SenderIDPreprocessor is not configured with a SenderID";
		}

		if (log.isDebugEnabled()) {
			log.debug("Preprocessor input: " + message.toString());
		}

		JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
		jmf.setSenderID(senderId);
		message.setBody(jmf.toXML());

		if (log.isDebugEnabled()) {
			log.debug("Preprocessor output: " + message);
		}
		return message;
	}
}