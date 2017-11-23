/**
 *
 */
package com.vd.services;

import java.util.List;

import com.vd.key.frames.processing.HighSoundKeyFrameExtractor;
import com.vd.key.frames.processing.KeyFrameExtractor;

/**
 * @author Vis
 *
 */
public class KeyFrameService {

	private KeyFrameExtractor keyFrameExtractor;

	private SoundService soundService;

	public KeyFrameService(String videoFileName, String audioFileName) {
		soundService = new SoundService(audioFileName);

	}

	public List<Integer> processFrames() {
		// get key frames from Audio
		keyFrameExtractor = new HighSoundKeyFrameExtractor(soundService);
		List<Integer> audioKeyFrames = keyFrameExtractor.getKeyFrames();
		// get Scenes as key frames
		return audioKeyFrames;

	}

}
