/*
 * Created on Mar 27, 2007
 */
package org.cip4.elk.alces.preprocessor.jdf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.preprocessor.PreprocessorContext;
import org.cip4.elk.alces.preprocessor.PreprocessorException;
import org.cip4.jdflib.auto.JDFAutoComChannel.EnumChannelType;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFComChannel;

public class ComChannelPreprocessorTest extends AlcesTestCase {

	public void testPreprocessDefaultEmail() throws PreprocessorException,
			URISyntaxException, IOException {
		final String email = "alces@example.org";
		final ComChannelPreprocessor p = new ComChannelPreprocessor(email);
		JDFNode jdf = getTestFileAsJDF("CIP4Blossoms_Gamsys-Partner.jdf");
		jdf = p.preprocess(jdf);
		assertEmails(jdf, email);
	}

	public void testPreprocessGeneratedEmail() throws PreprocessorException,
			URISyntaxException, IOException {
		final ComChannelPreprocessor p = new ComChannelPreprocessor();
		JDFNode jdf = getTestFileAsJDF("CIP4Blossoms_Gamsys-Partner.jdf");
		jdf = p.preprocess(jdf);
		assertEmails(jdf);
	}

	public void testPreprocessConext() throws PreprocessorException,
			URISyntaxException, IOException {
		final String email = "alces@example.org";
		final ComChannelPreprocessor p = new ComChannelPreprocessor();
		final PreprocessorContext context = new PreprocessorContext();
		context.addAttribute(ComChannelPreprocessor.EMAIL_CONTEXT_ATTR, email);
		JDFNode jdf = getTestFileAsJDF("CIP4Blossoms_Gamsys-Partner.jdf");
		jdf = p.preprocess(jdf, context);
		assertEmails(jdf, email);
	}

	private boolean assertEmails(JDFNode jdf) {
		final List<KElement> comChannels = getEmailComChannels(jdf);
		for (KElement comChannelElement : comChannels) {
			final JDFComChannel comChannel = (JDFComChannel) comChannelElement;
			assertTrue(comChannel.buildXPath("/", 1), comChannel.getLocator()
					.endsWith(ComChannelPreprocessor.DEFAULT_HOSTNAME));
			assertEquals(comChannel.getLocator(), comChannel.getLocator()
					.indexOf("@"), comChannel.getLocator().indexOf(
					ComChannelPreprocessor.DEFAULT_HOSTNAME) - 1);
		}
		return true;
	}

	private boolean assertEmails(JDFNode jdf, String email) {
		final List<KElement> comChannels = getEmailComChannels(jdf);
		for (KElement comChannelElement : comChannels) {
			final JDFComChannel comChannel = (JDFComChannel) comChannelElement;
			assertEquals(comChannel.buildXPath("/", 1), email, comChannel
					.getLocator());
		}
		return true;
	}

	private List<KElement> getEmailComChannels(JDFNode jdf) {
		return jdf.getChildrenByTagName(ElementName.COMCHANNEL, null,
				new JDFAttributeMap(AttributeName.CHANNELTYPE,
						EnumChannelType.Email.getName()), false, false, 0);
	}

}
