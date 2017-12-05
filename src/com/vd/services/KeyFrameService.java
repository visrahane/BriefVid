/**
 *
 */
package com.vd.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	public static List<Integer> level1, level2, level3;

	public KeyFrameService(String videoFilePath, String audioFilePath) {
		this.audioFilePath = audioFilePath;
		this.videoFilePath = videoFilePath;
		soundService = new SoundService(audioFilePath);
	}

	public List<Integer> processFrames() {
		// get key frames from Audio
		keyFrameExtractor = new AudioSceneKeyFrameExtractor(soundService);
		List<Integer> audioKeyFrames = keyFrameExtractor.getKeyFrames(2);
		System.out.println("Audio KeyFrames Count:" + audioKeyFrames.size());
		System.out.println("Audio key frames List : " + audioKeyFrames.toString());
		// get Scenes as key frames
		Video video = new Video(videoFilePath, VideoConstant.VIDEO_PLAYER_HEIGHT, VideoConstant.VIDEO_PLAYER_WIDTH);
		keyFrameExtractor = new VideoSceneKeyFrameExtractor(video);

		// getting hist diff keyframes from the entire video
		List<Integer> videoKeyFrames = keyFrameExtractor.getKeyFrames(4);
		// add audioKeyFrames to videoKeyFrames and merge
		videoKeyFrames.addAll(audioKeyFrames);
		Set<Integer> totalkeyFrames = new TreeSet<>(videoKeyFrames);

		level3 = new ArrayList<>(totalkeyFrames);
		System.out.println("Video level3 KeyFrames Count:" + level3.size());
		System.out.println("Video level3 key frames List : " + level3.toString());
		level2 = keyFrameExtractor.getKeyFrames(level3, 1);
		System.out.println("Video level2 KeyFrames Count:" + level2.size());
		System.out.println("Video level2 key frames List : " + level2.toString());
		level1 = keyFrameExtractor.getKeyFrames(level2, 2);
		System.out.println("Video level1 KeyFrames Count:" + level1.size());
		System.out.println("Video level1 key frames List : " + level1.toString());
		return level1;
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
