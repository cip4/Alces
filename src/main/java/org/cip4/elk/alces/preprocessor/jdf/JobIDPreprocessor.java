/*
 * Created on Mar 20, 2007
 */
package org.cip4.elk.alces.preprocessor.jdf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.elk.alces.jdf.AlcesJobIDFactory;
import org.cip4.elk.alces.jdf.JobIDFactory;
import org.cip4.elk.alces.preprocessor.PreprocessorContext;
import org.cip4.elk.alces.preprocessor.PreprocessorException;
import org.cip4.jdflib.node.JDFNode;

/**
 * Replaces <em>/JDF/@JobID</em> of the JDF instance with a generated job ID.
 * 
 * @see #JOBID_ATTR
 * @see #DISABLE_ATTR
 */
public class JobIDPreprocessor implements JDFPreprocessor {

	private static Log log = LogFactory.getLog(JobIDPreprocessor.class);

	/**
	 * Attribute for storing the new JobID in the context. If this preprocessor
	 * finds this attribute in the context it will use it as the new JobID.
	 */
	public final static String JOBID_ATTR = "org.cip4.elk.alces.JobIDPreprocessor.JobID";

	/**
	 * Attribute for temporarily disabling JobID preprocessing. If this preprocessor
	 * finds this attribute in the context it will skip the prepocessing and return the
	 * JDF node unmodified.
	 */
	public final static String DISABLE_ATTR = "org.cip4.elk.alces.JobIDPreprocessor.Disable";

	protected final JobIDFactory factory;

	public JobIDPreprocessor() {
		this(new AlcesJobIDFactory());
	}

	public JobIDPreprocessor(JobIDFactory factory) {
		this.factory = factory;
	}

	/**
	 * Replaces the /JDF/@JobID with a new value that is based on the existing
	 * one. See {@link AlcesJobIDFactory#newJobID(String)} for details on the
	 * new JobID's format.
	 */
	public JDFNode preprocess(final JDFNode jdf) throws PreprocessorException {
		return preprocess(jdf, null);
	}

	/**
	 * Replaces <em>/JDF/@JobID</em> with a new value. See
	 * {@link AlcesJobIDFactory#newJobID(String)} for details on the new JobID's
	 * format.
	 * 
	 * @see #JOBID_ATTR
	 * @see #DISABLE_ATTR
	 */
	public JDFNode preprocess(final JDFNode jdf, final PreprocessorContext context)
			throws PreprocessorException {
		final String jobId;
		if (context != null && context.getAttribute(JOBID_ATTR) != null) {
			jobId = (String) context.getAttribute(JOBID_ATTR);
		} else {
			jobId = factory.newJobID(jdf.getJobID(true));
		}
		log.debug("Replacing JobID of JDF '" + jdf.getJobID(true) + "' with new JobID '" + jobId + "'...");
		return replaceJobID(jdf, jobId);
	}

	protected JDFNode replaceJobID(final JDFNode jdf, final String newJobId) {
		jdf.setJobID(newJobId);
		return jdf;
	}
}
