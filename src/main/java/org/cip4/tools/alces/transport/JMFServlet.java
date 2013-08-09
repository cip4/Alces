/*
 * Created on Sep 12, 2004
 */
package org.cip4.tools.alces.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;
import org.cip4.tools.alces.jmf.JMFMessageBuilder;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSuite;
import org.cip4.tools.alces.transport.util.HttpHeaderUtils;
import org.cip4.tools.alces.util.ConfigurationHandler;

/**
 * A servlet for receiving JMF messages.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 * 
 * @web:servlet name="JMFServlet" display-name="Alces JMF Servlet" description="A servlet that processes JMF messages" load-on-startup="1"
 * 
 * @web:servlet-mapping url-pattern="/jmf"
 */
public class JMFServlet extends HttpServlet {

	public static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";

	public static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";

	public static final String MIME_CONTENT_TYPE = "multipart/related";

	public static final String JDF_EXTENSION = ".jdf";

	public static final String JMF_EXTENSION = ".jmf";

	public static final String JDF_MIME_EXTENSION = ".mjd";

	public static final String JMF_MIME_EXTENSION = ".mjm";

	public static final String SERVLET_NAME = "Elk Alces JMFServlet";

	protected static Logger LOGGER;

	private ConfigurationHandler _confHand = ConfigurationHandler.getInstance();

	private TestSuite _testSuite;

	/**
	 * Sets the test sessions that this servlet should forward messages to.
	 * 
	 * @param testSuite
	 */
	public void setTestSessions(TestSuite testSuite) {
		_testSuite = testSuite;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		LOGGER = Logger.getLogger(this.getClass().getName());
		LOGGER.debug("Initializing " + getServletName() + "...");
		// TODO Do initializing...
		LOGGER.debug("Initialized " + getServletName() + ".");
	}

	@Override
	public String getServletName() {
		return SERVLET_NAME;
	}

