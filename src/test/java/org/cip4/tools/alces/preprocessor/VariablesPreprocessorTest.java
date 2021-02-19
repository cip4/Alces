package org.cip4.tools.alces.preprocessor;

import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VariablesPreprocessorTest extends AlcesTestCase {

    private static final String ID = "VariablesPreprocessorTest.ID";
    private static final String JOBID = "VariablesPreprocessorTest.JobID";
    private static final String JOBPARTID = "VariablesPreprocessorTest.JobPartID";

    @Before
    public void setUp() {
        ConfigurationHandler.getInstance().loadConfiguration(
                getTestFileAsFile("alces.properties"));
    }

    @Test
    public void testPreprocessJMF() throws PreprocessorException, IOException {
        Preprocessor p = new VariablesPreprocessor();
        Message mOriginal = new OutMessageImpl(null,
                getTestFileAsString("Status-Subscription.jmf"), true);
        Message mPreprocessed = p.preprocess(mOriginal);
        Assert.assertSame(mOriginal, mPreprocessed);
        Assert.assertFalse(mPreprocessed.getBody(), mPreprocessed.getBody().contains("${alces.host}"));
        Assert.assertFalse(mPreprocessed.getBody(), mPreprocessed.getBody().contains("${alces.port}"));
    }

    @Test
    public void testPreprocessJDF() throws PreprocessorException, IOException {
        JDFPreprocessor p = new VariablesPreprocessor();
        JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
        // Validate input
        Assert.assertTrue(jdf.toXML().contains("${jdf.id}"));
        Assert.assertTrue(jdf.toXML().contains("${jdf.jobid}"));
        Assert.assertTrue(jdf.toXML().contains("${jdf.jobpartid}"));
        Assert.assertFalse(jdf.toXML().contains(ID));
        Assert.assertFalse(jdf.toXML().contains(JOBID));
        Assert.assertFalse(jdf.toXML().contains(JOBPARTID));
        // Preprocess
        PreprocessorContext context = new PreprocessorContext();
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("jdf.id", ID);
        vars.put("jdf.jobid", JOBID);
        vars.put("jdf.jobpartid", JOBPARTID);
        context.addAttribute(VariablesPreprocessor.VARIABLES_MAP, vars);
        context.addAttribute(JDFPreprocessor.PREPROCESSING_ENABLED, "true");
        // Validate output
        JDFNode newJdf = p.preprocess(jdf, context);
        Assert.assertNotSame(jdf, newJdf);
        Assert.assertFalse(newJdf.toXML().contains("${jdf.id}"));
        Assert.assertFalse(newJdf.toXML().contains("${jdf.jobid}"));
        Assert.assertFalse(newJdf.toXML().contains("${jdf.jobpartid}"));
        Assert.assertTrue(newJdf.toXML().contains(ID));
        Assert.assertTrue(newJdf.toXML().contains(JOBID));
        Assert.assertTrue(newJdf.toXML().contains(JOBPARTID));
    }

}
