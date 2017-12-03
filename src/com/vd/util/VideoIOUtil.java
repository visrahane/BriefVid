/**
 *
 */
package com.vd.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.vd.constants.VideoConstant;
import com.vd.models.PythonParams;
import com.vd.models.Video;

/**
 * @author Vis
 *
 */
public class VideoIOUtil {

	private static final File CURRENT_DIRECTORY = new File(".");;
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
		return finalImg;
	}

	public static BufferedImage mergeImages(Integer[] keyFramesArray, File file) {
		// Initializing the final image
		BufferedImage originalFrame = VideoIOUtil.getFrame(file, 0);
		BufferedImage scaledFrame;
		int newHeight = VideoConstant.VIDEO_PLAYER_SCALED_HEIGHT;
		int newWidth = VideoConstant.VIDEO_PLAYER_SCALED_WIDTH;
		BufferedImage tapestry = new BufferedImage(
				(int) ((newWidth - 45) * Math.ceil((float) keyFramesArray.length / 2)),
				newHeight * 2, originalFrame.getType());

		int widthPointLocation = 0;
		int prevLocation = 0, prevHeight = 0;

		try {
			for (int j = 0; j < keyFramesArray.length; j++) {
				originalFrame = VideoIOUtil.getFrame(file, keyFramesArray[j]);
				scaledFrame = VideoIOUtil.getScaledFrame(newWidth, newHeight, originalFrame);

				ImageIO.write(scaledFrame, "jpeg", new File("intermediate.jpg"));
				FaceDetectorUtil.detectFaces("intermediate.jpg");
				scaledFrame = ImageIO.read(new File("intermediate.jpg"));
				// scaledFrame = VideoIOUtil.getScaledFrame(newWidth-45,
				// newHeight , scaledFrame);

				if (j % 2 == 0) {
					tapestry.createGraphics().drawImage((scaledFrame), widthPointLocation, 0, null);
					prevLocation = widthPointLocation;
					widthPointLocation += scaledFrame.getWidth();
				} else {
					tapestry.createGraphics().drawImage(scaledFrame, prevLocation,
							VideoConstant.VIDEO_PLAYER_SCALED_HEIGHT, null);
				}
				prevHeight = scaledFrame.getHeight();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Image concatenated.....");

		try {
			ImageIO.write(tapestry, "jpeg", new File("finalImg.jpg"));
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tapestry;

		/*		BufferedImage originalFrame = VideoIOUtil.getFrame(file, 0);
		BufferedImage scaledFrame;
		int newHeight = VideoConstant.VIDEO_PLAYER_SCALED_HEIGHT;
		int newWidth = VideoConstant.VIDEO_PLAYER_SCALED_WIDTH;
		BufferedImage tapestry = new BufferedImage(VideoConstant.VIDEO_PLAYER_SCALED_WIDTH * keyFramesArray.length
				- keyFramesArray.length * 30 + VideoConstant.VIDEO_PLAYER_SCALED_WIDTH,
				originalFrame.getHeight(), originalFrame.getType());

		int widthPointLocation = 0;
		try {
			for (int j = 0; j < keyFramesArray.length; j++) {
				originalFrame = VideoIOUtil.getFrame(file, keyFramesArray[j]);
				scaledFrame = VideoIOUtil.getScaledFrame(newWidth, newHeight, originalFrame);
				ImageIO.write(scaledFrame, "jpeg", new File("intermediate.jpg"));
				FaceDetectorUtil.detectFaces("intermediate.jpg");
				scaledFrame = ImageIO.read(new File("intermediate.jpg"));
				// scaledFrame = VideoIOUtil.getScaledFrame(newWidth, newHeight
				// - 50, scaledFrame);

				if (j % 2 == 0) {
					tapestry.createGraphics().drawImage((scaledFrame), widthPointLocation, 0, null);
				} else {
					tapestry.createGraphics().drawImage(scaledFrame, widthPointLocation, 50, null);
				}
				widthPointLocation += scaledFrame.getWidth() - 30;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Image concatenated.....");

		try {
			ImageIO.write(tapestry, "jpeg", new File("finalImg.jpg"));
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tapestry;*/


		/*BufferedImage originalFrame = VideoIOUtil.getFrame(file, keyFramesArray[37]);
		int newHeight = 155;
		int newWidth = 200;
		try {
			ImageIO.write(VideoIOUtil.getScaledFrame(newWidth, newHeight, originalFrame), "jpeg",
					new File("35.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage tapestry = new BufferedImage(
				newWidth * keyFramesArray.length - keyFramesArray.length * 30 + VideoConstant.VIDEO_PLAYER_WIDTH,
				newHeight, originalFrame.getType());

		for (int j = 1; j < keyFramesArray.length / 10; j++) {
			prepareTapestryImage(keyFramesArray, file, newHeight, newWidth, j);
		}
		//
		try {
			tapestry = ImageIO.read(new File("finalTapestry.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// tapestry.createGraphics().drawImage(scaledFrame, 0, 0, null);

		System.out.println("Image concatenated.....");


		 * try { ImageIO.write(tapestry, "jpeg", new File("finalTapestry.jpg"));
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }


		return tapestry;
		 */
	}

	private static void prepareTapestryImage(Integer[] keyFramesArray, File file, int newHeight, int newWidth, int j) {
		BufferedImage originalFrame;
		BufferedImage scaledFrame;
		originalFrame = VideoIOUtil.getFrame(file, keyFramesArray[j]);
		scaledFrame = VideoIOUtil.getScaledFrame(newWidth, newHeight, originalFrame);
		// get combined img from python
		try {
			ImageIO.write(scaledFrame, "jpeg", new File(j + ".jpg"));
			PythonExecutor.executePython(
					new PythonParams("C:/Users/visha/PycharmProjects/PyramidBlending/pyramidBlend.py",
							new ArrayList<>(
									Arrays.asList(CURRENT_DIRECTORY.getCanonicalFile() + "/" + "finalTapestry.jpg",
											CURRENT_DIRECTORY.getCanonicalFile() + "/" + j + ".jpg"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage getScaledFrame(int newWidth, int newHeight, BufferedImage original) {
		BufferedImage scaledFrame = new BufferedImage(newWidth, newHeight, original.getType());
		Graphics2D g = scaledFrame.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(), original.getHeight(), null);
		g.dispose();
		return scaledFrame;

	}

	public static void main(String args[]) throws IOException {

	}

	public static BufferedImage blend(BufferedImage bi1, BufferedImage bi2, double weight) {
		if (bi1 == null) {
			throw new NullPointerException("bi1 is null");
		}

		if (bi2 == null) {
			throw new NullPointerException("bi2 is null");
		}

		int width = bi1.getWidth();
		if (width != bi2.getWidth()) {
			throw new IllegalArgumentException("widths not equal");
		}

		int height = bi1.getHeight();
		if (height != bi2.getHeight()) {
			throw new IllegalArgumentException("heights not equal");
		}

		BufferedImage bi3 = new BufferedImage(width * 2 - 30, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi3.createGraphics();
		g2d.drawImage(bi1, null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0 - weight)));
		g2d.drawImage(bi2, null, 330, 0);
		g2d.dispose();

		return bi3;
	}

}
