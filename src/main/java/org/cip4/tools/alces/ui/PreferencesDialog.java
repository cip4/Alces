/*
 * Created on Jun 1, 2005
 */
package org.cip4.tools.alces.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Display the Preferences DiaLOGGER. To choose settings for validation
 */
public class PreferencesDialog extends JDialog implements ActionListener {

	private static Logger log = LoggerFactory.getLogger(PreferencesDialog.class);

	private JPanel validationPanel = null;

	private JPanel pathPanel = null;

	/**
	 * Custom Panel represents Custom Settings only.
	 */
	private JPanel customPanel = null;

	private JPanel mimePanel = null;

	private JPanel messagePanel = null;

	private JPanel batchPanel = null;

	private JTabbedPane tabbedPane = null;

	private JButton saveButton = null;

	private JButton cancelButton = null;

	private JButton jdfPathButton = null;

	private JButton jmfPathButton = null;

	private JLabel incomingLabel = null;

	private JLabel outgoingLabel = null;

	private JLabel jdfPathDesLabel = null;

	private JLabel jmfPathDesLabel = null;

	JTextField jdfPathField = null;

	JTextField jmfPathField = null;

	private Map<String, Boolean> _prefIn = null;

	private Map<String, Boolean> _prefOut = null;

	private Map _gerneralPrefs = null;

	private Alces _owner = null;

	private SettingsService settingsService;

	private File jdfPath = null;

	private File jmfPath = null;

	// Message-Settings tab
	private JCheckBox noContentTypeCheckBox = null;

	private JCheckBox specificIpCheckBox = null;
	private JComboBox ipComboBox = null;

	private JTextField indentTextField;

	private JTextField indentWidthTextField;

