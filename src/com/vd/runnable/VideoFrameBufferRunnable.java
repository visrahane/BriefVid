/**
 *
 */
package com.vd.runnable;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

import com.vd.constants.VideoConstant;
import com.vd.models.Video;
import com.vd.util.VideoIOUtil;

/**
 * @author Vis
 *
 */
public class VideoFrameBufferRunnable implements Runnable {

	private ArrayBlockingQueue<BufferedImage> bufferQ;

	private Video video;

	private ArrayBlockingQueue<BufferedImage> availableResourcesQ;

	private boolean stop;

	public VideoFrameBufferRunnable(ArrayBlockingQueue<BufferedImage> bufferQ, Video video,
			ArrayBlockingQueue<BufferedImage> availableResourcesQ) {
		this.bufferQ = bufferQ;
		this.availableResourcesQ = availableResourcesQ;
		this.video = video;
	}
	@Override
	public void run() {
		for (int i = VideoConstant.VIDEO_FRAME_BUFFER_LENGTH
				+ video.getCurrentFramePtr(); i < VideoConstant.VIDEO_FRAME_COUNT && !stop; i++) {
			try {
				bufferQ.put(VideoIOUtil.getFrame(video.getFile(), i, availableResourcesQ.take()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toggleStop();
	}

	public void toggleStop() {
		stop = !stop;
	}
}
