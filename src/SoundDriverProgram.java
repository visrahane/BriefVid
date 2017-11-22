import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vd.services.GUI;
import com.vd.services.SoundService;
import com.vd.util.VideoIOUtil;

/**
 *
 */

/**
 * @author Vis
 *
 */
public class SoundDriverProgram {

	/**
	 * @param args
	 */
	public static final File VIDEO_FILE = new File(
			"R:/Study/Masters/Fall 2017/CSCI-576 Multimedia/Project/CS576_Project_Videos/Apple.rgb");

	public static final File AUDIO_FILE = new File(
			"R:/Study/Masters/Fall 2017/CSCI-576 Multimedia/Project/CS576_Project_Videos/Apple.wav");

	public static void main(String[] args) {
		// outputAudioBytesToFile();

		SoundService soundService = new SoundService(AUDIO_FILE.getAbsolutePath());
		// displayGrpOfFrames(soundService);

		float[] rms = getRMSValues(soundService);
		float var = getVariance(rms);
		float deviation = (float) Math.sqrt(var);
		System.out.println("Deviation" + Math.sqrt(var));
		List<Integer> keyFrames = getKeyFrames(deviation, rms);

		System.out.println("Size:" + (keyFrames.size()));
		displayGrpOfFrames(keyFrames, 70, 90);
		// rmsToFile(soundService);
		// avgToFile(soundService);
	}

	private static List<Integer> getKeyFrames(float deviation, float[] rms) {
		List<Integer> keyFrames = new ArrayList<>();
		for(int i=1;i<6000;i++){
			if (rms[i] - rms[i - 1] >= deviation) {
				keyFrames.add(i);
			}
		}
		return keyFrames;
	}

	private static float getVariance(float[] rms) {
		float var = 0;
		for (int i = 0; i < 6000; i++) {
			var += Math.pow((rms[6000] - rms[i]), 2);
		}
		return var / 6000;
	}

	private static void displayGrpOfFrames(List<Integer> framesIndex, int start, int end) {
		GUI ids;

		for (int i = start; i < end; i++) {
			ids = new GUI("TestUI", new String[] { VIDEO_FILE.getAbsolutePath(), AUDIO_FILE.getAbsolutePath() });
			// soundService.playMusicFrameByFrame(i);
			ids.displayImage(VideoIOUtil.getFrame(VIDEO_FILE, framesIndex.get(i)));
		}
	}

	private static void avgToFile(SoundService soundService) {
		File file = new File("avgOp");
		PrintWriter pr = null;
		try {
			pr = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] audioFrame;
		for (int i = 0; i < 6000; i++) {
			audioFrame = soundService.getAudioFrame(i);
			// soundService.playMusicFrameByFrame(i);
			float avg = calculateAvg(audioFrame);
			System.out.println(i + ":" + avg);
			pr.println(Float.toString(avg));
		}
		pr.close();

	}

	private static float calculateAvg(byte[] audioFrame) {
		float sum = 0;
		for (int i = 0; i < audioFrame.length; i++) {
			sum += audioFrame[i];
		}
		return (sum / audioFrame.length);
	}

	private static float[] getRMSValues(SoundService soundService) {
		float rms[] = new float[6001];// 6000-avg
		byte[] audioFrame;
		float sum = 0;
		for (int i = 0; i < 6000; i++) {
			audioFrame = soundService.getAudioFrame(i);
			// soundService.playMusicFrameByFrame(i);
			rms[i] = calculateRMS(audioFrame);
			sum += rms[i];
			System.out.println(i + ":" + rms[i]);
		}
		rms[6000] = sum / 6000;
		System.out.println(rms[6000]);
		return rms;

	}

	private static void rmsToFile(SoundService soundService) {
		File file = new File("rmsOp");
		PrintWriter pr = null;
		try {
			pr = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] audioFrame;
		for (int i = 0; i < 6000; i++) {
			audioFrame = soundService.getAudioFrame(i);
			// soundService.playMusicFrameByFrame(i);
			float rms = calculateRMS(audioFrame);
			System.out.println(i + ":" + rms);
			pr.println(Float.toString(rms));
		}
		pr.close();
	}

	private static float calculateRMS(byte[] audioFrame) {
		float rms = 0;
		for (int i = 0; i < audioFrame.length; i++) {
			rms += audioFrame[i] * audioFrame[i];
		}
		return (float) Math.sqrt(rms / audioFrame.length);
	}

	private static void outputAudioBytesToFile(SoundService soundService) {

		PrintWriter pr = null;
		try {
			pr = new PrintWriter(new File("audioOp"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < 6000; i++) {
			pr.println(Arrays.toString(soundService.getAudioFrame(i)));

		}
	}

}
