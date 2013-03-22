/*
 * Created on Mar 20, 2007
 */
package org.cip4.tools.alces.jdf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Generates a JDF JobID (<em>/JDF/@JobID</em>) on the format
 * <code>ALCES_rrrrrr_n_yyyyMMddHHmmss</code>.
 * 
 * @author Claes Buckwalter (claes@jtech.se)
 */
public class AlcesJobIDFactory implements JobIDFactory {

	protected final DateFormat dateFormat;

	protected static int counter = 0;

	protected static final String PREFIX = "ALCES_"
			+ RandomStringUtils.randomAlphanumeric(6).toUpperCase() + "_";

	public AlcesJobIDFactory() {
		dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	}

	/**
	 * @return a JDF job ID on the format
	 *         <code>ALCES_rrrrrr_n_yyyyMMddHHmmss</code>
	 */
	public synchronized String newJobID() {
		return PREFIX + (counter++) + "_" + dateFormat.format(new Date());
	}

	/**
	 * Generates a JobID based on an existing JobID. The new JobID is the
	 * existing one suffixed with the string
	 * <code> [Alces yyyy-MM-dd HH.mm.ss.SSS]</code>. If the new JobID is
	 * longer than 63 characters (a limitation required for conformance with
	 * CIP4's Base ICS) the existing JobID is trimmed so that the length of the
	 * new JobID is exactly 63 characters.
	 * 
	 * @param existingJobId
	 *            a string used as a prefix for the new JobID generated
	 * @return a new JobID of maximum length 63 characters
	 */
	public synchronized String newJobID(String existingJobId) {
		String alcesSuffix = " [" + newJobID() + "]";
		String prefix = existingJobId;
		if (prefix.length() > 63 - alcesSuffix.length()) {
			prefix = prefix.substring(0, 63 - alcesSuffix.length());
		}
		return prefix + alcesSuffix;
	}
}
