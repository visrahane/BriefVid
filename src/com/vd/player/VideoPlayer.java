/**
 *
 */
package com.vd.player;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author vis
 *
 */
public class VideoPlayer extends JPanel {

	@Override
	public void paintComponent(Graphics g) {
		// No loops or delays, just fetch the next image, preferrably it has been
		// already been loaded by another thread.
		g.drawImage(getNextImage(), 0, 0, null);
	}

	private Image getNextImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void playVideo(VideoPlayer vp) {
		ActionListener timerTask = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vp.repaint();

			}
		};

		Timer timer = new Timer(20, timerTask);
		timer.start();
	}
}