	/**
	 * Constructor for the Dialog
	 * 
	 * @param owner
	 * @param title
	 */
	public PreferencesDialog(Alces owner, String title) {
		super(owner, true);
		settingsService = ApplicationContextUtil.getBean(SettingsService.class);
		_owner = owner;

		setTitle("Preferences");
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		setSize(500, 300);

		validationPanel = new JPanel();
		validationPanel.setLayout(new BorderLayout());

		pathPanel = new JPanel();
		buildPathPanel();

		customPanel = new JPanel();
		customPanel.setLayout(new BorderLayout());
		buildCustomPanel();

		mimePanel = new JPanel();
		mimePanel.setLayout(new BorderLayout());
		buildMimePanel();

		messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		buildMessagePanel();

		batchPanel = new JPanel();
		batchPanel.setLayout(new BorderLayout());
		buildBatchPanel();

		tabbedPane.addTab("Validation", createIcon("/org/cip4/tools/alces/icons/test_pass.gif"), validationPanel, "");

		tabbedPane.addTab("JMF Preprocessing", createIcon("/org/cip4/tools/alces/icons/test_pass.gif"), messagePanel, "");

		tabbedPane.addTab("MIME Preprocessing", createIcon("/org/cip4/tools/alces/icons/test_pass.gif"), mimePanel, "");

		tabbedPane.addTab("Directories", createIcon("/org/cip4/tools/alces/icons/jmf.gif"), pathPanel, "");

		tabbedPane.addTab("Batch Mode", createIcon("/org/cip4/tools/alces/icons/test_pass.gif"), batchPanel, "");

		tabbedPane.addTab("Advanced", createIcon("/org/cip4/tools/alces/icons/test_pass.gif"), customPanel, "");

		// Incoming tests
		JPanel inPanel = new JPanel();
		inPanel.setLayout(new BoxLayout(inPanel, BoxLayout.PAGE_AXIS));
		incomingLabel = new JLabel("Incoming Messages:");
		incomingLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
		inPanel.add(incomingLabel);
		// Load incoming tests from file
		_prefIn = settingsService.getIncomingTestConfig();
		Set inTestClasses = _prefIn.keySet();
		for (Iterator i = inTestClasses.iterator(); i.hasNext();) {
			final String testClass = (String) i.next();
			String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);
			boolean checked = _prefIn.get(testClass);
			JCheckBox testCheck = new JCheckBox(testClassName, checked);
			testCheck.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent ie) {
					if (ie.getStateChange() == ItemEvent.SELECTED) {
						_prefIn.put(testClass, true);
					} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
						_prefIn.put(testClass, false);
					}
				}
			});
			inPanel.add(testCheck);
		}
		validationPanel.add(inPanel, BorderLayout.WEST);

		// Outgoing tests
		JPanel outPanel = new JPanel();
		outPanel.setLayout(new BoxLayout(outPanel, BoxLayout.PAGE_AXIS));
		outgoingLabel = new JLabel("Outgoing Messages:");
		outgoingLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
		outPanel.add(outgoingLabel);
		// Load outgoing tests from file
		_prefOut = new HashMap(settingsService.getOutgoingTestConfig());
		Set outTestClasses = _prefOut.keySet();
		for (Iterator i = outTestClasses.iterator(); i.hasNext();) {
			final String testClass = (String) i.next();
			String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);
			boolean checked = _prefOut.get(testClass);
			JCheckBox testCheck = new JCheckBox(testClassName, checked);
			testCheck.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent ie) {
					if (ie.getStateChange() == ItemEvent.SELECTED) {
						_prefOut.put(testClass, true);
					} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
						_prefOut.put(testClass, false);
					}
				}
			});
			outPanel.add(testCheck);
		}
		validationPanel.add(outPanel, BorderLayout.EAST);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setPosition(owner);
		this.setResizable(true);
		setVisible(true);
	}

	/**
	 * Builds the GUI elements of the <code>pathPanel</code>
	 * 
	 */
	private void buildPathPanel() {
		getGeneralPrefs();
		pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.PAGE_AXIS));
		// JDF
		JPanel jdfPathPanel = new JPanel();
		jdfPathPanel.setLayout(new BorderLayout());
		jdfPathDesLabel = new JLabel("Path to JDF-Files for SubmitQueueEntry context-menu:");
		jdfPathPanel.add(jdfPathDesLabel, BorderLayout.NORTH);
		jdfPathField = new JTextField();
		jdfPathPanel.add(jdfPathField, BorderLayout.CENTER);
		jdfPathButton = new JButton("Browse");
		jdfPathButton.addActionListener(this);
		jdfPathPanel.add(jdfPathButton, BorderLayout.EAST);
		pathPanel.add(jdfPathPanel);
		// JMF
		JPanel jmfPathPanel = new JPanel();
		jmfPathPanel.setLayout(new BorderLayout());
		jmfPathDesLabel = new JLabel("Path to Files for Send File context-menu:");
		jmfPathDesLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		jmfPathPanel.add(jmfPathDesLabel, BorderLayout.NORTH);
		jmfPathField = new JTextField();
		jmfPathPanel.add(jmfPathField, BorderLayout.CENTER);
		jmfPathButton = new JButton("Browse...");
		jmfPathButton.addActionListener(this);
		jmfPathPanel.add(jmfPathButton, BorderLayout.EAST);
		pathPanel.add(jmfPathPanel);

		pathPanel.add(Box.createVerticalGlue());

		updatePathLabel();
	}

	private void buildCustomPanel() {
		JPanel inPanel = new JPanel();
		inPanel.setLayout(new BoxLayout(inPanel, BoxLayout.PAGE_AXIS));

		JPanel noContentTypePanel = new JPanel();
		noContentTypePanel.setLayout(new BorderLayout());
		Boolean noContentTypeEnabled = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.NO_CONTENT_TYPE));
		noContentTypeCheckBox = new JCheckBox("Treat incoming messages with empty Content-Type as JMF", noContentTypeEnabled);
		noContentTypePanel.add(noContentTypeCheckBox, BorderLayout.NORTH);

		JPanel connectMsgPanel = new JPanel();
		connectMsgPanel.setLayout(new BorderLayout());
		Boolean showConnectMessagesEnabled = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES));
		JCheckBox showConnectMsgCheckBox = new JCheckBox("Show JMF messages sent during connect handshake", showConnectMessagesEnabled);
		showConnectMsgCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES, "false");
				}
			}
		});
		connectMsgPanel.add(showConnectMsgCheckBox, BorderLayout.NORTH);

		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BorderLayout());

		specificIpCheckBox = new JCheckBox("Use specified IP-address", Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.USE_SPECIFIED_IP)));
		ipPanel.add(new JLabel("Reply IP-address to use:"), BorderLayout.WEST);
		ipComboBox = new JComboBox();
		ipComboBox.setEditable(true);
		if (specificIpCheckBox.isSelected()) {
			fillWithIPAddresses(ipComboBox);
		} else {
			fillWithIPAddress(ipComboBox);
		}

		specificIpCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (specificIpCheckBox.isSelected()) {
					fillWithIPAddresses(ipComboBox);
				} else {
					fillWithIPAddress(ipComboBox);
				}
			}
		});
		ipPanel.add(specificIpCheckBox, BorderLayout.NORTH);
		ipPanel.add(ipComboBox, BorderLayout.CENTER);

		JPanel pathToSavePanel = new JPanel();
		pathToSavePanel.setLayout(new BorderLayout());

		JLabel pathLabel = new JLabel("Path to save Requests / Responses");
		pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		final JTextField pathTextField = new JTextField(settingsService.getProp(SettingsServiceImpl.PATH_TO_SAVE));
		JButton savePathButton = new JButton("Browse...");
		savePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(jdfPath);
				chooser.setCurrentDirectory(new File(pathTextField.getText()));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = chooser.showDialog(pathPanel, "Choose Folder for Output-Files");
				chooser.setFileHidingEnabled(true);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					pathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
					settingsService.putProp(SettingsServiceImpl.PATH_TO_SAVE, chooser.getSelectedFile().getAbsolutePath());
				}
			}

		});

		pathToSavePanel.add(pathLabel, BorderLayout.NORTH);
		pathToSavePanel.add(pathTextField, BorderLayout.CENTER);
		pathToSavePanel.add(savePathButton, BorderLayout.EAST);

		inPanel.add(noContentTypePanel);
		inPanel.add(connectMsgPanel);
		inPanel.add(ipPanel);
		inPanel.add(pathToSavePanel);

		customPanel.add(inPanel);
	}

	private void fillWithIPAddress(JComboBox ipComboBox) {
		ipComboBox.removeAllItems();
		try {
			String addr = (InetAddress.getLocalHost().getHostAddress()).toString();
			log.info("addr = " + addr);
			ipComboBox.setSelectedItem(addr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ipComboBox.setEditable(false);
	}

	private void fillWithIPAddresses(JComboBox ipComboBox) {
		ipComboBox.removeAllItems();
		ipComboBox.setEditable(true);
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> inetAddress = ni.getInetAddresses();
				while (inetAddress.hasMoreElements()) {
					InetAddress address = inetAddress.nextElement();
					log.info("address: " + address.getHostAddress());
					ipComboBox.addItem(address.getHostAddress());
				}
				log.info("------- next interface");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		ipComboBox.setSelectedItem(settingsService.getProp(SettingsServiceImpl.HOST));
	}

	private void buildMimePanel() {
		JPanel inPanel = new JPanel();
		inPanel.setLayout(new BoxLayout(inPanel, BoxLayout.PAGE_AXIS));

		// .MJM Panel
		JPanel sendMjmMimeFilePanel = new JPanel();
		sendMjmMimeFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 6, 6), BorderFactory.createTitledBorder("Send .MJM MIME File")), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		sendMjmMimeFilePanel.setLayout(new BorderLayout());

		String mjmCheckedStr = settingsService.getProp(SettingsServiceImpl.MJM_MIME_FILE_PARSE);
		boolean mjmChecked = Boolean.parseBoolean(mjmCheckedStr);
		JCheckBox fileParsingCheck = new JCheckBox("Enable .MJM MIME file parsing", mjmChecked);
		fileParsingCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.MJM_MIME_FILE_PARSE, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.MJM_MIME_FILE_PARSE, "false");
				}
			}
		});

		// Panel with MIME specific settings
		JPanel mimeSettingsPanel = new JPanel(new GridLayout(3, 2));
		JLabel contentTypeLabel = new JLabel("Content-Type");
		JComboBox contentTypeComboBox = new JComboBox();
		contentTypeComboBox.addItem("binary");
		contentTypeComboBox.addItem("7bit");
		contentTypeComboBox.addItem("quoted-printable");
		contentTypeComboBox.setSelectedItem(settingsService.getProp(SettingsServiceImpl.MIME_CONTENT_TYPE));
		contentTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.MIME_CONTENT_TYPE, (String) ie.getItem());
				}
			}
		});

		JLabel indentLabel = new JLabel("XML indent");
		indentTextField = new JTextField(settingsService.getProp(SettingsServiceImpl.MIME_INDENT));

		JLabel indentWidthLabel = new JLabel("XML line width");
		indentWidthTextField = new JTextField(settingsService.getProp(SettingsServiceImpl.MIME_LINE_WIDTH));

		mimeSettingsPanel.add(contentTypeLabel);
		mimeSettingsPanel.add(contentTypeComboBox);
		mimeSettingsPanel.add(indentLabel);
		mimeSettingsPanel.add(indentTextField);
		mimeSettingsPanel.add(indentWidthLabel);
		mimeSettingsPanel.add(indentWidthTextField);

		sendMjmMimeFilePanel.add(fileParsingCheck, BorderLayout.NORTH);
		sendMjmMimeFilePanel.add(mimeSettingsPanel, BorderLayout.CENTER);

		inPanel.add(sendMjmMimeFilePanel);
		mimePanel.add(inPanel);
	}

	private void buildMessagePanel() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));

		boolean checked = false;

		JLabel messageIdPreprocessorLabel = new JLabel("MessageIDPreprocessor:");
		messageIdPreprocessorLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
		westPanel.add(messageIdPreprocessorLabel);

		checked = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.UPDATE_MESSAGEID));
		JCheckBox updateMessageIdCheckBox = new JCheckBox("Update Message-ID (Query@ID, Command@ID)", checked);
		updateMessageIdCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_MESSAGEID, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_MESSAGEID, "false");
				}
			}
		});
		westPanel.add(updateMessageIdCheckBox);

		JLabel urlPreprocessorLabel = new JLabel("URLPreprocessor:");
		urlPreprocessorLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 10));
		westPanel.add(urlPreprocessorLabel);

		checked = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.UPDATE_ACKNOWLEDGEURL));
		JCheckBox updateAcknowledgeUrlCheckBox = new JCheckBox("Update AcknowledgeURL", checked);
		updateAcknowledgeUrlCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_ACKNOWLEDGEURL, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_ACKNOWLEDGEURL, "false");
				}
			}
		});
		westPanel.add(updateAcknowledgeUrlCheckBox);

		checked = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.UPDATE_RETURNURL));
		JCheckBox updateReturnUrlCheckBox = new JCheckBox("Update ReturnURL", checked);
		updateReturnUrlCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_RETURNURL, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_RETURNURL, "false");
				}
			}
		});
		westPanel.add(updateReturnUrlCheckBox);

		checked = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.UPDATE_RETURNURL));
		JCheckBox updateReturnJmfCheckBox = new JCheckBox("Update ReturnJMF", checked);
		updateReturnJmfCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_RETURNJMF, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_RETURNJMF, "false");
				}
			}
		});
		westPanel.add(updateReturnJmfCheckBox);

		checked = Boolean.parseBoolean(settingsService.getProp(SettingsServiceImpl.UPDATE_WATCHURL));
		JCheckBox updateWatchUrlCheckBox = new JCheckBox("Update WatchURL", checked);
		updateWatchUrlCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_WATCHURL, "true");
				} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
					settingsService.putProp(SettingsServiceImpl.UPDATE_WATCHURL, "false");
				}
			}
		});
		westPanel.add(updateWatchUrlCheckBox);

		messagePanel.add(westPanel, BorderLayout.WEST);
	}

	private void buildBatchPanel() {
		JPanel panel = new JPanel();
		String value = settingsService.getProp(SettingsServiceImpl.BATCHMODE_DELAYTONEXT_FILE);
		final JSpinner s = new JSpinner();
		s.setValue(Integer.parseInt(value));
		s.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settingsService.putProp(SettingsServiceImpl.BATCHMODE_DELAYTONEXT_FILE, "" + (Integer) s.getValue());
			}
		});
		panel.add(s);

		panel.add(new JLabel("Delay to next file, ms"));

		batchPanel.add(panel);
	}

	private void getGeneralPrefs() {
		_gerneralPrefs = settingsService.getGeneralPrefs();

		jdfPath = new File(_gerneralPrefs.get("alces.context.jdf.path").toString());
		jmfPath = new File(_gerneralPrefs.get("alces.context.jmf.path").toString());
	}

	/**
	 * Sets the dialog position to the center of the main-gui position
	 * 
	 * @param source
	 */
	private void setPosition(JFrame source) {
		int x = source.getWidth();
		int y = source.getHeight();
		this.setLocation(x / 2 - (this.getWidth() / 2), y / 2 - (this.getHeight() / 2));

	}

	/**
	 * Reads the changes for the general Preferences, e.g. JDF- and JMF-Path
	 * 
	 * @return _gerneralPrefs
	 */
	private Map adoptGeneralPrefs() {
		_gerneralPrefs = null;
		_gerneralPrefs = new HashMap();
		_gerneralPrefs.put("alces.context.jdf.path", jdfPath.getPath());
		_gerneralPrefs.put("alces.context.jmf.path", jmfPath.getPath());
		return _gerneralPrefs;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.dispose();
		} else if (e.getSource() == saveButton) {
			settingsService.setIncomingTestConfig(_prefIn);
			settingsService.setOutgoingTestConfig(_prefOut);
			settingsService.setPreferences(adoptGeneralPrefs());

			settingsService.putProp(SettingsServiceImpl.NO_CONTENT_TYPE, "" + noContentTypeCheckBox.isSelected());

			settingsService.putProp(SettingsServiceImpl.USE_SPECIFIED_IP, "" + specificIpCheckBox.isSelected());
			settingsService.putProp(SettingsServiceImpl.HOST, (String) ipComboBox.getSelectedItem());

			settingsService.putProp(SettingsServiceImpl.MIME_INDENT, indentTextField.getText());
			settingsService.putProp(SettingsServiceImpl.MIME_LINE_WIDTH, indentWidthTextField.getText());

			this.dispose();
		} else if (e.getSource() == jdfPathButton) {
			openFileChooserJDF();
		} else if (e.getSource() == jmfPathButton) {
			openFileChooserJMF();
		}
	}

	/**
	 * Builds the FileChooser for selecting the default Folder for JDF-Files
	 * 
	 */
	public void openFileChooserJDF() {
		JFileChooser chooser = new JFileChooser(jdfPath);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showDialog(this, "Choose Folder for JDF-Files");
		chooser.setFileHidingEnabled(true);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			jdfPath = chooser.getSelectedFile();
		}
		updatePathLabel();

	}

	/**
	 * Builds the FileChooser for selecting the default Folder for JMF-Files
	 * 
	 */
	public void openFileChooserJMF() {
		JFileChooser chooser = new JFileChooser(jmfPath);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showDialog(this, "Choose Folder for JMF-Files");
		chooser.setFileHidingEnabled(true);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			jmfPath = chooser.getSelectedFile();
		}
		updatePathLabel();

	}

	/**
	 * Trims the path for the display as label
	 * 
	 */
	private void updatePathLabel() {
		jdfPathField.setText(jdfPath.getPath());
		jmfPathField.setText(jmfPath.getPath());
	}

	/**
	 * Loads an icon.
	 * 
	 * @param path
	 * @return the icon, or null if the icon could not be loaded
	 */
	private ImageIcon createIcon(String path) {
		java.net.URL imgURL = this.getClass().getResource(path);
		ImageIcon icon = null;
		if (imgURL != null) {
			icon = new ImageIcon(imgURL);
		} else {
			log.warn("Could not load icon from path: " + path);
		}
		return icon;
	}

}
