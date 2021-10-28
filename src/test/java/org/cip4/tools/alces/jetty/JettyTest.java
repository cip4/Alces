package org.cip4.tools.alces.jetty;

import org.junit.jupiter.api.Test;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JettyTest {

    private static Logger log = LoggerFactory.getLogger(JettyTest.class);

    @Test
    public void testJetty() throws Exception {

        // Create the server
        HttpServer server = new HttpServer();

        // Create a port listener
        SocketListener listener = new SocketListener();
        listener.setPort(8181);
        server.addListener(listener);

        // Create a context
        HttpContext context = new ServletHttpContext();
        context.setContextPath("/apan/");
        server.addContext(context);

        // Serve static content from the context
        context.setResourceBase("./bin/");
        context.addHandler(new ResourceHandler());

        // Create a servlet container
        ServletHandler servlets = new ServletHandler();
        // Map a servlet onto the container
        servlets.addServlet("/dump/*", "org.mortbay.servlet.Dump");
        context.addHandler(servlets);

        // Start the http server
        server.start();
        int sleepTime = 2000;
        log.info("Sleeping " + sleepTime + "...");
        Thread.sleep(sleepTime);
        server.stop();
    }
}
