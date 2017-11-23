/**
 *
 */
package com.vd.gui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JProgressBar;

import com.vd.constants.VideoConstant;
import com.vd.services.GUI;

/**
 * @author Vis
 *
 */
public class VideoSliderChangeListener implements MouseListener {

	private GUI gui;

	public VideoSliderChangeListener(GUI gui) {
		this.gui = gui;
	}
	/*@Override
	public void stateChanged(ChangeEvent e) {
		if(){

		}
		JSlider source = (JSlider) e.getSource();
		int fps = source.getValue();
		gui.getAvPlayer().getVideo().setCurrentFramePtr(fps);
		if (!source.getValueIsAdjusting()) {
			System.out.println("Slider values:" + fps);
			gui.pausePlay();
			gui.startPlay();
		}
	}*/

	@Override
	public void mouseClicked(MouseEvent e) {
		JProgressBar progressBar = (JProgressBar) e.getSource();
		int v = progressBar.getValue();
		// Retrieves the mouse position relative to the component origin.
		int mouseX = e.getX();

		// Computes how far along the mouse is relative to the component width
		// then multiply it by the progress bar's maximum value.
		int progressBarVal = (int) Math.round((mouseX / (double) progressBar.getWidth()) * progressBar.getMaximum());
		int value = (int) (progressBarVal * VideoConstant.VIDEO_FRAME_COUNT / 100);
		progressBar.setValue(progressBarVal);
		System.out.println("Slider values:" + (progressBarVal * VideoConstant.VIDEO_FRAME_COUNT / 100));
		gui.pausePlay();
		gui.getAvPlayer().getVideo().setCurrentFramePtr(value);
		gui.startPlay();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
