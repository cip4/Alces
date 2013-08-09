/*
 * Created on Apr 23, 2005
 */
package org.cip4.tools.alces.transport;

import org.apache.log4j.Logger;
import org.cip4.tools.alces.test.TestSuite;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * A service for receiving JMF messages over HTTP, essentially a HTTP server.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class HttpReceiver {

	private static Logger LOGGER = Logger.getLogger(HttpReceiver.class);

	private Server _server = null;

	private TestSuite _testSuite = null;

	/**
	 * Creates a new HttpReceiver. Received messages are added to the looked the specified <code>TestSuite</code>.
	 * 
	 * @param testSuite the <code>TestSuite</code> to add received messages to
	 */
	public HttpReceiver(TestSuite testSuite) {

		_testSuite = testSuite;

	}

	/**
	 * Starts a server that listens to requests at: <code>http://127.0.0.1:<strong>port</strong>/alces/jmf</code>
	 * 
	 * @param port
	 * @throws Exception
	 */
	public void startServer(int port) throws Exception {
		startServer("127.0.0.1", port);
	}

	/**
	 * Starts a server that listens for requests at: <code>http://<strong>host</strong>:<strong>port</strong>/alces/jmf</code>
	 * 
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public void startServer(String host, int port) throws Exception {
		startServer(host, port, "/alces/jmf/*", "../testdata");
	}

	/**
	 * Starts a HTTP server that publishes static files, such as JDF and resource files, to <code>http://<em>host</em>:<em>port</em>/</code> and listens for JMF
	 * requests on <code>http://<em>host</em>:<em>port</em>/<em>contextPath</em></code>.
	 * 
	 * @param host server hostname
	 * @param port server port
	 * @param jmfContextPath server context path where server listens for JMF messages
	 * @param resourceBase path in local filesystem to the root folder of the static files that will be published be the server
	 * @throws Exception if the HTTP server could not by started
	 */
	public synchronized void startServer(String host, int port, String jmfContextPath, String resourceBase) throws Exception {
		LOGGER.debug("Starting HTTP server on " + host + ":" + port + jmfContextPath + " ...");
		LOGGER.debug("Static content will be served from: " + resourceBase);
		if (_server != null && _server.isStarted()) {
			try {
				LOGGER.debug("Stopping active HTTP server before starting a new one.");
				stopServer();
			} catch (InterruptedException ie) {
				LOGGER.warn("HTTP server could not be stopped.");
			}
		}
		// Create the server
		_server = new Server();

		// Create a port listener
		SocketListener listener = new SocketListener();
		// XXXlistener.setHost(host);
		listener.setPort(port);
		_server.addListener(listener);

		// Create a context
		HttpContext context = new ServletHttpContext();
		context.setContextPath("/");
		_server.addContext(context);

		// Serve static content from the context
		context.setResourceBase(resourceBase);
		context.addHandler(new ResourceHandler());

		// Create a servlet container
		ServletHandler servlets = new ServletHandler();
		// Map a servlet onto the container
		ServletHolder holder = servlets.addServlet(jmfContextPath, "org.cip4.tools.alces.transport.JMFServlet");
		context.addHandler(servlets);

		// Start the http server
		_server.start();

		// Gives JMFServlet a reference to the pool of test sessions
		final JMFServlet servlet = (JMFServlet) holder.getServlet();
		servlet.setTestSessions(_testSuite);

		LOGGER.debug("Started HTTP server.");
	}

	/**
	 * Returns true if the service is started.
	 * 
	 * @return true if the service is started; false otherwise
	 */
	public synchronized boolean isStarted() {
		return _server.isStarted();
	}

	/**
	 * Stops the HTTP server.
	 * 
	 * @throws InterruptedExceptions
	 */
	public synchronized void stopServer() throws InterruptedException {
		LOGGER.debug("Stopping HTTP server...");
		_server.stop();
		_server = null;
		LOGGER.debug("Stopped HTTP server.");
	}
}
