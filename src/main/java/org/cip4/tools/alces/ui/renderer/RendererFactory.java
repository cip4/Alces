/*
 * Created on Jul 10, 2007
 */
package org.cip4.tools.alces.ui.renderer;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFComment;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.ui.actions.ActionWordWrap;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.tests.XsltTestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;

public class RendererFactory {

    private static Logger log = LoggerFactory.getLogger(RendererFactory.class);

    public static boolean wordWrap = false;


    /**
     * Returns a component object that is a rendered version of a content object.
     *
     * @param content the content to render
     * @return The Component that is a rendered version of the content.
     */
    public static Component getRenderedComponent(Object content) {
        Component renderer;

        if (content instanceof AbstractJmfMessage abstractJmfMessage) { // jmf message
            renderer = getRenderer(abstractJmfMessage);

        } else if (content instanceof TestResult testResult) { // test result
            if (testResult instanceof XsltTestResult) {
                renderer = getRenderer((XsltTestResult) testResult);
            } else {
                renderer = getRenderer(testResult);
            }

        } else if (content instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
            renderer = getRenderer(defaultMutableTreeNode);

        } else {
            renderer = getTextRenderer(content.toString());
        }

        // return render
        return renderer;
    }

    private static synchronized Component getRenderer(final AbstractJmfMessage content) {
        if (content.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
            JDFComment comment = (JDFComment) JmfUtil.getBodyAsJMF(content).getChildByTagName(ElementName.COMMENT, null, 0, new JDFAttributeMap(AttributeName.NAME, "AgfaICSReport"), false, true);
            if (comment != null) {
                return getAgfaICSReportRenderer(comment);
            }
            return getXMLRenderer(content.getBody());
        } else if (content.getContentType().startsWith(JDFConstants.JDF_CONTENT_TYPE)) {
            return getXMLRenderer(content.getBody());
        }
        return getTextRenderer(content.getBody());
    }

    private static synchronized Component getRenderer(final XsltTestResult content) {
        log.debug("Getting renderer for XSLT TestResult...");
        return getXHTMLRenderer(content.getResultString());
    }

    private static Component getRenderer(final TestResult content) {
        return getTextRenderer(content.getResultString());
    }

    private static Component getRenderer(final DefaultMutableTreeNode content) {
        return getTextRenderer(content.getUserObject().toString());
    }

    private static Component getXHTMLRenderer(String xhtml) {
        if (log.isDebugEnabled()) {
            log.debug("Getting renderer for XHTML:\n" + xhtml);
        }
        XHTMLPanel htmlRenderer = new XHTMLPanel();
        htmlRenderer.setDocumentFromString(xhtml, null, new XhtmlNamespaceHandler());
        return htmlRenderer;
    }

    private static synchronized Component getXMLRenderer(String xml) {
        try {
            StringWriter sw = new StringWriter();
            StringReader sr = new StringReader(xml);
            Document doc = new SAXBuilder().build(sr);
            XMLOutputter xmlOut = new XMLOutputter();
            xmlOut.setFormat(Format.getPrettyFormat());
            xmlOut.output(doc, sw);
            xml = sw.getBuffer().toString();
        } catch (Exception e) {
            log.debug("Could not format XML.", e);
        }
        JTextArea xmlRenderer = getTextRenderer(xml);
        xmlRenderer.setWrapStyleWord(true);
        return xmlRenderer;
    }

    private static JTextArea getTextRenderer(String text) {
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(wordWrap);
        textArea.setWrapStyleWord(wordWrap);
        textArea.setText(text);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu popUp = new JPopupMenu("test");
                    Action actionWrap = new ActionWordWrap(textArea);
                    if (RendererFactory.wordWrap) {
                        actionWrap.putValue(Action.NAME, "Do not wrap lines");
                    } else {
                        actionWrap.putValue(Action.NAME, "Warp lines");
                    }
                    popUp.add(actionWrap);
                    popUp.show(textArea, e.getX(), e.getY());
                }
            }
        });
        return textArea;
    }

    private static JTextArea getAgfaICSReportRenderer(JDFComment comment) {
        return getTextRenderer(comment.getText());
    }
}
