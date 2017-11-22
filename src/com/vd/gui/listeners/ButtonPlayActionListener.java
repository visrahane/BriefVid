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
		// TODO Auto-generated method stub
		if (VideoConstant.BUTTON_START_TEXT.toString().equals(btn.getText())) {
			gui.startPlay();
			btn.setText(VideoConstant.BUTTON_PAUSE_TEXT);
		} else if (VideoConstant.BUTTON_PAUSE_TEXT.toString().equals(btn.getText())) {
			gui.pausePlay();
			btn.setText(VideoConstant.BUTTON_START_TEXT);
		}

	}

}
