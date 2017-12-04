/**
 *
 */
package com.vd.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.vd.constants.VideoConstant;
import com.vd.services.GUI;

/**
 * @author Vis
 *
 */
public class ButtonPlayActionListener implements ActionListener {

	private GUI gui;

	public ButtonPlayActionListener(GUI gui) {
		this.gui = gui;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// disable playBtn,enable pauseBn and start playing
		JButton btn = (JButton) e.getSource();
		// if isPlay true --> stop else start
		if (gui.isPlay()) {
			gui.togglePlay();
			gui.pausePlay();
			btn.setText(VideoConstant.BUTTON_START_TEXT);
		} else {
			gui.togglePlay();
			gui.startPlay();
			btn.setText(VideoConstant.BUTTON_PAUSE_TEXT);
		}

	}

}
