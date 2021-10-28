package org.cip4.tools.alces.test.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.util.JDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Test</code> that detects <i>Preview</i> elements in JMF messages
 * and verifies that the preview files can be downloaded.
 * 
 * @author Claes Buckwalter
 * 
 */
public class PreviewTest extends Test {

	protected static Logger log = LoggerFactory.getLogger(PreviewTest.class);

	private final File previewDir;

	public PreviewTest() {
		this(new File(new File(System.getProperty("user.dir")).getParentFile(), "PreviewTest output"));
	}

	public PreviewTest(File previewDir) {
		super(
				"PreviewTest - if a JMF contains Preview elements, verifies that the preview files can be downloaded and saves them to '"
						+ previewDir.getAbsolutePath() + "'.");
		this.previewDir = previewDir;
	}

	@Override
	public TestResult runTest(Message message) {
		if (!isJMF(message)) {
			// Ignore if not JMF
			return new TestResultImpl(this, message, Result.IGNORED,
					"Test ignored because message did not contain JMF. Message content-type was: "
							+ message.getContentType());
		}
		final StringBuilder logMsg = new StringBuilder();
		int ignored = 0;
		int failed = 0;
		int passed = 0;
		final JDFJMF jmf = message.getBodyAsJMF();
		File jmfDumpDir = new File(previewDir, jmf.getID());
		List<KElement> previews = jmf.getChildrenByTagName(ElementName.PREVIEW, null, new JDFAttributeMap(AttributeName.URL, "*"), false, false, 0);
		for (KElement preview : previews) {		
			final String url = preview.getAttribute(AttributeName.URL);
			if (StringUtils.isEmpty(url)) {
				ignored++;
				logMsg.append("Ignored Preview because URL attribute was empty.\n");
				continue;
			}
			try {
				URL previewUrl = new URL(url);
				// Create file
				File previewFile = new File(jmfDumpDir, buildPreviewFileName(preview, previewUrl.getPath()));
				previewFile.getParentFile().mkdirs();
				log.debug("Downloading preview '" + url + "' to '"
						+ previewFile.getAbsolutePath() + "'...");
				// Download file
				InputStream in = previewUrl.openStream();
				OutputStream out = new FileOutputStream(previewFile);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
				passed++;
				logMsg.append(
						"Successfully downloaded Preview file: "
								+ previewFile.getAbsolutePath()).append("\n");
			} catch (MalformedURLException e) {
				log.info("Could not parse preview URL '" + url + "':" + e);
				failed++;
				logMsg.append("Could not parse Preview URL '").append(url).append("'.\n");
			} catch (IOException e) {
				log.info("Could not download preview '" + url + "': " + e);
				failed++;
				logMsg.append("Could not download Preview URL '").append(url).append("': ").append(e).append("\n");
			}

		}
		// Calculate result
		Result result;
		if (failed > 0) {
			result = Result.FAILED;
		} else if (passed > 0) {
			result = Result.PASSED;
		} else {
			result = Result.IGNORED;
		}
		return new TestResultImpl(this, message, result, logMsg.toString());
	}

	private String buildPreviewFileName(KElement preview, String originalPath) {
		final StringBuilder filename = new StringBuilder("Preview");
		filename.append("_").append(preview.getAttribute(AttributeName.SIGNATURENAME));
		filename.append("_").append(preview.getAttribute(AttributeName.SHEETNAME));
		filename.append("_").append(preview.getAttribute(AttributeName.SIDE));
		filename.append("_").append(preview.getAttribute(AttributeName.SEPARATION));		
		filename.append("_").append(originalPath.substring(originalPath.lastIndexOf("/")+1));
		return filename.toString();
	}

	private boolean isJMF(Message message) {
		return message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE);
	}

}
