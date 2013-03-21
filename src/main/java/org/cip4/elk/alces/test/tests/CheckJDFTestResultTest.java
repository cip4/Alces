/*
 * Created on Jul 12, 2007
 */
package org.cip4.elk.alces.test.tests;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

public class CheckJDFTestResultTest extends TestCase {

	public void testCompileStyleSheet() throws TransformerException {
		File stylesheet = new File("src/conf/report/checkjdf.xsl");
		assertTrue(stylesheet.canRead());
		Transformer transformer;
		Source xslSource = new StreamSource(stylesheet);
		TransformerFactory factory = TransformerFactory.newInstance();
		transformer = factory.newTransformer(xslSource);		
		Source xmlSource = new StreamSource(new File("testarea/checkjdf.xml"));
		Writer stringWriter = new StringWriter();
		transformer.transform(xmlSource, new StreamResult(stringWriter));
		System.out.println(stringWriter.toString());
	}

}