	/**
	 * Entry point that delegates to the process methods based on the request header <code>Content-type</code>.
	 * 
	 * @see #processJDF(HttpServletRequest, HttpServletResponse)
	 * @see #processMessage(HttpServletRequest, HttpServletResponse)
	 * @see #processMime(HttpServletRequest, HttpServletResponse)
	 * @see #processOther(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// Debug logging
		if (LOGGER.isDebugEnabled()) {
			StringBuffer debugInfo = new StringBuffer("Received request from ");
			debugInfo.append(req.getHeader("User-Agent")).append(" @ ");
			debugInfo.append(req.getRemoteHost()).append(" (");
			debugInfo.append(req.getRemoteAddr()).append("):\n");
			debugInfo.append("  Protocol: ");
			debugInfo.append(req.getProtocol()).append("\n");
			for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();) {
				String headerName = e.nextElement();
				debugInfo.append("  ").append(headerName).append(": ");
				debugInfo.append(req.getHeader(headerName)).append("\n");
			}
			LOGGER.debug(debugInfo);
		}
		// Delegate to methods
		try {
			processMessage(req, res);
		} catch (Exception e) {
			String err = "The request body could not be processed. Maybe it did not contain JMF or JDF? ";
			LOGGER.error(err, e);
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err + e);
		}
		// try {
		// if (req.getHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER).startsWith(
		// JDFConstants.JMF_CONTENT_TYPE)) {
		// processJMF(req, res);
		// } else if (req.getHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER)
		// .startsWith(JDFConstants.JDF_CONTENT_TYPE)) {
		// processJDF(req, res);
		// } else if (req.getHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER)
		// .startsWith(JDFConstants.JMF_MIME_CONTENT_TYPE)) {
		// processMime(req, res);
		// } else {
		// LOGGER.debug("Attempting to receive message with content-type '" +
		// req.getHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER) + "' as JMF...");
		// processJMF(req, res);
		// }
		// } catch (Exception e) {
		// String err = "The request body could not be processed. Maybe it did not contain JMF or JDF?"
		// + " [Java Exception: " + e + "]";
		// LOGGER.error(err, e);
		// e.printStackTrace();
		// res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
		// }
	}

	/**
	 * HTTP GET is not implemented.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String msg = "Received HTTP GET request from " + req.getHeader("User-Agent") + " @ " + req.getRemoteHost() + " (" + req.getRemoteAddr() + "). Request ignored.";
		LOGGER.warn(msg);
		res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "HTTP GET not implemented.");
	}

	/**
	 * Processes an incoming JMF message.
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	public void processMessage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String msg = "Receiving message from " + req.getHeader("User-Agent") + " @ " + req.getRemoteHost() + " (" + req.getRemoteAddr() + ")...";
		LOGGER.debug(msg);
		// Build incoming Message
		final String messageBody = toString(req.getInputStream());
		LOGGER.debug("Incoming message body: " + messageBody); // XXX
		String contentType = req.getHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER);
		LOGGER.info("contentType: " + contentType);
		if (contentType == null && new Boolean(_confHand.getProp(ConfigurationHandler.NO_CONTENT_TYPE))) {
			contentType = JMFServlet.JMF_CONTENT_TYPE;
		}
		final String header = HttpHeaderUtils.convertHttpHeadersToString(req);
		final InMessage message = _testSuite.createInMessage(contentType, header, messageBody, false);

		// Create and send outgoing Message in HTTP response
		final JDFJMF jmfIn = message.getBodyAsJMF();
		if (jmfIn != null) {
			if (jmfIn.getAcknowledge(0) != null) {
				LOGGER.debug("Receiving Acknowledge message...");
				startTestSession(req, res, message);
			} else if (jmfIn.getSignal(0) != null) {
				LOGGER.debug("Receiving Signal message...");
				startTestSession(req, res, message);
			} else if (jmfIn.getMessageElement(EnumFamily.Command, EnumType.ReturnQueueEntry, 0) != null) {
				LOGGER.debug("Receiving RetunQueueEntry message...");
				startTestSession(req, res, message);
				res.setContentType(JMFServlet.JMF_CONTENT_TYPE);
				JDFJMF jmfOut = JMFMessageBuilder.buildResponse(jmfIn);
				IOUtils.write(jmfOut.toXML(), res.getOutputStream());
			} else {
				LOGGER.debug("Receiving unhandled JMF message...");
				startTestSession(req, res, message);
				res.setContentType(JMFServlet.JMF_CONTENT_TYPE);
				JDFJMF jmfOut = JMFMessageBuilder.buildNotImplementedResponse(jmfIn);
				// Override ReturnCode 5 with 0 so that the Controller does not keep retrying the message
				jmfOut.getResponse(0).setReturnCode(Integer.parseInt(_confHand.getProp(ConfigurationHandler.JMF_NOT_IMPLEMENTED_RETURN_CODE)));
				IOUtils.write(jmfOut.toXML(), res.getOutputStream());
			}
			res.setStatus(HttpServletResponse.SC_OK);
		} else if (contentType.startsWith(JMFServlet.MIME_CONTENT_TYPE)) {
			LOGGER.debug("Receiving MIME package...");
			startTestSession(req, res, message);
			// TODO Extract JMF/Command/@ID from MIME package and use in Response
			res.setStatus(HttpServletResponse.SC_OK);
		} else if (contentType.startsWith(JMFServlet.JDF_CONTENT_TYPE)) {
			LOGGER.debug("Receiving JDF file...");
			startTestSession(req, res, message);
			res.setStatus(HttpServletResponse.SC_OK);
		} else {
			LOGGER.debug("Unknown content-type '" + contentType + "'...");
			startTestSession(req, res, message);
			res.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "The request's content-type was: " + contentType);
		}
	}

	//
	// /**
	// * Processes JDF.
	// *
	// * @param req
	// * @param resp
	// * @throws IOException
	// */
	// public void processJDF(HttpServletRequest req, HttpServletResponse res)
	// throws IOException {
	// String msg = "Received JDF from " + req.getHeader("User-Agent") + " @ "
	// + req.getRemoteHost() + " (" + req.getRemoteAddr() + ")";
	// LOGGER.debug(msg);
	//
	// process(req, res);
	// }
	//
	// /**
	// * Processes MIME package.
	// *
	// * @param req
	// * @param resp
	// * @throws IOException
	// */
	// public void processMime(HttpServletRequest req, HttpServletResponse res)
	// throws IOException {
	// String msg = "Received MIME from " + req.getHeader("User-Agent")
	// + " @ " + req.getRemoteHost() + " (" + req.getRemoteAddr()
	// + ")";
	// LOGGER.debug(msg);
	//
	// process(req, res);
	// }
	//
	// /**
	// * Processes other.
	// *
	// * @param req
	// * @param resp
	// * @throws IOException
	// */
	// public void processOther(HttpServletRequest req, HttpServletResponse res)
	// throws IOException {
	// String msg = "Received unrecognized data ("
	// + req.getHeader("Content-type") + " from "
	// + req.getHeader("User-Agent") + " @ " + req.getRemoteHost()
	// + " (" + req.getRemoteAddr() + ")";
	// LOGGER.debug(msg);
	//
	// process(req, res);
	// }
	//
	// public void process(HttpServletRequest req, HttpServletResponse res)
	// throws IOException {
	//
	// }

	/**
	 * Creates a new TestSession for an InMessage.
	 * 
	 * @param req
	 * @param res
	 * @param message
	 */
	private void startTestSession(HttpServletRequest req, @SuppressWarnings("unused") HttpServletResponse res, InMessage message) {
		// _confHand = ConfigurationHandler.getInstance();
		// TODO Make asynchronous
		if (_testSuite != null) {
			// Find the TestSession the message belongs to
			TestSession testSession = _testSuite.findTestSession(message);
			// Add the message to the TestSession
			if (testSession != null) {
				testSession.receiveMessage(message);
			} else {
				LOGGER.warn("No test session found that matches the message: " + message);
				LOGGER.debug("Creating new TestSession for InMessage...");
				// Create a objects using factory
				InMessage newMessage = _testSuite.createInMessage(message.getContentType(), message.getHeader(), message.getBody(), true);
				testSession = _testSuite.createTestSession(req.getRemoteAddr());
				// Add TestSession to suite
				_testSuite.addTestSession(testSession);
				// Configure tests
				_confHand.configureIncomingTests(testSession);
				_confHand.configureOutgoingTests(testSession);
				// Add message to TestSession
				testSession.receiveMessage(newMessage);
			}
		}
	}

	// ----------------------------------------------------------------
	// Utility methods, extracted from Jakarta Commons IO
	// ----------------------------------------------------------------

	/**
	 * Get the contents of an <code>InputStream</code> as a String. The platform's default encoding is used for the byte-to-char conversion.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested <code>String</code>
	 * @throws IOException In case of an I/O problem
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
	 * @param input the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws IOException In case of an I/O problem
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