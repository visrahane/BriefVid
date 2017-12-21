/**
 *
 */
package com.vd.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.vd.services.GUI;

/**
 * @author Vis
 *
 */
public class ButtonStopActionListener implements ActionListener {

	private GUI gui;

	public ButtonStopActionListener(GUI gui) {
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
		gui.setPlay(false);
		gui.stopPlay();

	}

}
