package jsettlers.common.music;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

/* dependencies for .ogg and .mp3 files:
 * ogg libraries: vorbisspi1.0.2.jar, tritonus_share.jar, jorbis-0.0.15.jar, jogg-0.0.7.jar
 * mp3 libraries: jl1.0.1.jar, tritonus_share.jar, mp3spi1.9.5.jar
 *
 * gradle takes care of dependencies
 */

/**
 *
 * Supports .mp3 and .ogg music file streams
 *
 * dependencies for .ogg and .mp3 files: ogg libraries: vorbisspi1.0.2.jar, tritonus_share.jar, jorbis-0.0.15.jar, jogg-0.0.7.jar mp3 libraries: jl1.0.1.jar, tritonus_share.jar, mp3spi1.9.5.jar
 *
 * gradle takes care of dependencies
 *
 * @author MarviMarv
 */
public final class MusicPlayer {
	private SourceDataLine line;

	public MusicPlayer() {
		line = null;
	}

	public void play(final String filePath) {
		play(filePath, 100);
	}

	public void play(final String filePath, final int volume) {
		final File file = new File(filePath);

		try (final AudioInputStream in = getAudioInputStream(file)) {

			final AudioFormat outFormat = getOutFormat(in.getFormat());
			final Info info = new Info(SourceDataLine.class, outFormat);

			try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {

				if (line != null) {
					this.line = line;

					line.open(outFormat);
					setVolume(volume);

					line.start();
					stream(getAudioInputStream(outFormat, in), line);
					line.drain();
					line.stop();
				}
			}

		} catch (UnsupportedAudioFileException
				| LineUnavailableException
				| IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private AudioFormat getOutFormat(final AudioFormat inFormat) {
		final int ch = inFormat.getChannels();
		final float rate = inFormat.getSampleRate();
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}

	private void stream(final AudioInputStream in, final SourceDataLine line)
			throws IOException {
		final byte[] buffer = new byte[65536];
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, n);
		}
	}

	public void stop() {
		if (this.line != null) {
			line.stop();
		}
	}

	public void setVolume(final int volume) {
		if (this.line != null) {
			try {
				FloatControl gainControl = (FloatControl) this.line.getControl(FloatControl.Type.MASTER_GAIN);
				BooleanControl muteControl = (BooleanControl) this.line.getControl(BooleanControl.Type.MUTE);
				if (volume == 0) {
					muteControl.setValue(true);
				} else {
					muteControl.setValue(false);
					gainControl.setValue((float) (Math.log(volume / 100d) / Math.log(10.0) * 20.0));
				}
			} catch (Exception ex) {
				System.out.println("unable to set the volume to the provided source");
			}
		}
	}
}
