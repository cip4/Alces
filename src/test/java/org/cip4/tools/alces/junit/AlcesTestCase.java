/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;

/**
 * Base class for all Alces test cases. Provides convenience methods for
 * accessing resources, etc.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public abstract class AlcesTestCase extends TestCase {

	// ----------------------------------------------------------------
	// Utility methods
	// ----------------------------------------------------------------

	public JDFNode getResourceAsJdf(String resourcePath) {
		JDFNode jdf = null;
		try {
			jdf = new JDFParser().parseStream(getResourceAsStream(resourcePath)).getJDFRoot();
		} catch (Exception e) {
			fail("An exception occurred whil parsing JDF from resource: " + resourcePath);
		}
		assertNotNull("Could not parse JDF from resource: " + resourcePath, jdf);
		return jdf;
	}

	public String getResourceAsString(String resourcePath) throws IOException {
		InputStream stream = getResourceAsStream(resourcePath);
		assertNotNull("Could not read resource: " + resourcePath, stream);
		return toString(stream);
	}

	/**
	 * Loads a test file relative <code>src/test/data/<TestClassName>/</code>.
	 * 
	 * @param filePath
	 *            a test file relative
	 *            <code>src/test/data/<TestClassName>/</code>
	 * @return
	 * @throws IOException
	 */
	public String getTestFileAsString(String filePath) throws IOException {
		return toString(getTestFileAsStream(filePath));
	}

	public JDFNode getTestFileAsJDF(String filePath) throws IOException {
		JDFDoc jdf = null;
		jdf = new JDFParser().parseStream(getTestFileAsStream(filePath));
		assertNotNull("JDF could not be parsed from: " + filePath);
		return jdf.getJDFRoot();
	}

	public JDFJMF getTestFileAsJMF(String filePath) throws IOException {
		JDFDoc jdf = null;
		jdf = new JDFParser().parseStream(getTestFileAsStream(filePath));
		assertNotNull("JDF could not be parsed from: " + filePath);
		return jdf.getJMFRoot();
	}

	public InputStream getTestFileAsStream(String filePath) throws IOException {		
		return new FileInputStream(getTestFileAsFile(filePath));
	}
	
	public File getTestFileAsFile(String filePath) {
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		
		String resPath = "/org/cip4/tools/alces/data/" + className + "/" + filePath;
		File file = new File( AlcesTestCase.class.getResource(resPath).getFile());
		
		assertTrue("Test file does not exist: " + file.getAbsolutePath(), file.exists());
		assertTrue("Test file is not a file: " + file.getAbsolutePath(), file.isFile());
		return file;
	}

	/**
	 * Loads a resource from the classpath.
	 * 
	 * @param resourcePath
	 * @return
	 */
	public InputStream getResourceAsStream(String resourcePath) {
		return this.getClass().getClassLoader().getResourceAsStream(resourcePath);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String. The
	 * platform's default encoding is used for the byte-to-char conversion.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from
	 * @return the requested <code>String</code>
	 * @throws IOException
	 *             In case of an I/O problem
	 */
	public static String toString(InputStream input) throws IOException {
		StringWriter writer = new StringWriter();
		InputStreamReader reader = new InputStreamReader(input);
		copy(reader, writer);
		return writer.toString();
	}

	/**
	 * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
	 * 
	 * @param input
	 *            the <code>Reader</code> to read from
	 * @param output
	 *            the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws IOException
	 *             In case of an I/O problem
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
