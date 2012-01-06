package go.graphics.swing.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import go.graphics.sound.SoundPlayer;

public class SwingSoundPlayer implements SoundPlayer {
	ArrayList<Clip> clips = new ArrayList<Clip>();

	public SwingSoundPlayer() {

	}

	@Override
	public void playSound(int sound, float lvolume, float rvolume) {
		if (sound < 0) {
			return;
		}
		System.out.println("playing sound");
		Clip clip = clips.get(sound);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
	}

	@Override
	public int load(short[] data) {
		try {
			System.out.println("loading sound file ");
			Clip clip = AudioSystem.getClip();
			int index = clips.size();

			AudioFormat format =
			        new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100,
			                false);

			//clip.open(inputStream);
			clips.add(clip);
			return index;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
