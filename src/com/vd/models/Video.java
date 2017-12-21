/**
 *
 */
package com.vd.models;

import java.io.File;

/**
 * @author Vis
 *
 */
public class Video {

	private File file;

	private int width;

	private int height;

	private long frameSize;

	private int currentFramePtr;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Video [file=").append(file).append(", width=").append(width).append(", height=").append(height)
		.append(", frameSize=").append(frameSize).append(", currentFramePtr=").append(currentFramePtr)
		.append("]");
		return builder.toString();
	}

	public Video(String fileLoc, int height, int width) {
		file = new File(fileLoc);
		this.height=height;
		this.width=width;
		frameSize = height * width * 3;
	}

	public Video(int videoPlayerHeight, int videoPlayerWidth) {
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(long frameSize) {
		this.frameSize = frameSize;
	}

	public int getCurrentFramePtr() {
		return currentFramePtr;
	}

	public void setCurrentFramePtr(int currentFramePtr) {
		this.currentFramePtr = currentFramePtr;
	}

}
