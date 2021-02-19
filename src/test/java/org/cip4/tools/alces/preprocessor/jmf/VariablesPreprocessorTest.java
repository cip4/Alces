package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class VariablesPreprocessorTest extends AlcesTestCase {

    @Before
    public void setUp() {
        ConfigurationHandler.getInstance().loadConfiguration(
                getTestFileAsFile("alces.properties"));
    }

    @Test
    public void testPreprocess() throws PreprocessorException, IOException {
        final Preprocessor p = new VariablesPreprocessor();
        final Message mIn = new OutMessageImpl(null,
                getTestFileAsString("Status-Subscription.jmf"), true);
        final Message mOut = p.preprocess(mIn);
        Assert.assertSame(mIn, mOut);
        Assert.assertFalse(mOut.getBody(), mOut.getBody().contains("${alces.host}"));
        Assert.assertFalse(mOut.getBody(), mOut.getBody().contains("${alces.port}"));
    }

}
