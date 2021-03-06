package com.vd.player;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.vd.constants.VideoConstant;
import com.vd.models.Video;
import com.vd.runnable.VideoFrameBufferRunnable;
import com.vd.services.GUI;
import com.vd.services.SoundService;
import com.vd.util.VideoIOUtil;


public class AVPlayer {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	private SoundService soundService;
	public static Video video;
	private VideoFrameBufferRunnable videoFrameBufferRunnable;

	private ArrayBlockingQueue<BufferedImage> bufferQ = new ArrayBlockingQueue<>(
			VideoConstant.VIDEO_FRAME_BUFFER_LENGTH, true);

	private ArrayBlockingQueue<BufferedImage> availableResourcesQ = new ArrayBlockingQueue<>(
			VideoConstant.VIDEO_FRAME_BUFFER_LENGTH, true);

	public AVPlayer() {
		video = new Video(VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
	}

	public AVPlayer(String[] args) {
		video = new Video(args[0], VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
		soundService = new SoundService(args[1]);
		videoFrameBufferRunnable = new VideoFrameBufferRunnable(bufferQ, video, availableResourcesQ);
	}

	private void displayVideo(String[] args, List<byte[]> framesList) {
		BufferedImage bufferedImage;
		int count = 0;
		for (byte[] bytes : framesList) {
			bufferedImage = getFrame(bytes);
			// bufferedImageList.add(bufferedImage);
			if (count++ == 0 || count++ == 200) {
				displayFrame(bufferedImage, args);
			}
		}
	}

	private BufferedImage getFrame(byte[] bytes) {
		BufferedImage bufferedImage = new BufferedImage(VideoConstant.VIDEO_PLAYER_WIDTH,
				VideoConstant.VIDEO_PLAYER_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int y = 0, ind = 0; y < bufferedImage.getHeight(); y++) {

			for (int x = 0; x < bufferedImage.getWidth(); x++, ind++) {

				byte r = bytes[ind];
				byte g = bytes[ind + bufferedImage.getHeight() * bufferedImage.getWidth()];
				byte b = bytes[ind + bufferedImage.getHeight() * bufferedImage.getWidth() * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				bufferedImage.setRGB(x,y,pix);

			}
		}
		return bufferedImage;
	}

	private void displayFrame(BufferedImage img, String[] args) {
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Video: " + args[0]);
		// lbText1.setHorizontalAlignment(SwingConstants.LEFT);
		JLabel lbText2 = new JLabel("Audio: " + args[1]);
		// lbText2.setHorizontalAlignment(SwingConstants.LEFT);
		lbIm1 = new JLabel(new ImageIcon(img));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
	}


	public void start() {
		fillUpInitialBuffer();
		startVideoFrameBufferProducer();
		// playVideoAudio();
	}

	public BufferedImage getCurrentPlayedFrame() {
		BufferedImage take = null;
		try {
			take = bufferQ.take();
			// System.out.println("BQ:" + bufferQ.size());
			// outputDisplayService.displayImage(take);
			// availableResourcesQ.put(take);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return take;
	}

	public void playVideoAudio(GUI outputDisplayService) {
		start();
		BufferedImage take;
		for (int i = 0; i < VideoConstant.VIDEO_FRAME_COUNT; i++) {
			long currTime = System.nanoTime() / 1000000;
			try {
				take = bufferQ.take();
				outputDisplayService.displayImage(take);
				availableResourcesQ.put(take);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			soundService.playMusicFrameByFrame(i);
			System.out.println(i + ":" + (System.nanoTime() / 1000000 - currTime));
		}
	}

	private void fillUpInitialBuffer() {
		int hundredFramesAhead = video.getCurrentFramePtr()
				+ VideoConstant.VIDEO_FRAME_BUFFER_LENGTH;
		bufferQ.clear();
		availableResourcesQ.clear();
		int i = video.getCurrentFramePtr();
		for (; i < hundredFramesAhead && i < VideoConstant.VIDEO_FRAME_COUNT; i++) {
			bufferQ.add(VideoIOUtil.getFrame(video.getFile(), i));
		}
		// video.setCurrentFramePtr(i);
	}

	private void startVideoFrameBufferProducer() {
		videoFrameBufferRunnable = new VideoFrameBufferRunnable(bufferQ, video, availableResourcesQ);
		videoFrameBufferRunnable.start();
	}


	private static int getAvg(int rgb1, int rgb2) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		int red = (c1.getRed() + c2.getRed()) / 2;
		int blue = (c1.getBlue() + c2.getBlue()) / 2;
		int green = (c1.getGreen() + c2.getGreen()) / 2;
		Color c3 = new Color(red, blue, green);
		return c3.getRGB();
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public void playSoundFrame(int i) {
		soundService.playMusicFrameByFrame(i);
	}

	public void putIntoAvailableResources(BufferedImage take) {
		try {
			availableResourcesQ.put(take);
			// System.out.println("AQ size:" + availableResourcesQ.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopBufferThread() {
		videoFrameBufferRunnable.toggleStop();
		try {
			// to unblock the frame thread
			if (bufferQ.size() != 0) {
				availableResourcesQ.put(bufferQ.take());
			}
			videoFrameBufferRunnable.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Video getCurrentlyPlayingVideo() {
		return video;
	}

}