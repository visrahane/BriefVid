package com.vd.key.frames.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vd.constants.VideoConstant;
import com.vd.models.Video;
import com.vd.util.VideoIOUtil;


// need to decide if to operate on byte array or buffered image
public class VideoSceneKeyFrameExtractor implements KeyFrameExtractor {

	public static double DEVIATION_MULTIPLIER = 4.0;
	private Video video;

	public VideoSceneKeyFrameExtractor(Video video) {
		this.video = video;
	}

	public VideoSceneKeyFrameExtractor(Video video, int multiplier) {
		this.video = video;
		DEVIATION_MULTIPLIER = multiplier;
	}

	public static int[][][] getFrameColorHistogram(byte[] frame) {
		// 4 bins of 64 length for r,g,b
		int [][][] colorHist = new int[4][4][4];

		for(int y = 0, ind = 0; y < VideoConstant.VIDEO_PLAYER_HEIGHT; y++){
			for(int x = 0; x < VideoConstant.VIDEO_PLAYER_WIDTH; x++, ind++){

				int r = frame[ind] & 0xff;
				int g = frame[ind+VideoConstant.VIDEO_PLAYER_HEIGHT * VideoConstant.VIDEO_PLAYER_WIDTH] & 0xff;
				int b = frame[ind+VideoConstant.VIDEO_PLAYER_HEIGHT * VideoConstant.VIDEO_PLAYER_WIDTH * 2] & 0xff;

				// incrementing the values in bins
				colorHist[r / 64][g / 64][b / 64]++;
			}
		}

		return colorHist;
	}

