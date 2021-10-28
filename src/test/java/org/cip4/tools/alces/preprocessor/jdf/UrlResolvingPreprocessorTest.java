/*
 * Created on Mar 27, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

public class UrlResolvingPreprocessorTest extends AlcesTestCase {

    @Test
    public void testPreprocess() throws PreprocessorException, URISyntaxException, IOException {
        String baseUrl = "http://localhost:9090/";
        UrlResolvingPreprocessor p = new UrlResolvingPreprocessor(baseUrl);
        JDFNode jdf = getTestFileAsJDF("UrlResolvingPreprocessorTest.jdf");
        jdf = p.preprocess(jdf);
        assertResolvedUrls(jdf, baseUrl);

    }

    private boolean assertResolvedUrls(JDFNode jdf, String baseUrl) throws URISyntaxException {
        List fileSpecs = jdf.getChildrenByTagName(ElementName.FILESPEC, null, null, false, false, 0);
        for (Iterator i = fileSpecs.iterator(); i.hasNext(); ) {
            JDFFileSpec fileSpec = (JDFFileSpec) i.next();
            String url = fileSpec.getURL();
            Assertions.assertTrue(url.startsWith(baseUrl), "URL has not been preprocessed: " + url);
            URI uri = new URI(url);
        }
        return true;
    }

}
