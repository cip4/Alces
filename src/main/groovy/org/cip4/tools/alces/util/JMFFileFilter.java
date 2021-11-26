/*
 * Created on Jun 2, 2005
 */
package org.cip4.tools.alces.util;

import java.io.File;

/**
 * A filter that only accepts JMF message files and directories that whose 
 * filenames do not start with '.'.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JMFFileFilter extends javax.swing.filechooser.FileFilter implements
		java.io.FileFilter {

	/**
	 * Returns <code>true</code> if the filename ends with <code>.jmf</code>
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".jmf")
				|| (pathname.isDirectory() && !pathname.getName().startsWith("."));
	}

	public String getDescription() {
		return "JMF Messages (*.jmf)";
	}

	/**
	 * Directories are not allowed, e.g. in the context menus
	 * 
	 * @param pathname
	 * @return
	 */
	public boolean acceptFilesOnly(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".jmf");
	}

}
