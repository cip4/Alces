package org.cip4.tools.alces.ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.cip4.jdflib.core.JDFAudit;
import org.cip4.tools.alces.Application;
import org.cip4.tools.alces.service.about.AboutService;
import org.cip4.tools.alces.service.discovery.DiscoveryService;
import org.cip4.tools.alces.service.discovery.model.*;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.cip4.tools.alces.ui.component.JContentRenderer;
import org.cip4.tools.alces.ui.component.JQueuePanel;
import org.cip4.tools.alces.ui.component.JTestSessionsTree;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.cip4.tools.alces.ui.filefilter.JDFFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * The Alces Swing GUI application for interactive testing.
 */
@org.springframework.stereotype.Component
public class Alces extends JFrame {

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
    private AboutService aboutService;

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private JmfMessageService jmfMessageService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TestRunnerService testRunnerService;

    private JSplitPane mainSplitPane;
    private JSplitPane sessionSplitPane;
    private JScrollPane sessionInfoScrollPane;
    private JSplitPane infoQueueSplitPane;

    private JTestSessionsTree jTestSessionsTree;

    private JPanel messagesPanel;
    private JComboBox<String> addressComboBox;
    private JComboBox<String> deviceListComboBox;
    private JLabel deviceStatusValue;
    private JTextArea deviceInfoTextArea;
    private JTree sessionTree;

    private JButton baseUrlButton;

    private JQueuePanel queuePanel;

    private JdfController jdfController;
    private JdfDevice activeJdfDevice;

    /**
     * Default constructor. Creates the Alces' main window.
     */
    public Alces() {
        super();
    }

