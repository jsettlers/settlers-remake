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
package jsettlers.graphics.sound;

import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.startscreen.SettingsManager;

/**
 * This class manages reading and playing of the sound file.
 * <p>
 * Some known sounds:
 * <p>
 * 1 (6 times): knock a tree <br>
 * 2 (3 times): digg <br>
 * 3 (twice): knock stone <br>
 * 5: saw <br>
 * 6: smith <br>
 * 7: smith <br>
 * 12: farmer <br>
 * 14: donkey <br>
 * 30: sword Soldier <br>
 * 31/32 (soldier ?) <br>
 * 33 Bowman <br>
 * 35 soldier killed <br>
 * 36 building getting removed <br>
 * 39: pigs <br>
 * 40: donkey <br>
 * 41: donkey <br>
 * 42: wind/mill: 2.5s <br>
 * 56: lock <br>
 * 57-59: notification sounds<br>
 * 62: Ui klick <br>
 * 68, 68b: Sea <br>
 * 69, 69b: Bird <br>
 * 70, 70b: Bird 71: Water (river) <br>
 * 72 (+ alternaitves): moor <br>
 * 73: wind <br>
 * 74: crazy wind <br>
 * 75 (3 times): thunder <br>
 * 76 (2 times): rain <br>
 * 80: You are beeing attacked <br>
 * 81: Mill, <br>
 * 82: older mill, <br>
 * 83: even older mill <br>
 * 84: catapult <br>
 * 85: Arrow shooting <br>
 * 86 -90: canon shooting <br>
 * 91: fire <br>
 * 92: small fire <br>
 * 100 - 110: Attacked (same sound?) ? <br>
 * 111, 112: gong, <br>
 * 113 (4 times): kill (maya?)
 * 
 * @author michael
 */
public class SoundManager {
	private static final int SOUND_META_LENGTH = 16;

	private static final int SOUND_FILE_START = 0x24;

	private static final byte[] SOUND_FILE_MAGIC = new byte[] {
			0x44,
			0x15,
			0x01,
			0x00,
			0x02,
			0x00,
			0x00,
			0x00,
			0x00,
			0x00,
			0x00,
			0x00,
			0x1C,
			0x00,
			0x00,
			0x00
	};
	private static final int SEQUENCE_N = 118;
	/**
	 * Sound ID when we are attacked.
	 */
	public static final int NOTIFY_ATTACKED = 80;

	/**
	 * The lookup paths for the dat files.
	 */

	private final SoundPlayer player;

	private final Random random = new Random();

	private static ArrayList<File> lookupPaths = new ArrayList<File>();

	/**
	 * The start positions of all the playable sounds.
	 */
	private int[][] soundStarts;
	private boolean initializing = false;

	/**
	 * Creates a new sound manager.
	 * 
	 * @param player
	 *            The player to play sounds at.
	 */
	public SoundManager(SoundPlayer player) {
		this.player = player;
		initialize();
	}

	private void loadSounds() throws FileNotFoundException, IOException {
		ByteReader reader = openSoundFile();

		this.soundStarts = getSoundStarts(reader);
		player.setSoundDataRetriever(new SoundDataRetriever(reader));
	}

	/**
	 * Reads the start indexes of the sounds.
	 * 
	 * @param reader
	 *            The reader to read from.
	 * @return An array of start indexes for each sound and it's variants.
	 * @throws IOException
	 *             If the file could not be read.
	 */
	protected static int[][] getSoundStarts(ByteReader reader)
			throws IOException {
		int[] seqheaderstarts = new int[SEQUENCE_N];
		for (int i = 0; i < SEQUENCE_N; i++) {
			seqheaderstarts[i] = reader.read32();
		}

		int[][] playerids = new int[SEQUENCE_N][];
		for (int i = 0; i < SEQUENCE_N; i++) {
			reader.skipTo(seqheaderstarts[i]);
			int alternaitvecount = reader.read32();
			int[] starts = new int[alternaitvecount];
			for (int j = 0; j < alternaitvecount; j++) {
				starts[j] = reader.read32();
			}

			playerids[i] = starts;
		}
		return playerids;
	}

