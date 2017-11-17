package com.vd.player;
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
import com.vd.services.ImageDisplayService;
import com.vd.services.SoundService;
import com.vd.util.VideoIOUtil;


public class AVPlayer {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	private SoundService soundService;

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


	/*public static void main(String[] args) throws InterruptedException {

		AVPlayer ren = new AVPlayer();
		ImageDisplayService outputDisplayService = new ImageDisplayService("Video Player");
		Video video = new Video(args[0], VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
		// List<BufferedImage> buffImages = ren.getAllFrames(args);

		// List<byte[]> audioFrames = ren.sound.getAudioBuffer();
		//		List<BufferedImage> buffImages = VideoIOUtil.getAllFrames(framesList);
		for (int i = 0; i < 6000; i++) {
			long currTime = System.nanoTime()/1000000;

			// byte[] frameBytes = framesList.get(i);
			// ren.displayVideo(args, framesList);
			BufferedImage img = VideoIOUtil.getFrame(video.getFile(), i);

			// BufferedImage img = buffImages.get(i);
			outputDisplayService.displayImage(img);
			// ren.sound.playMusicFrameByFrame(i);
			// Thread.sleep();
			// ren.displayFrame(img, args);
			System.out.println(System.nanoTime()/1000000 - currTime);
		}
		// ren.playWAV(args[1]);
	}*/

	public void start(String[] args) {
		soundService = new SoundService(args[1]);
		Video video = new Video(args[0], VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
		ArrayBlockingQueue<BufferedImage> bufferQ = new ArrayBlockingQueue<>(
				(int) VideoConstant.VIDEO_FRAME_BUFFER_LENGTH, true);
		fillUpInitialBuffer(video, bufferQ);
		startVideoFrameBufferProducer(video, bufferQ);
		playVideoAudio(bufferQ);
	}

	private void playVideoAudio(ArrayBlockingQueue<BufferedImage> bufferQ) {
		ImageDisplayService outputDisplayService = new ImageDisplayService("Video Player");
		for (int i = 0; i < 6000; i++) {
			long currTime = System.nanoTime() / 1000000;
			try {
				outputDisplayService.displayImage(bufferQ.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			soundService.playMusicFrameByFrame(i);
			System.out.println(i + ":" + (System.nanoTime() / 1000000 - currTime));
		}
	}

	private void fillUpInitialBuffer(Video video, ArrayBlockingQueue<BufferedImage> bufferQ) {
		for (int i = 0; i < 100; i++) {
			bufferQ.add(VideoIOUtil.getFrame(video.getFile(), i));
		}
	}

	private void startVideoFrameBufferProducer(Video video, ArrayBlockingQueue<BufferedImage> bufferQ) {
		Thread t = new Thread(new VideoFrameBufferRunnable(bufferQ, video));
		t.start();
	}

}