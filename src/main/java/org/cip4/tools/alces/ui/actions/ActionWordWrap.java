package org.cip4.tools.alces.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import org.cip4.tools.alces.ui.renderer.RendererFactory;

/**
 * An action for toggling wrapping of lines in the message content pane.
 * 
 * @author Alex Khilov
 * @since 0.9.9.3
 */
@SuppressWarnings("serial")
public class ActionWordWrap extends AbstractAction {
	private JTextArea textArea;

	public ActionWordWrap(JTextArea textArea) {
		this.textArea = textArea;
	}

	public void actionPerformed(ActionEvent e) {
		RendererFactory.wordWrap = !RendererFactory.wordWrap;
		textArea.setLineWrap(RendererFactory.wordWrap);
		textArea.setWrapStyleWord(RendererFactory.wordWrap);
		textArea.validate();
	}

}
