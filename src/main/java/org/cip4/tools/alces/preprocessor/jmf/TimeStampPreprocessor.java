/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.apache.log4j.Logger;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.util.JDFDate;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.JDFConstants;

/**
 * Preprocesses a JMF message by replacing JMF/@TimeStamp
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TimeStampPreprocessor implements Preprocessor {

	private static Logger LOGGER = Logger.getLogger(TimeStampPreprocessor.class);

	public TimeStampPreprocessor() {
	}

	/**
	 * Preprocesses a JMF message by replacing JMF/@TimeStamp
	 */
	public Message preprocess(Message message, PreprocessorContext context) throws PreprocessorException {
		if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			LOGGER.debug("Message not preprocessed because it did not contain JMF. Content-type was: " + message.getContentType());
			return message;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Preprocessor input: " + message.toString());
		}

		JDFJMF jmf = message.getBodyAsJMF();
		jmf.setTimeStamp(new JDFDate());
		message.setBody(jmf.toXML());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Preprocessor output: " + message);
		}
		return message;
	}

	public Message preprocess(Message message) throws PreprocessorException {
		return preprocess(message, null);
	}
}