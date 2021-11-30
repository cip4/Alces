package org.cip4.tools.alces.ui.component

import groovy.xml.XmlSlurper
import groovy.xml.XmlUtil
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage
import org.cip4.tools.alces.service.testrunner.model.TestResult
import org.xhtmlrenderer.simple.XHTMLPanel
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler

import javax.swing.*
import java.awt.Component

/**
 * The rendered text area component.
 */
class JContentRenderer extends Component {

    /**
     * Private default constructor.
     */
    private JContentRenderer() {
        throw new UnsupportedOperationException("Class is static and cannot be instantiated.")
    }

    /**
     * Factory method to create a new instance.
     * @return A new JContentRenderer instance.
     */
    static Component newInstance() {
        return newInstance(null)
    }

    /**
     * Factory method to create a new instance.
     * @param userObject The user object to be rendered.
     * @return A new JContentRenderer instance.
     */
    static Component newInstance(Object userObject) {

        // analyze input
        if (userObject instanceof AbstractJmfMessage) {
            return render(userObject)

        } else if (userObject instanceof TestResult) {
            return render(userObject)

        } else {
            return createJTextArea();
        }
    }

    /**
     * Render an JTextArea showing the JMF Message.
     * @param abstractJmfMessage The JMF Message to be rendered.
     * @return JTextArea containing the rendered JMF Message.
     */
    private static Component render(TestResult testResult) {

        String XHTML_TAG = """
            <html xmlns="http://www.w3.org/1999/xhtml">
            """

        if (testResult.getResultBody().contains(XHTML_TAG.trim())) {

            // xhtml output
            XHTMLPanel htmlRenderer = new XHTMLPanel()
            htmlRenderer.setDocumentFromString(testResult.getResultBody(), null, new XhtmlNamespaceHandler())
            return htmlRenderer

        } else {

            // create text output
            JTextArea textArea = createJTextArea()
            textArea.setText(testResult.getResultBody())
            return textArea
        }
    }

    /**
     * Render an JTextArea showing the JMF Message.
     * @param abstractJmfMessage The JMF Message to be rendered.
     * @return JTextArea containing the rendered JMF Message.
     */
    private static Component render(AbstractJmfMessage abstractJmfMessage) {

        // parse xml
        def jmf = new XmlSlurper(false, false).parseText(abstractJmfMessage.getBody())

        // new line after xml declaration
        String strXml = XmlUtil.serialize(jmf)

        int pos = strXml.indexOf("<", 1)
        strXml = strXml.substring(0, pos) + System.getProperty("line.separator") + strXml.substring(pos)

        // create output component
        JTextArea textArea = createJTextArea()
        textArea.setText(strXml)

        // return text area
        return textArea
    }

    /**
     * Returns a empty JTextArea object for further processing.
     * @return An empty JTextArea object.
     */
    private static JTextArea createJTextArea() {

        // create JTextArea component
        JTextArea textArea = new JTextArea()

        // initialize
        textArea.setEditable(false)
        textArea.setLineWrap(false)
        textArea.setWrapStyleWord(false)

        // return
        return textArea;
    }
}
