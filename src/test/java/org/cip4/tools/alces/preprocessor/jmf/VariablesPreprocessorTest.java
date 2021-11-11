package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class VariablesPreprocessorTest extends AlcesTestCase {

    @BeforeEach
    public void setUp() {
        ConfigurationHandler.getInstance().loadConfiguration(
                getTestFileAsFile("alces.properties"));
    }

    @Test
    public void testPreprocess() throws PreprocessorException, IOException {
        final Preprocessor p = new VariablesPreprocessor();
        final AbstractJmfMessage mIn = new OutgoingJmfMessage(null,
                getTestFileAsString("Status-Subscription.jmf"), true);
        final AbstractJmfMessage mOut = p.preprocess(mIn);
        Assertions.assertSame(mIn, mOut);
        Assertions.assertFalse(mOut.getBody().contains("${alces.host}"), mOut.getBody());
        Assertions.assertFalse(mOut.getBody().contains("${alces.port}"), mOut.getBody());
    }

}
