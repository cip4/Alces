package org.cip4.tools.alces.jetty;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.*;
import org.mortbay.jetty.servlet.*;
import org.mortbay.http.handler.*;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JettyTest extends TestCase {

    public void testJetty() throws Exception {
        
        // Create the server
        HttpServer server=new HttpServer();
          
        // Create a port listener
        SocketListener listener=new SocketListener();
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
        servlets.addServlet("/dump/*","org.mortbay.servlet.Dump");
        context.addHandler(servlets);
        
        // Start the http server
        server.start();
        int sleepTime = 5000;
        System.out.println("Sleeping " + sleepTime + "...");
        Thread.sleep(sleepTime);
        server.stop();
    }
}