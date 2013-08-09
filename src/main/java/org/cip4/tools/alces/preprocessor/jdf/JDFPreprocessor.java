/*
 * Created on Mar 20, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;

/**
 * This class preprocesses a <i>JDF instance</i>. For example, an
 * implementation of this class could replace the <code>JDF/@JobID</code> and
 * be used to preprocess JDF instances before submitting them to a <i>JDF Device</i>.
 * 
 * @author Claes Buckwalter (claes@jtech.se)
 */
public interface JDFPreprocessor {

	public static String PREPROCESSING_ENABLED = "org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor.PreprocessingEnabled";
	
	/**
	 * Preprocesses a JDF insance, modifying it in some way. The
	 * <code>JDFNode</code> object returned by this method is the same
	 * <code>JDFNode</code> object that was passed as input.
	 * 
	 * @param jdf
	 *            the JDF instance to preprocess
	 * @return the preprocessed JDF instance
	 */
	public JDFNode preprocess(final JDFNode jdf) throws PreprocessorException;

	/**
	 * Preprocesses a JDF insance, modifying it in some way. The
	 * <code>JDFNode</code> object returned by this method is the same
	 * <code>JDFNode</code> object that was passed as input.
	 * 
	 * @param jdf
	 *            the JDF instance to preprocess
	 * @param context
	 *            a preprocessing context that may contain attributes that can
	 *            be used by this preprocesser
	 * @return the preprocessed JDF instance@throws PreprocessorException
	 */
	public JDFNode preprocess(final JDFNode jdf, PreprocessorContext context)
			throws PreprocessorException;

}
