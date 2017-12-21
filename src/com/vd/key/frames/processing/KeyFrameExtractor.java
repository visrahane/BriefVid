/**
 *
 */
package com.vd.key.frames.processing;

import java.util.List;

/**
 * @author Vis
 *
 */
public interface KeyFrameExtractor {

	List<Integer> getKeyFrames(List<Integer> level3, int multiplier);

	List<Integer> getKeyFrames(int multiplier);

}
