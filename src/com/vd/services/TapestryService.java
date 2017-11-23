/**
 *
 */
package com.vd.services;

import java.awt.image.BufferedImage;
import java.util.List;

import com.vd.models.Video;
import com.vd.util.VideoIOUtil;

/**
 * @author Vis
 *
 */
public class TapestryService {

	private GUI gui;

	public TapestryService(GUI gui) {
		this.gui = gui;
	}

	public BufferedImage prepareTapestry(List<Integer> keyFrames) {
		Video video = gui.getAvPlayer().getVideo();
		Integer[] keyFramesArray = new Integer[keyFrames.size()];
		keyFrames.toArray(keyFramesArray);
		BufferedImage tapestryImage = VideoIOUtil.mergeImages(keyFramesArray, video.getFile());
		return tapestryImage;
	}

	public void displayTapestry(BufferedImage img) {
		gui.displayTapestry(img);
	}

}
