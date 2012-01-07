package go.graphics.swing.sound;

import go.graphics.sound.ForgettingQueue;
import go.graphics.sound.ForgettingQueue.Sound;
import go.graphics.sound.SoundPlayer;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SwingSoundPlayer implements SoundPlayer {
	private static final int BUFFER_SIZE = 4048 * 4;
	private static final int SOUND_THREADS = 3;
	ArrayList<byte[]> clips = new ArrayList<byte[]>();

	ForgettingQueue<byte[]> queue = new ForgettingQueue<byte[]>();

	public SwingSoundPlayer() {
		ThreadGroup soundgroup = new ThreadGroup("soundplayer");
		for (int i = 0; i < SOUND_THREADS; i++) {
			new Thread(soundgroup, new SoundPlayerTask(), "soundplayer" + i)
			        .start();
		}
	}

	@Override
	public void playSound(int sound, float lvolume, float rvolume) {
		if (sound < 0 || sound > clips.size()) {
			return;
		}
		System.out.println("playing sound");
		byte[] clip = clips.get(sound);
		queue.offer(clip, lvolume, rvolume);
	}

	@Override
	public int load(short[] data) {
		try {
			System.out.println("loading sound file ");
			int index = clips.size();

			byte[] buffer = new byte[data.length * 4];
			for (int i = 0; i < data.length; i++) {
				buffer[4 * i] = buffer[4 * i + 2] = (byte) data[i];
				buffer[4 * i + 1] = buffer[4 * i + 3] = (byte) (data[i] >> 8);
			}
			clips.add(buffer);

			return index;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private class SoundPlayerTask implements Runnable {

		@Override
		public void run() {

			AudioFormat format = new AudioFormat(22050, 16, 2, true, false);

			Line.Info info = new Line.Info(SourceDataLine.class);

			try {
				SourceDataLine dataLine =
				        (SourceDataLine) AudioSystem.getMixer(null).getLine(
				                info);
				dataLine.open(format, BUFFER_SIZE);

				while (true) {
					// Sound playing code
					dataLine.start();

					Sound<byte[]> sound = queue.take();
					byte[] buffer = sound.getData();
					dataLine.write(buffer, 0, buffer.length);

					// stop playing
					dataLine.drain();
					dataLine.stop();
				}

			} catch (InterruptedException e) {
				// exit

			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}

	}
}
