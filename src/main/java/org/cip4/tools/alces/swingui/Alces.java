package org.cip4.tools.alces.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
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
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.setting.SettingsService;
import org.cip4.tools.alces.swingui.actions.ActionCollapse;
import org.cip4.tools.alces.swingui.actions.ActionCollapseAll;
import org.cip4.tools.alces.swingui.actions.ActionSaveRequestsResponcesToDisk;
import org.cip4.tools.alces.swingui.renderer.AlcesTreeCellRenderer;
import org.cip4.tools.alces.swingui.renderer.RendererFactory;
import org.cip4.tools.alces.swingui.tree.test.TestSuiteTreeNode;
import org.cip4.tools.alces.test.TestRunner;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.service.setting.SettingsServiceImpl;
import org.cip4.tools.alces.util.JDFFileFilter;
import org.cip4.tools.alces.util.JMFFileFilter;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * The Alces Swing GUI application for interactive testing.
 */
@org.springframework.stereotype.Component
public class Alces extends JFrame implements ActionListener, TreeModelListener, TreeSelectionListener, MouseListener {

	// -----------------------------------------------------
	// | Address Bar                                       |
	// |---------------------------------------------------|
	// | Device  | Test    | Test View                     |
	// | Info    | Session |                               |
	// |---------| Tree    |                               |
	// | Message |         |                               |
	// | Buttons |         |                               |
	// |         |         |                               |
	// |         |         |                               |
	// |         |         |-------------------------------|
	// |         |         | Queue                         |
	// |         |         |                               |
	// |         |         |                               |
	// -----------------------------------------------------

	private static final Logger log = LoggerFactory.getLogger(Alces.class);

	private String deviceUrl;

	@Autowired
	private SettingsService settingsService;

	private JSplitPane mainSplitPane;
	private JSplitPane sessionSplitPane;
	private JScrollPane sessionInfoScrollPane;
	private JSplitPane infoQueueSplitPane;

	private JPanel messagesPanel;
	private JComboBox<String> addressComboBox;
	private JComboBox<String> deviceListComboBox;
	private JButton connectButton;
	private JLabel deviceStatusValue;
	private JLabel batchModeLabel;
	private JTextArea deviceInfoTextArea;
	private JTree sessionTree;

	private QueuePanel queuePanel;
	private TestSuiteTreeNode testSuiteTreeNode;

	private JDFDeviceList knownDevices;
	private ConnectThread connectThread;

	private static final String ACTION_CONNECT = "ACTION_CONNECT";
	private static final String ACTION_CONNECT_CANCEL = "ACTION_CONNECT_CANCEL";
	private static final String ACTION_SELECT_DEVICE = "ACTION_SELECT_DEVICE";
	private static final String ACTION_SEND_FILE = "ACTION_SEND_FILE";

	// variables related to Batch-execution
	private static final String ACTION_BATCH_SELECT_FILE = "ACTION_BATCH_SELECT_FILE";
	private static final String ACTION_BATCH_SELECT_FOLDER = "ACTION_BATCH_SELECT_FOLDER";
	private static final String ACTION_BATCH_START = "ACTION_BATCH_START";
	private static final String ACTION_BATCH_STOP = "ACTION_BATCH_STOP";
	private final List<File> filesToSendInBatch = new ArrayList<>();
	private JButton batchStartButton;
	private JButton batchStopButton;
	private static boolean isBatchRunned;

