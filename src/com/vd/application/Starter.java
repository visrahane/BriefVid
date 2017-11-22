/**
 *
 */
package com.vd.application;

import com.vd.services.GUI;

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
		GUI gui = new GUI("CSCI-576", args);
		// AVPlayer player = new AVPlayer();
		// player.start(args);
	}

}
