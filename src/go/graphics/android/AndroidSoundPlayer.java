package go.graphics.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;

import go.graphics.sound.SoundPlayer;

public class AndroidSoundPlayer implements SoundPlayer {

	// SoundPool pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

	private static final int SAMPLERATE = 22050;
	private static final int VOLUMESCALE = 1;
	private final ArrayList<short[]> tracks = new ArrayList<short[]>();
	private final AudioTrack track;

	public AndroidSoundPlayer() {
		int bufferSize =
		        AudioTrack.getMinBufferSize(SAMPLERATE,
		                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
		                AudioFormat.ENCODING_PCM_16BIT);
		track =
		        new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE,
		                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
		                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
		                AudioTrack.MODE_STREAM);
		track.play();
	}

	@Override
	public void playSound(int sound, float lvolume, float rvolume) {
		// pool.play(sound, lvolume, rvolume, 0, 0, 1);
		if (sound >= 0 && sound < tracks.size()) {
			try {
				short[] data = tracks.get(sound);
				System.out.println("Playing " + data.length
				        + " samples of sound " + sound);
				track.write(data, 0, data.length);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int load(short[] data) {
		// try {
		// return pool.load(new FileInputStream(sndfile).getFD(), leftstart,
		// length * 2, 1);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		if (data.length % 2 != 0 || data.length == 0) {
			return -1;
		}

		int id = tracks.size();
		tracks.add(data);

		System.out.println("Loaded sound " + id + ", with " + data.length
		        + " samples");

		return id;

	}

}
