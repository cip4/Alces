package org.cip4.tools.alces.swingui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.swingui.tree.message.AbstractMessageNode;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.util.ApplicationContextUtil;
import org.cip4.tools.alces.util.JDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action that saves the selected message sub-tree to disk.
 * 
 * @author Alex Khilov
 * @since 0.9.9.3
 */
public class ActionSaveRequestsResponcesToDisk extends AbstractAction {
	private static Logger log = LoggerFactory.getLogger(ActionSaveRequestsResponcesToDisk.class);

	private JTree tree;
	private TreePath path;

	public static int counterFiles = 0;
	public static String pathPrefix = "c:/temp";

	private SettingsService settingsService = ApplicationContextUtil.getBean(SettingsService.class);

	public ActionSaveRequestsResponcesToDisk(final JTree tree, final TreePath path) {
		this.tree = tree;
		this.path = path;
	}

	public void actionPerformed(ActionEvent e) {
		TreeNode rootNode = (TreeNode) tree.getModel().getRoot();
		findSelectedNode(new TreePath(rootNode), tree.getSelectionPath().getLastPathComponent());
	}

	private void findSelectedNode(TreePath parent, Object currentlySelectedObject) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				// TreePath path = parent.pathByAddingChild(n);

				// if (currentlySelectedPath.equals(path.toString()))
				if (currentlySelectedObject == n) {
					// System.out.println("--- Node found n = " + n + ", class = " + n.getClass());
					String folder = n.toString();
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
					String outputDirDate = dateFormat.format(new Date());
					folder = outputDirDate + ".--." + folder;

					folder = updateString(folder);
					log.info("Folder name = " + folder);
					findReqRes(n, folder);
				}

				// findSelectedNode(tree, path, currentlySelectedPath); // not needed, due to - single node selection
			}
		}
	}

	private void findReqRes(TreeNode node, String folderName) {
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				// TreePath path = parent.pathByAddingChild(n);

				if (n instanceof AbstractMessageNode) {
					AbstractMessageNode messageNode = (AbstractMessageNode) n;
					// System.out.println("Title/filename = " +
					// messageNode.toString().trim() + ".xml");
					String extension = JDFConstants.XML_EXTENSION;
					// TODO: see JDFConstants
					if (messageNode.getJmfMessage().getContentType().contains(JDFConstants.JMF_CONTENT_TYPE)) {
						extension = JDFConstants.JMF_EXTENSION;
					} else if (messageNode.getJmfMessage().getContentType().contains(JDFConstants.JDF_CONTENT_TYPE)) {
						extension = JDFConstants.JDF_EXTENSION;
					} else if (messageNode.getJmfMessage().getContentType().contains(JDFConstants.MIME_CONTENT_TYPE)) {
						extension = JDFConstants.JMF_MIME_EXTENSION;
					}

					String fileName = updateString(messageNode.toString().trim());
					fileName += extension;
					log.info("File name = " + fileName);
					log.info("file content type = " + messageNode.getJmfMessage().getContentType());
					log.info("file content = " + messageNode.getJmfMessage().getBody());
					save(folderName, fileName, messageNode.getJmfMessage().getBody());
				}
				findReqRes(n, folderName);
			}
		}

	}

	private String updateString(String str) {
		str = str.replace(':', '!');
		str = str.replace('/', '-');
		return str;
	}

	private void save(String folderName, String fileName, String fileContent) {
		counterFiles++;

		pathPrefix = settingsService.getProp(SettingsServiceImpl.PATH_TO_SAVE);
		String strDirectory = pathPrefix + "/" + folderName;

		// create folder
		boolean success = (new File(strDirectory)).mkdir();
		/*
		 * if (success) { System.out.println("Directory: " + strDirectory + " created"); } else { System.out.println("Directory: " + strDirectory +
		 * " does not created"); }
		 */

		String defPrefix = "0000";
		String filePrefix = defPrefix + counterFiles;
		filePrefix = filePrefix.substring(filePrefix.length() - defPrefix.length(), filePrefix.length());
		filePrefix += "-";

		fileName = filePrefix + fileName;

		// if the file does not already exist, it is automatically created
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(strDirectory + File.separator + fileName));
			out.write(fileContent);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
