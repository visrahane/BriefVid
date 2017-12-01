/**
 *
 */
package com.vd.services;

import java.util.ArrayList;
import java.util.List;

import com.vd.constants.VideoConstant;
import com.vd.key.frames.processing.AudioSceneKeyFrameExtractor;
import com.vd.key.frames.processing.KeyFrameExtractor;
import com.vd.key.frames.processing.VideoSceneKeyFrameExtractor;
import com.vd.models.Video;

public class KeyFrameService {

	private KeyFrameExtractor keyFrameExtractor;
	private String audioFilePath;
	private String videoFilePath;
	private SoundService soundService;

	public KeyFrameService(String videoFilePath, String audioFilePath) {
		this.audioFilePath = audioFilePath;
		this.videoFilePath = videoFilePath;
		soundService = new SoundService(audioFilePath);
	}

	public List<Integer> processFrames() {
		// get key frames from Audio
		keyFrameExtractor = new AudioSceneKeyFrameExtractor(soundService);
		List<Integer> audioKeyFrames = keyFrameExtractor.getKeyFrames();
		System.out.println(audioKeyFrames.size());

		// get Scenes as key frames
		Video video = new Video(videoFilePath, VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
		keyFrameExtractor = new VideoSceneKeyFrameExtractor(video);
		List<Integer> videoKeyFrames = keyFrameExtractor.getKeyFrames(audioKeyFrames);
		System.out.println("Video frame count from histogram: " + videoKeyFrames.size());
		// return mergeKeyFrames(videoKeyFrames, audioKeyFrames);
		// return audioKeyFrames;
		return videoKeyFrames;
	}

	public List<Integer> mergeKeyFrames(List<Integer> videoKeyFrames, List<Integer> audioKeyFrames) {
		List<Integer> uniqueFrames = new ArrayList<>();
		int p = 0, q = 0;
		while (p < audioKeyFrames.size() && q < videoKeyFrames.size()) {
			if (audioKeyFrames.get(p) > videoKeyFrames.get(q)) {
				uniqueFrames.add(videoKeyFrames.get(q++));
			} else {
				uniqueFrames.add(audioKeyFrames.get(p++));
			}
		}

		while (p < audioKeyFrames.size()) {
			uniqueFrames.add(audioKeyFrames.get(p++));
		}
		while (q < videoKeyFrames.size()) {
			uniqueFrames.add(videoKeyFrames.get(q++));
		}
		System.out.println(uniqueFrames.size());
		return uniqueFrames;
	}
}
