/**
 *
 */
package com.vd.constants;

/**
 * @author Vis
 *
 */
public interface VideoConstant {
	public static final int VIDEO_PLAYER_WIDTH = 352;
	public static final int VIDEO_PLAYER_HEIGHT = 288;
	public static final long VIDEO_FRAME_SIZE = VIDEO_PLAYER_WIDTH * VIDEO_PLAYER_HEIGHT * 3;
	public static final long VIDEO_FRAME_BUFFER_LENGTH = 100;
	public static final long VIDEO_FRAME_BUFFER_SIZE = VIDEO_FRAME_BUFFER_LENGTH * VIDEO_FRAME_SIZE;
	public static final long VIDEO_BUFFER_COUNT = 6000;

}
