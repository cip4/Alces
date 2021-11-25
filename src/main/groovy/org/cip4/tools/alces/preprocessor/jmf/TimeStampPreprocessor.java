/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.util.JDFDate;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocesses a JMF message by replacing JMF/@TimeStamp
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TimeStampPreprocessor implements Preprocessor {

	private static Logger log = LoggerFactory.getLogger(TimeStampPreprocessor.class);

	public TimeStampPreprocessor() {
	}

	/**
	 * Preprocesses a JMF message by replacing JMF/@TimeStamp
	 */
	public AbstractJmfMessage preprocess(AbstractJmfMessage message, PreprocessorContext context) throws PreprocessorException {
		if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			log.debug("Message not preprocessed because it did not contain JMF. Content-type was: " + message.getContentType());
			return message;
		}
		if (log.isDebugEnabled()) {
			log.debug("Preprocessor input: " + message.toString());
		}

		JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
		jmf.setTimeStamp(new JDFDate());
		message.setBody(jmf.toXML());

		if (log.isDebugEnabled()) {
			log.debug("Preprocessor output: " + message);
		}
		return message;
	}

	public AbstractJmfMessage preprocess(AbstractJmfMessage message) throws PreprocessorException {
		return preprocess(message, null);
	}
}