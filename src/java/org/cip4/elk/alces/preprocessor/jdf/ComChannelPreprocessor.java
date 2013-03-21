/*
 * Copyright (c) 2001-2008 Agfa-Graphics N.V. All Rights Reserved.
 *
 * The material contained herein may not be reproduced in whole or
 * in part without the prior written consent of Agfa-Graphics N.V.
 *
 * $File: //delano/build-1.0/release/build-tools/jalopy/CodingStyle_1.6_154.xml $
 * $Revision: #6 $
 *
 * $Change: 66485 $
 * $DateTime: 2008/02/15 14:09:48 $
 * $Author: cbuckwalter $
 */

/*
 * Created on Mar 20, 2007
 */
package org.cip4.elk.alces.preprocessor.jdf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.elk.alces.preprocessor.PreprocessorContext;
import org.cip4.elk.alces.preprocessor.PreprocessorException;
import org.cip4.jdflib.auto.JDFAutoComChannel.EnumChannelType;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFComChannel;

/**
 * A preprocessor that updates the <em>//ComChannel</em> elements of a JDF.
 * 
 * <p>
 * Currently replaces existing e-mail addresses in
 * <em>//ComChannel[@Locator='Email"]</em> by a specified or random e-mail
 * address. To specify an e-mail address, add it to the <code>
 * PreprocessorContext</code>
 * using key {@link #EMAIL_CONTEXT_ATTR}. If no e-mail address is specified the
 * default hostname ({@link #DEFAULT_HOSTNAME}) is used with the original
 * e-mail address as the account name.
 * </p>
 * 
 * @see #EMAIL_PREFIX_ATTR
 * @see #DEFAULT_HOSTNAME
 */
public class ComChannelPreprocessor implements JDFPreprocessor {
	/**
	 * Attribute for storing the
	 * <em>//ComChannel[@ChannelType='Email']/@Locator</em> in the context. If
	 * this preprocessor finds this attribute in the context it will use it as a
	 * the e-mail address for all Email ComChannels found.
	 */
	public static final String EMAIL_CONTEXT_ATTR = "org.cip4.elk.alces.ComChannelPreprocessor.Email";

	/**
	 * Default e-mail address hostname: <i>cip4.example.org</i>. Used when no
	 * e-mail address is specified in the <code>PreprocessorContext</code> or
	 * no default e-mail address is specified. The hostname is prefixed by the
	 * original e-mail address with the <em>@</em> character replaced with a
	 * <em>-</em> character.
	 */
	public static final String DEFAULT_HOSTNAME = "cip4.example.org";

	private static Log log = LogFactory.getLog(ComChannelPreprocessor.class);

	private final String defaultEmail;

	public ComChannelPreprocessor() {
		this(null);
	}

	/**
	 * Creates a ContactPreprocessor.
	 * 
	 * @param defaultEmail
	 *            the e-mail address that will replace the existing ones in the
	 *            JDF
	 */
	public ComChannelPreprocessor(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public JDFNode preprocess(JDFNode jdf) throws PreprocessorException {
		return preprocess(jdf, null);
	}

	/**
	 * Updates the JDF node's NodeInfo (<em>/JDF/NodeInfo</em>).
	 * 
	 * @see #MESSAGEID_PREFIX_ATTR
	 * @see #SUBSCRIPTION_URL_ATTR
	 */
	public JDFNode preprocess(JDFNode jdf, PreprocessorContext context)
			throws PreprocessorException {
		log.debug("Updating Contact elements of JDF '" + jdf.getJobID(true)
				+ "'...");
		List<KElement> comChannels = jdf.getChildrenByTagName(
				ElementName.COMCHANNEL, null, new JDFAttributeMap(
						AttributeName.CHANNELTYPE, EnumChannelType.Email
								.getName()), false, false, 0);
		for (KElement comChannelElement : comChannels) {
			final JDFComChannel comChannel = (JDFComChannel) comChannelElement;
			final String oldEmail = comChannel.getLocator();
			final String newEmail;
			// Replace e-mail address
			if (context != null
					&& context.getAttribute(EMAIL_CONTEXT_ATTR) != null) {
				newEmail = (String) context.getAttribute(EMAIL_CONTEXT_ATTR);
			} else if (defaultEmail != null) {
				newEmail = defaultEmail;
			} else {
				newEmail = oldEmail.replace('@', '-') + "@" + DEFAULT_HOSTNAME;
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Replacing Email '%s' with '%s' in %s/@Locator",
						oldEmail, newEmail, comChannel.buildXPath("/", 1)));
			}
			comChannel.setLocator(newEmail);
		}
		return jdf;
	}
}
