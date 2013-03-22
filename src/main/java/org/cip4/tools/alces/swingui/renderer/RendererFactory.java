/*
 * Created on Jul 10, 2007
 */
package org.cip4.tools.alces.swingui.renderer;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFComment;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.swingui.actions.ActionWordWrap;
import org.cip4.tools.alces.swingui.tree.test.TestResultNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.tests.XsltTestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;

public class RendererFactory {
    public static boolean wordWrap = false;

	private static Log log = LogFactory.getLog(RendererFactory.class);

	/**
	 * Returns a <code>Component</code> that is a rendered version of a
	 * content object.
	 * 
	 * @param content
	 *            the content to render
	 * @return the <code>Component</code> that is a rendered version of the
	 *         content
	 */
	public static Component getRenderer(final Object content) {
		final Component renderer;
		if (content instanceof Message) {
			renderer = getRenderer((Message) content);
		} else if (content instanceof TestResultNode) {
			TestResult testResult = ((TestResultNode) content)
					.getWrappedTestResult();
			if (testResult instanceof XsltTestResult) {
				renderer = getRenderer((XsltTestResult) testResult);
			} else {
				renderer = getRenderer(testResult);
			}
		} else if (content instanceof TestResult) {
			renderer = getRenderer((TestResult) content);
		} else if (content instanceof DefaultMutableTreeNode) {
			renderer = getRenderer((DefaultMutableTreeNode) content);
		} else {
			renderer = getTextRenderer(content.toString());
		}
		return renderer;
	}

	public static synchronized Component getRenderer(final Message content) {
		if (content.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			JDFComment comment = (JDFComment) content.getBodyAsJMF()
					.getChildByTagName(
							ElementName.COMMENT,
							null,
							0,
							new JDFAttributeMap(AttributeName.NAME,
									"AgfaICSReport"), false, true);
			if (comment != null) {
				return getAgfaICSReportRenderer(comment);
			}
			return getXMLRenderer(content.getBody());			
		} else if (content.getContentType().startsWith(
				JDFConstants.JDF_CONTENT_TYPE)) {
			return getXMLRenderer(content.getBody());
		}
		return getTextRenderer(content.getBody());
	}

	public static synchronized Component getRenderer(
			final XsltTestResult content) {
		log.debug("Getting renderer for XSLT TestResult...");
		return getXHTMLRenderer(content.getResultString());
	}

	public static Component getRenderer(final TestResult content) {
		return getTextRenderer(content.getResultString());
	}

	public static Component getRenderer(final DefaultMutableTreeNode content) {
		return getTextRenderer(content.getUserObject().toString());
	}

	private static Component getXHTMLRenderer(String xhtml) {
		if (log.isDebugEnabled()) {
			log.debug("Getting renderer for XHTML:\n" + xhtml);
		}
		XHTMLPanel htmlRenderer = new XHTMLPanel();
		htmlRenderer.setDocumentFromString(xhtml, null,
				new XhtmlNamespaceHandler());
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
                    if (RendererFactory.wordWrap)
                    {
                    	actionWrap.putValue(Action.NAME, "Do not wrap lines");
                    }
                    else
                    {
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
