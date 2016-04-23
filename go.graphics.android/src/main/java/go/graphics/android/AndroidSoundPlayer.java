/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package go.graphics.android;

import go.graphics.sound.ForgettingQueue;
import go.graphics.sound.ForgettingQueue.Sound;
import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AndroidSoundPlayer implements SoundPlayer {
	private static final int SAMPLERATE = 22050;

	private final ForgettingQueue<Integer> queue = new ForgettingQueue<Integer>();

	private ISoundDataRetriever soundDataRetriever;

	private boolean paused;

	public AndroidSoundPlayer(int parallelSounds) {
		ThreadGroup soundgroup = new ThreadGroup("soundplayer");
		for (int i = 0; i < parallelSounds; i++) {
			new Thread(soundgroup, new PlaySoundTask(), "soundplayer" + i).start();
		}
	}

	@Override
	public void playSound(int soundStart, float lvolume, float rvolume) {
		try {
			if (!paused) {
				queue.offer(soundStart, lvolume, rvolume);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	private class PlaySoundTask implements Runnable {
		@Override
		public void run() {
			int bufferSize = AudioTrack.getMinBufferSize(SAMPLERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
			AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
			track.play();
			try {
				while (true) {
					try {
						Sound<Integer> sound = queue.take();
						long start = System.currentTimeMillis();
						short[] data = soundDataRetriever.getSoundData(sound.getData());
						System.out.println("loading sound data took: " + (System.currentTimeMillis() - start) + " ms");

						track.setStereoVolume(sound.getLvolume(), sound.getRvolume());
						System.out.println("sound: playing " + data.length + " samples");
						track.write(data, 0, data.length);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				// exit
			}
		}

	}

	@Override
	public void setSoundDataRetriever(ISoundDataRetriever soundDataRetriever) {
		this.soundDataRetriever = soundDataRetriever;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