	public static int[][][] getFrameColorHistogram(BufferedImage frame) {
		// 4 bins of 64 length for r,g,b
		int [][][] colorHist = new int[4][4][4];

		int height = frame.getHeight();
		int width = frame.getWidth();

		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){

				int rgb = frame.getRGB(x, y);
				Color c = new Color(rgb);

				// incrementing the values in bins
				colorHist[c.getRed() / 64][c.getGreen() / 64][c.getBlue() / 64]++;
			}
		}
		return colorHist;
	}

	public static int getColorHistogramSAD(byte[] frame1, byte[] frame2) {
		int [][][] colorHist1 = getFrameColorHistogram(frame1);
		int [][][] colorHist2 = getFrameColorHistogram(frame2);

		int absDiff = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					absDiff += Math.abs(colorHist1[i][j][k] - colorHist2[i][j][k]);
				}
			}
		}

		return absDiff;
	}

	public static int getColorHistogramSAD(BufferedImage frame1, BufferedImage frame2) {
		int [][][] colorHist1 = getFrameColorHistogram(frame1);
		int [][][] colorHist2 = getFrameColorHistogram(frame2);

		int absDiff = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					absDiff += Math.abs(colorHist1[i][j][k] - colorHist2[i][j][k]);
				}
			}
		}

		return absDiff;
	}

	public static int getColorHistogramChiSquareTest(BufferedImage frame1, BufferedImage frame2) {
		int[][][] colorHist1 = getFrameColorHistogram(frame1);
		int[][][] colorHist2 = getFrameColorHistogram(frame2);

		double value = 0;
		int max = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					int histValue1 = colorHist1[i][j][k];
					int histValue2 = colorHist2[i][j][k];

					if (histValue1 != 0 && histValue2 != 0) {
						if (histValue1 > histValue2) {
							max = histValue1;
						} else {
							max = histValue2;
						}

						value += Math.pow(histValue1 - histValue2, 2) / max;
					}
				}
			}
		}

		return (int) value;
	}

	@Override
	public List<Integer> getKeyFrames(int multiplier) {
		// temporary assignment
		DEVIATION_MULTIPLIER = multiplier;
		File file = video.getFile();
		BufferedImage prev, next;
		int diffValues[] = new int[VideoConstant.VIDEO_FRAME_COUNT - 1];
		int sum = 0;

		for(int i=0; i<VideoConstant.VIDEO_FRAME_COUNT-1; i++) {
			prev = VideoIOUtil.getFrame(file, i);
			next = VideoIOUtil.getFrame(file, i+1);

			diffValues[i] = getColorHistogramChiSquareTest(prev, next);
			sum += diffValues[i];
		}
		int avg = sum / (VideoConstant.VIDEO_FRAME_COUNT - 1);
		System.out.println("Video Average:" + avg);
		float variance = getVariance(diffValues, avg);
		float deviation=(float) Math.sqrt(variance);

		return getKeyFramesFromDeviation(diffValues, deviation);

		// testing
		//		List<Integer> test = new ArrayList<>();
		//		test.add(4190); test.add(4191);
		//		return test;
	}

	public List<Integer> getKeyFramesUsingLocality() {
		File file = video.getFile();
		BufferedImage prev, next;
		int diffValues[] = new int[VideoConstant.VIDEO_FRAME_COUNT - 1];
		int sum = 0, avg = 0, count = 0;

		List<Integer> allKeyFrames = new ArrayList<>();
		int totalFramesInVideo = VideoConstant.VIDEO_FRAME_COUNT;

		for (int i = 0; i < totalFramesInVideo - 1; i += 100) {
			sum = 0;
			avg = 0;
			count = 0;
			diffValues = new int[100];

			for (int j = i; j < i + 100 && j < totalFramesInVideo - 1; j++) {
				prev = VideoIOUtil.getFrame(file, j);
				next = VideoIOUtil.getFrame(file, j + 1);

				diffValues[count] = getColorHistogramChiSquareTest(prev, next);
				sum += diffValues[count];
				count++;
			}
			System.out.println("count : " + count);
			avg = sum / count;
			float variance = getVariance(diffValues, avg);
			float deviation = (float) Math.sqrt(variance);

			List<Integer> localKeyFrames = getKeyFramesFromDeviation(diffValues, deviation);
			allKeyFrames.addAll(localKeyFrames);
		}
		return allKeyFrames;
	}

	@Override
	public List<Integer> getKeyFrames(List<Integer> framesList, int multiplier) {
		DEVIATION_MULTIPLIER = multiplier;
		int size = framesList.size();
		BufferedImage prev, next;
		File file = video.getFile();
		int diffValues[] = new int[size];
		int sum = 0;

		for (int i = 0; i < size - 1; i++) {
			prev = VideoIOUtil.getFrame(file, framesList.get(i));
			next = VideoIOUtil.getFrame(file, framesList.get(i + 1));
			diffValues[i] = getColorHistogramChiSquareTest(prev, next);
			sum += diffValues[i];
		}

		int avg = sum / (size - 1);
		System.out.println("DiffList:" + Arrays.toString(diffValues));
		System.out.println("Frames list average : " + avg);
		float variance = getVariance(diffValues, avg);
		float deviation = (float) Math.sqrt(variance);
		return getKeyFramesFromDeviation(diffValues, deviation, framesList);
	}

	private List<Integer> getKeyFramesFromThreshold(int[] diffValues, int threshold, List<Integer> frames) {
		List<Integer> keyFrames = new ArrayList<>();

		for(int i=0; i<diffValues.length; i++) {
			if(diffValues[i] > threshold) {
				keyFrames.add(frames.get(i));
				keyFrames.add(frames.get(i+1));
			}
		}

		return keyFrames;
	}

	private List<Integer> getKeyFramesFromDeviation(int[] diffValues, float deviation, List<Integer> framesList) {
		Set<Integer> keyFrames = new LinkedHashSet<>();
		System.out.println("Frames list Deviation : " + deviation);
		deviation *= DEVIATION_MULTIPLIER;
		System.out.println("Using deviation multiplier: " + DEVIATION_MULTIPLIER);

		for (int i = 0; i < diffValues.length; i++) {
			if (diffValues[i] > deviation) {
				keyFrames.add(framesList.get(i));
				keyFrames.add(framesList.get(i+1));
			}
		}
		return new ArrayList<>(keyFrames);
	}

	private List<Integer> getKeyFramesFromDeviation(int[] diffValues, float deviation) {
		List<Integer> keyFrames = new ArrayList<>();
		System.out.println("Using deviation multiplier: " + DEVIATION_MULTIPLIER);
		System.out.println("Video Deviation : " + deviation);
		for(int i=0;i<diffValues.length;i++){
			if(diffValues[i] > deviation*DEVIATION_MULTIPLIER){
				keyFrames.add(i);
				keyFrames.add(i+1);
			}
		}
		return keyFrames;
	}

	private float getVariance(int[] values, int avg) {
		float var = 0;
		for (int i = 0; i < values.length - 1; i++) {
			var += Math.pow((avg - values[i]), 2);
		}
		return var / (values.length - 1);
	}

}
