package org.cip4.elk.alces.preprocessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.message.OutMessageImpl;
import org.cip4.elk.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.elk.alces.preprocessor.jmf.Preprocessor;
import org.cip4.elk.alces.preprocessor.VariablesPreprocessor;
import org.cip4.elk.alces.util.ConfigurationHandler;
import org.cip4.jdflib.node.JDFNode;

public class VariablesPreprocessorTest extends AlcesTestCase {
	
	private static final String ID = "VariablesPreprocessorTest.ID";
	private static final String JOBID = "VariablesPreprocessorTest.JobID";
	private static final String JOBPARTID = "VariablesPreprocessorTest.JobPartID";
	
	@Override
	public void setUp() {
		ConfigurationHandler.getInstance().loadConfiguration(
				getTestFileAsFile("alces.properties"));
	}

	public void testPreprocessJMF() throws PreprocessorException, IOException {
		Preprocessor p = new VariablesPreprocessor();
		Message mOriginal = new OutMessageImpl(null,
				getTestFileAsString("Status-Subscription.jmf"), true);		
		Message mPreprocessed = p.preprocess(mOriginal);		
		assertSame(mOriginal, mPreprocessed);
		assertFalse(mPreprocessed.getBody(), mPreprocessed.getBody().contains("${alces.host}"));
		assertFalse(mPreprocessed.getBody(), mPreprocessed.getBody().contains("${alces.port}"));
	}
	
	public void testPreprocessJDF() throws PreprocessorException, IOException {
		JDFPreprocessor p = new VariablesPreprocessor();
		JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
		// Validate input
		assertTrue(jdf.toXML().contains("${jdf.id}"));
		assertTrue(jdf.toXML().contains("${jdf.jobid}"));
		assertTrue(jdf.toXML().contains("${jdf.jobpartid}"));
		assertFalse(jdf.toXML().contains(ID));
		assertFalse(jdf.toXML().contains(JOBID));
		assertFalse(jdf.toXML().contains(JOBPARTID));
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
		assertNotSame(jdf, newJdf);
		assertFalse(newJdf.toXML().contains("${jdf.id}"));
		assertFalse(newJdf.toXML().contains("${jdf.jobid}"));
		assertFalse(newJdf.toXML().contains("${jdf.jobpartid}"));
		assertTrue(newJdf.toXML().contains(ID));
		assertTrue(newJdf.toXML().contains(JOBID));
		assertTrue(newJdf.toXML().contains(JOBPARTID));
	}

}
