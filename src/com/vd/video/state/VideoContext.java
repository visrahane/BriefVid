/**
 *
 */
package com.vd.video.state;

/**
 * @author Vis
 *
 */
public class VideoContext implements State {

	private State videoState;

	@Override
	public void doAction() {
		videoState.doAction();
	}

	public State getVideoState() {
		return videoState;
	}

	public void setVideoState(State videoState) {
		this.videoState = videoState;
	}

}
