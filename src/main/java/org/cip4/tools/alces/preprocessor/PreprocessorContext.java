/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.preprocessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A context where attributes can be set that and accessed by a preprocessor.
 * 
 * @author Claes Buckwalter
 */
public class PreprocessorContext {

	private final Map<String, Object> contextAttributes = new HashMap<String, Object>();
	
	public void addAttribute(String name, Object value) {
		contextAttributes.put(name, value);
	}
	
	public Object getAttribute(String name) {
		return contextAttributes.get(name);
	}
}
