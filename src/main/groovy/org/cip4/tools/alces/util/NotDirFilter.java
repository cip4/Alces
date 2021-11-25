/*
 * Created on Apr 3, 2007
 */
package org.cip4.tools.alces.util;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter that does not accept directories or files that start with ".".
 * 
 * @author Claes Buckwalter
 */
public class NotDirFilter implements FileFilter {
	public boolean accept(File file) {
		return !(file.isDirectory() || file.getName().startsWith("."));
	}
}