	/**
	 * Default constructor. Creates a new instance of the Alces Swing application.
	 */
	public Alces()  {
		super();




	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() {

		this.testSuiteTreeNode = new TestSuiteTreeNode(TestRunner.getInstance().getTestSuite());

		// initialize window (main panel)
		Container mainPanel = getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(initAddressBarPanel(), BorderLayout.NORTH);

		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setDividerLocation(Integer.parseInt(settingsService.getProp(SettingsServiceImpl.MAIN_HEIGHT)));
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);

		mainSplitPane.add(initControlPanel(), JSplitPane.LEFT);
		mainSplitPane.add(initSessionPanel(), JSplitPane.RIGHT);

		// window configurations
		setIconImage(Toolkit.getDefaultToolkit().getImage(Alces.class.getResource("/org/cip4/tools/alces/alces.png")));
		this.setTitle(SettingsServiceImpl.getSenderId() + "  -  " + settingsService.getServerJmfUrl());
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				quitAlces();
			}
		});
		this.setVisible(true);
		connectButton.requestFocusInWindow();

		// apply window preferences
		this.setSize(
				Integer.parseInt(settingsService.getProp(SettingsServiceImpl.WIN_WIDTH)),
				Integer.parseInt(settingsService.getProp(SettingsServiceImpl.WIN_HEIGHT))
		);
		sessionSplitPane.setDividerLocation(Integer.parseInt(settingsService.getProp(SettingsServiceImpl.DEVICE_WIDTH)));
		infoQueueSplitPane.setDividerLocation(Integer.parseInt(settingsService.getProp(SettingsServiceImpl.TEST_WIDTH)));

		// show form
		setVisible(true);
	}

	/**
	 * Initializes the address bar panel.
	 * @return The initialized address bar panel
	 */
	private JPanel initAddressBarPanel() {

		// address bar panel
		JPanel addressBarPanel = new JPanel();
		addressBarPanel.setLayout(new BoxLayout(addressBarPanel, BoxLayout.X_AXIS));
		addressBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 11));

		// preferences Button
		JButton preferenceButton = new JButton("Preferences...");
		preferenceButton.addActionListener(e -> showPreferencesDialog());
		addressBarPanel.add(preferenceButton);
		addressBarPanel.add(Box.createRigidArea(new Dimension(11, 0)));

		// address field
		JLabel addressLabel = new JLabel("Device/Controller URL:");
		addressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		addressBarPanel.add(addressLabel);

		addressComboBox = new JComboBox<>(settingsService.loadHistory());
		addressComboBox.setEditable(true);
		addressComboBox.addActionListener(e -> {
			JComboBox<String> obj = (JComboBox<String>) e.getSource();
			obj.insertItemAt(obj.getSelectedItem().toString(), 0);
			this.deviceUrl = obj.getSelectedItem().toString();
		});
		addressBarPanel.add(addressComboBox);
		this.deviceUrl = addressComboBox.getSelectedItem().toString();
		addressComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					connect();
				}
			}
		});

		// connect button
		connectButton = new JButton("Connect");
		connectButton.setActionCommand(ACTION_CONNECT);
		connectButton.addActionListener(this);
		addressBarPanel.add(connectButton);

		return addressBarPanel;
	}

	/**
	 * Initialize the left side control panel.
	 * @return The initialized control panel.
	 */
	private JScrollPane initControlPanel() {

		JScrollPane controlPanel = new JScrollPane();

		// known devices
		JPanel devicePanel = new JPanel(new BorderLayout());
		JPanel deviceInfoPanel = new JPanel();
		deviceInfoPanel.setLayout(new BoxLayout(deviceInfoPanel, BoxLayout.Y_AXIS));
		deviceListComboBox = new JComboBox<>();
		deviceListComboBox.setEnabled(false);
		deviceListComboBox.setActionCommand(ACTION_SELECT_DEVICE);
		deviceListComboBox.addActionListener(this);
		deviceInfoPanel.add(deviceListComboBox);

		// device status
		deviceStatusValue = new JLabel(" ");
		deviceStatusValue.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 0));
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(new JLabel("Device Status:"), BorderLayout.WEST);
		statusPanel.add(deviceStatusValue, BorderLayout.CENTER);
		deviceInfoPanel.add(statusPanel);

		// device info
		deviceInfoTextArea = new JTextArea();
		deviceInfoTextArea.setEditable(false);
		JScrollPane deviceInfoScrollPane = new JScrollPane(deviceInfoTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		deviceInfoScrollPane.setPreferredSize(new Dimension(150, 100));
		deviceInfoPanel.add(deviceInfoScrollPane);
		deviceInfoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 0, 6), BorderFactory.createTitledBorder("Known Devices")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		devicePanel.add(deviceInfoPanel, BorderLayout.NORTH);

		// known messages
		messagesPanel = new JPanel();
		messagesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 6, 6, 6), BorderFactory.createTitledBorder("Known Messages")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
		devicePanel.add(messagesPanel, BorderLayout.CENTER);

		// batch panel
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
		controlPanel.setViewportView(devicePanel);

		// return control panel
		return controlPanel;
	}

	/**
	 * Initializes the Session panel.
	 * @return The initialized session panel.
	 */
	private JSplitPane initSessionPanel() {

		sessionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// session tree panel
		JPanel sessionPanel = new JPanel(new BorderLayout());
		sessionSplitPane.add(sessionPanel, JSplitPane.LEFT);

		JScrollPane sessionTreeScrollPane = new JScrollPane();

		sessionTree = initTree(); // DEEPT INIT !!

		sessionTreeScrollPane.setViewportView(sessionTree);
		sessionPanel.add(sessionTreeScrollPane, BorderLayout.CENTER);
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
						actionSaveReqResToDisk.putValue(Action.NAME, "Save Selection To: " + StringUtils.substring(settingsService.getProp(SettingsServiceImpl.PATH_TO_SAVE), 0, 25));
						popUp.add(actionSaveReqResToDisk);
					}

					popUp.show(sessionTree, e.getX(), e.getY());
				}
			}
		});

		JPanel sessionButtonPanel = new JPanel();
		sessionPanel.add(sessionButtonPanel, BorderLayout.SOUTH);

		JButton clearButton = new JButton("Clear All");
		sessionButtonPanel.add(clearButton);
		clearButton.addActionListener(e -> {
			DefaultTreeModel model = (DefaultTreeModel) sessionTree.getModel();
			testSuiteTreeNode.removeAllChildren();
			model.reload();
			sessionTree.setSelectionPath(new TreePath(""));
			queuePanel.clearQueue();
		});

		JButton removeSelectedButton = new JButton("Clear Selected");
		sessionButtonPanel.add(removeSelectedButton);
		removeSelectedButton.addActionListener(e -> {
			DefaultTreeModel model = (DefaultTreeModel) sessionTree.getModel();
			if (!(sessionTree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode selectedNode))
				return;
			model.removeNodeFromParent(selectedNode);
		});

		// session/message/queue info
		infoQueueSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sessionSplitPane.add(infoQueueSplitPane, JSplitPane.RIGHT);

		sessionInfoScrollPane = new JScrollPane();
		JTextArea sessionInfoTextArea = new JTextArea();
		sessionInfoTextArea.setEditable(false);
		sessionInfoScrollPane.setViewportView(sessionInfoTextArea);
		infoQueueSplitPane.add(sessionInfoScrollPane, JSplitPane.TOP);

		queuePanel = new QueuePanel(this);
		infoQueueSplitPane.add(queuePanel, JSplitPane.BOTTOM);

		// return session panel
		return sessionSplitPane;
	}

	/**
	 * Sends a KnownDevices messages.
	 */
	private JDFJMF sendKnownDevices() throws IOException {
		log.info("Sending KnownDevices...");
		final OutgoingJmfMessage outgoingJmfMessage = createMessage("Connect_KnownDevices");
		IncomingJmfMessage incomingJmfMessage;

		if (settingsService.getProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			incomingJmfMessage = TestRunner.getInstance().sendMessage(outgoingJmfMessage, getDeviceUrl());

		} else {
			TestSession testSession = TestRunner.getInstance().startTestSession(outgoingJmfMessage, getDeviceUrl());

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

			incomingJmfMessage = testSession.getIncomingMessage(outgoingJmfMessage);
		}

		final JDFJMF jmf = JmfUtil.getBodyAsJMF(incomingJmfMessage);
		log.info("Sending KnownDevices...done");
		return jmf;
	}

	/**
	 * Sends a JMF message containing a Status message with a QueueInfo='true' and QueueStatus message.
	 */
	private JDFJMF sendQueueStatus() throws IOException {
		log.info("Sending QueueStatus...");
		final OutgoingJmfMessage outgoingJmfMessage = createMessage("Connect_QueueStatus");
		IncomingJmfMessage incomingJmfMessage;

		if (settingsService.getProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			incomingJmfMessage = TestRunner.getInstance().sendMessage(outgoingJmfMessage, getDeviceUrl());

		} else {
			TestSession testSession = TestRunner.getInstance().startTestSession(outgoingJmfMessage, getDeviceUrl());

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

			incomingJmfMessage = testSession.getIncomingMessage(outgoingJmfMessage);
		}

		final JDFJMF jmf = JmfUtil.getBodyAsJMF(incomingJmfMessage);
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
	 */
	private void setKnownDevices(JDFJMF knownDevicesResponse) {

		// remove old known devices first
		clearKnownDevices();
		clearActiveDevice();
		log.debug("Updating Known Devices combobox...");
		if (knownDevicesResponse == null) {
			return;
		}

		// get list of known devices
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
		deviceInfoTextArea.append("DeviceID: " + device.getDeviceID() + "\n");
		deviceInfoTextArea.append("JMFSenderID: " + device.getJMFSenderID() + "\n");
		deviceInfoTextArea.append("JMFURL: " + device.getJMFURL() + "\n");
		deviceInfoTextArea.append("JDFVersions: " + device.getJDFVersions() + "\n");
		deviceInfoTextArea.append("ICSVersions: " + device.getAttribute(AttributeName.ICSVERSIONS) + "\n");
		deviceInfoTextArea.append("DescriptiveName: " + device.getDescriptiveName() + "\n");
		deviceInfoTextArea.append("AgentName: " + device.getAgentName() + "\n");
		deviceInfoTextArea.append("AgentVersion: " + device.getAgentVersion() + "\n");
		deviceInfoTextArea.append("DeviceType: " + device.getDeviceType() + "\n");
		deviceInfoTextArea.append("Manufacturer: " + device.getManufacturer() + "\n");
		deviceInfoTextArea.append("ModelName: " + device.getModelName() + "\n");
		deviceInfoTextArea.append("ModelNumber: " + device.getModelNumber() + "\n");
		deviceInfoTextArea.setCaretPosition(0);
		deviceInfoTextArea.setEditable(false);
		log.debug("Active device is now '" + device.getDeviceID() + "'.");
	}

	/**
	 * Sends a KnownMessages query to the configured device and populates the UI with the buttons representing all messages the device supports.
	 */
	private JDFJMF sendKnownMessages() throws IOException {
		log.info("Sending KnownMessages...");
		OutgoingJmfMessage outMessage = createMessage("Connect_KnownMessages");
		IncomingJmfMessage inMessage = null;
		if (settingsService.getProp(SettingsServiceImpl.SHOW_CONNECT_MESSAGES).equalsIgnoreCase("FALSE")) {
			inMessage = TestRunner.getInstance().sendMessage(outMessage, getDeviceUrl());
		} else {
			TestSession testSession = TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
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

		JDFJMF jmf = JmfUtil.getBodyAsJMF(inMessage);
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
		JButton sendFileButton = createMessageButton("Send File...", "Send File...", ACTION_SEND_FILE);
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
		List<org.cip4.jdflib.core.KElement> services = response.getChildElementVector(ElementName.MESSAGESERVICE, null, null, true, 0, false);
		if (services.size() == 0) {
			JOptionPane.showMessageDialog(this, "The device's reply to a KnownMessages query did not contain\n" + "any message services. You can still try sending JMF to the\n"
					+ "device using the 'Send File...' button to the left.", "No Message Services", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// Sort services alphabetically
		JDFMessageService[] jmfServices = services.toArray(new JDFMessageService[0]);
		Arrays.sort(jmfServices, Comparator.comparing(o -> o.getType()));
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
	 * @param messageTemplate the name of the message's template
	 * @return a message generated from the specified template
	 */
	public OutgoingJmfMessage createMessage(String messageTemplate) {
		String header = null;
		String body = null;
		header = "Content-Type: application/vnd.cip4-jmf+xml";
		JDFJMF jmf = JMFMessageFactory.getInstance().createJMF(messageTemplate);
		if (deviceListComboBox.getSelectedItem() != null) {
			jmf.setDeviceID("" + deviceListComboBox.getSelectedItem());
		}
		body = jmf.getOwnerDocument_KElement().write2String(2);
		return new OutgoingJmfMessage(header, body, true);
	}

	private OutgoingJmfMessage createSubmitQueueEntry(File jdfFile) {
		log.debug("Creating a SubmitQueueEntry message for submitting JDF '" + jdfFile.getAbsolutePath() + "'...");
		String publicDirPath = settingsService.getProp(SettingsServiceImpl.RESOURCE_BASE);

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
			String host = settingsService.getProp(SettingsServiceImpl.HOST);
			log.debug("The Host:" + host);

			int port = Integer.parseInt(settingsService.getProp(SettingsServiceImpl.PORT));
			log.debug("port:" + port);
			publicJdfUrl = "http://" + host + ":" + port + "/jdf/" + jdfFilename;
		} catch (Exception uhe) {
			log.error("Could not build public URL.");
			return null; // XXX
		}

		// Load SubmitQueueEntry template
		OutgoingJmfMessage sqeMsg = createMessage("Template_SubmitQueueEntry");
		// Set URL in SubmitQueueEntry to JDF URL
		JDFJMF sqeJmf = JmfUtil.getBodyAsJMF(sqeMsg);
		sqeJmf.setDeviceID("" + deviceListComboBox.getSelectedItem());
		sqeJmf.getCommand(0).getQueueSubmissionParams(0).setURL(publicJdfUrl);
		sqeMsg.setBody(sqeJmf.getOwnerDocument_KElement().write2String(2));
		return sqeMsg;
	}

	/**
	 * Doing the init stuff for the Tree, it's Renderer and Model Sets the TreeModel in the SettingsServiceImpl
	 *
	 * @return
	 */
	private JTree initTree() {

		// init tree model (data)
		DefaultTreeModel defaultTreeModel = new DefaultTreeModel(this.testSuiteTreeNode);
		testSuiteTreeNode.setTreeModel(defaultTreeModel);
		defaultTreeModel.addTreeModelListener(this);

		// create JTree object
		JTree tree = new JTree(defaultTreeModel);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new AlcesTreeCellRenderer());

		// return tree
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
		return this.deviceUrl;
	}

	/**
	 * Cancels that connection attempt by calling the thread executing the connect.
	 * 
	 * @see #connect()
	 */
	private void cancelConnect() {
		synchronized (TestRunner.getInstance()) {
			connectButton.setEnabled(false);
			connectThread.cancel();
			connectButton.setText("Connect");
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

		connectButton.setText("Cancel");
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

				// Send JMF handshake
				try {
					if (cancel)
						return;
					JDFJMF knownDevicesResponse = sendKnownDevices();
					JDFJMF knownMessagesResponse = sendKnownMessages();
					if (cancel)
						return;
					synchronized (TestRunner.getInstance()) {
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
					connectButton.setText("Connect");
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

		// process action command
		final JFileChooser fileChooser;
		int returnValue;
		boolean packageAsMime;
		boolean disablePreprocessing;

		String queueEntryId;
		String jobId;

		OutgoingJmfMessage outMessage;

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
				fileChooser = new JFileChooser(settingsService.getProp(SettingsServiceImpl.LAST_DIR));
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Select a File to Send");
				returnValue = fileChooser.showOpenDialog(this);
				// Store last used dir
				// _props.put("last.dir",
				// fc.getCurrentDirectory().getAbsolutePath());
				settingsService.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					TestRunner.getInstance().startTestSession(TestRunner.getInstance().loadMessage(fileChooser.getSelectedFile()), getDeviceUrl());
				}
				break;

			case ACTION_BATCH_SELECT_FILE:
				fileChooser = new JFileChooser(settingsService.getProp(SettingsServiceImpl.LAST_DIR));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Batch Mode: Select a File to Send");
				returnValue = fileChooser.showOpenDialog(this);

				settingsService.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					batchStartButton.setEnabled(true);
					batchModeLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());

					filesToSendInBatch.clear();
					filesToSendInBatch.add(fileChooser.getSelectedFile());
				}
				break;

			case ACTION_BATCH_SELECT_FOLDER:
				fileChooser = new JFileChooser(settingsService.getProp(SettingsServiceImpl.LAST_DIR));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.addChoosableFileFilter(new JMFFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(true);
				fileChooser.setDialogTitle("Batch Mode: Select a Folder to Send");
				returnValue = fileChooser.showOpenDialog(this);

				settingsService.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
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
								TestRunner.getInstance().startTestSession(
										TestRunner.getInstance().loadMessage(f), getDeviceUrl());

								try {
									String delayStr = settingsService.getProp(SettingsServiceImpl.BATCHMODE_DELAYTONEXT_FILE);
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
				fileChooser = new JFileChooser(settingsService.getProp(SettingsServiceImpl.LAST_DIR));
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
				settingsService.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					TestRunner.getInstance().startTestSessionWithSubmitQueueEntry(fileChooser.getSelectedFile(), getDeviceUrl(), !disablePreprocessing, packageAsMime);
				}
				break;

			case "CommandResubmitQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				if (queueEntryId != null && jobId != null) {
					fileChooser = new JFileChooser(settingsService.getProp(SettingsServiceImpl.LAST_DIR));
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
					settingsService.putProp("last.dir", fileChooser.getCurrentDirectory().getAbsolutePath());
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						TestRunner.getInstance().startTestSessionWithResubmitQueueEntry(fileChooser.getSelectedFile(), queueEntryId, jobId, getDeviceUrl(), !disablePreprocessing, packageAsMime);
					}
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandStopPersistentChannel":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				outMessage = JMFMessageBuilder.buildStopPersistentChannel(settingsService.getServerJmfUrl(), queueEntryId, jobId);
				TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				break;

			case "CommandAbortQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildAbortQueueEntry(queueEntryId);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandHoldQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildHoldQueueEntry(queueEntryId);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandRemoveQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildRemoveQueueEntry(queueEntryId);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandResumeQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildResumeQueueEntry(queueEntryId);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSuspendQueueEntry":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSuspendQueueEntry(queueEntryId);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSetQueueEntryPriority":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSetQueueEntryPriority(queueEntryId, 100);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "CommandSetQueueEntryPosition":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				if (queueEntryId != null) {
					outMessage = JMFMessageBuilder.buildSetQueueEntryPostion(queueEntryId, 0, null, null);
					TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
				} else {
					JOptionPane.showMessageDialog(this, "Please select a queue entry in the queue table",
							"No Row Selected", JOptionPane.WARNING_MESSAGE);
				}
				break;

			case "QueryStatus":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				if (queueEntryId != null) {
					OutgoingJmfMessage message = JMFMessageBuilder.buildStatus(queueEntryId, jobId);
					TestRunner.getInstance().startTestSession(message, getDeviceUrl());
				} else {
					TestRunner.getInstance().startTestSession(createMessage(actionCommand), getDeviceUrl());
				}
				break;

			case "QueryResource":
				queueEntryId = queuePanel.getSelectedQueueEntryID();
				jobId = queuePanel.getSelectedJobID();
				outMessage = JMFMessageBuilder.buildQueryResource(jobId, queueEntryId);
				TestRunner.getInstance().startTestSession(outMessage, getDeviceUrl());
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

//			case ACTION_SHOW_PREFERENCES:
//				new PreferencesDialog(this, "Preferences");
//				setTitle(SettingsServiceImpl.getSenderId() + "  -  " + settingsServiceImpl.getServerJmfUrl());
//				break;

			default:
				TestRunner.getInstance().startTestSession(createMessage(actionCommand), getDeviceUrl());
				break;
		}

		if(testSuiteTreeNode.getTreeModel() != null ) {
			testSuiteTreeNode.getTreeModel().reload();
		}
	}

	/**
	 * Show the preferences dialog.
	 */
	private void showPreferencesDialog() {
		new PreferencesDialog(this, "Preferences");
		setTitle(SettingsServiceImpl.getSenderId() + "  -  " + settingsService.getServerJmfUrl());
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
			if (child instanceof IncomingJmfMessage) {
				JDFJMF jmf = JmfUtil.getBodyAsJMF((IncomingJmfMessage) child);
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

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}


	/**
	 * Sends a JMF-Message selected with the "Send_JMF File..." button
	 * 
	 * @param file
	 */
	public void loadContextMessage(File file) {
		TestRunner.getInstance().startTestSession(TestRunner.getInstance().loadMessage(file), getDeviceUrl());
	}

	/**
	 * Sends a JDF-File selected with the "Send_JMF File..." button
	 * 
	 * @param file
	 */
	public void loadContextJDF(File file) {
		TestRunner.getInstance().startTestSession(createSubmitQueueEntry(file), getDeviceUrl());
	}

	/**
	 * Saves the configuration and writes a test report before Alces quits.
	 */
	private void quitAlces() {
		log.debug("Quitting Alces...");

		// save test report
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
		String outputDir = settingsService.getProp(SettingsServiceImpl.OUTPUT_DIR) + dateFormat.format(new Date());
		log.info("Writing test report to '" + outputDir + "'...");

		String outputFile;
		try {
			outputFile = TestRunner.getInstance().serializeTestSuite(outputDir);
			log.info("Wrote test report to: " + outputFile);
		} catch (Exception e) {
			log.error("Could not write test report.", e);
		}

		// save configuration
		settingsService.saveHistory(addressComboBox.getModel());
		settingsService.saveConfiguration(this.getWidth(), this.getHeight(), sessionSplitPane.getDividerLocation(), infoQueueSplitPane.getDividerLocation(), mainSplitPane.getDividerLocation());
		System.exit(0);
	}

	/**
	 * Thread used during Alces's connect phase
	 */
	abstract static class ConnectThread extends Thread {
		abstract void cancel();
	}
}