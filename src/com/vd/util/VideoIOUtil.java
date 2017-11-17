/**
 *
 */
package com.vd.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.vd.constants.VideoConstant;
import com.vd.models.Video;

/**
 * @author Vis
 *
 */
public class VideoIOUtil {

	private static final List<Long> frameBufferPointers;

	static {
		frameBufferPointers = new ArrayList<>();
		for (long i = 0; i < VideoConstant.VIDEO_FRAME_COUNT; i++) {
			frameBufferPointers.add(i * VideoConstant.VIDEO_FRAME_SIZE);
		}
	}

	public static BufferedImage getFrame(File file, int frameNo) {
		return getFrame(getFrameBytes(file, frameNo));
	}

	public static BufferedImage getFrame(File file, int frameNo, BufferedImage bufferedImage) {
		return getFrame(getFrameBytes(file, frameNo), bufferedImage);
	}

	public static byte[] getFrameBytes(File file, int frameNo) {
		byte[] bytes = new byte[(int) VideoConstant.VIDEO_FRAME_SIZE];
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			randomAccessFile.seek(frameBufferPointers.get(frameNo));
			randomAccessFile.readFully(bytes);
			randomAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static BufferedImage getFrame(byte[] bytes, BufferedImage bufferedImage) {
		for (int y = 0, ind = 0; y < bufferedImage.getHeight(); y++) {

			for (int x = 0; x < bufferedImage.getWidth(); x++, ind++) {

				byte r = bytes[ind];
				byte g = bytes[ind + bufferedImage.getHeight() * bufferedImage.getWidth()];
				byte b = bytes[ind + bufferedImage.getHeight() * bufferedImage.getWidth() * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				bufferedImage.setRGB(x, y, pix);

			}
		}
		return bufferedImage;
	}

	public static BufferedImage getFrame(byte[] bytes) {
		BufferedImage bufferedImage = new BufferedImage(VideoConstant.VIDEO_PLAYER_WIDTH,
				VideoConstant.VIDEO_PLAYER_HEIGHT, BufferedImage.TYPE_INT_RGB);
		return getFrame(bytes, bufferedImage);
	}

	public static List<BufferedImage> getFrameBuffer(Video video, int startFrameNo) {
		List<BufferedImage> buffImages = new ArrayList<>(1);
		try {
			buffImages = readBufferedImages(video.getFile(), startFrameNo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffImages;
	}

	private static List<BufferedImage> readBufferedImages(File file,int startFrameNo) throws IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		List<BufferedImage> bufferedImages = new ArrayList<>();
		for (int i = startFrameNo; i < startFrameNo + VideoConstant.VIDEO_FRAME_BUFFER_LENGTH; i++) {
			bufferedImages.add(getFrame(file, i));
		}
		return bufferedImages;
	}

	public static BufferedImage mergeImages(List<BufferedImage> buffImages) {
		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(VideoConstant.VIDEO_PLAYER_WIDTH * buffImages.size(),
				buffImages.get(0).getHeight(), buffImages.get(0).getType());

		int num = 0;
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < buffImages.size(); j++, num++) {
				finalImg.createGraphics().drawImage(buffImages.get(num), VideoConstant.VIDEO_PLAYER_WIDTH * j,
						buffImages.get(0).getHeight() * i, null);

			}
		}
		System.out.println("Image concatenated.....");
		/*
		 * try { ImageIO.write(finalImg, "jpeg", new File("finalImg.jpg")); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		return finalImg;

	}

	public static void main(String args[]) {
		System.out.println(frameBufferPointers.get(5999));
	}

}
