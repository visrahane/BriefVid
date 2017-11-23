/**
 *
 */
package com.vd.services;

import java.util.List;

import com.vd.key.frames.processing.AudioSceneKeyFrameExtractor;
import com.vd.key.frames.processing.KeyFrameExtractor;
import com.vd.key.frames.processing.VideoSceneKeyFrameExtractor;

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
		keyFrameExtractor = new AudioSceneKeyFrameExtractor(soundService);
		List<Integer> audioKeyFrames = keyFrameExtractor.getKeyFrames();
		System.out.println(audioKeyFrames.size());
		// get Scenes as key frames
		keyFrameExtractor = new VideoSceneKeyFrameExtractor();
		return audioKeyFrames;

	}

}
