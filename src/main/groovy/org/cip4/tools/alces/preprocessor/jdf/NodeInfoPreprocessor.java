/*
 * Created on Mar 20, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import java.util.List;
import java.util.Vector;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFNodeInfo;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.jdflib.jmf.JDFSubscription;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.jmf.AlcesMessageIDFactory;
import org.cip4.tools.alces.jmf.MessageIDFactory;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates the <em>/JDF/NodeInfo</em> element of a JDF.
 * 
 * @see #MESSAGEID_PREFIX_ATTR
 * @see #SUBSCRIPTION_URL_ATTR
 */
public class NodeInfoPreprocessor implements JDFPreprocessor {

	/**
	 * Attribute for storing the <em>/JMF/Message/@ID</em> in the context. If this preprocessor finds this attribute in the context it will use it as a prefix
	 * for the IDs of the messages found in the <em>/JDF/NodeInfo</em> element.
	 */
	public final static String MESSAGEID_PREFIX_ATTR = "org.cip4.tools.alces.NodeInfoPreprocessor.MessageID";

	/**
	 * Attribute for storing the <em>/JMF/Message/Subscript/@URL</em> in the context. If this preprocessor finds this attribute in the context it will replace
	 * all existing subscription URLs with this attribute's value.
	 */
	public final static String SUBSCRIPTION_URL_ATTR = "org.cip4.tools.alces.NodeInfoPreprocessor.SubscriptionURL";

	private static Logger log = LoggerFactory.getLogger(NodeInfoPreprocessor.class);

	protected final MessageIDFactory factory;

	public NodeInfoPreprocessor() {
		factory = new AlcesMessageIDFactory();
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
	public JDFNode preprocess(JDFNode jdf, PreprocessorContext context) throws PreprocessorException {
		log.debug("Updating NodeInfo of JDF '" + jdf.getJobID(true) + "'...");
		JDFNodeInfo nodeInfo = jdf.getNodeInfo();
		if (nodeInfo == null) {
			return jdf;
		}
		List<JDFJMF> jmfs = (Vector) nodeInfo.getChildrenByTagName(ElementName.JMF, null, null, true, false, 0);
		for (JDFJMF jmf : jmfs) {
			final List<JDFMessage> msgs = (Vector) jmf.getMessageVector(null, null);
			for (JDFMessage msg : msgs) {
				// Replace message ID
				if (context != null && context.getAttribute(MESSAGEID_PREFIX_ATTR) != null) {
					msg.setID(context.getAttribute(MESSAGEID_PREFIX_ATTR) + "_" + factory.newMessageID());
				} else {
					msg.setID(factory.newMessageID());
				}
				// Replace subscription URL
				if (context != null && context.getAttribute(SUBSCRIPTION_URL_ATTR) != null) {
					KElement subElement = msg.getChildByTagName(ElementName.SUBSCRIPTION, null, 0, null, true, false);
					if (subElement != null) {
						((JDFSubscription) subElement).setURL((String) context.getAttribute(SUBSCRIPTION_URL_ATTR));
					}
				}
			}
		}
		return jdf;
	}
}
