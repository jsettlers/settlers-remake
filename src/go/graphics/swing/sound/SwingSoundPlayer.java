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
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;

import go.graphics.sound.SoundPlayer;

public class SwingSoundPlayer implements SoundPlayer {
	private static final int BUFFER_SIZE = 4048;
	ArrayList<Clip> clips = new ArrayList<Clip>();

	public SwingSoundPlayer() {

	}

	@Override
	public void playSound(int sound, float lvolume, float rvolume) {
		if (sound < 0 || sound > clips.size()) {
			return;
		}
		System.out.println("playing sound");
		Clip clip = clips.get(sound);
	}

	@Override
	public int load(short[] data) {
		try {
			System.out.println("loading sound file ");
			int index = clips.size();

			AudioFormat format =
			        new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2,
			                44100, false);
			
			Line.Info info = new Line.Info(SourceDataLine.class);

			SourceDataLine dataLine =
			        (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(format, BUFFER_SIZE);

			
			//Sound playing code
			dataLine.start();

			byte[] buffer = new byte[BUFFER_SIZE];
			for (int i = 0; i < BUFFER_SIZE / 2 && i < data.length; i++) {
				buffer[i] = (byte) data[i];
				buffer[i + 1] = (byte) (data[i] >> 8);
			}
			dataLine.write(buffer, 0, buffer.length);
			

			return index;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
