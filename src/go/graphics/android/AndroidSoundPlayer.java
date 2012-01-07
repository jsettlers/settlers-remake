package go.graphics.android;

import go.graphics.sound.ForgettingQueue;
import go.graphics.sound.ForgettingQueue.Sound;
import go.graphics.sound.SoundPlayer;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AndroidSoundPlayer implements SoundPlayer {

	// SoundPool pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

	private static final int SAMPLERATE = 22050;
	private final ArrayList<short[]> tracks = new ArrayList<short[]>();

	private final ForgettingQueue<short[]> queue =
	        new ForgettingQueue<short[]>();

	public AndroidSoundPlayer(int parallelSounds) {
		ThreadGroup soundgroup = new ThreadGroup("soundplayer");
		for (int i = 0; i < parallelSounds; i++) {
			new Thread(soundgroup, new PlaySoundTask(), "soundplayer" + i)
			        .start();
		}
	}

	@Override
	public void playSound(int sound, float lvolume, float rvolume) {
		if (sound >= 0 && sound < tracks.size()) {
			try {
				short[] data = tracks.get(sound);
				queue.offer(data, lvolume, rvolume);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int load(short[] data) {
		if (data.length == 0) {
			return -1;
		}

		int id = tracks.size();
		tracks.add(data);

		System.out.println("Loaded sound " + id + ", with " + data.length
		        + " samples");

		return id;

	}

	private class PlaySoundTask implements Runnable {
		@Override
		public void run() {
			int bufferSize =
			        AudioTrack.getMinBufferSize(SAMPLERATE,
			                AudioFormat.CHANNEL_CONFIGURATION_MONO,
			                AudioFormat.ENCODING_PCM_16BIT);
			AudioTrack track =
			        new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE,
			                AudioFormat.CHANNEL_CONFIGURATION_MONO,
			                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
			                AudioTrack.MODE_STREAM);
			track.play();
			try {
				while (true) {
					Sound<short[]> sound = queue.take();
					short[] data = sound.getData();
					track.setStereoVolume(sound.getLvolume(),
					        sound.getRvolume());
					System.out.println("sound: playing " + data.length
					        + " samples");
					track.write(data, 0, data.length);
				}
			} catch (InterruptedException e) {
				// exit
			}
		}

	}

}
