/**
 *
 */
package com.vd.runnable;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

import com.vd.models.Video;
import com.vd.util.VideoIOUtil;

/**
 * @author Vis
 *
 */
public class VideoFrameBufferRunnable implements Runnable {

	private ArrayBlockingQueue<BufferedImage> bufferQ;

	private Video video;

	public VideoFrameBufferRunnable(ArrayBlockingQueue<BufferedImage> bufferQ, Video video) {
		this.bufferQ = bufferQ;
		this.video = video;
	}
	@Override
	public void run() {
		for (int i = 100; i < 6000; i++) {
			try {
				bufferQ.put(VideoIOUtil.getFrame(video.getFile(), i));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
