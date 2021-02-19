/*
 * Created on Jul 12, 2007
 */
package org.cip4.tools.alces.test.tests;

import org.junit.Assert;
import org.junit.Test;

public class CheckJDFTestResultTest {

    private final static String RES_REPORT_XSL_FILE = "/org/cip4/tools/alces/report/checkjdf.xsl";

    // public void testCompileStyleSheet() throws TransformerException {
    //
    // File stylesheet = new File(CheckJDFTest.class.getResource(RES_REPORT_XSL_FILE).getFile());
    //
    // assertTrue(stylesheet.canRead());
    // Transformer transformer;
    // Source xslSource = new StreamSource(stylesheet);
    // TransformerFactory factory = TransformerFactory.newInstance();
    // transformer = factory.newTransformer(xslSource);
    // Source xmlSource = new StreamSource(new File("testarea/checkjdf.xml"));
    // Writer stringWriter = new StringWriter();
    // transformer.transform(xmlSource, new StreamResult(stringWriter));
    // System.out.println(stringWriter.toString());
    // }

    // TODO
    @Test
    public void testSkipTest() {
        Assert.assertTrue(true);
    }

}
