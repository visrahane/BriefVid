package com.vd.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import com.vd.constants.VideoConstant;

public class FaceDetectorUtil {

	private static final CascadeClassifier faceDetector;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
	}

	public static BufferedImage detectFaces(BufferedImage scaledFrame) {
		int[] inputData = ((DataBufferInt) scaledFrame.getRaster().getDataBuffer()).getData();
		System.out.println("\nRunning FaceDetector" + inputData.length);
		Mat imageMat = getMat(inputData);

		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(imageMat, faceDetections);
		System.out.println("Detected faces-" + faceDetections.toArray().length);
		MaxMinCoords maxMinCoords = getBorderCoords(faceDetections);
		expandCoords(maxMinCoords, imageMat);

		Rect rectCrop;
		Mat image_roi = null;
		if (!faceDetections.empty()) {
			rectCrop = new Rect(maxMinCoords.minX, maxMinCoords.minY, maxMinCoords.maxX - maxMinCoords.minX,
					maxMinCoords.maxY - maxMinCoords.minY);
			image_roi = new Mat(imageMat, rectCrop);
		} else {
			image_roi = imageMat;
		}

		Imgcodecs.imwrite("hi.jpg", image_roi);
		return getBufferedImage(image_roi);
	}

	private static Mat getMat(int[] inputData) {
		Mat imageMat = new Mat(VideoConstant.VIDEO_PLAYER_SCALED_WIDTH, VideoConstant.VIDEO_PLAYER_SCALED_HEIGHT,
				CvType.CV_8UC3);

		ByteBuffer byteBuffer = ByteBuffer.allocate(inputData.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(inputData);

		imageMat.put(0, 0, byteBuffer.array());
		return imageMat;
	}

	private static BufferedImage getBufferedImage(Mat image_roi) {
		int type = BufferedImage.TYPE_INT_RGB;
		int bufferSize = image_roi.channels() * image_roi.cols() * image_roi.rows();
		int[] b = new int[bufferSize];
		image_roi.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(image_roi.cols(), image_roi.rows(), type);
		final int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static void detectFaces(String imageFileName) {
		//System.out.println("\nRunning FaceDetector");
		Mat image = Imgcodecs.imread(imageFileName);
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		// System.out.println("Detected faces-" +
		// faceDetections.toArray().length);
		MaxMinCoords maxMinCoords = getBorderCoords(faceDetections);
		expandCoords(maxMinCoords, image);

		Rect rectCrop;
		Mat image_roi = null;
		if (!faceDetections.empty()) {
			rectCrop = new Rect(maxMinCoords.minX, maxMinCoords.minY, maxMinCoords.maxX - maxMinCoords.minX,
					maxMinCoords.maxY - maxMinCoords.minY);
			image_roi = new Mat(image, rectCrop);

			// scaling detected face to the same height as the keyframe
			/*Mat dst = new Mat();
			
			Size size = new Size(VideoConstant.VIDEO_PLAYER_SCALED_WIDTH - 45,
					VideoConstant.VIDEO_PLAYER_SCALED_HEIGHT);

			Imgproc.resize(image_roi, dst, size, 0, 0, Imgproc.INTER_AREA);
			Imgcodecs.imwrite("intermediate.jpg", dst);*/
		
			Imgcodecs.imwrite("intermediate.jpg", image_roi);
		} else {
			//Picture pic = SeamCarver2.carveSeam(imageFileName);
			//pic.save("intermediate.jpg");
			// pic.show();
		}
	}

	private static void expandCoords(MaxMinCoords maxMinCoords, Mat image) {
		// 28 black bar
		if (maxMinCoords.minX - 10 >= 0) {
			maxMinCoords.minX -= 10;
		} else {
			maxMinCoords.minX = 0;
		}
		if (maxMinCoords.minY - 10 >= 0) {
			maxMinCoords.minY -= 10;
		} else {
			maxMinCoords.minY = 0;
		}
		if (maxMinCoords.maxX + 20 <= image.width()) {
			maxMinCoords.maxX += 20;
		} else {
			maxMinCoords.maxX = image.width();
		}
		if (maxMinCoords.maxY + 20 <= image.height() - 28) {
			maxMinCoords.maxY += 20;
		} else {
			maxMinCoords.maxY = image.height();
		}
	}

	public static void main(String[] args) {
		detectFaces("6-806.jpg");

	}

	private static MaxMinCoords getBorderCoords(MatOfRect faceDetections) {
		MaxMinCoords maxMinCoords = new MaxMinCoords();

		for (Rect rect : faceDetections.toArray()) {
			if (rect.x < maxMinCoords.minX) {
				maxMinCoords.minX = rect.x;
			}
			if (rect.y < maxMinCoords.minY) {
				maxMinCoords.minY = rect.y;
			}
			if (rect.x + rect.width > maxMinCoords.maxX) {
				maxMinCoords.maxX = rect.x + rect.width;
			}
			if (rect.y + rect.height > maxMinCoords.maxY) {
				maxMinCoords.maxY = rect.y + rect.height;
			}
		}
		return maxMinCoords;
	}

	private static class MaxMinCoords {
		private int maxX = Integer.MIN_VALUE;
		private int maxY = Integer.MIN_VALUE;
		private int minX = Integer.MAX_VALUE;;
		private int minY = Integer.MAX_VALUE;
	}
}