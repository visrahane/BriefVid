/**
 *
 */
package com.vd.application;

import java.awt.image.BufferedImage;
import java.util.List;

import com.vd.services.GUI;
import com.vd.services.KeyFrameService;
import com.vd.services.TapestryService;

/**
 * @author Vis
 *
 */
public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("usage: java -jar AVPlayer.jar [RGB file] [WAV file]");
			return;
		}
		KeyFrameService keyFrameService = new KeyFrameService(args[0], args[1]);
		List<Integer> keyFrames = keyFrameService.processFrames();
		
		GUI gui = new GUI("CSCI-576", args);
		TapestryService tapestryService = new TapestryService(gui);
		BufferedImage tapestryImage = tapestryService.prepareTapestry(keyFrames);
		tapestryService.displayTapestry(tapestryImage);
		// AVPlayer player = new AVPlayer();
		// player.start(args);
	}

}