    @PostConstruct
    public void postConstruct() {

        // listen to jdf controller updates
        discoveryService.registerJdfControllerListener(jdfController -> {
            this.jdfController = jdfController;

            SwingUtilities.invokeLater(() -> {
                updateJdfDevices(jdfController);
                updateJdfMessageServices(jdfController);
            });
        });

        // listen to queue updates
        discoveryService.registerQueueListener(queue -> queuePanel.refreshQueue(queue));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() throws IOException {

        // initialize window (main panel)
        Container mainPanel = getContentPane();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(initAddressBarPanel(), BorderLayout.NORTH);
        mainPanel.add(initStatusPanel(), BorderLayout.SOUTH);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(settingsService.getMainPaneHeight());
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        mainSplitPane.add(initControlPanel(), JSplitPane.LEFT);
        mainSplitPane.add(initSessionPanel(), JSplitPane.RIGHT);

        // window configurations
        setIconImage(Toolkit.getDefaultToolkit().getImage(Alces.class.getResource("/org/cip4/tools/alces/alces.png")));
        this.setTitle(aboutService.getAppName() + " " + aboutService.getAppVersion());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                quitAlces();
            }
        });
        this.setVisible(true);

        // apply window preferences
        this.setSize(settingsService.getAlcesDialogWidth(), settingsService.getAlcesDialogHeight());
        sessionSplitPane.setDividerLocation(settingsService.getDevicePaneWidth());
        infoQueueSplitPane.setDividerLocation(settingsService.getTestPaneWidth());

        // show form
        setVisible(true);
    }

    /**
     * Initializes the status bar panel.
     * @return The initialized status bar panel
     */
    private JPanel initStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new EmptyBorder(3, 5, 3, 5));

        // statusPanel.setPreferredSize(new Dimension(0, 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        JLabel statusLabel = new JLabel("JDF Library: CIP4 JDFLibJ " + JDFAudit.getStaticAgentVersion() + "  ");
        statusPanel.add(statusLabel);

        statusPanel.add(Box.createHorizontalGlue());

        JLabel baseUrlLabel = new JLabel("BaseUrl: ");
        statusPanel.add(baseUrlLabel);

        baseUrlButton = new JButton();
        baseUrlButton.setFocusPainted(false);
        baseUrlButton.setMargin(new Insets(0, 0, 0, 0));
        baseUrlButton.setContentAreaFilled(false);
        baseUrlButton.setBorderPainted(false);
        baseUrlButton.setOpaque(false);
        baseUrlButton.setText(settingsService.getBaseUrl());
        baseUrlButton.addActionListener(e -> {
            Component btn = (Component) e.getSource();
            JPopupMenu baseUrlPopupMenu = createBaseUrlPopUp();
            baseUrlPopupMenu.show(btn, 0, 0);
        });
        statusPanel.add(baseUrlButton);

        return statusPanel;
    }

    /**
     * Creation of a BaseUrl PopUp Menu showing all network interfaces.
     * @return A PopUpMenu showing all network interfaces.
     */
    private JPopupMenu createBaseUrlPopUp() {
        final JPopupMenu baseUrlPopupMenu = new JPopupMenu("Base URL");

        JMenuItem menuItemLocalhost = new JMenuItem("localhost");
        menuItemLocalhost.addActionListener(e -> updateBaseUrlsIp(((JMenuItem) e.getSource()).getText()));
        baseUrlPopupMenu.add(menuItemLocalhost);

        try {
            NetworkInterface.networkInterfaces().forEach(networkInterface -> networkInterface.getInterfaceAddresses().forEach(interfaceAddress -> {
                String hostAddress = interfaceAddress.getAddress().getHostAddress();

                if (hostAddress.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                    JMenuItem menuItem = new JMenuItem(interfaceAddress.getAddress().getHostAddress());
                    menuItem.addActionListener(e -> updateBaseUrlsIp(((JMenuItem) e.getSource()).getText()));
                    baseUrlPopupMenu.add(menuItem);
                }
            }));

        } catch (SocketException e) {
            log.error("Error reading network interfaces", e);
        }

        return baseUrlPopupMenu;
    }

    /**
     * Update the IP address of the base url.
     * @param ip The new ip address.
     */
    private void updateBaseUrlsIp(String ip) {
        settingsService.updateBaseUrlIp(ip);
        baseUrlButton.setText(settingsService.getBaseUrl());
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

        // settings Button
        JButton settingsButton = new JButton("Settings...");
        settingsButton.addActionListener(e -> showSettingsDialog());
        addressBarPanel.add(settingsButton);
        addressBarPanel.add(Box.createRigidArea(new Dimension(11, 0)));

        // address field
        JLabel addressLabel = new JLabel("Device/Controller URL:");
        addressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        addressBarPanel.add(addressLabel);

        addressComboBox = new JComboBox<>(settingsService.getAddressHistory());
        addressComboBox.setEditable(true);
        addressComboBox.addActionListener(e -> {
            JComboBox obj = (JComboBox) e.getSource();
            this.deviceUrl = obj.getSelectedItem().toString();
        });
        addressBarPanel.add(addressComboBox);
        this.deviceUrl = addressComboBox.getSelectedItem() == null ? null : addressComboBox.getSelectedItem().toString();
        addressComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    discover();
                }
            }
        });

        // discover button
        JButton discoverButton = new JButton("Discover");
        discoverButton.addActionListener(e -> discover());
        addressBarPanel.add(discoverButton);
        discoverButton.requestFocusInWindow();

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
        deviceListComboBox.addActionListener(e -> {
            String deviceId = (String) ((JComboBox) e.getSource()).getSelectedItem();
            this.updateActiveDevice(deviceId);
        });
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

        controlPanel.setViewportView(devicePanel);

        // return control panel
        return controlPanel;
    }

    /**
     * Initializes the Session panel.
     * @return The initialized session panel.
     */
    private JSplitPane initSessionPanel() throws IOException {

        sessionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // session tree panel
        JPanel sessionPanel = new JPanel(new BorderLayout());
        sessionSplitPane.add(sessionPanel, JSplitPane.LEFT);

        JScrollPane sessionTreeScrollPane = new JScrollPane();

        // init test suite tree
        jTestSessionsTree = JTestSessionsTree.newInstance(testRunnerService.getTestSessions());
        testRunnerService.registerTestSuiteListener(jTestSessionsTree);
        jTestSessionsTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            sessionInfoScrollPane.setViewportView(JContentRenderer.newInstance(treeNode.getUserObject()));
        });
        sessionTree = jTestSessionsTree;

        sessionTreeScrollPane.setViewportView(sessionTree);
        sessionPanel.add(sessionTreeScrollPane, BorderLayout.CENTER);

        JPanel sessionButtonPanel = new JPanel();
        sessionPanel.add(sessionButtonPanel, BorderLayout.SOUTH);

        // add test sessions button
        JButton refreshButton = new JButton("Refresh");
        sessionButtonPanel.add(refreshButton);
        refreshButton.addActionListener(e -> jTestSessionsTree.handleTestSessionsUpdate(this.testRunnerService.getTestSessions()));

        JButton clearButton = new JButton("Clear All");
        sessionButtonPanel.add(clearButton);
        clearButton.addActionListener(e -> {
            testRunnerService.clearTestSessions();
            queuePanel.clearQueue();
        });

        JButton removeSelectedButton = new JButton("Clear Selected");
        sessionButtonPanel.add(removeSelectedButton);
        removeSelectedButton.addActionListener(e -> {
            if (sessionTree.getSelectionPath() != null) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) sessionTree.getSelectionPath().getPathComponent(1);
                testRunnerService.clearTestSession((TestSession) treeNode.getUserObject());
            }
        });

        // session/message/queue info
        infoQueueSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sessionSplitPane.add(infoQueueSplitPane, JSplitPane.RIGHT);

        sessionInfoScrollPane = new JScrollPane();
        sessionInfoScrollPane.setViewportView(JContentRenderer.newInstance());
        infoQueueSplitPane.add(sessionInfoScrollPane, JSplitPane.TOP);

        // queuePanel = new QueuePanel(this);
        queuePanel = new JQueuePanel();
        infoQueueSplitPane.add(queuePanel, JSplitPane.BOTTOM);

        // return session panel
        return sessionSplitPane;
    }

    private void clear() {

        // clear queue
        queuePanel.clearQueue();

        // clear message buttons
        messagesPanel.removeAll();
        messagesPanel.repaint();

        // clear test sessions
        testRunnerService.clearTestSessions();

        // remove jdf devices
        deviceListComboBox.removeAllItems();
        deviceListComboBox.setEnabled(false);

        // clear active device
        deviceStatusValue.setText("");
        deviceInfoTextArea.setText("");
    }



    /**
     * Updates the active device.
     *
     * @param deviceId The device id of the new active device.
     */
    private void updateActiveDevice(String deviceId) {

        // get device
        JdfDevice jdfDevice = this.jdfController.getJdfDevices().stream()
                .filter(it -> it.getDeviceId().equals(deviceId))
                .findFirst()
                .orElse(new JdfDevice.Builder().build());

        // update active device
        this.activeJdfDevice = jdfDevice;

        // build info string and show in text area
        String tplInfoText = """
                DeviceID: %s
                JMFSenderID: %s
                JMFURL: %s
                JDFVersions: %s
                ICSVersions: %s
                DescriptiveName: %s
                AgentName: %s
                AgentVersion: %s
                DeviceType: %s
                Manufacturer: %s
                ModelName: %s
                ModelNumber: %s
                """;

        String infoText = String.format(tplInfoText,
                jdfDevice.getDeviceId(),
                jdfDevice.getJmfSenderId(),
                jdfDevice.getJmfUrl(),
                jdfDevice.getJdfVersions(),
                jdfDevice.getIcsVerions(),
                jdfDevice.getDescriptiveName(),
                jdfDevice.getAgentName(),
                jdfDevice.getAgentVersion(),
                jdfDevice.getDeviceType(),
                jdfDevice.getManufacturer(),
                jdfDevice.getModelName(),
                jdfDevice.getModelNumber()
        );

        deviceInfoTextArea.setText(infoText);
        deviceInfoTextArea.setCaretPosition(0);

        // update queue
        if (StringUtils.isNotEmpty(jdfDevice.getDeviceId())) {
            discoveryService.loadQueue(jdfDevice);
        }

        queuePanel.clearQueue();
    }

    /**
     * Update jdf message services.
     */
    private void updateJdfMessageServices(JdfController jdfController) {

        // clear old buttons
        messagesPanel.removeAll();

        // get supported messages
        List<MessageService> messageServices = jdfController.getJdfMessageServices();

        // create buttons
        messageServices.stream()
                .sorted(Comparator.comparing(MessageService::getType))
                .forEach(messageService -> {

                    // make the button JMF type specific
                    switch (messageService.getType()) {

                        // queries
                        case "Status" -> {
                            JButton button = createButton("Status");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createStatusQuery()));
                            messagesPanel.add(button);

                            button = createButton("StatusSubscription");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createStatusSubscription()));
                            messagesPanel.add(button);
                        }
                        case "QueueStatus" -> {
                            JButton button = createButton("QueueStatus");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createQueueStatusQuery()));
                            messagesPanel.add(button);

                            button = createButton("QueueStatusSubscription");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createQueueStatusSubscription()));
                            messagesPanel.add(button);
                        }
                        case "Resource" -> {
                            JButton button = createButton("Resource");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createResourceQuery()));
                            messagesPanel.add(button);

                            button = createButton("ResourceSubscription");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createResourceSubscription()));
                            messagesPanel.add(button);
                        }
                        case "Notification" -> {
                            JButton button = createButton("NotificationSubscription");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createNotificationSubscription()));
                            messagesPanel.add(button);
                        }
                        // discovery queries
                        case "KnownMessages" -> {
                            JButton button = createButton("KnownMessages");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createKnownMessagesQuery()));
                            messagesPanel.add(button);
                        }
                        case "KnownDevices" -> {
                            JButton button = createButton("KnownDevices");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createKnownDevicesQuery()));
                            messagesPanel.add(button);
                        }
                        case "KnownSubscriptions" -> {
                            JButton button = createButton("KnownSubscriptions");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createKnownSubscriptionsQuery()));
                            messagesPanel.add(button);
                        }

                        // queue entry commands
                        case "SubmitQueueEntry" -> {
                            JButton button = createButton("SubmitQueueEntry");
                            button.addActionListener(e -> {
                                JFileChooser fileChooser = new JFileChooser(settingsService.getLastSelectedDir());
                                fileChooser.addChoosableFileFilter(new JDFFileFilter());
                                fileChooser.setDialogTitle("Select a JDF Job Ticket to Submit");
                                int returnValue = fileChooser.showOpenDialog(this);
                                settingsService.setLastSelectedDir(fileChooser.getCurrentDirectory().getAbsolutePath());
                                if (returnValue == JFileChooser.APPROVE_OPTION) {
                                    startTestSession(jmfMessageService.createSubmitQueueEntry(fileChooser.getSelectedFile()));
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "ResubmitQueueEntry" -> {
                            JButton button = createButton("ResubmitQueueEntry");
                            button.addActionListener(e -> {

                                // check queue entry id
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);

                                } else {
                                    // select file
                                    JFileChooser fileChooser = new JFileChooser(settingsService.getLastSelectedDir());
                                    fileChooser.addChoosableFileFilter(new JDFFileFilter());
                                    fileChooser.setDialogTitle("Select a JDF Job Ticket to Submit");
                                    int returnValue = fileChooser.showOpenDialog(this);
                                    settingsService.setLastSelectedDir(fileChooser.getCurrentDirectory().getAbsolutePath());

                                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                                        startTestSession(jmfMessageService.createResubmitQueueEntry(fileChooser.getSelectedFile(), queueEntryId));
                                    }
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "SuspendQueueEntry" -> {
                            JButton button = createButton("SuspendQueueEntry");
                            button.addActionListener(e -> {
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    startTestSession(jmfMessageService.createSuspendQueueEntry(queueEntryId));
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "ResumeQueueEntry" -> {
                            JButton button = createButton("ResumeQueueEntry");
                            button.addActionListener(e -> {
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    startTestSession(jmfMessageService.createResumeQueueEntry(queueEntryId));
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "AbortQueueEntry" -> {
                            JButton button = createButton("AbortQueueEntry");
                            button.addActionListener(e -> {
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    startTestSession(jmfMessageService.createAbortQueueEntry(queueEntryId));
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "HoldQueueEntry" -> {
                            JButton button = createButton("HoldQueueEntry");
                            button.addActionListener(e -> {
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    startTestSession(jmfMessageService.createHoldQueueEntry(queueEntryId));
                                }
                            });
                            messagesPanel.add(button);
                        }
                        case "RemoveQueueEntry" -> {
                            JButton button = createButton("RemoveQueueEntry");
                            button.addActionListener(e -> {
                                String queueEntryId = queuePanel.getSelectedQueueEntryId();

                                if (StringUtils.isEmpty(queueEntryId)) {
                                    JOptionPane.showMessageDialog(this, "No QueueEntry is selected in the queue list.", "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    startTestSession(jmfMessageService.createRemoveQueueEntry(queueEntryId));
                                }
                            });
                            messagesPanel.add(button);
                        }

                        // persistent channel
                        case "StopPersistentChannel" -> {
                            JButton button = createButton("StopPersistentChannel");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createStopPersistentChannelCommand()));
                            messagesPanel.add(button);
                        }

                        // queue commands
                        case "HoldQueue" -> {
                            JButton button = createButton("HoldQueue");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createHoldQueue()));
                            messagesPanel.add(button);
                        }
                        case "ResumeQueue" -> {
                            JButton button = createButton("ResumeQueue");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createResumeQueue()));
                            messagesPanel.add(button);
                        }
                        case "OpenQueue" -> {
                            JButton button = createButton("OpenQueue");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createOpenQueue()));
                            messagesPanel.add(button);
                        }
                        case "CloseQueue" -> {
                            JButton button = createButton("CloseQueue");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createCloseQueue()));
                            messagesPanel.add(button);
                        }
                        case "FlushQueue" -> {
                            JButton button = createButton("FlushQueue");
                            button.addActionListener(e -> startTestSession(jmfMessageService.createFlushQueue()));
                            messagesPanel.add(button);
                        }
                        default -> {
                        }
                    }

                });
    }

    /**
     * Helper method for starting a test session.
     *
     * @param jmf The JMF Message initializing the test session.
     */
    private void startTestSession(String jmf) {
        testRunnerService.startTestSession(jmf, activeJdfDevice.getJmfUrl());
    }

    /**
     * Helper method to create a raw message button for further customization.
     *
     * @param text the text on the button, should be the message's type
     * @return The raw message button
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, button.getPreferredSize().height));
        return button;
    }

    /**
     * Discover the JMF URL provided in the address bar by applying a JMF Handshake.
     */
    private void discover() {

        // clean up
        clear();

        // discover target url (controller)
        discoveryService.discover(this.deviceUrl);
        settingsService.appendAddress(this.deviceUrl);



        addressComboBox.setModel(new DefaultComboBoxModel<>(settingsService.getAddressHistory()));
    }

    /**
     * Show the settings dialog.
     */
    private void showSettingsDialog() {
        JOptionPane.showMessageDialog(this, "Settings do no longer exist. To change the base url, please click on the lower right corner of the window");
    }

    /**
     * Saves the configuration and writes a test report before Alces quits.
     */
    private void quitAlces() {

        // save configuration
        settingsService.setAlcesDialogWidth(this.getWidth());
        settingsService.setAlcesDialogHeight(this.getHeight());

        settingsService.setMainPaneHeight(mainSplitPane.getDividerLocation());
        settingsService.setDevicePaneWidth(sessionSplitPane.getDividerLocation());
        settingsService.setTestPaneWidth(infoQueueSplitPane.getDividerLocation());

        // quite alces
        Application.initiateShutdown();
    }

    /**
     * Update jdf devices.
     * @param jdfController The controller containing the new devices information.
     */
    private void updateJdfDevices(JdfController jdfController) {

        // clean up
        deviceListComboBox.removeAllItems();
        deviceListComboBox.setEnabled(false);

        // refresh devices list
        jdfController.getJdfDevices().forEach(jdfDevice -> deviceListComboBox.addItem(jdfDevice.getDeviceId()));
        deviceListComboBox.setEnabled(true);
    }
}