/*
 * Created on Mar 27, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.preprocessor.jdf.UrlResolvingPreprocessor;

public class UrlResolvingPreprocessorTest extends AlcesTestCase {

	public void testPreprocess() throws PreprocessorException, URISyntaxException, IOException {
		String baseUrl = "http://localhost:9090/";
		UrlResolvingPreprocessor p = new UrlResolvingPreprocessor(baseUrl);
		JDFNode jdf = getTestFileAsJDF("UrlResolvingPreprocessorTest.jdf");
		jdf = p.preprocess(jdf);
		assertResolvedUrls(jdf, baseUrl);
		
	}
	
	private boolean assertResolvedUrls(JDFNode jdf, String baseUrl) throws URISyntaxException {
		List fileSpecs = jdf.getChildrenByTagName(ElementName.FILESPEC, null, null, false, false, 0);
		for (Iterator i=fileSpecs.iterator(); i.hasNext(); ) {
			JDFFileSpec fileSpec = (JDFFileSpec) i.next();
			String url = fileSpec.getURL();
			assertTrue("URL has not been preprocessed: " + url, url.startsWith(baseUrl));
			URI uri = new URI(url);
		}
		return true;
	}

}
