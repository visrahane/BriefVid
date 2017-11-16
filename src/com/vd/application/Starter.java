/**
 *
 */
package com.vd.application;

import com.vd.player.AVPlayer;

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
		AVPlayer player = new AVPlayer();
		player.start(args);
	}

}
