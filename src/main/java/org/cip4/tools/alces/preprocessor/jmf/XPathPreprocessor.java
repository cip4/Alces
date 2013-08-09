/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.JDFConstants;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * A preprocessor that sets a node selected by an XPaths to a new value.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class XPathPreprocessor implements Preprocessor {

	public static final String DEAFAULTNS_PREFIX_ATTR = "org.cip4.tools.alces.XPathPreprocessor.DefualtNSPrefix";

	public static final String XPATHPAIRS_ATTR = "org.cip4.tools.alces.XPathPreprocessor.XPathValuePairs";

	private static final String DEFAULT_NS_PREFIX = "jdf";

	private Map _xpathValuePairs = null;

	private String _defaultNsPrefix = DEFAULT_NS_PREFIX;

	private static Logger LOGGER = Logger.getLogger(XPathPreprocessor.class);

	public XPathPreprocessor() {
		this(null, null);
	}

	public XPathPreprocessor(Map xpathValuePairs) {
		this(xpathValuePairs, null);
	}

	public XPathPreprocessor(Map xpathValuePairs, String defaultNsPrefix) {
		_xpathValuePairs = xpathValuePairs;
		if (defaultNsPrefix != null) {
			_defaultNsPrefix = defaultNsPrefix;
		}
	}

	/**
	 * Sets the XPaths and the corresponding value that each XPath will replace the node it selects with.
	 * @param xpathValuePairs XPath expressions as keys; the new values as values
	 */
	public void setXpathValuePairs(Map xpathValuePairs) {
		_xpathValuePairs = xpathValuePairs;
	}

	/**
	 * Sets the prefix to use for XML documents that have a 'default namespace'. All namespaces need a prefix in order to be used in XPath expressions.
	 * 
	 * @param defaultNsPrefix
	 * @throws IllegalArgumentException if the String is null or of length zero
	 */
	public void setDefaultNsPrefix(String defaultNsPrefix) {
		if (defaultNsPrefix == null || defaultNsPrefix.length() == 0) {
			throw new IllegalArgumentException("The default namespace " + "prefix may not be null or of length zero.");
		}
		_defaultNsPrefix = defaultNsPrefix;
	}

	/**
	 * Preprocesses a message replacing all values that match the configured XPath expressions with their value pairs.
	 */
	public Message preprocess(Message message, PreprocessorContext context) throws PreprocessorException {
		if (!(message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE) || message.getContentType().startsWith(JDFConstants.JDF_CONTENT_TYPE) || message.getContentType().startsWith(
				JDFConstants.XML_CONTENT_TYPE))) {
			LOGGER.debug("Message not preprocessed because it did not contain JMF. Content-type was: " + message.getContentType());
			return message;
		}

		final Map xpathValuePairs;
		final String defaultNsPrefix;
		if (context != null && context.getAttribute(XPATHPAIRS_ATTR) != null) {
			xpathValuePairs = (Map) context.getAttribute(XPATHPAIRS_ATTR);
		} else {
			xpathValuePairs = _xpathValuePairs;
		}
		if (context != null && context.getAttribute(DEAFAULTNS_PREFIX_ATTR) != null) {
			defaultNsPrefix = (String) context.getAttribute(DEFAULT_NS_PREFIX);
		} else {
			defaultNsPrefix = _defaultNsPrefix;
		}
		if (_xpathValuePairs == null) {
			LOGGER.warn("The transformation will not modify the object because no" + " XPaths are configured.");
			return message;
		}

		try {
			// Parse String
			String xml = message.getBody();
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new StringReader(xml));

			// Replace values specified by XPaths
			for (Iterator i = xpathValuePairs.keySet().iterator(); i.hasNext();) {
				String xpath = (String) i.next();
				String value = (String) xpathValuePairs.get(xpath);
				doc = replace(doc, xpath, defaultNsPrefix, value);
			}
			// Output string
			XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
			message.setBody(outputter.outputString(doc));
			return message;
		} catch (Exception e) {
			String msg = "The XML String could not be preprocessed: " + message;
			throw new PreprocessorException(msg, e);
		}

	}

	/**
	 * Replaces Attribute values, Element text children, or text content that matches the specified XPath.
	 * 
	 * The namespace prefix <code>jdf</code> is used for the namespace URI <code>http://www.CIP4.org/JDFSchema_1_1</code>. Any previous namespace that uses the
	 * <code>jdf</code> prefix will be replaced.
	 * 
	 * @param doc the document to replace values in
	 * @param xpath the XPath to the nodes to replace
	 * @param value the new value that replaces the previous values
	 * @return a document with replaced values
	 * @throws JDOMException
	 */
	private Document replace(Document doc, String xpath, String defaultNsPrefix, String value) throws JDOMException {
		LOGGER.debug("Using XPath-value pair: " + xpath + " = " + value);
		XPath xp = XPath.newInstance(xpath);
		// Add root element's namespace to XPath
		Namespace n = doc.getRootElement().getNamespace();
		if (n.getPrefix().length() == 0) {
			// Replace empty prefix with default prefix
			xp.addNamespace(defaultNsPrefix, n.getURI());
			LOGGER.debug("Assigned namespace prefix '" + defaultNsPrefix + "' to default namespace URI '" + n.getURI() + "'.");
		} else {
			xp.addNamespace(n);
		}
		// Add other namespaces defined on root element to XPath
		List nsList = doc.getRootElement().getAdditionalNamespaces();
		for (Iterator i = nsList.iterator(); i.hasNext();) {
			Namespace ns = (Namespace) i.next();
			if (ns.getPrefix().length() == 0) {
				// Replace empty prefix with default prefix
				xp.addNamespace(defaultNsPrefix, ns.getURI());
				LOGGER.debug("Assigned namespace prefix '" + defaultNsPrefix + "' to default namespace URI '" + ns.getURI() + "'.");
			} else {
				xp.addNamespace(ns);
			}
		}
		// Evaluate XPath expression and replace
		List nodes = xp.selectNodes(doc);
		LOGGER.debug("Matching nodes: " + nodes.size());
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof Attribute) {
				((Attribute) o).setValue(value);
			} else if (o instanceof Element) {
				((Element) o).setText(value);
			} else if (o instanceof Text) {
				((Text) o).setText(value);
			} else {
				LOGGER.warn("XPath-value pair '" + xpath + " = " + value + "' was ignored because it did not select a node.");
			}
		}
		return doc;
	}

	public Message preprocess(final Message message) throws PreprocessorException {
		return preprocess(message, null);
	}
}