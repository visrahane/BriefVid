/**
 *
 */
package com.vd.key.frames.processing;

import java.util.ArrayList;
import java.util.List;

import com.vd.services.SoundService;

/**
 * @author Vis
 *
 */
public class AudioSceneKeyFrameExtractor implements KeyFrameExtractor {

	private SoundService soundService;

	public AudioSceneKeyFrameExtractor(SoundService soundService) {
		this.soundService = soundService;
	}

	@Override
	public List<Integer> getKeyFrames() {
		float[] rms = getRMSValues();
		float variance = getVariance(rms);
		float deviation = (float) Math.sqrt(variance);
		System.out.println("Deviation" + Math.sqrt(variance));
		return getKeyFrames(deviation, rms);
	}

	private List<Integer> getKeyFrames(float deviation, float[] rms) {
		List<Integer> keyFrames = new ArrayList<>();
		for (int i = 1; i < rms.length - 1; i++) {
			if (rms[i] - rms[i - 1] >= deviation) {
				keyFrames.add(i);
			}
		}
		return keyFrames;
	}

	private float getVariance(float[] rms) {
		float var = 0;
		float avg = rms[rms.length - 1];// last element is avg
		for (int i = 0; i < rms.length - 1; i++) {
			var += Math.pow((avg - rms[i]), 2);
		}
		return var / (rms.length - 1);
	}

	private float[] getRMSValues() {
		int countOfFrames = soundService.getCountOfFrames();
		float rms[] = new float[countOfFrames + 1];// extra for-avg
		byte[] audioFrame;
		float sum = 0;
		for (int i = 0; i < countOfFrames; i++) {
			audioFrame = soundService.getAudioFrame(i);
			rms[i] = calculateRMS(audioFrame);
			sum += rms[i];
			// System.out.println(i + " frame: rms-" + rms[i]);
		}
		rms[countOfFrames] = sum / countOfFrames;
		System.out.println("Avg:" + rms[countOfFrames]);
		return rms;
	}

	private float calculateRMS(byte[] audioFrame) {
		float rms = 0;
		for (int i = 0; i < audioFrame.length; i++) {
			rms += audioFrame[i] * audioFrame[i];
		}
		return (float) Math.sqrt(rms / audioFrame.length);
	}

}
