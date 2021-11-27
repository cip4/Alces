/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.jmf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Generates JMF message IDs on the format
 * <em>ALCES_rrrrrr_n_yyyyMMddHHmmss</em>.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class AlcesMessageIDFactory implements MessageIDFactory {

	protected final DateFormat dateFormat;

	protected static int counter = 0;

	public static final String PREFIX = "ALCES_"
			+ RandomStringUtils.randomAlphanumeric(6).toUpperCase() + "_";

	public AlcesMessageIDFactory() {
		dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	}

	public String newMessageID() {
		return PREFIX + (counter++) + "_" + dateFormat.format(new Date());
	}
}
