/**
 *
 */
package com.vd.runnable;

import java.awt.image.BufferedImage;

import com.vd.constants.VideoConstant;
import com.vd.player.AVPlayer;
import com.vd.services.GUI;
import com.vd.videoprocessing.Histogram;

/**
 * @author Vis
 *
 */
public class VideoRunnable extends Thread {

	private GUI gui;

	private volatile boolean stop;

	public VideoRunnable(GUI gui) {
		this.gui = gui;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AVPlayer avPlayer = gui.getAvPlayer();
		avPlayer.start();
		BufferedImage take = null;
		
		// testing histogram
		BufferedImage curr = null, next = null;
		
		int i;
		for (i = avPlayer.getVideo().getCurrentFramePtr(); i < VideoConstant.VIDEO_FRAME_COUNT && !stop; i++) {
			take = avPlayer.getCurrentPlayedFrame();
			gui.displayImage(take);
			gui.updateSlider(100 * i / 6000);
			avPlayer.putIntoAvailableResources(take);
			avPlayer.playSoundFrame(i);
		}
		avPlayer.getVideo().setCurrentFramePtr(i);
		toggleStop();
//		System.out.println("hist diff: " + Histogram.getColorHistogramSAD(curr, next));
	}

	public void toggleStop() {
		stop = !stop;
	}

}
