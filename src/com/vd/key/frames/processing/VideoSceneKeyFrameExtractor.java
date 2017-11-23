package com.vd.key.frames.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import com.vd.constants.VideoConstant;


// need to decide if to operate on byte array or buffered image
public class VideoSceneKeyFrameExtractor implements KeyFrameExtractor {

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
		// TODO Auto-generated method stub
		return null;
	}
}
