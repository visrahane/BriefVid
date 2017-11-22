/**
 *
 */
package com.vd.application;

import com.vd.services.GUI;
import com.vd.services.KeyFrameService;

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
		keyFrameService.processFrames();
		GUI gui = new GUI("CSCI-576", args);
		// AVPlayer player = new AVPlayer();
		// player.start(args);
	}

}
