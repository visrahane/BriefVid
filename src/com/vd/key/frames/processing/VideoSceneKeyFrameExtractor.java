package com.vd.key.frames.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vd.constants.VideoConstant;
import com.vd.models.Video;
import com.vd.util.VideoIOUtil;


// need to decide if to operate on byte array or buffered image
public class VideoSceneKeyFrameExtractor implements KeyFrameExtractor {

	private Video video;

	public VideoSceneKeyFrameExtractor(Video video) {
		this.video = video;
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

		for(int y = 0; y < VideoConstant.VIDEO_PLAYER_HEIGHT; y++){
			for(int x = 0; x < VideoConstant.VIDEO_PLAYER_WIDTH; x++){

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

	@Override
	public List<Integer> getKeyFrames() {
		// List<Integer> keyFrames = new ArrayList<>();
		File file = video.getFile();
		BufferedImage prev, next;
		int diffValues[] = new int[VideoConstant.VIDEO_FRAME_COUNT - 1];
		int sum = 0;
		for(int i=0; i<VideoConstant.VIDEO_FRAME_COUNT-1; i++) {
			prev = VideoIOUtil.getFrame(file, i);
			next = VideoIOUtil.getFrame(file, i+1);

			diffValues[i] = getColorHistogramSAD(prev, next);
			sum += diffValues[i];
			// System.out.println("above diff : " + diffValues[i]);
			/*if (diffValues[i] > 20000) {
				// System.out.println("above diff : " + i);
				keyFrames.add(i);
			}*/
		}
		int avg = sum / (VideoConstant.VIDEO_FRAME_COUNT - 1);
		System.out.println("average:" + avg);
		float variance = getVariance(diffValues, avg);
		float deviation=(float) Math.sqrt(variance);
		return getKeyFramesFromDeviation(diffValues, deviation);
	}

	@Override
	public List<Integer> getKeyFrames(List<Integer> framesList) {
		int size = framesList.size();
		BufferedImage prev, next;
		File file = video.getFile();
		int diffValues[] = new int[size];
		int sum = 0;

		for (int i = 0; i < size - 1; i++) {
			prev = VideoIOUtil.getFrame(file, i);
			next = VideoIOUtil.getFrame(file, i + 1);

			diffValues[i] = getColorHistogramSAD(prev, next);
			System.out.println(diffValues[i]);
			sum += diffValues[i];
		}

		int avg = sum / (size - 1);

		float variance = getVariance(diffValues, avg);
		float deviation = (float) Math.sqrt(variance);
		return getKeyFramesFromDeviation(diffValues, deviation, framesList);
	}

	private List<Integer> getKeyFramesFromDeviation(int[] diffValues, float deviation, List<Integer> framesList) {
		List<Integer> keyFrames = new ArrayList<>();
		deviation *= 2;
		System.out.println(deviation);

		for (int i = 0; i < diffValues.length; i++) {
			if (diffValues[i] > deviation) {
				keyFrames.add(framesList.get(i));
				System.out.println("i frame selected-" + i);
			}
		}
		return keyFrames;
	}

	private List<Integer> getKeyFramesFromDeviation(int[] diffValues, float deviation) {
		List<Integer> keyFrames = new ArrayList<>();
		System.out.println(deviation);
		for(int i=0;i<diffValues.length;i++){
			if(diffValues[i]>deviation){
				keyFrames.add(i);
				System.out.println("i frame selected-" + i);
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
