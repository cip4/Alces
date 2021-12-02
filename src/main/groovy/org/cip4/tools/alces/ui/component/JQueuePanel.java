package org.cip4.tools.alces.ui.component;

import org.cip4.jdflib.auto.JDFAutoQueueEntry;
import org.cip4.tools.alces.service.discovery.model.Queue;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The queue panel component.
 */
public class JQueuePanel extends JPanel {

    private final JLabel queueStatusLabel;
    private final JLabel queueSizeLabel;
    private final JLabel queueDeviceLabel;

    private final JTable queueTable;
    private final QueueTableModel queueTableModel;

    /**
     * Default constructor. Creates the Queue Panels appearance.
     */
    public JQueuePanel() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 11, 11, 11));

        // queue status
        JPanel queueStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queueStatusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.add(queueStatusPanel, BorderLayout.PAGE_START);

        JLabel queueStatusCaptionLabel = new JLabel("Queue Status:");
        queueStatusPanel.add(queueStatusCaptionLabel);
        queueStatusLabel = new JLabel("-");
        queueStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
        queueStatusPanel.add(queueStatusLabel);

        JLabel queueSizeCaptionLabel = new JLabel("Queue Size:");
        queueStatusPanel.add(queueSizeCaptionLabel);
        queueSizeLabel = new JLabel("-");
        queueSizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
        queueStatusPanel.add(queueSizeLabel);

        JLabel queueDeviceCaptionLabel = new JLabel("Queue Device:");
        queueStatusPanel.add(queueDeviceCaptionLabel);
        queueDeviceLabel = new JLabel("-");
        queueStatusPanel.add(queueDeviceLabel);

        // queue buttons
        JPanel queueButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.add(queueButtonPanel, BorderLayout.PAGE_END);

        JButton refreshButton = new JButton("Refresh Queue");
        refreshButton.setMnemonic(KeyEvent.VK_R);

        // TODO uncomment
        // refreshButton.addActionListener(e -> refreshQueue());
        queueButtonPanel.add(refreshButton);

        // queue table
        queueTableModel = new QueueTableModel();
        queueTableModel.setColumnIdentifiers(new Object[] {"QueueEntryID", "JobID", "JobPartID", "Priority", "Status"});

        queueTable = new JTable(queueTableModel);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queueTable.setColumnSelectionAllowed(false);
        queueTable.setDefaultRenderer(Object.class, new QueueCellRenderer());

        JScrollPane scrollPane = new JScrollPane(queueTable);
        queueTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Refresh the queue details.
     * @param queue The queue object.
     */
    public void refreshQueue(Queue queue) {

        if(queue == null) {
            // clear queue
            clearQueue();

        } else {

            // update labels
            this.queueStatusLabel.setText(queue.getStatus());
            this.queueSizeLabel.setText(Integer.toString(queue.getQueueEntries().size()));
            this.queueDeviceLabel.setText(queue.getDeviceId());

            // update queue entries
            queueTableModel.setRowCount(0);

            queue.getQueueEntries().forEach(queueEntry -> queueTableModel.addRow(new Object[]{
                    queueEntry.getQueueEntryId(),
                    queueEntry.getJobId(),
                    queueEntry.getJobPartId(),
                    queueEntry.getPriority(),
                    queueEntry.getStatus()
            }));
        }
    }

    /**
     * Clear all queue details.
     */
    public void clearQueue() {

        // update labels
        this.queueStatusLabel.setText("-");
        this.queueSizeLabel.setText("-");
        this.queueDeviceLabel.setText("-");

        // update queue entries
        queueTableModel.setRowCount(0);
    }

    /**
     * Getter for the selected queue entry id.
     * @return  Returns the selected queueEntryId. Null in case nothing is selected.
     */
    public String getSelectedQueueEntryId() {
        return queueTable.getSelectedRow() >= 0 ? (String) queueTableModel.getValueAt(queueTable.getSelectedRow(), 0) : null;
    }

    /**
     * A modified default table model without editable cells.
     */
    private static class QueueTableModel extends DefaultTableModel {
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    /**
     * A modified default cell renderer that colors cells in the Status column depending on the
     * queue entry's status.
     */
    private static class QueueCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Color color;

            if (table.getColumnName(column).equals("Status")) {
                if (value.equals(JDFAutoQueueEntry.EnumQueueEntryStatus.Running.getName())) {
                    color = Color.GREEN;

                } else if (value.equals(JDFAutoQueueEntry.EnumQueueEntryStatus.Completed.getName())) {
                    color = Color.LIGHT_GRAY;

                } else if (value.equals(JDFAutoQueueEntry.EnumQueueEntryStatus.Waiting.getName())) {
                    color = Color.YELLOW;

                } else {
                    color = Color.WHITE;
                }

            } else {
                color = Color.WHITE;
            }

            this.setBackground(color);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }
}
