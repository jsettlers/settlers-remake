package go.graphics.swing.sound;

import go.graphics.sound.ForgettingQueue;
import go.graphics.sound.ForgettingQueue.Sound;
import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
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
			new Thread(soundgroup, new SoundPlayerTask(), "soundplayer" + i)
					.start();
		}
	}

	@Override
	public void playSound(int soundStart, float lvolume, float rvolume) {
		if (lvolume > 0 || rvolume > 0) {
			queue.offer(soundStart, lvolume, rvolume);
		}
	}

	public byte[] transformData(short[] data) {
		byte[] buffer = new byte[data.length * 4];
		for (int i = 0; i < data.length; i++) {
			buffer[4 * i] = buffer[4 * i + 2] = (byte) data[i];
			buffer[4 * i + 1] = buffer[4 * i + 3] = (byte) (data[i] >> 8);
		}

		return buffer;
	}

	public byte[] transformData(short[] data, float l, float r) {
		byte[] buffer = new byte[data.length * 4];
		for (int i = 0; i < data.length; i++) {
			int ld = (int) (data[i] * l);
			buffer[4 * i] = (byte) ld;
			buffer[4 * i + 1] = (byte) (ld >> 8);
			int rd = (int) (data[i] * r);
			buffer[4 * i + 2] = (byte) rd;
			buffer[4 * i + 3] = (byte) (rd >> 8);
		}

		return buffer;
	}

	private class SoundPlayerTask implements Runnable {

		@Override
		public void run() {
			AudioFormat format = new AudioFormat(22050, 16, 2, true, false);

			Line.Info info = new Line.Info(SourceDataLine.class);

			try {
				SourceDataLine dataLine = (SourceDataLine) AudioSystem
						.getMixer(null).getLine(info);
				dataLine.open(format, BUFFER_SIZE);

				while (true) {
					try {
						// start sound playing
						dataLine.start();

						Sound<Integer> sound = queue.take();
						System.out.println("Volume: " +sound.getVolume());

						byte[] buffer;
						if (dataLine
								.isControlSupported(FloatControl.Type.VOLUME)
								&& dataLine
										.isControlSupported(FloatControl.Type.BALANCE)) {
							buffer = transformData(soundDataRetriever
									.getSoundData(sound.getData()));
							((FloatControl) dataLine
									.getControl(FloatControl.Type.VOLUME))
									.setValue(sound.getVolume());
							((FloatControl) dataLine
									.getControl(FloatControl.Type.BALANCE))
									.setValue(sound.getBalance());
						} else {
							buffer = transformData(
									soundDataRetriever.getSoundData(sound
											.getData()), sound.getLvolume(),
									sound.getRvolume());
						}

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
