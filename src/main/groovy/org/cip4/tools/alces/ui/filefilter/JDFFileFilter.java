package org.cip4.tools.alces.ui.filefilter;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A filter that only accepts JDF job ticket files and directories that whose 
 * filenames do not start with '.'.
 */
public class JDFFileFilter extends FileFilter {

	/**
	 * Returns true if the filename ends with 'jdf'.
	 */
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".jdf")
				|| (pathname.isDirectory() && !pathname.getName().startsWith("."));
	}

	public String getDescription() {
		return "JDF Job Tickets (*.jdf)";
	}
}
