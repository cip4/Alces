package org.cip4.tools.alces.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.cip4.jdflib.auto.JDFAutoMessageService.EnumJMFRole;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.jmf.*;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;
import org.cip4.jdflib.resource.JDFDevice;
import org.cip4.jdflib.resource.JDFDeviceList;
import org.cip4.tools.alces.jmf.JMFMessageBuilder;
import org.cip4.tools.alces.jmf.JMFMessageFactory;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.service.AboutService;
import org.cip4.tools.alces.swingui.actions.ActionCollapse;
import org.cip4.tools.alces.swingui.actions.ActionCollapseAll;
import org.cip4.tools.alces.swingui.actions.ActionSaveRequestsResponcesToDisk;
import org.cip4.tools.alces.swingui.renderer.RendererFactory;
import org.cip4.tools.alces.swingui.tree.test.TestSuiteNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestRunner;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.cip4.tools.alces.util.JDFFileFilter;
import org.cip4.tools.alces.util.JMFFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

/**
 * The Alces Swing GUI application for interactive testing.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
@SpringBootApplication
@ComponentScan({"org.cip4.tools.alces"})
public class Alces extends JFrame implements ActionListener, TreeModelListener, TreeSelectionListener, MouseListener {

	// -----------------------------------------------------
	// | Address Bar |
	// |---------------------------------------------------|
	// | Device | Test | Test View |
	// | Info | Session | |
	// |---------| Tree | |
	// | Message | | |
	// | Buttons | | |
	// | | | |
	// | | | |
	// | | |------------------------------|
	// | | | Queue |
	// | | | |
	// | | | |
	// -----------------------------------------------------

	private static Logger log = LoggerFactory.getLogger(Alces.class);

	private static String RES_LOG4J_TPL = "/org/cip4/tools/alces/conf/log4j.xml.tpl";

	private DefaultTreeModel _treeModel = null;

	private ConfigurationHandler _confHand = null;

	private TestRunner testRunner = null;

	private TestSuiteNode _testSuite = null;

	JPanel addressBarPanel = null;

	JLabel addressLabel = null;

	JComboBox addressComboBox = null;

	JButton connectButton = null;

	JButton prefButton = null;

	JToolBar toolBar = null;

	JSplitPane mainSplitPane = null;

	JSplitPane sessionQueueSplitPane = null;

	JScrollPane deviceScrollPane = null;

	JPanel devicePanel = null;

	JPanel deviceInfoPanel = null;

	JComboBox deviceListComboBox = null;

	JLabel deviceStatusValue = null;

	JTextArea deviceInfoTextArea = null;

	JPanel messagesPanel = null;

	JSplitPane infoQueueSplitPane = null;

	JScrollPane sessionTreeScrollPane = null;

	JPanel sessionTreePanel = null;

	JTree sessionTree = null;

	JButton refreshButton = null;

	JScrollPane sessionInfoScrollPane = null;

	JTextArea sessionInfoTextArea = null;

	JScrollPane logScrollPane = null;

	AlcesTreeCellRenderer cellRenderer = null;

	private JDFDeviceList knownDevices;

	private final QueuePanel queuePanel;

	private ConnectThread connectThread;

	private static final String ACTION_CONNECT = "ACTION_CONNECT";

	private static final String ACTION_CONNECT_CANCEL = "ACTION_CONNECT_CANCEL";

	private static final String ACTION_SELECT_DEVICE = "ACTION_SELECT_DEVICE";

	private static final String ACTION_SHOW_PREFERENCES = "ACTION_SHOW_PREFERENCES";

	private static final String ACTION_SEND_FILE = "ACTION_SEND_FILE";

	// variables related to Batch-execution
	private static final String ACTION_BATCH_SELECT_FILE = "ACTION_BATCH_SELECT_FILE";
	private static final String ACTION_BATCH_SELECT_FOLDER = "ACTION_BATCH_SELECT_FOLDER";
	private static final String ACTION_BATCH_START = "ACTION_BATCH_START";
	private static final String ACTION_BATCH_STOP = "ACTION_BATCH_STOP";
	private final JLabel batchModeLabel;
	private final List<File> filesToSendInBatch = new ArrayList<File>();
	private final JButton batchStartButton, batchStopButton;
	private static boolean isBatchRunned;

	@Autowired
	private AboutService aboutService;

	/**
	 * Creates a new instance of the Alces Swing application using the specified locale.
	 * 
	 * @param locale the <code>Locale</code> used for labels in the user interface
	 * @throws Exception
	 */
	public Alces() throws Exception {
		super();
		setIconImage(Toolkit.getDefaultToolkit().getImage(Alces.class.getResource("/org/cip4/tools/alces/alces.png")));

		_confHand = ConfigurationHandler.getInstance();
		_confHand.loadConfiguration(_confHand.getProp(ConfigurationHandler.PROPERTIES_FILE));

		_testSuite = new TestSuiteNode();
		testRunner = new TestRunner(_testSuite);// _deviceUrl, queuePanel,

		// MAIN PANEL
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// ADDRESS BAR
		addressBarPanel = new JPanel();
		addressBarPanel.setLayout(new BoxLayout(addressBarPanel, BoxLayout.X_AXIS));
		addressBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 11));
		pane.add(addressBarPanel, BorderLayout.NORTH);
		// Preferences Button
		prefButton = new JButton(_confHand.getLabel("Preferences...", "Preferences..."));
		prefButton.setActionCommand(ACTION_SHOW_PREFERENCES);
		prefButton.addActionListener(this);
		addressBarPanel.add(prefButton);
		addressBarPanel.add(Box.createRigidArea(new Dimension(11, 0)));
		// Address Field
		addressLabel = new JLabel(_confHand.getLabel("Address", "Device/Controller URL") + ":");
		addressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		addressBarPanel.add(addressLabel);
		addressComboBox = new JComboBox(_confHand.loadHistory());
		addressComboBox.setEditable(true);
		addressComboBox.addActionListener(this);
		// Add an Enter-Key listener
		addressComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					connect();
				}
			}
		});
		addressBarPanel.add(addressComboBox);
		// Connect Button
		connectButton = new JButton(_confHand.getLabel("Connect", "Connect"));
		connectButton.setActionCommand(ACTION_CONNECT);
		connectButton.addActionListener(this);
		addressBarPanel.add(connectButton);
		// Proxy checkbox
		JCheckBox proxyCheckBox = new JCheckBox();
		proxyCheckBox.setToolTipText(_confHand.getLabel("Connect.through.proxy", "Connect through proxy") + ": " + _confHand.getProp(ConfigurationHandler.PROXY_HOST) + ":"
				+ _confHand.getProp(ConfigurationHandler.PROXY_PORT));
		proxyCheckBox.setSelected(Boolean.parseBoolean(_confHand.getProp(ConfigurationHandler.PROXY_ENABLED)));
		proxyCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				_confHand.putProp(ConfigurationHandler.PROXY_ENABLED, Boolean.toString(e.getStateChange() == ItemEvent.SELECTED));
			}
		});
		addressBarPanel.add(proxyCheckBox);

		// DEVICES AND MESSAGES
		deviceScrollPane = new JScrollPane();
		// Known Devices
		devicePanel = new JPanel(new BorderLayout());
		deviceInfoPanel = new JPanel();
		deviceInfoPanel.setLayout(new BoxLayout(deviceInfoPanel, BoxLayout.Y_AXIS));
		deviceListComboBox = new JComboBox();
		deviceListComboBox.setEnabled(false);
		deviceListComboBox.setActionCommand(ACTION_SELECT_DEVICE);
		deviceListComboBox.addActionListener(this);
		deviceInfoPanel.add(deviceListComboBox);
		// Device status
		deviceStatusValue = new JLabel(" ");
		deviceStatusValue.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 0));
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(new JLabel(_confHand.getLabel("device.status", "Device Status") + ":"), BorderLayout.WEST);
		statusPanel.add(deviceStatusValue, BorderLayout.CENTER);
		deviceInfoPanel.add(statusPanel);
		// Device info
		deviceInfoTextArea = new JTextArea();
		deviceInfoTextArea.setEditable(false);
		JScrollPane deviceInfoScrollPane = new JScrollPane(deviceInfoTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		deviceInfoScrollPane.setPreferredSize(new Dimension(150, 100));
		deviceInfoPanel.add(deviceInfoScrollPane);
		deviceInfoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 0, 6), BorderFactory.createTitledBorder(_confHand.getLabel("known.devices", "Known Devices"))),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		devicePanel.add(deviceInfoPanel, BorderLayout.NORTH);
		// Known Messages
		messagesPanel = new JPanel();
		messagesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 6, 6), BorderFactory.createTitledBorder(_confHand.getLabel("known.messages", "Known Messages"))),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
		devicePanel.add(messagesPanel, BorderLayout.CENTER);

		// Batch Panel
		JPanel batchPanel = new JPanel();
		batchPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 6, 6), BorderFactory.createTitledBorder("Batch Mode")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		batchPanel.setLayout(new BoxLayout(batchPanel, BoxLayout.Y_AXIS));
		batchPanel.add(createMessageButton("Select file", "", ACTION_BATCH_SELECT_FILE));
		batchPanel.add(createMessageButton("Select folder", "", ACTION_BATCH_SELECT_FOLDER));
		batchModeLabel = new JLabel();
		batchModeLabel.setText("Batch: File or Folder");
		batchPanel.add(batchModeLabel);
		batchStartButton = createMessageButton("Start execution", null, ACTION_BATCH_START);
		batchStartButton.setEnabled(false);
		batchStopButton = createMessageButton("Stop execution", null, ACTION_BATCH_STOP);
		batchStopButton.setEnabled(false);
		batchPanel.add(batchStartButton);
		batchPanel.add(batchStopButton);
		devicePanel.add(batchPanel, BorderLayout.SOUTH);
		deviceScrollPane.setViewportView(devicePanel);

		// SESSION TREE, SESSION/MESSAGE INFO, QUEUE
		sessionQueueSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		infoQueueSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		// Session tree
		sessionTreePanel = new JPanel(new BorderLayout());
		sessionTreeScrollPane = new JScrollPane();
		sessionTree = initTree();
		sessionTreeScrollPane.setViewportView(sessionTree);
		sessionTreePanel.add(sessionTreeScrollPane, BorderLayout.CENTER);
		sessionTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					TreePath path = sessionTree.getClosestPathForLocation(e.getX(), e.getY());
					sessionTree.setSelectionPath(path);

					JPopupMenu popUp = new JPopupMenu("test");

					Action actionCollapse = new ActionCollapse(sessionTree, path);
					actionCollapse.putValue(Action.NAME, "Collapse");
					popUp.add(actionCollapse);
					Action actionCollapseAll = new ActionCollapseAll(sessionTree);
					actionCollapseAll.putValue(Action.NAME, "Collapse All");
					popUp.add(actionCollapseAll);

					if (path != null && path.getPathCount() == 2) {
						popUp.addSeparator();

						ActionSaveRequestsResponcesToDisk actionSaveReqResToDisk = new ActionSaveRequestsResponcesToDisk(sessionTree, path);
						actionSaveReqResToDisk.putValue(Action.NAME, "Save Selection To: " + StringUtils.substring(_confHand.getProp(ConfigurationHandler.PATH_TO_SAVE), 0, 25));
						popUp.add(actionSaveReqResToDisk);
					}

					popUp.show(sessionTree, e.getX(), e.getY());
				}
			}
		});

		// Buttons
		JPanel p = new JPanel();
		JButton clearButton = new JButton("Clear All");
		JButton removeSelectedButton = new JButton("Clear Selected");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) sessionTree.getModel();
				_testSuite.removeAllChildren();
				model.reload();
				sessionTree.setSelectionPath(new TreePath(""));
				queuePanel.clearQueue();
			}
		});
		removeSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel) sessionTree.getModel();
				if (!(sessionTree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode))
					return;
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) sessionTree.getLastSelectedPathComponent();
				if (selectedNode != null)
					model.removeNodeFromParent(selectedNode);
			}
		});
		p.add(clearButton);
		p.add(removeSelectedButton);
		sessionTreePanel.add(p, BorderLayout.SOUTH);
		// Session/message info
		sessionInfoScrollPane = new JScrollPane();
		sessionInfoTextArea = new JTextArea();
		sessionInfoTextArea.setEditable(false);
		sessionInfoScrollPane.setViewportView(sessionInfoTextArea);
		infoQueueSplitPane.add(sessionInfoScrollPane, JSplitPane.TOP);
		sessionQueueSplitPane.add(sessionTreePanel, JSplitPane.LEFT);
		sessionQueueSplitPane.add(infoQueueSplitPane, JSplitPane.RIGHT);
		mainSplitPane.add(deviceScrollPane, JSplitPane.LEFT);
		mainSplitPane.add(sessionQueueSplitPane, JSplitPane.RIGHT);
		// Queue
		queuePanel = new QueuePanel(this);
		infoQueueSplitPane.add(queuePanel, JSplitPane.BOTTOM);
		pane.add(mainSplitPane, BorderLayout.CENTER);

		// LAYOUT UI
		this.setSize(_confHand.getWindowWidth(), _confHand.getWindowHeight());
		sessionQueueSplitPane.setDividerLocation(_confHand.getDevicePaneWidth());
		infoQueueSplitPane.setDividerLocation(_confHand.getTestPaneWidth());
		mainSplitPane.setDividerLocation(_confHand.getMainPaneHeight());

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				quitAlces();
			}
		});
		this.setVisible(true);
		connectButton.requestFocusInWindow();
	}

	/**
	 * Applications main entrance point.
	 * @param args Applications parameter.
	 */
	public static void main(String[] args) {
		SpringApplication.run(Alces.class, args);
	}

	/**
	 * Event is called after applications start up.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void onStartUp() {
		this.setTitle(ConfigurationHandler.getSenderId() + "  -  " + _confHand.getServerJmfUrl());
		log.warn(String.format("%s %s has started. (buildtime: %s)", aboutService.getAppName(), aboutService.getAppVersion(), aboutService.getBuildTime()));
	}

	/**
	 * Sends a KnownDevices messages.
	 * 
	 * @throws IOException
	 * 
	 * @throws PreprocessorException
	 */
	private JDFJMF sendKnownDevices() throws IOException {
		log.info("Sending KnownDevices...");
		final OutMessage outMessage = createMessage("Connect_KnownDevices");
		InMessage inMessage = null;
		if (_confHand.getProp(ConfigurationHandler.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			inMessage = testRunner.sendMessage(outMessage, getDeviceUrl());
		} else {
			TestSession testSession = testRunner.startTestSession(outMessage, getDeviceUrl());
			int i = 0;
			while (testSession.getIncomingMessages().isEmpty() && i < 120) // 0.5 sec * 120 = 60 sec
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			inMessage = testSession.getIncomingMessage(outMessage);
		}

		final JDFJMF jmf = inMessage.getBodyAsJMF();
		log.info("Sending KnownDevices...done");
		return jmf;
	}

	/**
	 * Sends a JMF message containing a Status message with a QueueInfo='true' and QueueStatus message.
	 * 
	 * @throws IOException
	 */
	private JDFJMF sendQueueStatus() throws IOException {
		log.info("Sending QueueStatus...");
		final OutMessage outMessage = createMessage("Connect_QueueStatus");
		InMessage inMessage = null;
		if (_confHand.getProp(ConfigurationHandler.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			inMessage = testRunner.sendMessage(outMessage, getDeviceUrl());
		} else {
			TestSession testSession = testRunner.startTestSession(outMessage, getDeviceUrl());
			int i = 0;
			while (testSession.getIncomingMessages().isEmpty() && i < 120) // 0.5 sec * 120 = 60 sec
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			inMessage = testSession.getIncomingMessage(outMessage);
		}

		final JDFJMF jmf = inMessage.getBodyAsJMF();
		log.info("Sending QueueStatus...done");
		return jmf;
	}

	private void clearKnownDevices() {
		deviceListComboBox.setEnabled(false);
		deviceListComboBox.removeAllItems();
		knownDevices = null;
	}

	/**
	 * Updates the combobox listing known devices.
	 * 
	 * @param knownDevices a JMF message containing a KnownDevices response
	 */
	private void setKnownDevices(JDFJMF knownDevicesResponse) {
		// Remove old known devices first
		clearKnownDevices();
		clearActiveDevice();
		log.debug("Updating Known Devices combobox...");
		if (knownDevicesResponse == null) {
			return;
		}
		// Get list of known devices
		final JDFResponse response = knownDevicesResponse.getResponse(0);
		final JDFDeviceList deviceList = response.getDeviceList(0);
		this.knownDevices = deviceList;
		if (response.getDeviceList(0) == null) {
			return;
		}
		// Fill combobox with known devices
		for (int i = 0, imax = deviceList.getLength(); i < imax; i++) {
			String deviceID = deviceList.getDeviceInfo(i).getDeviceID();
			if (deviceID == null || deviceID.length() == 0) {
				deviceID = deviceList.getDeviceInfo(i).getDevice().getDeviceID();
			}
			deviceListComboBox.addItem(deviceID);
			// TODO Select active device
			if (knownDevicesResponse.getSenderID() != null && knownDevicesResponse.getSenderID().equals(deviceID)) {
				deviceListComboBox.setSelectedItem(deviceID);
			}
		}

		deviceListComboBox.setEnabled(true);
		log.debug("Known Devices combobox updated.");
	}

	private void clearActiveDevice() {
		clearDeviceStatus();
		deviceInfoTextArea.setText("");
	}

	private void clearDeviceStatus() {
		deviceStatusValue.setText("");
	}

	/**
	 * Sets which device to display device information about and to send messages to.
	 * 
	 * @param idx the index of the device in the combobox
	 */
	private void setActiveDevice(int idx) {
		log.debug("Updating device info...");
		JDFDeviceInfo deviceInfo = this.knownDevices.getDeviceInfo(idx);
		JDFDevice device = deviceInfo.getDevice();
		setDeviceStatus(deviceInfo);
		deviceInfoTextArea.setText("");
		deviceInfoTextArea.append(_confHand.getLabel("DeviceID", "DeviceID: ") + device.getDeviceID() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("JMFSenderID", "JMFSenderID: ") + device.getJMFSenderID() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("JMFURL", "JMFURL: ") + device.getJMFURL() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("JDFVersions", "JDFVersions: ") + device.getJDFVersions() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("ICSVersions", "ICSVersions: ") + device.getAttribute(AttributeName.ICSVERSIONS) + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("DescriptiveName", "DescriptiveName: ") + device.getDescriptiveName() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("AgentName", "AgentName: ") + device.getAgentName() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("AgentVersion", "AgentVersion: ") + device.getAgentVersion() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("DeviceType", "DeviceType: ") + device.getDeviceType() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("Manufacturer", "Manufacturer: ") + device.getManufacturer() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("ModelName", "ModelName: ") + device.getModelName() + "\n");
		deviceInfoTextArea.append(_confHand.getLabel("ModelNumber", "ModelNumber: ") + device.getModelNumber() + "\n");
		deviceInfoTextArea.setCaretPosition(0);
		deviceInfoTextArea.setEditable(false);
		log.debug("Active device is now '" + device.getDeviceID() + "'.");
	}

	/**
	 * Sends a KnownMessages query to the configured device and populates the UI with the buttons representing all messages the device supports.
	 * 
	 * @throws IOException
	 */
	private JDFJMF sendKnownMessages() throws IOException {
		log.info("Sending KnownMessages...");
		OutMessage outMessage = createMessage("Connect_KnownMessages");
		InMessage inMessage = null;
		if (_confHand.getProp(ConfigurationHandler.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			inMessage = testRunner.sendMessage(outMessage, getDeviceUrl());
		} else {
			TestSession testSession = testRunner.startTestSession(outMessage, getDeviceUrl());
			int i = 0;
			while (testSession.getIncomingMessages().isEmpty() && i < 120) // 0.5 sec * 120 = 60 sec
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			inMessage = testSession.getIncomingMessage(outMessage);
		}

		JDFJMF jmf = inMessage.getBodyAsJMF();
		log.info("Sending KnownMessages...done");
		return jmf;
	}

	/**
	 * Removes all messages buttons from the GUI
	 */
	private void clearMessageButtons() {
		log.debug("Clearing message buttons...");
		messagesPanel.removeAll();
	}

	/**
	 * Adds buttons to the GUI based on the message services specified in the JMF response. If the JMF
	 * response does not contain any message services a warning dialog box is displayed.
	 *
	 * @param knownMessages The KnownMessages JMF message
	 */
	public void buildMessageButtons(JDFJMF knownMessages) {

		// add 'Send File...' button (default)
		JButton sendFileButton = createMessageButton(_confHand.getLabel("Send.File", "Send File..."), "Send File...", ACTION_SEND_FILE);
		sendFileButton.setMnemonic(KeyEvent.VK_D);
		messagesPanel.add(sendFileButton);

		// add SubmitQueueEntry button if there was no JMF response
		if (knownMessages == null || knownMessages.getResponse(0) == null || knownMessages.getResponse(0).getMessageService(0) == null) {
			JButton sqeButton = createMessageButton(EnumType.SubmitQueueEntry.getName() + "...", "Command", "Command" + EnumType.SubmitQueueEntry.getName());
			sqeButton.setMnemonic(KeyEvent.VK_E);
			messagesPanel.add(sqeButton);
			return;
		}

		// Get known message services
		JDFMessage response = knownMessages.getResponse(0);
		List services = response.getChildElementVector(ElementName.MESSAGESERVICE, null, null, true, 0, false);
		if (services.size() == 0) {
			JOptionPane.showMessageDialog(this, "The device's reply to a KnownMessages query did not contain\n" + "any message services. You can still try sending JMF to the\n"
					+ "device using the 'Send File...' button to the left.", "No Message Services", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// Sort services alphabetically
		JDFMessageService[] jmfServices = (JDFMessageService[]) services.toArray(new JDFMessageService[0]);
		Arrays.sort(jmfServices, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((JDFMessageService) o1).getType().compareTo(((JDFMessageService) o2).getType());
			}
		});
		// Create buttons
		for (int i = 0; i < jmfServices.length; i++) {
			if (jmfServices[i].getJMFRole() != null && jmfServices[i].getJMFRole().equals(EnumJMFRole.Sender)) {
				continue;
			}
			StringBuffer toolTip = new StringBuffer();
			String actionCommand = null;
			if (jmfServices[i].getAcknowledge()) {
				toolTip.append("Acknowledge ");
			}
			if (jmfServices[i].getCommand() || jmfServices[i].getType().contains("QueueEntry")) {
				toolTip.append("Command ");
				actionCommand = "Command" + jmfServices[i].getType();
			}
			if (jmfServices[i].getPersistent()) {
				toolTip.append("Persistent ");
			}
			if (jmfServices[i].getQuery() || jmfServices[i].getType().contains("Status") || jmfServices[i].getType().contains("Known")) {
				toolTip.append("Query ");
				actionCommand = "Query" + jmfServices[i].getType();
			}
			if (jmfServices[i].getSignal()) {
				toolTip.append("Signal ");
			}
			JButton button = createMessageButton(jmfServices[i].getType(), toolTip.toString(), actionCommand);

			// Add ... to SubmitQueueEntry button
			if (jmfServices[i].getType().equals("SubmitQueueEntry")) {
				button.setText(jmfServices[i].getType() + "...");
				button.setMnemonic(KeyEvent.VK_E);
			}

			// Add ... to ResubmitQueueEntry button
			if (jmfServices[i].getType().equals("ResubmitQueueEntry")) {
				button.setText(jmfServices[i].getType() + "...");
				button.setMnemonic(KeyEvent.VK_U);
			}
			messagesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
			messagesPanel.add(button);
		}
	}

	/**
	 * Creates a new message button
	 * 
	 * @param text the text on the button, should be the message's type
	 * @param toolTip a help message
	 * @param actionCommand the message's xsi:type
	 * @return
	 */
	private JButton createMessageButton(String text, String toolTip, String actionCommand) {
		JButton button = new JButton(text);
		button.setToolTipText(toolTip);
		button.setActionCommand(actionCommand);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.addActionListener(this);
		button.addMouseListener(this);
		button.setMaximumSize(new Dimension(Short.MAX_VALUE, button.getPreferredSize().height));
		return button;
	}

	/**
	 * Returns an instance of a <code>Message</code> generated from the specified message template.
	 * 
	 * TODO Cache loaded messages TODO Set message header
	 * 
	 * @param messageTemplate the name of the message's template
	 * @return a message generated from the specified template
	 */
	public OutMessageImpl createMessage(String messageTemplate) {
		String header = null;
		String body = null;
		header = "Content-Type: application/vnd.cip4-jmf+xml";
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF(messageTemplate);
		if (deviceListComboBox.getSelectedItem() != null) {
			jmf.setDeviceID("" + deviceListComboBox.getSelectedItem());
		}
		body = jmf.getOwnerDocument_KElement().write2String(2);
		return new OutMessageImpl(header, body, true);
	}

	private OutMessage createSubmitQueueEntry(File jdfFile) {
		log.debug("Creating a SubmitQueueEntry message for submitting JDF '" + jdfFile.getAbsolutePath() + "'...");
		String publicDirPath = _confHand.getProp(ConfigurationHandler.RESOURCE_BASE);

		File publicJdfDir = new File(publicDirPath, "jdf");
		publicJdfDir.mkdir();
		// Create public JDF filename
		String jdfFilename = RandomStringUtils.randomAlphanumeric(16) + ".jdf";
		File publicJdfFile = new File(publicJdfDir, jdfFilename);
		// Copy JDF to public JDF file
		try {
			log.debug("Copying JDF to public dir '" + publicJdfFile + "'...");
			IOUtils.copy(new FileInputStream(jdfFile), new FileOutputStream(publicJdfFile));
		} catch (IOException ioe) {
			log.error("The JDF file could not be copied from '" + jdfFile.getAbsolutePath() + "' to '" + publicJdfFile.getAbsolutePath() + "'.");
			return null; // /XXX
		}
		// TODO Copy files referenced by JDF to public dir

		// Build URL to public JDF
		String publicJdfUrl = null;
		try {
			String host = _confHand.getProp(ConfigurationHandler.HOST);
			log.debug("The Host:" + host);

			int port = Integer.parseInt(_confHand.getProp(ConfigurationHandler.PORT));
			log.debug("port:" + port);
			publicJdfUrl = "http://" + host + ":" + port + "/jdf/" + jdfFilename;
		} catch (Exception uhe) {
			log.error("Could not build public URL.");
			return null; // XXX
		}

		// Load SubmitQueueEntry template
		OutMessage sqeMsg = createMessage("Template_SubmitQueueEntry");
		// Set URL in SubmitQueueEntry to JDF URL
		JDFJMF sqeJmf = sqeMsg.getBodyAsJMF();
		sqeJmf.setDeviceID("" + deviceListComboBox.getSelectedItem());
		sqeJmf.getCommand(0).getQueueSubmissionParams(0).setURL(publicJdfUrl);
		sqeMsg.setBody(sqeJmf.getOwnerDocument_KElement().write2String(2));
		return sqeMsg;
	}

	/**
	 * Doing the init stuff for the Tree, it's Renderer and Model Sets the TreeModel in the ConfigurationHandler
	 * 
	 * @return
	 */
	private JTree initTree() {
		// Create TreeModel
		_treeModel = new DefaultTreeModel(_testSuite);
		// Givet the TestSuite a reference to the TreeModel
		_testSuite.setTreeModel(_treeModel);
		_treeModel.addTreeModelListener(this);
		JTree tree = new JTree(_treeModel);
		log.debug("Shows root handles: " + tree.getShowsRootHandles());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		cellRenderer = new AlcesTreeCellRenderer();
		tree.setCellRenderer(cellRenderer);
		return tree;
	}

	/**
	 * Process received JMF messages
	 * 
	 * @param jmf
	 */
	private void processReceivedJMF(JDFJMF jmf) {
		if (jmf == null)
			return;
		// Gets all Device elements with the specified DeviceID
		final JDFAttributeMap deviceID = new JDFAttributeMap();
		log.debug("deviceListComboBox.getSelectedItem() = " + deviceListComboBox.getSelectedItem());
		if (deviceListComboBox.getSelectedItem() != null) {
			deviceID.put(AttributeName.DEVICEID, (String) deviceListComboBox.getSelectedItem());
		}
		// Updates GUI with the device's status
		final List deviceList = jmf.getChildrenByTagName(ElementName.DEVICE, "http://www.CIP4.org/JDFSchema_1_1", deviceID, false, true, 0);
		if (deviceList.size() != 0) {
			// Assumes a message will only contain one Device element
			Object deviceInfo = ((JDFDevice) deviceList.get(0)).getParentNode_KElement();
			if (deviceInfo instanceof JDFDeviceInfo) {
				setDeviceStatus((JDFDeviceInfo) deviceInfo);
			}
		}
		// Updates the GUI with the device's queue status
		List qList = jmf.getChildrenByTagName(ElementName.QUEUE, "http://www.CIP4.org/JDFSchema_1_1", deviceID, false, true, 0);
		if (qList.size() != 0) {
			// Assumes a message will only contain one relevant Queue element
			queuePanel.refreshQueue((JDFQueue) qList.get(0));
		}
	}

	/**
	 * Updates the devices status in the GUI
	 * 
	 * @param deviceInfo the DeviceInfo to update the GUI with
	 */
	private void setDeviceStatus(JDFDeviceInfo deviceInfo) {
		// Get status from Device element's parent DeviceInfo/@DeviceStatus
		final String deviceStatus;
		if (deviceInfo.getDeviceStatus() == null) {
			deviceStatus = "-";
		} else {
			deviceStatus = deviceInfo.getDeviceStatus().getName();
		}
		final String statusDetails;
		if (deviceInfo.getStatusDetails() == null || deviceInfo.getStatusDetails().length() == 0) {
			statusDetails = "-";
		} else {
			statusDetails = deviceInfo.getStatusDetails();
		}
		deviceStatusValue.setText(deviceStatus + " (" + statusDetails + ")");
	}

	private String getDeviceUrl() {
		return (String) addressComboBox.getSelectedItem();
	}

	/**
	 * Cancels that connection attempt by calling the thread executing the connect.
	 * 
	 * @see #connect()
	 */
	private void cancelConnect() {
		synchronized (testRunner) {
			connectButton.setEnabled(false);
			connectThread.cancel();
			connectButton.setText(_confHand.getLabel("Connect", "Connect"));
			connectButton.setActionCommand(Alces.ACTION_CONNECT);
			connectButton.setEnabled(true);
		}
	}

	/**
	 * Connects Alces to the device/controller specified by the URL in the address bar. Connecting consists of the following phases:
	 * 
	 * 1. Clear GUI with information about previous device 2. Initialize a new test invironment -> Abort if error occurs 3. Send KnownDevice handshake ->
	 * Display warning if error occurs 4. Send KnownMessages handshake -> Display warning if error occurs 5. Update GUI with device information
	 * 
	 * A separate thread is created for executing the connect. If the user cancels connecting, the thread is left orphaned and will self-terminate when it
	 * completes the phase it was in when the user cancelled.
	 * 
	 * @see #cancelConnect()
	 */
	private synchronized void connect() {
		log.debug("Connecting...");

		connectButton.setText(_confHand.getLabel("Cancel", "Cancel"));
		connectButton.setActionCommand(Alces.ACTION_CONNECT_CANCEL);

		// Cleanup
		clearMessageButtons();
		clearKnownDevices();
		clearActiveDevice();
		queuePanel.clearQueue();
		// Create connect thread
		connectThread = new ConnectThread() {
			private boolean cancel = false;

			@Override
			void cancel() {
				cancel = true;
			}

			@Override
			public void run() {
				log.debug("Connecting... [Thread: " + hashCode() + "]");
				// Initialize test environment
				try {
					synchronized (testRunner) {
						testRunner.destroy();
						testRunner.init();
					}
				} catch (Exception e) {
					log.error("Failed to initialize TestRunner before connecting", e);
					JOptionPane.showMessageDialog(Alces.this, "Alces test envirnement could not be initialized\n" + "prior to connecting. View the log file 'alces.log'\n" + "for details.",
							"Could Not Connect", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Send JMF handshake
				try {
					if (cancel)
						return;
					JDFJMF knownDevicesResponse = sendKnownDevices();
					JDFJMF knownMessagesResponse = sendKnownMessages();
					if (cancel)
						return;
					synchronized (testRunner) {
						if (cancel)
							return;
						setKnownDevices(knownDevicesResponse);
						// Update queue
						JDFJMF queueStatusResponse = sendQueueStatus();
						processReceivedJMF(queueStatusResponse);
						buildMessageButtons(knownMessagesResponse);
					}
				} catch (UnknownHostException e) {
					log.error("Could not connect to device.", e);
					String msg = "The device's hostname '" + e.getMessage() + "' could not\n" + "be found. Make sure that the entered device URL is correct.";
					JOptionPane.showMessageDialog(Alces.this, msg, "Could Not Connect", JOptionPane.WARNING_MESSAGE);
				} catch (IOException e) {
					if (cancel)
						return;
					log.error("Could not connect to device.", e);
					String msg = "The device did not respond correctly to the JMF handshake.\n" + "You can still try sending JMF to the device URL using\n" + "the buttons to the left.";
					JOptionPane.showMessageDialog(Alces.this, msg, "Could Not Connect", JOptionPane.WARNING_MESSAGE);
					buildMessageButtons(null);
				} catch (Exception e) {
					if (cancel)
						return;
					log.error("Could not connect to device.", e);
					String msg = "An unexpected error occured while connecting to the device.\n" + "You can still try sending JMF to the device URL using\n" + "the buttons to the left.";
					JOptionPane.showMessageDialog(Alces.this, msg, "Could Not Connect", JOptionPane.WARNING_MESSAGE);
					buildMessageButtons(null);
				} finally {
					connectButton.setText(_confHand.getLabel("Connect", "Connect"));
					connectButton.setActionCommand(Alces.ACTION_CONNECT);
					connectButton.setEnabled(true);
					if (cancel) {
						log.debug("Connecting cancelled. [Thread: " + hashCode() + "]");
					} else {
						log.debug("Connecting done. [Thread: " + hashCode() + "]");
					}
				}
			}
		};
		connectThread.start();
		log.debug("Connected.");
	}

	/**
	 * Implementation of the ActionListner Interface.
	 * @param e The action event.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(addressComboBox)) {
			addressComboBox.insertItemAt(addressComboBox.getSelectedItem(), 0);
			return;
		}

		// process action command
		final JFileChooser fileChooser;
		int returnValue;
		boolean packageAsMime;
		boolean disablePreprocessing;

		String queueEntryId;
		String jobId;

		OutMessage outMessage;

		String actionCommand = e.getActionCommand();
		log.info("New action event received: '{}'", actionCommand);

		switch (actionCommand) {
			case ACTION_CONNECT:
				connect();
				break;

			case ACTION_CONNECT_CANCEL:
				cancelConnect();
				break;

			case ACTION_SEND_FILE:
				fileChooser = new JFileChooser(_confHand.getProp(ConfigurationHandler.LAST_DIR));
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Select a File to Send");
				returnValue = fileChooser.showOpenDialog(this);
				// Store last used dir
				// _props.put("last.dir",
				// fc.getCurrentDirectory().getAbsolutePath());
				_confHand.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					testRunner.startTestSession(testRunner.loadMessage(fileChooser.getSelectedFile()), getDeviceUrl());
				}
				break;

			case ACTION_BATCH_SELECT_FILE:
				fileChooser = new JFileChooser(_confHand.getProp(ConfigurationHandler.LAST_DIR));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Batch Mode: Select a File to Send");
				returnValue = fileChooser.showOpenDialog(this);

				_confHand.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					batchStartButton.setEnabled(true);
					batchModeLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());

					filesToSendInBatch.clear();
					filesToSendInBatch.add(fileChooser.getSelectedFile());
				}
				break;

			case ACTION_BATCH_SELECT_FOLDER:
				fileChooser = new JFileChooser(_confHand.getProp(ConfigurationHandler.LAST_DIR));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Batch Mode: Select a Folder to Send");
				returnValue = fileChooser.showOpenDialog(this);

				_confHand.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					batchStartButton.setEnabled(true);
					batchModeLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());

					filesToSendInBatch.clear();
					File folder = fileChooser.getSelectedFile();
					File[] listOfFiles = folder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile())
							filesToSendInBatch.add(listOfFiles[i]);
					}
				}
				break;

			case ACTION_BATCH_START:
				// create and run thread
				batchStartButton.setEnabled(false);
				batchStopButton.setEnabled(true);
				SwingWorker<Object, Object> worker = new SwingWorker<>() {
					@Override
					protected Object doInBackground() {
						isBatchRunned = true;
						while (isBatchRunned) {
							log.info("-> batch executer");
							// prepare file to send
							for (File f : filesToSendInBatch) {
								log.info("Batch: send file: " + f.getAbsolutePath());
								testRunner.startTestSession(
										testRunner.loadMessage(f), getDeviceUrl());

								try {
									String delayStr = _confHand.getProp(ConfigurationHandler.BATCHMODE_DELAYTONEXT_FILE);
									int delayMs = Integer.parseInt(delayStr);
									Thread.sleep(delayMs);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						return null;
					}

					@Override
					protected void done() {
						batchStartButton.setEnabled(true);
						batchStopButton.setEnabled(false);
					}
				};
				worker.execute();
				break;

			case ACTION_BATCH_STOP:
				batchStopButton.setEnabled(false);
				isBatchRunned = false;
				break;

			case "CommandSubmitQueueEntry":
				fileChooser = new JFileChooser(_confHand.getProp(ConfigurationHandler.LAST_DIR));
				fileChooser.addChoosableFileFilter(new JDFFileFilter());
				fileChooser.setDialogTitle("Select a JDF Job Ticket to Submit");
				packageAsMime = (e.getModifiers() & ActionEvent.ALT_MASK) != 0;
				if (packageAsMime) {
					fileChooser.setDialogTitle(fileChooser.getDialogTitle() + " in a MIME package");
				}
				disablePreprocessing = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0;
				if (disablePreprocessing) {
					fileChooser.setDialogTitle(fileChooser.getDialogTitle() + " - JDF preprocessing is disabled");
				}
				returnValue = fileChooser.showOpenDialog(this);
				_confHand.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					testRunner.startTestSessionWithSubmitQueueEntry(fileChooser.getSelectedFile(), getDeviceUrl(), !disablePreprocessing, packageAsMime);
				}
				break;

			case "CommandResubmitQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				if (queueEntryId != null && jobId != null) {
					fileChooser = new JFileChooser(_confHand.getProp(ConfigurationHandler.LAST_DIR));
					fileChooser.addChoosableFileFilter(new JDFFileFilter());
					fileChooser.setDialogTitle("Select a JDF Job Ticket to Resubmit");
					packageAsMime = (e.getModifiers() & ActionEvent.ALT_MASK) != 0;
					if (packageAsMime) {
						fileChooser.setDialogTitle(fileChooser.getDialogTitle() + " in a MIME package");
					}
					disablePreprocessing = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0;
					if (disablePreprocessing) {
						fileChooser.setDialogTitle(fileChooser.getDialogTitle() + " - JDF preprocessing is disabled");
					}
					returnValue = fileChooser.showOpenDialog(this);
					_confHand.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						testRunner.startTestSessionWithResubmitQueueEntry(fileChooser.getSelectedFile(), queueEntryId, jobId, getDeviceUrl(), !disablePreprocessing, packageAsMime);
					}
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandStopPersistentChannel":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				outMessage = JMFMessageBuilder.buildStopPersistentChannel(_confHand.getServerJmfUrl(), queueEntryId, jobId);
				testRunner.startTestSession(outMessage, getDeviceUrl());
				break;

			case "CommandAbortQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildAbortQueueEntry(queueEntryId);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandHoldQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildHoldQueueEntry(queueEntryId);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandRemoveQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildRemoveQueueEntry(queueEntryId);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandResumeQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildResumeQueueEntry(queueEntryId);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSuspendQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSuspendQueueEntry(queueEntryId);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSetQueueEntryPriority":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSetQueueEntryPriority(queueEntryId, 100);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSetQueueEntryPosition":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSetQueueEntryPostion(queueEntryId, 0, null, null);
					testRunner.startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, _confHand.getLabel("please.select.row", "Please select a queue entry in the queue table"),
							_confHand.getLabel("No Row Selected", "No Row Selected"), JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "QueryStatus":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				if (queueEntryId != null) {
					OutMessage message = JMFMessageBuilder.buildStatus(queueEntryId, jobId);
					testRunner.startTestSession(message, getDeviceUrl());
				} else {
					testRunner.startTestSession(createMessage(actionCommand), getDeviceUrl());
				}
				break;

			case "QueryResource":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				outMessage = JMFMessageBuilder.buildQueryResource(jobId, queueEntryId);
				testRunner.startTestSession(outMessage, getDeviceUrl());
				break;

			case QueuePanel.REFRESH_QUEUE:
				JDFJMF jmf = null;
				try {
					jmf = sendQueueStatus();
				} catch (IOException e1) {
					log.error("Could not send QueueStatus message to refresh queue.", e1);
				}
				processReceivedJMF(jmf);
				break;

			case ACTION_SELECT_DEVICE:
				final int idx = ((JComboBox) e.getSource()).getSelectedIndex();
				if (idx != -1) {
					setActiveDevice(idx);
				}
				break;

			case ACTION_SHOW_PREFERENCES:
				new PreferencesDialog(this, "Preferences");
				setTitle(ConfigurationHandler.getSenderId() + "  -  " + _confHand.getServerJmfUrl());
				break;

			default:
				testRunner.startTestSession(createMessage(actionCommand), getDeviceUrl());
				break;
		}
	}

	// ----------------------------------------------------------------
	// TreeModelListener
	// ----------------------------------------------------------------

	public void treeNodesChanged(TreeModelEvent e) {
	}

	public void treeNodesInserted(TreeModelEvent e) {
		// Expand and scroll to inserted node
		int pathLength = e.getPath().length;
		if (pathLength == 2) {
			sessionTree.expandPath(e.getTreePath());
		} else if (pathLength == 3) {
			sessionTree.scrollPathToVisible(e.getTreePath());
			// expand current path also
			sessionTree.expandPath(e.getTreePath());
		}
		// Examine node
		Object[] children = e.getChildren();
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (child instanceof InMessage) {
				JDFJMF jmf = ((InMessage) child).getBodyAsJMF();
				if (jmf != null) {
					processReceivedJMF(jmf);
				}
			}
		}
	}

	public void treeNodesRemoved(TreeModelEvent e) {
	}

	public void treeStructureChanged(TreeModelEvent e) {
	}

	// ----------------------------------------------------------------
	// TreeSelectionListener
	// ----------------------------------------------------------------

	public void valueChanged(TreeSelectionEvent tse) {
		Object node = tse.getPath().getLastPathComponent();
		Component renderer = RendererFactory.getRenderer(node);
		sessionInfoScrollPane.setViewportView(renderer);
	}

	// ----------------------------------------------------------------
	// TreeCellRenderer
	// ----------------------------------------------------------------

	class AlcesTreeCellRenderer extends DefaultTreeCellRenderer {

		Icon messageInPassIcon = null;

		Icon messageInFailIcon = null;

		Icon messageOutPassIcon = null;

		Icon messageOutFailIcon = null;

		Icon sessionIcon = null;

		Icon testPassIcon = null;

		Icon testFailIcon = null;

		Icon testIgnoredIcon = null;
		Icon sessionFailIcon = null;

		Icon sessionPassIcon = null;

		TestSession _testsession = null;

		public AlcesTreeCellRenderer() {
			sessionIcon = createIcon("/org/cip4/tools/alces/icons/session.gif");
			messageInPassIcon = createIcon("/org/cip4/tools/alces/icons/message_in_pass.gif");
			messageInFailIcon = createIcon("/org/cip4/tools/alces/icons/message_in_fail.gif");
			messageOutPassIcon = createIcon("/org/cip4/tools/alces/icons/message_out_pass.gif");
			messageOutFailIcon = createIcon("/org/cip4/tools/alces/icons/message_out_fail.gif");
			testPassIcon = createIcon("/org/cip4/tools/alces/icons/test_pass.gif");
			testFailIcon = createIcon("/org/cip4/tools/alces/icons/test_fail.gif");
			testIgnoredIcon = createIcon("/org/cip4/tools/alces/icons/test_ignored.gif");
			sessionFailIcon = createIcon("/org/cip4/tools/alces/icons/session_fail.gif");
			sessionPassIcon = createIcon("/org/cip4/tools/alces/icons/session_pass.gif");
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (log.isDebugEnabled()) {
				log.debug("Rendering tree cell " + value.getClass() + " - " + value + "...");
			}
			if (value instanceof OutMessage) {
				if (((OutMessage) value).hasPassedAllTests()) {
					setIcon(messageOutPassIcon);
					setToolTipText("Outgoing message");
				} else {
					setIcon(messageOutFailIcon);
					setToolTipText("Outgoing message");
				}
			} else if (value instanceof InMessage) {
				if (((InMessage) value).hasPassedAllTests()) {
					setIcon(messageInPassIcon);
					setToolTipText("Incoming message");
				} else {
					setIcon(messageInFailIcon);
					setToolTipText("Incoming message");
				}
			} else if (value instanceof TestSession) {
				if (((TestSession) value).hasPassedAllTests()) {
					setIcon(sessionPassIcon);
				} else {
					setIcon(sessionFailIcon);
				}
			} else if (value instanceof TestResult) {
				TestResult result = (TestResult) value;
				switch (result.getResult()) {
				case PASSED:
					setIcon(testPassIcon);
					setToolTipText("Test passed");
					break;
				case FAILED:
					setIcon(testFailIcon);
					setToolTipText("Test failed");
					break;
				case IGNORED:
					setIcon(testIgnoredIcon);
					setToolTipText("Test ignored");
					break;
				default:
				}
			} else {
				setIcon(null);
				setToolTipText("No tooltip");
			}
			return this;
		}

		/**
		 * Loads an icon.
		 * 
		 * @param path
		 * @return
		 */
		private ImageIcon createIcon(String path) {
			ImageIcon icon = null;
			final java.net.URL imgURL = this.getClass().getResource(path);
			if (imgURL != null) {
				icon = new ImageIcon(imgURL);
			} else {
				log.warn("Could not load icon from path: " + path);
			}
			return icon;
		}
	}

	// ----------------------------------------------------------------
	// MouseListener
	// ----------------------------------------------------------------

	// MouseEvent for the right mouse button (count = 3)
	public void mouseClicked(MouseEvent e) {
		JButton comp = (JButton) e.getComponent();
		if (e.getButton() == 3 && comp.getActionCommand().equals(ACTION_SEND_FILE)) {
			SendContext sc = new SendContext(this, "jmf");
			sc.show(e.getComponent(), e.getX(), e.getY());
		}
		if (e.getButton() == 3 && comp.getActionCommand().equals("CommandSubmitQueueEntry")) {
			SendContext sc = new SendContext(this, "jdf");
			sc.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * Sends a JMF-Message selected with the "Send_JMF File..." button
	 * 
	 * @param file
	 */
	public void loadContextMessage(File file) {
		testRunner.startTestSession(testRunner.loadMessage(file), getDeviceUrl());
	}

	/**
	 * Sends a JDF-File selected with the "Send_JMF File..." button
	 * 
	 * @param file
	 */
	public void loadContextJDF(File file) {
		testRunner.startTestSession(createSubmitQueueEntry(file), getDeviceUrl());
	}

	/**
	 * Saves the configuration and writes a test report before Alces quits.
	 */
	private void quitAlces() {
		log.debug("Quitting Alces...");
		try {
			testRunner.destroy();
		} catch (Exception e) {
			log.warn("Could not shut down TestRunner.", e);
		}
		// Save test report
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
		String outputDir = _confHand.getProp(ConfigurationHandler.OUTPUT_DIR) + dateFormat.format(new Date());
		log.info("Writing test report to '" + outputDir + "'...");
		String outputFile;
		try {
			outputFile = testRunner.serializeTestSuite(outputDir);
			log.info("Wrote test report to: " + outputFile);
		} catch (Exception e) {
			log.error("Could not write test report.", e);
		}
		// Save configuration
		_confHand.saveHistory(addressComboBox.getModel());
		_confHand.saveConfiguration(this.getWidth(), this.getHeight(), sessionQueueSplitPane.getDividerLocation(), infoQueueSplitPane.getDividerLocation(), mainSplitPane.getDividerLocation());
		System.exit(0);
	}

	/**
	 * Thread used during Alces's connect phase
	 * 
	 * @author Claes Buckwalter (clabu@itn.liu.se)
	 */
	abstract class ConnectThread extends Thread {
		abstract void cancel();
	}
}