	/**
	 * Opens the sound file.
	 * 
	 * @return The file reader.
	 * @throws IOException
	 *             If the file could not be opened,
	 * @throws FileNotFoundException
	 *             If the file was not found.
	 */
	protected static ByteReader openSoundFile() throws IOException,
			FileNotFoundException {
		File sndfile = getSoundFile();

		if (sndfile == null) {
			throw new IOException("Sound file not found.");
		}

		RandomAccessFile randomAccessFile = new RandomAccessFile(sndfile, "r");
		ByteReader reader = new ByteReader(randomAccessFile);

		reader.assumeToRead(SOUND_FILE_MAGIC);

		reader.skipTo(SOUND_FILE_START);
		return reader;
	}

	private static File getSoundFile() {
		File sndfile = null;
		synchronized (lookupPaths) {
			for (File dir : lookupPaths) {
				File file = new File(dir, "Siedler3_00.dat");
				if (file.exists()) {
					sndfile = file;
					break;
				}
			}
		}
		return sndfile;
	}

	/**
	 * Plays a given sound.
	 * 
	 * @param soundid
	 *            The sound id to play.
	 * @param volume1
	 *            The volume for the left speaker.
	 * @param volume2
	 *            The volume for the right speaker.
	 */
	public void playSound(int soundid, float volume1, float volume2) {
		initialize();

		if (soundStarts != null && soundid >= 0 && soundid < SEQUENCE_N) {
			int[] alternatives = soundStarts[soundid];
			if (alternatives != null && alternatives.length > 0) {
				int rand = random.nextInt(alternatives.length);
				float volume = SettingsManager.getInstance().getVolume();
				player.playSound(alternatives[rand], volume1 * volume, volume2
						* volume);
			}
		}
	}

	private void initialize() {
		synchronized (this) {
			if (initializing) {
				return;
			}
			initializing = true;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					loadSounds();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, "sound loader").start();
	}

	/**
	 * Adds a sound file lookup path.
	 * 
	 * @param file
	 *            The file path.
	 */
	public static void addLookupPath(File file) {
		synchronized (lookupPaths) {
			lookupPaths.add(file);
		}
	}

	/**
	 * This class wraps an open {@link ByteReader} to a {@link ISoundDataRetriever}.
	 * 
	 * @author Michael Zangl
	 *
	 */
	protected static class SoundDataRetriever implements ISoundDataRetriever {

		private final ByteReader reader;

		/**
		 * Create a new {@link SoundDataRetriever}.
		 * 
		 * @param reader
		 *            The byte reader.
		 */
		public SoundDataRetriever(ByteReader reader) {
			this.reader = reader;
		}

		@Override
		public synchronized short[] getSoundData(int soundStart)
				throws IOException {
			return SoundManager.getSoundData(reader, soundStart);
		}
	}

	/**
	 * Reads the sound data from a byte reader.
	 * 
	 * @param reader
	 *            The reader to read.
	 * @param start
	 *            The sound start position.
	 * @return The read sound data.
	 * @throws IOException
	 *             If that sound could not be read.
	 */
	protected static short[] getSoundData(ByteReader reader, int start)
			throws IOException {
		reader.skipTo(start);

		int length = reader.read32() / 2 - SOUND_META_LENGTH;
		reader.read32();
		reader.read32(); // mostly 22050
		reader.read32(); // mostly 44100
		reader.read32();

		return loadSound(reader, length);
	}

	private static short[] loadSound(ByteReader reader, int length)
			throws IOException {
		if (length < 0) {
			return new short[0];
		}
		short[] data = new short[length];
		for (int i = 0; i < length; i++) {
			data[i] = (short) reader.read16signed();
		}

		return data;
	}
}
