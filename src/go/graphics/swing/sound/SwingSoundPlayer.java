package go.graphics.swing.sound;

import go.graphics.sound.ForgettingQueue;
import go.graphics.sound.ForgettingQueue.Sound;
import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SwingSoundPlayer implements SoundPlayer {
	private static final int BUFFER_SIZE = 4048 * 4;
	private static final int SOUND_THREADS = 3;

	ForgettingQueue<Integer> queue = new ForgettingQueue<Integer>();
	private ISoundDataRetriever soundDataRetriever;

	public SwingSoundPlayer() {
		ThreadGroup soundgroup = new ThreadGroup("soundplayer");
		for (int i = 0; i < SOUND_THREADS; i++) {
			new Thread(soundgroup, new SoundPlayerTask(), "soundplayer" + i).start();
		}
	}

	@Override
	public void playSound(int soundStart, float lvolume, float rvolume) {
		queue.offer(soundStart, lvolume, rvolume);
	}

	public byte[] transformData(short[] data) {
		try {
			byte[] buffer = new byte[data.length * 4];
			for (int i = 0; i < data.length; i++) {
				buffer[4 * i] = buffer[4 * i + 2] = (byte) data[i];
				buffer[4 * i + 1] = buffer[4 * i + 3] = (byte) (data[i] >> 8);
			}

			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private class SoundPlayerTask implements Runnable {

		@Override
		public void run() {
			AudioFormat format = new AudioFormat(22050, 16, 2, true, false);

			Line.Info info = new Line.Info(SourceDataLine.class);

			try {
				SourceDataLine dataLine = (SourceDataLine) AudioSystem.getMixer(null).getLine(info);
				dataLine.open(format, BUFFER_SIZE);

				while (true) {
					try {
						// start sound playing
						dataLine.start();

						Sound<Integer> sound = queue.take();
						long start = System.currentTimeMillis();
						byte[] buffer = transformData(soundDataRetriever.getSoundData(sound.getStart()));
						System.out.println("loading sound took: " + (System.currentTimeMillis() - start) + " ms");

						dataLine.write(buffer, 0, buffer.length);

						// stop playing
						dataLine.drain();
						dataLine.stop();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (InterruptedException e) {
				// exit
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setSoundDataRetriever(ISoundDataRetriever soundDataRetriever) {
		this.soundDataRetriever = soundDataRetriever;
	}

}
