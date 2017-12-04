/**
 *
 */
package com.vd.test;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vd.services.KeyFrameService;

/**
 * @author Vis
 *
 */
public class KeyFrameServiceTest {
	KeyFrameService keyFrameService = new KeyFrameService(
			"R:/Study/Masters/Fall 2017/CSCI-576 Multimedia/Project/CS576_Project_Videos/Apple.rgb",
			"R:/Study/Masters/Fall 2017/CSCI-576 Multimedia/Project/CS576_Project_Videos/Apple.wav");
	@Test
	public void testMergeKeyFrames() {
		List<Integer> videoKeyFrames = Arrays.asList(1, 4, 5, 8, 11, 12, 15);
		List<Integer> audioKeyFrames = Arrays.asList(2, 3, 6, 7, 10, 14);
		List<Integer> observedUniqueFrames=keyFrameService.mergeKeyFrames(videoKeyFrames, audioKeyFrames);
		List<Integer> expectedUniqueFrames = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 14, 15);
		assertArrayEquals(expectedUniqueFrames.toArray(), observedUniqueFrames.toArray());
	}

}
