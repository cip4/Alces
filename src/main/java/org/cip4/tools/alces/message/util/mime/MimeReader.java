/*
 * Created on Sep 28, 2004
 */
package org.cip4.tools.alces.message.util.mime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * A class that reads JMF/JDF MIME packages and extracts their contents.
 * @see <a href="http://www.cip4.org/documents/jdf_specifications/JDF1.2.pdf">JDF Specification Release 1.2, 8.3 JDF Packaging</a>
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class MimeReader {

	public static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";
	public static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";
	public static final String MIME_CONTENT_TYPE = "multipart/related";
	public static final String JDF_EXTENSION = ".jdf";
	public static final String JMF_EXTENSION = ".jmf";
	public static final String JDF_MIME_EXTENSION = ".mjd";
	public static final String JMF_MIME_EXTENSION = ".mjm";

	public static final String CONTENT_ID_HEADER = "Content-ID";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String FIRST_PART = "FIRST PART";
	private Logger LOGGER;
	private Session _mailSession;

	/**
	 * Creates a new MIME reader.
	 */
	public MimeReader() {
		LOGGER = Logger.getLogger(this.getClass().getName());
		_mailSession = Session.getDefaultInstance(new Properties());
	}

	/**
	 * Extracts the contents of a MIME package from an input stream to the specified output directory. Returns an array of strings where each file extracted
	 * from the MIME package has an element in the array that is the absolute URL to where the file was extracted.
	 * <p>
	 * All files extracted from the MIME package will be given file names that start with the same sequence of 12 characters according to the following pattern:
	 * </p>
	 * <p>
	 * <code>ELK_{8 random letters}</code> - base file name (12 characters)<br/>
	 * <code>{base file name}.jmf</code> - if first part has Content-Type <code>application/vnd.cip4-jmf+xml</code><br/>
	 * <code>{base file name}.jdf</code> - if first part has Content-Type <code>application/vnd.cip4-jdf+xml</code><br/>
	 * <code>{base file name}_{part file name}</code> - for file part with a file name specified in the MIME package<br/>
	 * <code>{base file name}_{5 random letters}.dat</code> - for file part without a file name specified in the MIME package<br/>
	 * </p>
	 * <p>
	 * All <code>cid</code> URLs in all JMF and JDF files in the MIME package will be replaced with URLs that point to the corresponding extracted files.
	 * </p>
	 * <p>
	 * According to the JDF specification a MIME package's Content-Type should be <code>multipart/related</code> and the first part should have Content-Type
	 * <code>application/vnd.cip4-jmf+xml</code> (JMF) or <code>application/vnd.cip4-jdf+xml</code> (JDF). A MimePackageException will be thrown if the MIME
	 * package does not comply with the JDF specification.
	 * </p>
	 * @param mimeInputStream an input stream containing a MIME package
	 * @param outputDirUrl the directory where files extracted from the MIME package will be written
	 * @return An array of <code>String</code>s that are absolute URLs to where each file in the MIME package was extracted. The order of the file URLs in the
	 * array is unspecified.
	 * @throws MimePackageException if the MIME package is incorrectly formatted
	 * @throws IOException if there is an IO error occurs
	 */
	public String[] extractMimePackage(InputStream mimeInputStream, String outputDirUrl) throws MimePackageException, IOException, URISyntaxException {
		String[] fileUrls = null;
		try {
			Message message = new MimeMessage(_mailSession, mimeInputStream);
			// Debug logging
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("MIME package content type: " + message.getContentType());
				StringBuffer headers = new StringBuffer();
				for (Enumeration e = message.getAllHeaders(); e.hasMoreElements();) {
					Header header = (Header) e.nextElement();
					headers.append(header.getName());
					headers.append(": ");
					headers.append(header.getValue());
					headers.append("\n");
				}
				LOGGER.debug("MIME package headers:\n" + headers);
			}
			// Extract parts
			Multipart multipart = (Multipart) message.getContent();
			Map fileToCidMap = extractParts(multipart, outputDirUrl);
			// Replace cid URLs with new URLs
			replaceAllCidUrls(fileToCidMap);
			fileUrls = (String[]) fileToCidMap.keySet().toArray(new String[fileToCidMap.size()]);
		} catch (MessagingException me) {
			String err = "The MIME package could not be parsed. ";
			LOGGER.error(err + me, me);
			me.printStackTrace();
			throw new MimePackageException(err, me);
		}
		return fileUrls;
	}

	/**
	 * Extracts each part of the MIME multipart package to the specified output directory.
	 * @param multipart
	 * @param outputDirUrl
	 * @return a map containing each extracted part's new URL (that points to the part's extracted file in the output directory) as key, and the corresponding
	 * CID as value
	 * @throws MimePackageException if the MIME package was incorrectly formatted
	 * @throws IOException
	 * @throws MessagingException
	 */
	private Map extractParts(Multipart multipart, String outputDirUrl) throws MessagingException, IOException, MimePackageException, URISyntaxException {
		// Check if Content-Type header is valid
		String contentType = multipart.getContentType();
		if (!contentType.startsWith(MIME_CONTENT_TYPE)) {
			String err = "This is not a valid JDF/JMF MIME package. " + "The Content-Type of a package should be '" + MIME_CONTENT_TYPE + "' but was '" + contentType + "'.";
			LOGGER.error(err);
			throw new MimePackageException(err);
		}
		// Extract parts
		Map fileToCidMap = new HashMap();
		String baseFileUrl = outputDirUrl + "ELK_" + randomString(8);
		for (int i = 0, imax = multipart.getCount(); i < imax; i++) {
			Part part = multipart.getBodyPart(i);
			UrlCidPair ucp;
			if (i == 0) {
				ucp = extractFirstPart(part, baseFileUrl);
			} else {
				ucp = extractPart(part, baseFileUrl);
			}
			fileToCidMap.put(ucp.getUrl(), ucp.getCid());
		}
		return fileToCidMap;
	}

	/**
	 * Extracts the first part of a MIME package and writes it to the specified output directory. According to the JDF specification a MIME package's first part
	 * should have Content-Type <code>application/vnd.cip4-jmf+xml</code> (JMF) or <code>application/vnd.cip4-jdf+xml</code> (JDF).
	 * @param firstPart this should be the first part in a MIME package
	 * @param outputDirUrl the directory to write the extracted part to
	 * @return the complete URL to where the extracted part was written and the part's CID
	 * @throws MimePackageException if the MIME package was incorrectly formatted
	 * @throws IOException
	 * @throws MessagingException
	 */
	private UrlCidPair extractFirstPart(Part firstPart, String outputDirUrl) throws MessagingException, IOException, MimePackageException, URISyntaxException {
		// Debug logging
		if (LOGGER.isDebugEnabled()) {
			StringBuffer headers = new StringBuffer();
			for (Enumeration e = firstPart.getAllHeaders(); e.hasMoreElements();) {
				Header header = (Header) e.nextElement();
				headers.append(header.getName());
				headers.append(": ");
				headers.append(header.getValue());
				headers.append("\n");
			}
			LOGGER.debug("First part's headers:\n" + headers);
		}

		String firstFileUrl = outputDirUrl;
		String contentType = firstPart.getContentType();
		if (contentType.startsWith(JMF_CONTENT_TYPE)) {
			firstFileUrl += JMF_EXTENSION;
		} else if (contentType.startsWith(JDF_CONTENT_TYPE)) {
			firstFileUrl += JDF_EXTENSION;
		} else {
			String err = "This is not a valid JDF/JMF MIME package. " + "The Content-Type of the first section in the MIME package " + "should be '" + JDF_CONTENT_TYPE + "' or '" + JMF_CONTENT_TYPE
					+ ". It was '" + contentType + "'.";
			LOGGER.error(err);
			throw new MimePackageException(err);
		}
		InputStream firstPartIn = firstPart.getInputStream();
		OutputStream firstPartOut = new FileOutputStream(new File(new URI(firstFileUrl)));
		IOUtils.copy(firstPartIn, firstPartOut);
		IOUtils.closeQuietly(firstPartIn);
		IOUtils.closeQuietly(firstPartOut);
		LOGGER.debug("Wrote first MIME part to: " + firstFileUrl);
		return new UrlCidPair(firstFileUrl, FIRST_PART);
	}

	/**
	 * Extracts the specified MIME package part to the directory specified by the base URL. The extracted part's file name will be:
	 * <p>
	 * <code>{base file name}_{part file name}</code> - if the part has a file name in the MIME package<br/>
	 * <code>{base file name}_{5 random letters}.dat</code> - if the part does not have a file name in the MIME package<br/>
	 * </p>
	 * @param part the part to extract
	 * @param baseFileUrl the base URL used for building the part's file name
	 * @return the complete URL to where the extracted part was written and the part's CID
	 * @throws MessagingException
	 * @throws IOException
	 */
	private UrlCidPair extractPart(Part part, String baseFileUrl) throws MessagingException, IOException, URISyntaxException {
		// Debug logging
		if (LOGGER.isDebugEnabled()) {
			StringBuffer headers = new StringBuffer();
			for (Enumeration e = part.getAllHeaders(); e.hasMoreElements();) {
				Header header = (Header) e.nextElement();
				headers.append(header.getName());
				headers.append(": ");
				headers.append(header.getValue());
				headers.append("\n");
			}
			LOGGER.debug("Part headers:\n" + headers);
		}
		// Get Content-ID
		String cid = part.getHeader(CONTENT_ID_HEADER)[0];
		if (cid == null || cid.length() == 0) {
			cid = "UNKNOWN";
			LOGGER.warn("MIME part did not have a Content-ID header.");
		} else {
			cid = unescapeCid(cid); // Unescape %hh and < >
		}
		// Get file name
		String fileName = part.getFileName();
		if (fileName == null || fileName.length() == 0) {
			fileName = randomString(5) + ".dat";
			LOGGER.warn("MIME part did  not have a file name. Using random file name: " + fileName);
		}
		String fileUrl = baseFileUrl + "_" + attemptToUnescapeString(fileName);
		// Write to file {outputdir}/{base filename}_{part filename}
		InputStream partIn = part.getInputStream();
		OutputStream partOut = new FileOutputStream(new File(new URI(fileUrl)));
		IOUtils.copy(partIn, partOut);
		IOUtils.closeQuietly(partIn);
		IOUtils.closeQuietly(partOut);
		LOGGER.debug("Wrote MIME part to: " + fileUrl);
		return new UrlCidPair(fileUrl, cid);
	}

	/**
	 * Replaces the <code>cid</code> URLs in JMF and JDF files with the new URLs. This method does the following:
	 * 
	 * <pre>
	 * For each new URL
	 *    If the new URL ends with .jmf or .jdf read the file and
	 *       For each CID
	 *          Replace cid URL with corresponding new URL
	 * </pre>
	 * @todo Optimize. Each JMF/JDF is currently read to memory, strings replaced, and then written to disk again. An input/ouput stream that replaces value on
	 * the fly would use less memory.
	 * @param cidFileMap a map with the new URLs as keys and the <code>cid</code> URLs as values
	 */
	private void replaceAllCidUrls(Map fileToCidMap) throws IOException, URISyntaxException {
		// For each file URL
		for (Iterator i = fileToCidMap.keySet().iterator(); i.hasNext();) {
			String url = (String) i.next();
			// If file URL ends with .jmf or .jdf
			if (url.endsWith(JMF_EXTENSION) || url.endsWith(JDF_EXTENSION)) {
				LOGGER.debug("Replacing cid URLs in file: " + url);
				InputStream in = new FileInputStream(new File(new URI(url)));
				String fileData = IOUtils.toString(in);
				// For each cid URL
				for (Iterator j = fileToCidMap.keySet().iterator(); j.hasNext();) {
					// Replace cid URL with new URL
					String newUrl = (String) j.next();
					String cidUrl = "cid:" + fileToCidMap.get(newUrl);
					if (!cidUrl.equals("cid:" + FIRST_PART)) {
						LOGGER.debug("   Replacing '" + cidUrl + "' with '" + newUrl + "' in: " + url);
						// Case-insensitive regexp matching
						fileData = fileData.replaceAll("(?i)" + cidUrl, newUrl);
					}
				}
				OutputStream out = new FileOutputStream(new File(new URI(url)));
				IOUtils.write(fileData, out);
			}
		}
	}

	/**
	 * Removes leading < and trailing > from a CID (Content-ID) and then unescapes all occurrences of <code>%hh</code> in the CID. The CID is decoded using
	 * <code>URLDecoder.decode(cid, "UTF-8")</code>.
	 * @param cid the CID to unescape
	 * @return the unescaped CID
	 */
	private String unescapeCid(String cid) {
		LOGGER.debug("Escaping cid URL: " + cid);
		StringBuffer cidBuffer = new StringBuffer(cid);
		// Delete leading <
		if (cid.startsWith("<")) {
			cidBuffer.deleteCharAt(0);
		}
		// Delete trailing >
		if (cid.endsWith(">")) {
			cidBuffer.deleteCharAt(cidBuffer.length() - 1);
		}
		// Unescape %hh
		String newCid = cidBuffer.toString();
		try {
			newCid = unescapeString(newCid);
		} catch (UnsupportedEncodingException uee) {
			LOGGER.warn("Could not unescape CID: " + newCid);
		}
		LOGGER.debug("Escaped cid URL: " + cid + " -> " + newCid);
		return newCid;
	}

	/**
	 * Unescapes all occurrences of <code>%hh</code> in the specified string. The The unescaping/decoding is performed using
	 * <code>URLDecoder.decode(cid, "UTF-8")</code>.
	 * @param encodedString
	 * @return decoded string
	 * @throws UnsupportedEncodingException if UTF-8 is not supported
	 */
	private String unescapeString(String encodedString) throws UnsupportedEncodingException {
		return encodedString = URLDecoder.decode(encodedString, "UTF-8");
	}

	/**
	 * Attempts to unescape all occurrences of <code>%hh</code> in the specified string. If the operation fails then the original string is returned.
	 * @param encodedString
	 * @return the decoded string; or the original string if decoding failed
	 */
	private String attemptToUnescapeString(String encodedString) {
		try {
			encodedString = unescapeString(encodedString);
		} catch (UnsupportedEncodingException uee) {
			LOGGER.warn("Could not unescape string: " + encodedString);
		}
		return encodedString;
	}

	/**
	 * Generates a random string of the specified length and only contains characters a-z, A-Z.
	 * @return a random string
	 */
	private String randomString(int length) {
		return random(length, 0, 0, true, false, null, new Random());
	}

	/**
	 * <strong>Cut and paste from <code>RandomStringUtils</code>. See <a
	 * href="http://jakarta.apache.org/commons/lang/api/org/apache/commons/lang/RandomStringUtils.html">org.apache.commons.lang.RandomStringUtils</a>.</strong>
	 * 
	 * <p>
	 * Creates a random string based on a variety of options, using supplied source of randomness.
	 * </p>
	 * 
	 * <p>
	 * If start and end are both <code>0</code>, start and end are set to <code>' '</code> and <code>'z'</code>, the ASCII printable characters, will be used,
	 * unless letters and numbers are both <code>false</code>, in which case, start and end are set to <code>0</code> and <code>Integer.MAX_VALUE</code>.
	 * 
	 * <p>
	 * If set is not <code>null</code>, characters between start and end are chosen.
	 * </p>
	 * 
	 * <p>
	 * This method accepts a user-supplied {@link Random} instance to use as a source of randomness. By seeding a single {@link Random} instance with a fixed
	 * seed and using it for each call, the same random sequence of strings can be generated repeatedly and predictably.
	 * </p>
	 * 
	 * @param count the length of random string to create
	 * @param start the position in set of chars to start at
	 * @param end the position in set of chars to end before
	 * @param letters only allow letters?
	 * @param numbers only allow numbers?
	 * @param chars the set of chars to choose randoms from. If <code>null</code>, then it will use the set of all chars.
	 * @param random a source of randomness.
	 * @return the random string
	 * @throws ArrayIndexOutOfBoundsException if there are not <code>(end - start) + 1</code> characters in the set array.
	 * @throws IllegalArgumentException if <code>count</code> &lt; 0.
	 * @since 2.0
	 */
	private String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
		if (count == 0) {
			return "";
		} else if (count < 0) {
			throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
		}
		if ((start == 0) && (end == 0)) {
			end = 'z' + 1;
			start = ' ';
			if (!letters && !numbers) {
				start = 0;
				end = Integer.MAX_VALUE;
			}
		}

		StringBuffer buffer = new StringBuffer();
		int gap = end - start;

		while (count-- != 0) {
			char ch;
			if (chars == null) {
				ch = (char) (random.nextInt(gap) + start);
			} else {
				ch = chars[random.nextInt(gap) + start];
			}
			if ((letters && numbers && Character.isLetterOrDigit(ch)) || (letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
				buffer.append(ch);
			} else {
				count++;
			}
		}
		return buffer.toString();
	}

	/**
	 * A new URL and the corresponding CID.
	 */
	private static class UrlCidPair {
		private String _newUrl;
		private String _cid;

		UrlCidPair(String newUrl, String cid) {
			_newUrl = newUrl;
			_cid = cid;
		}

		String getUrl() {
			return _newUrl;
		}

		String getCid() {
			return _cid;
		}
	}

}
