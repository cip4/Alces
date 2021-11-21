/*
 * Created on Apr 25, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;

/**
 * This class preprocesses JMF messages. For example, a message might be
 * generated from a template that contains a timestamp. A preprocessor could
 * update the timestamp before sending the message.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public interface Preprocessor {
	
	public static String PREPROCESSING_ENABLED = "org.cip4.tools.alces.preprocessor.jmf.Preprocessor.PreprocessingEnabled";

	/**
	 * Preprocesses a message, modifying it in some way. The
	 * <code>Message</code> object returned by this method is the same
	 * <code>Message</code> object that was passed as input.
	 * 
	 * @param message
	 *            the message to preprocess
	 * @return the preprocessed message
	 */
	public AbstractJmfMessage preprocess(final AbstractJmfMessage message) throws PreprocessorException;

	/**
	 * Preprocesses a message, modifying it in some way. The
	 * <code>Message</code> object returned by this method is the same
	 * <code>Message</code> object that was passed as input.
	 * 
	 * @param message
	 *            the message to preprocess
	 * @param context
	 *            a preprocessing context that may contain attributes that can
	 *            be used by this preprocesser
	 * @return the preprocessed message
	 */
	public AbstractJmfMessage preprocess(final AbstractJmfMessage message, PreprocessorContext context) throws PreprocessorException;
}
