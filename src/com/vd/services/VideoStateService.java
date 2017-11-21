/**
 *
 */
package com.vd.services;

import com.vd.models.Video;

/**
 * @author Vis
 *
 */
public class VideoStateService {

	private Video video;

	public VideoStateService(Video video) {
		this.video = video;
	}

	public void updateState(boolean play, boolean stop, boolean pause) {
		// video.set
	}

}
