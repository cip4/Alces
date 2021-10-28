/*
 * Created on Mar 27, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import org.cip4.jdflib.auto.JDFAutoComChannel.EnumChannelType;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFComChannel;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ComChannelPreprocessorTest extends AlcesTestCase {

    @Test
    public void testPreprocessDefaultEmail() throws PreprocessorException,
            URISyntaxException, IOException {
        final String email = "alces@example.org";
        final ComChannelPreprocessor p = new ComChannelPreprocessor(email);
        JDFNode jdf = getTestFileAsJDF("CIP4Blossoms_Gamsys-Partner.jdf");
        jdf = p.preprocess(jdf);
        assertEmails(jdf, email);
    }

    @Test
    public void testPreprocessGeneratedEmail() throws PreprocessorException,
            URISyntaxException, IOException {
        final ComChannelPreprocessor p = new ComChannelPreprocessor();
        JDFNode jdf = getTestFileAsJDF("CIP4Blossoms_Gamsys-Partner.jdf");
        jdf = p.preprocess(jdf);
        assertEmails(jdf);
    }

    @Test
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
            Assertions.assertTrue(comChannel.getLocator()
                    .endsWith(ComChannelPreprocessor.DEFAULT_HOSTNAME), comChannel.buildXPath("/", 1));
            Assertions.assertEquals(comChannel.getLocator()
                    .indexOf("@"), comChannel.getLocator().indexOf(
                    ComChannelPreprocessor.DEFAULT_HOSTNAME) - 1, comChannel.getLocator());
        }
        return true;
    }

    private boolean assertEmails(JDFNode jdf, String email) {
        final List<KElement> comChannels = getEmailComChannels(jdf);
        for (KElement comChannelElement : comChannels) {
            final JDFComChannel comChannel = (JDFComChannel) comChannelElement;
            Assertions.assertEquals(email, comChannel.getLocator(), comChannel.buildXPath("/", 1));
        }
        return true;
    }

    private List<KElement> getEmailComChannels(JDFNode jdf) {
        return jdf.getChildrenByTagName(ElementName.COMCHANNEL, null,
                new JDFAttributeMap(AttributeName.CHANNELTYPE,
                        EnumChannelType.Email.getName()), false, false, 0);
    }

}
