package org.cip4.elk.alces.preprocessor.jmf;

import java.io.IOException;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.message.OutMessageImpl;
import org.cip4.elk.alces.preprocessor.PreprocessorException;
import org.cip4.elk.alces.util.ConfigurationHandler;

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
