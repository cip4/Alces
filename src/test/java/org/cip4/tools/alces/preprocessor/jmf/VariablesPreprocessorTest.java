package org.cip4.tools.alces.preprocessor.jmf;

import java.io.IOException;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.preprocessor.jmf.VariablesPreprocessor;
import org.cip4.tools.alces.util.ConfigurationHandler;

public class VariablesPreprocessorTest extends AlcesTestCase {
	@Override
	public void setUp() {
		ConfigurationHandler.getInstance().loadConfiguration(
				getTestFileAsFile("alces.properties"));
	}

	public void testPreprocess() throws PreprocessorException, IOException {
		final Preprocessor p = new VariablesPreprocessor();
		final Message mIn = new OutMessageImpl(null,
				getTestFileAsString("Status-Subscription.jmf"), true);
		final Message mOut = p.preprocess(mIn);
		assertSame(mIn, mOut);
		assertFalse(mOut.getBody(), mOut.getBody().contains("${alces.host}"));
		assertFalse(mOut.getBody(), mOut.getBody().contains("${alces.port}"));
	}

}
