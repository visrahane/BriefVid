package com.vd.player;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.vd.constants.VideoConstant;
import com.vd.exception.PlayWaveException;
import com.vd.io.VideoIOUtil;
import com.vd.services.ImageDisplayService;


public class AVPlayer {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;


	public List<BufferedImage> getAllFrames(String[] args) {
		List<BufferedImage> buffImages = null;
		try {
			File file = new File(args[0]);
			buffImages = readIntoList(VideoConstant.VIDEO_PLAYER_WIDTH, VideoConstant.VIDEO_PLAYER_HEIGHT,
					file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffImages;
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

	private List<BufferedImage> readIntoList(int width, int height, File file) throws IOException {
		long len = width*height*3;
//		List<byte[]> byteList = new ArrayList<>();
		RandomAccessFile f = new RandomAccessFile(file, "r");
		List<BufferedImage> bufferedImages = new ArrayList<>();
		
		while (f.getFilePointer() != f.length()/2) {
			byte[] bytes = new byte[(int) len];
			f.readFully(bytes);
//			byteList.add(bytes);
			bufferedImages.add(getFrame(bytes));
		}
		return bufferedImages;
	}

	public void playWAV(String filename){
		// opens the inputStream
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// initializes the playSound Object
		Sound playSound = new Sound(inputStream);

		// plays the sound
		try {
			playSound.play();
		} catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length < 2) {
			System.err.println("usage: java -jar AVPlayer.jar [RGB file] [WAV file]");
			return;
		}
		AVPlayer ren = new AVPlayer();
		ImageDisplayService outputDisplayService = new ImageDisplayService("Video Player");
		
		List<BufferedImage> buffImages = ren.getAllFrames(args);
//		List<BufferedImage> buffImages = VideoIOUtil.getAllFrames(framesList);
		// read 1st 1000 frames and display first and last frame for testing
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ren.playWAV(args[1]);
			}

		});

		t.start();
		for (int i = 0; i < 6000; i++) {
			long currTime = System.nanoTime()/1000000;
//			byte[] frameBytes = VideoIOUtil.readFrameBuffer(new File(args[0]), i);
//			byte[] frameBytes = framesList.get(i);
			// ren.displayVideo(args, framesList);
//			BufferedImage img = VideoIOUtil.getFrame(frameBytes);
			
			BufferedImage img = buffImages.get(i);
			outputDisplayService.displayImage(img);
			Thread.sleep(45);
			// ren.displayFrame(img, args);
			System.out.println(System.nanoTime()/1000000 - currTime);
		}
		// ren.playWAV(args[1]);
	}

}