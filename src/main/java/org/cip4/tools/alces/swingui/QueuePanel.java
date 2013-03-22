package org.cip4.tools.alces.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.cip4.jdflib.auto.JDFAutoQueueEntry.EnumQueueEntryStatus;
import org.cip4.jdflib.jmf.JDFQueue;
import org.cip4.jdflib.jmf.JDFQueueEntry;
import org.cip4.tools.alces.util.ConfigurationHandler;

/**
 * A panel that dispalys information about a device's queue.
 * 
 * TODO: The panel also has funcationality for generating messages that modify
 * elements in the queue. This functionality should be removed so that this is
 * a pure GUI component. 
 * 
 * @author Marco Kornrumpf (Marco.Kornrumpf@Bertelsmann.de)
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */

public class QueuePanel extends JPanel {

	public static final String REFRESH_QUEUE = "REFRESH_QUEUE";

	private ConfigurationHandler _confHand = null;

	private JScrollPane scrollPane = null;

	private JLabel queueDeviceLabel = null;

	private JLabel queueDeviceValue = null;

	private JLabel queueStatusLabel = null;

	private JLabel queueStatusValue = null;

	private JLabel queueSizeLabel = null;

	private JLabel queueSizeValue = null;

	private JTable queueTable = null;

	private QueueTableModel tableModel = null;

	/**
	 * A panel that displays and allows manipulation of a device's queue.
	 */
	public QueuePanel(Alces alces) {
		_confHand = ConfigurationHandler.getInstance();
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 11, 11, 11));
		// Queue Status
		JPanel queueStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		queueStatusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		this.add(queueStatusPanel, BorderLayout.PAGE_START);
		queueStatusLabel = new JLabel("Queue Status:");
		queueStatusPanel.add(queueStatusLabel);
		queueStatusValue = new JLabel("-");
		queueStatusValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
		queueStatusPanel.add(queueStatusValue);
		queueSizeLabel = new JLabel("Queue Size:");
		queueStatusPanel.add(queueSizeLabel);
		queueSizeValue = new JLabel("-");
		queueSizeValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
		queueStatusPanel.add(queueSizeValue);
		queueDeviceLabel = new JLabel("Queue Device:");
		queueStatusPanel.add(queueDeviceLabel);
		queueDeviceValue = new JLabel("-");
		queueStatusPanel.add(queueDeviceValue);

		// Queue Table
		tableModel = new QueueTableModel();
		queueTable = new JTable(tableModel);
		queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queueTable.setColumnSelectionAllowed(false);
		queueTable.setDefaultRenderer(Object.class, new QueueCellRenderer());
		tableModel.setColumnIdentifiers(getColumnHeaders());

		// Queue Buttons
		JPanel queueButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(queueButtonPanel, BorderLayout.PAGE_END);
		JButton refreshButton = new JButton(_confHand.getLabel("Refresh.Queue",
				"Refresh Queue"));
		refreshButton.setActionCommand(REFRESH_QUEUE);
		refreshButton.addActionListener(alces);
		refreshButton.setMnemonic(KeyEvent.VK_R);
		queueButtonPanel.add(refreshButton);

		// ScrollPanel for the QueueTable
		scrollPane = new JScrollPane(queueTable);
		queueTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Builds a Vector containing column headers for the queue table.
	 * 
	 * @return a Vector containing the column headers
	 */
	public Vector getColumnHeaders() {
		String[] tableHead = { "QueueEntryID", "JobID", "JobPartID", "Priority", "Status" };
		List tableHeadList = Arrays.asList(tableHead);
		return new Vector(tableHeadList);
	}

	/**
	 * Returns the QueueEntryID of the selected queue entry row
	 * 
	 * @return the ID of the selected queue entry; null if no row was selected
	 */
	public synchronized String getSelectedQueueEntryID() {
		final String queueEntryId;
		if (queueTable.getSelectedRow() == -1) {
			// if (queueTable.getSelectedRow() == -1
			// || queueTable.getSelectedRow() > tableModel.getRowCount()
			// || tableModel.getRowCount() == 0) {
			queueEntryId = null;
		} else {
			queueEntryId = getQueueId(queueTable.getSelectedRow());
		}
		return queueEntryId;
	}

	public synchronized String getSelectedJobID() {
		final String jobId;
		if (queueTable.getSelectedRow() == -1) {
			jobId = null;
		} else {
			jobId = getJobId(queueTable.getSelectedRow());
		}
		return jobId;
	}

	/**
	 * Refreshes the list of queue entries in the queue view
	 * 
	 * @param q
	 *            the queue containing the new queue entries
	 */
	private synchronized void refreshQueueEntries(JDFQueue q) {
		// First remove everything from the model
		tableModel.setRowCount(0);
		// Then add the queue entries
		for (int i = 0, imax = q.getEntryCount(); i < imax; i++) {
			JDFQueueEntry queueEntry = q.getQueueEntry(i);
			Vector v = new Vector();
			v.add(queueEntry.getQueueEntryID());
			v.add(queueEntry.getJobID());
			v.add(queueEntry.getJobPartID());
			v.add("" + queueEntry.getPriority());
			v.add(queueEntry.getAttribute("Status"));
			tableModel.addRow(v);
		}
	}

	/**
	 * Refreshes the queue view.
	 * 
	 * @param jmf
	 *            the message possibly containing queue data to refresh the
	 *            queue view with
	 */
	public synchronized void refreshQueue(JDFQueue q) {
		queueDeviceValue.setText(q.getDeviceID());
		queueStatusValue.setText(q.getQueueStatus().getName());
		queueSizeValue.setText(q.getQueueSize() + " (" + q.getEntryCount() + ")");
		refreshQueueEntries(q);
	}

	/**
	 * Returns the queue entry ID of of the specfieid row
	 * 
	 * @param row
	 * @return the queue entry's ID
	 */
	private synchronized String getQueueId(int row) {
		return tableModel.getValueAt(row, 0).toString();
	}

	private String getJobId(int row) {
		return tableModel.getValueAt(row, 1).toString();
	}

	/**
	 * A table model without editable cells.
	 * 
	 * @author Claes Buckwalter (clabu@itn.liu.se)
	 */
	private static class QueueTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
	public void clearQueue() {
		tableModel.setRowCount(0);
	}

	/**
	 * A cell renderer that colors cells in the Status column depending on the
	 * queue entry's status.
	 * 
	 * @author Claes Buckwalter (clabu@itn.liu.se)
	 */
	private static class QueueCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Color color;
			if (table.getColumnName(column).equals("Status")) {
				if (value.equals(EnumQueueEntryStatus.Running.getName())) {
					color = Color.GREEN;
				} else if (value.equals(EnumQueueEntryStatus.Completed.getName())) {
					color = Color.LIGHT_GRAY;
				} else if (value.equals(EnumQueueEntryStatus.Waiting.getName())) {
					color = Color.YELLOW;
				} else {
					color = Color.WHITE;
				}
			} else {
				color = Color.WHITE;
			}
			this.setBackground(color);
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			return this;
		}
	}
}
