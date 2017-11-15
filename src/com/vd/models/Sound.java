/**
 *
 */
package com.vd.models;

/**
 * @author Vis
 *
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.vd.constants.SoundConstant;
import com.vd.exception.PlayWaveException;

/**
 *
 * <Replace this with a short description of the class.>
 *
 * @author Giulio
 */
public class Sound {

	private SourceDataLine dataLine;

	private InputStream waveStream;

	private AudioInputStream audioInputStream;

	private final int EXTERNAL_BUFFER_SIZE =(int) SoundConstant.TOTAL_FRAME_SIZE * 50 / 1000;// 524288; // 128Kb

	private List<byte[]> audioFrames = new ArrayList<>();
	/**
	 * CONSTRUCTOR
	 */
	public Sound(InputStream waveStream) {
		// this.waveStream = waveStream;
		this.waveStream = new BufferedInputStream(waveStream);
	}

	public void init() throws PlayWaveException {

		try {
			audioInputStream = AudioSystem.getAudioInputStream(waveStream);
		} catch (UnsupportedAudioFileException e1) {
			throw new PlayWaveException(e1);
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}

		// Obtain the information about the AudioInputStream
		AudioFormat audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);

		// opens the audio channel

		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, (int) SoundConstant.TOTAL_FRAME_SIZE * 50 / 1000);
		} catch (LineUnavailableException e1) {
			throw new PlayWaveException(e1);
		}

		startMusic(audioInputStream);
	}

	private void startMusic(AudioInputStream audioInputStream) throws PlayWaveException {
		// Starts the music :P
		// dataLine.start();

		int readBytes = 0;


		try {
			int i = 0;
			while (readBytes != -1) {
				byte[] audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];
				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
				audioFrames.add(audioBuffer);
				if (readBytes >= 0) {
					// dataLine.write(audioBuffer, 0, readBytes);
				}
			}
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		} finally {
			// plays what's left and and closes the audioChannel
			// dataLine.drain();
			// dataLine.close();
		}

		// for (int i = 0; i < 6000; i++) {
		// playMusicFrameByFrame(i);
		// dataLine.write(audioFrames.get(i), 0, 4410);
		// }
	}

	public void playMusicFrameByFrame(int frameSize) {
		dataLine.start();
		int readBytes = 0;
		try {
			readBytes = dataLine.write(audioFrames.get(frameSize), 0, 4410);
		} finally {
			// plays what's left and and closes the audioChannel
			// dataLine.drain();
			// dataLine.close();
		}
	}

	public List<byte[]> getAudioBuffer() {
		List<byte[]> audioFrames = new ArrayList<>();

		int readBytes = 0;
		try {
			while (readBytes != -1) {
				byte[] audioBuffer = new byte[(int) SoundConstant.TOTAL_FRAME_SIZE * 50 / 1000];
				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
				audioFrames.add(audioBuffer);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return audioFrames;
	}
}
