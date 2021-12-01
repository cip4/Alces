package org.cip4.tools.alces.util;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that reads JMF/JDF MIME packages and extracts their contents.
 */
public class MimeUtil {

    private static final Logger log = LoggerFactory.getLogger(MimeUtil.class);

    public static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";
    public static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";
    public static final String MIME_CONTENT_TYPE = "multipart/related";
    public static final String JDF_EXTENSION = ".jdf";
    public static final String JMF_EXTENSION = ".jmf";

    public static final String CONTENT_ID_HEADER = "Content-ID";
    private static final String FIRST_PART = "FIRST PART";
    private static Session mailSession = Session.getDefaultInstance(new Properties());

    /**
     * Extracts the contents of a MIME package from an input stream to the specified output directory. Returns an array of strings where each file extracted
     * from the MIME package has an element in the array that is the absolute URL to where the file was extracted.
     */
    public static String[] extractMimePackage(InputStream mimeInputStream, String outputDirUrl) throws IOException, URISyntaxException {
        String[] fileUrls = null;
        try {
            Message message = new MimeMessage(mailSession, mimeInputStream);

            // Extract parts
            Multipart multipart = (Multipart) message.getContent();
            Map fileToCidMap = extractParts(multipart, outputDirUrl);

            // Replace cid URLs with new URLs
            replaceAllCidUrls(fileToCidMap);
            fileUrls = (String[]) fileToCidMap.keySet().toArray(new String[fileToCidMap.size()]);

        } catch (MessagingException me) {
            String err = "The MIME package could not be parsed. ";
            log.error(err + me, me);
            me.printStackTrace();
            throw new IOException(err, me);
        }

        return fileUrls;
    }

    /**
     * Extracts each part of the MIME multipart package to the specified output directory.
     */
    private static Map extractParts(Multipart multipart, String outputDirUrl) throws MessagingException, IOException, URISyntaxException {

        // Check if Content-Type header is valid
        String contentType = multipart.getContentType();
        if (!contentType.startsWith(MIME_CONTENT_TYPE)) {
            String err = "This is not a valid JDF/JMF MIME package. " + "The Content-Type of a package should be '" + MIME_CONTENT_TYPE + "' but was '" + contentType + "'.";
            log.error(err);
            throw new IOException(err);
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
     */
    private static UrlCidPair extractFirstPart(Part firstPart, String outputDirUrl) throws MessagingException, IOException, URISyntaxException {

        String firstFileUrl = outputDirUrl;
        String contentType = firstPart.getContentType();
        if (contentType.startsWith(JMF_CONTENT_TYPE)) {
            firstFileUrl += JMF_EXTENSION;
        } else if (contentType.startsWith(JDF_CONTENT_TYPE)) {
            firstFileUrl += JDF_EXTENSION;
        } else {
            String err = "This is not a valid JDF/JMF MIME package. " + "The Content-Type of the first section in the MIME package " + "should be '" + JDF_CONTENT_TYPE + "' or '" + JMF_CONTENT_TYPE
                    + ". It was '" + contentType + "'.";
            log.error(err);
            throw new IOException(err);
        }
        InputStream firstPartIn = firstPart.getInputStream();
        OutputStream firstPartOut = new FileOutputStream(new File(new URI(firstFileUrl)));
        IOUtils.copy(firstPartIn, firstPartOut);
        IOUtils.closeQuietly(firstPartIn);
        IOUtils.closeQuietly(firstPartOut);
        log.debug("Wrote first MIME part to: " + firstFileUrl);
        return new UrlCidPair(firstFileUrl, FIRST_PART);
    }

    /**
     * Extracts the specified MIME package part to the directory specified by the base URL. The extracted part's file name will be:
     */
    private static UrlCidPair extractPart(Part part, String baseFileUrl) throws MessagingException, IOException, URISyntaxException {

        // Get Content-ID
        String cid = part.getHeader(CONTENT_ID_HEADER)[0];
        if (cid == null || cid.length() == 0) {
            cid = "UNKNOWN";
            log.warn("MIME part did not have a Content-ID header.");
        } else {
            cid = unescapeCid(cid); // Unescape %hh and < >
        }
        // Get file name
        String fileName = part.getFileName();
        if (fileName == null || fileName.length() == 0) {
            fileName = randomString(5) + ".dat";
            log.warn("MIME part did  not have a file name. Using random file name: " + fileName);
        }
        String fileUrl = baseFileUrl + "_" + attemptToUnescapeString(fileName);
        // Write to file {outputdir}/{base filename}_{part filename}
        InputStream partIn = part.getInputStream();
        OutputStream partOut = new FileOutputStream(new File(new URI(fileUrl)));
        IOUtils.copy(partIn, partOut);
        IOUtils.closeQuietly(partIn);
        IOUtils.closeQuietly(partOut);
        log.debug("Wrote MIME part to: " + fileUrl);
        return new UrlCidPair(fileUrl, cid);
    }

    /**
     * Replaces the <code>cid</code> URLs in JMF and JDF files with the new URLs. This method does the following:
     */
    private static void replaceAllCidUrls(Map fileToCidMap) throws IOException, URISyntaxException {
        // For each file URL
        for (Iterator i = fileToCidMap.keySet().iterator(); i.hasNext();) {
            String url = (String) i.next();
            // If file URL ends with .jmf or .jdf
            if (url.endsWith(JMF_EXTENSION) || url.endsWith(JDF_EXTENSION)) {
                log.debug("Replacing cid URLs in file: " + url);
                InputStream in = new FileInputStream(new File(new URI(url)));
                String fileData = IOUtils.toString(in);
                // For each cid URL
                for (Iterator j = fileToCidMap.keySet().iterator(); j.hasNext();) {
                    // Replace cid URL with new URL
                    String newUrl = (String) j.next();
                    String cidUrl = "cid:" + fileToCidMap.get(newUrl);
                    if (!cidUrl.equals("cid:" + FIRST_PART)) {
                        log.debug("   Replacing '" + cidUrl + "' with '" + newUrl + "' in: " + url);
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
    private static String unescapeCid(String cid) {
        log.debug("Escaping cid URL: " + cid);
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
            log.warn("Could not unescape CID: " + newCid);
        }
        log.debug("Escaped cid URL: " + cid + " -> " + newCid);
        return newCid;
    }

    /**
     * Unescapes all occurrences of <code>%hh</code> in the specified string. The The unescaping/decoding is performed using
     * <code>URLDecoder.decode(cid, "UTF-8")</code>.
     * @param encodedString
     * @return decoded string
     * @throws UnsupportedEncodingException if UTF-8 is not supported
     */
    private static String unescapeString(String encodedString) throws UnsupportedEncodingException {
        return encodedString = URLDecoder.decode(encodedString, "UTF-8");
    }

    /**
     * Attempts to unescape all occurrences of <code>%hh</code> in the specified string. If the operation fails then the original string is returned.
     * @param encodedString
     * @return the decoded string; or the original string if decoding failed
     */
    private static String attemptToUnescapeString(String encodedString) {
        try {
            encodedString = unescapeString(encodedString);
        } catch (UnsupportedEncodingException uee) {
            log.warn("Could not unescape string: " + encodedString);
        }
        return encodedString;
    }

    /**
     * Generates a random string of the specified length and only contains characters a-z, A-Z.
     * @return a random string
     */
    private static String randomString(int length) {
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
    private static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
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
