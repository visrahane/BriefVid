/**
 *
 */
package com.vd.services;

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
		List<Integer> videoKeyFrames = keyFrameExtractor.getKeyFrames();
		System.out.println("Video frame count from histogram: " + videoKeyFrames.size());
//		return audioKeyFrames;
		return videoKeyFrames;
	}
}
