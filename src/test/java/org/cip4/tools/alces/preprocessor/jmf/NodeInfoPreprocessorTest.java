/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.jmf.AlcesMessageIDFactory;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jdf.NodeInfoPreprocessor;

public class NodeInfoPreprocessorTest extends AlcesTestCase {

	public void testPreprocess() throws PreprocessorException, IOException {
		JDFPreprocessor pp = new NodeInfoPreprocessor();
		JDFNode jdf1 = getTestFileAsJDF("MISPrepress_ICS_PlateMaking.jdf");
		JDFNode jdf2 = getTestFileAsJDF("MISPrepress_ICS_PlateMaking.jdf");
		assertEquals(jdf1.toXML(), jdf2.toXML());
		JDFNode jdf3 = pp.preprocess(jdf2);
		assertSame(jdf2, jdf3);
		assertNotSame(jdf1, jdf3);
		List jmfs = jdf3.getChildrenByTagName(ElementName.JMF, null, null, false, false, 0);
		for (Iterator i = jmfs.iterator(); i.hasNext();) {
			JDFJMF jmf = (JDFJMF) i.next();
			List msgs = jmf.getMessageVector(null, null);
			for (Iterator j = msgs.iterator(); j.hasNext();) {
				JDFMessage msg = (JDFMessage) j.next();
				assertTrue(msg.getID().startsWith(AlcesMessageIDFactory.PREFIX));
			}
		}
	}

	public void testPreprocessWithContext() throws PreprocessorException, IOException {
		final String prefix = "ELK";
		PreprocessorContext context = new PreprocessorContext();
		context.addAttribute(NodeInfoPreprocessor.MESSAGEID_PREFIX_ATTR, prefix);
		JDFPreprocessor pp = new NodeInfoPreprocessor();
		JDFNode jdf1 = getTestFileAsJDF("MISPrepress_ICS_PlateMaking.jdf");
		JDFNode jdf2 = getTestFileAsJDF("MISPrepress_ICS_PlateMaking.jdf");
		assertEquals(jdf1.toXML(), jdf2.toXML());
		JDFNode jdf3 = pp.preprocess(jdf2, context);
		assertSame(jdf2, jdf3);
		assertNotSame(jdf1, jdf3);
		List jmfs = jdf3.getChildrenByTagName(ElementName.JMF, null, null, false, false, 0);
		for (Iterator i = jmfs.iterator(); i.hasNext();) {
			JDFJMF jmf = (JDFJMF) i.next();
			List msgs = jmf.getMessageVector(null, null);
			for (Iterator j = msgs.iterator(); j.hasNext();) {
				JDFMessage msg = (JDFMessage) j.next();
				assertTrue(msg.getID() + " did not start with " + prefix + ".", msg.getID()
						.startsWith(prefix));
			}
		}
	}

}
