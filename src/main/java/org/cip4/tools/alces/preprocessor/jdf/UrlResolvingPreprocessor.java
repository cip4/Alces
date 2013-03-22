/*
 * Created on Mar 27, 2007
 */
package org.cip4.tools.alces.preprocessor.jdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;

/**
 * A preprocessor that examines all //FileSpec/@URL attributes found in a JDF
 * instance. If a FileSpec/@URL contains a relative URL (the URL has no scheme),
 * then the URL is resolved against a base URL to create an absolute URL. The
 * FileSpec/@URL is then updated with the new aboluste URL.
 * 
 * @see #BASEURL_ATTR
 * 
 * @author Claes Buckwalter (claes.buckwalter@agfa.com)
 */
public class UrlResolvingPreprocessor implements JDFPreprocessor {

	private static Log log = LogFactory.getLog(UrlResolvingPreprocessor.class);

	/**
	 * Attribute for storing the new JobID in the context. If this preprocessor
	 * finds this attribute in the context it will use it as the new JobID.
	 */
	public final static String BASEURL_ATTR = "org.cip4.elk.alces.UrlResolvingPreprocessor.BaseURL";

	private final String defaultBaseUrl;

	public UrlResolvingPreprocessor() {
		this(null);
	}

	/**
	 * Creates a new URL resolving preprocessor.
	 * 
	 * @param baseUrl
	 *            the base URL to resolve relative URLs against
	 */
	public UrlResolvingPreprocessor(String baseUrl) {
		this.defaultBaseUrl = baseUrl;
	}

	/**
	 * All relative URLs (URLs witout a scheme) are resolved against the base
	 * URL and replaced with the resolved URL. If a URL cannot be resolved a
	 * warning is logged and the URL is skipped.
	 * @throws PreprocessorException 
	 */
	public JDFNode preprocess(JDFNode jdf) throws PreprocessorException {
		return preprocess(jdf, null);
	}

	/**
	 * All relative URLs (URLs witout a scheme) are resolved against the base
	 * URL and replaced with the resolved absolute URL. If a URL cannot be resolved a
	 * warning is logged and the URL is skipped.
	 * 
	 * If the {@link PreprocessorContext} contains the attribute {@link #BASEURL_ATTR} it
	 * will override the base URL configured by the constructor. 
	 */
	public JDFNode preprocess(JDFNode jdf, PreprocessorContext context)
			throws PreprocessorException {		
		String baseUrl = this.defaultBaseUrl;
		if (context != null && context.getAttribute(BASEURL_ATTR) != null) {
			baseUrl = (String) context.getAttribute(BASEURL_ATTR);
		}
		if (baseUrl == null) {
			log.warn("URL preprocessor not configured with base URL. No preprocessing will be performed.");
			return jdf;
		}
		log.debug("Resolving relative URLs in JDF '" + jdf.getJobID(true) + "' against base URL '" + baseUrl + "'...");
		return resolveReferencedFiles(jdf, baseUrl);
	}

	/**
	 * All relative URLs (URLs without a scheme) are resolved against the base
	 * URL and replaced with the resolved URL. If a URL cannot be resolved a
	 * warning is logged and the URL is skipped.
	 * 
	 * @param jdf
	 *            the JDF instance which file URLs should be resolved
	 * @param baseUrl
	 *            the base URL to resolved the relative URLs against
	 * @return the JDF instance with resolved URLs
	 */
	public static JDFNode resolveReferencedFiles(final JDFNode jdf, String baseUrl) {
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/"; // Add trailing slash to base URL
		}
		final List<KElement> fileSpecs = jdf.getChildrenByTagName(ElementName.FILESPEC, null, null,
				false, false, 0);
		for (KElement fileSpecElement : fileSpecs) {
			final JDFFileSpec fileSpec = (JDFFileSpec) fileSpecElement;
			final String url = fileSpec.getURL();
			log.debug("Resolving URL '" + url + "'...");
			final URI uri;
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				log.warn("Could not resolve URL '" + url + "'.", e);
				System.out.println(e);
				continue;
			}
			// Only resolve URLs without scheme or relative file URLs
			if (!uri.isAbsolute()) {
				String newUrl = baseUrl + uri.toASCIIString();
				try {
					URI newUri = new URI(newUrl);
					newUri = newUri.normalize();
					newUrl = newUri.toASCIIString();
				} catch (URISyntaxException e) {
					log.warn("Could not normalize URL '" + newUrl + "'. Normalization is skipped.");
				}
				fileSpec.setURL(newUrl);
				log.debug("Resolved and replaced URL with http URL '" + fileSpec.getURL()
						+ "'.");
			} else {
				log.warn("Could not resolve URL '" + url + "'. Left URL untouched.");
			}
		}
		return jdf;
	}
}
