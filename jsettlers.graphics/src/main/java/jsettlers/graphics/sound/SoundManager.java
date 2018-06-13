/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import go.graphics.sound.ISoundDataRetriever;
import go.graphics.sound.SoundPlayer;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.FileUtils;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.image.reader.bytereader.ByteReader;

/*
 * This class manages reading and playing of the sound file.
 * <p>
 * Some known sounds:
 * <p>
 *
 * 0 lumberjack
 * 1 (6 times): bricklayer <br>
 * 2 (3 times): digger <br>
 * 3 (twice): stonecutter <br>
 * 5: sawmiller <br>
 * 6/7 smith <br>
 * 8/9 and 12: farmer <br>
 * 13: fire
 * 14: dying pig <br>
 * 15/16/17: fisherman
 * 20: dockyard
 * 21: healer
 * 24: geologist
 * 25: bowman
 * 30: sword Soldier <br>
 * 31/32 (soldier ?) <br>
 * 33 Bowman <br>
 * 34 Pikeman
 * 35 soldier killed <br>
 * 36: falling tree <br>
 * 37/38 molten metal
 * 39: pigs <br>
 * 40/41: donkey <br>
 * 42: wind/mill: 5s <br>
 * 43/44: distillery
 * 45: charcoal burner coughing
 * 46-50 no sound
 * 51: trigger for building destruction
 * 56: lock <br>
 * 57-59: notification sounds<br>
 * 62: Ui klick <br>
 * 67: desert
 * 68, 68b: Sea <br>
 * 69, 69b: Bird <br>
 * 70, 70b: Bird
 * 71: Water (river) <br>
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
 * 92: small fire on wood or building <br>
 * 93: collapsing building
 * 100-110: Attacked (same sound?) ? <br>
 * 106: announcement of missing tool
 * 111: 112: gong, <br>
 * 113: (4 times): amazone killed
 * 116: refused center of work displacement
 * 117: bees
 *
 * @author michael
 */
public class SoundManager {

	private static final String SOUND_FILE_NAME = "Siedler3_00.dat";

	private static final int Z_STEPS_FOR_MAX_VOLUME = 50;
	private static final int SOUND_META_LENGTH = 16;
	private static final int SOUND_FILE_START = 0x24;
	private static final int SEQUENCE_N = 118;

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

	private static File lookupPath;

	private final SoundPlayer soundPlayer;
	public final Random random = new Random();

	/**
	 * The start positions of all the playable sounds.
	 */
	private int[][] soundStarts;
	private boolean initializing = false;
	private MapDrawContext map = null;
	private MapRectangle area = null;

	/**
	 * Creates a new sound manager.
	 *
	 * @param soundPlayer
	 * 		The soundPlayer to play sounds at.
	 */
	public SoundManager(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
		initialize();
	}

	/**
	 * Reads the start indexes of the sounds.
	 *
	 * @param reader
	 * 		The reader to read from.
	 * @return An array of start indexes for each sound and it's variants.
	 * @throws IOException
	 * 		If the file could not be read.
	 */
	protected static int[][] getSoundStarts(ByteReader reader) throws IOException {
		int[] sequenceHeaderStarts = new int[SEQUENCE_N];
		for (int i = 0; i < SEQUENCE_N; i++) {
			sequenceHeaderStarts[i] = reader.read32();
		}

		int[][] playerIds = new int[SEQUENCE_N][];
		for (int i = 0; i < SEQUENCE_N; i++) {
			reader.skipTo(sequenceHeaderStarts[i]);
			int alternativeCount = reader.read32();
			int[] starts = new int[alternativeCount];
			for (int j = 0; j < alternativeCount; j++) {
				starts[j] = reader.read32();
			}

			playerIds[i] = starts;
		}
		return playerIds;
	}

	/**
	 * Opens the sound file.
	 *
	 * @return The file reader.
	 * @throws IOException
	 * 		If the file could not be opened,
	 */
	protected static ByteReader openSoundFile() throws IOException {
		File sndFile = getSoundFile();

		if (sndFile == null) {
			throw new IOException("Sound file not found.");
		}

		RandomAccessFile randomAccessFile = new RandomAccessFile(sndFile, "r");
		ByteReader reader = new ByteReader(randomAccessFile);

		reader.assumeToRead(SOUND_FILE_MAGIC);

		reader.skipTo(SOUND_FILE_START);
		return reader;
	}

	private static File getSoundFile() {
		return FileUtils.getFileByNameIgnoringCase(lookupPath, SOUND_FILE_NAME);
	}

	/**
	 * Plays a given sound.
	 *
	 * @param soundId
	 * 		The sound id to play.
	 * @param volume
	 * 		The volume
	 */
	public void playSound(int soundId, float volume) {
		initialize();

		if (soundStarts != null && soundId >= 0 && soundId < SEQUENCE_N) {
			int[] alternatives = soundStarts[soundId];
			if (alternatives != null && alternatives.length > 0) {
				int rand = random.nextInt(alternatives.length);
				soundPlayer.playSound(alternatives[rand], volume, volume);
			}
		}
	}

	public void playSound(int soundId, float volume, ShortPoint2D position) {
		playSound(soundId, volume, position.x, position.y);
	}

	/**
	 * Plays a given sound at a given coordinate
	 *
	 * @param soundId
	 * 		The sound id to play
	 * @param volume
	 * 		The volume
	 * @param x
	 * 		The x coordinate of the sound
	 * @param y
	 * 		The y coordinate of the sound
	 */
	public void playSound(int soundId, float volume, int x, int y) {
		if (map == null || map.getVisibleStatus(x, y) <= CommonConstants.FOG_OF_WAR_EXPLORED) { // only play sounds when fog of war level is higher than explored
			return;
		}

		initialize();

		if (soundStarts != null && soundId >= 0 && soundId < SEQUENCE_N && area != null) {
			int[] alternatives = soundStarts[soundId];
			if (alternatives != null && alternatives.length > 0) {
				int rand = random.nextInt(alternatives.length);

				int maxA = area.getWidth(); // get screen area
				int maxB = area.getHeight();
				int b = y - area.getMinY();
				int a = x - area.getLineStartX(b);
				float leftVolume, rightVolume;

				if (a < 0) { // volume depending on position right or left
					leftVolume = 0;
					rightVolume = 0;
				} else if (a < maxA / 4) {
					leftVolume = volume * 4f * a / maxA;
					rightVolume = 0;
				} else if (a < 3 * maxA / 4) {
					leftVolume = volume * (2f * a / maxA - .5f);
					rightVolume = volume * (2f * (maxA - a) / maxA - .5f);
				} else if (a < maxA) {
					leftVolume = 0;
					rightVolume = volume * 4f * (maxA - a) / maxA;
				} else {
					leftVolume = 0;
					rightVolume = 0;
				}

				float distanceVolume = Z_STEPS_FOR_MAX_VOLUME;
				if (b < 0) { // volume depending on position up or down
					distanceVolume = 0;
				} else if (b < maxB / 4) {
					distanceVolume = 4f * Z_STEPS_FOR_MAX_VOLUME * b / maxB;
				} else if (b >= maxB) {
					distanceVolume = 0;
				} else if (b >= 3 * maxB / 4) {
					distanceVolume = 4f * Z_STEPS_FOR_MAX_VOLUME * (maxB - b) / maxB;
				}

				distanceVolume /= maxA; // volume depending on zoom level
				if (distanceVolume > 1) {
					distanceVolume = 1;
				}

				leftVolume *= distanceVolume;
				rightVolume *= distanceVolume;
				soundPlayer.playSound(alternatives[rand], leftVolume, rightVolume);
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
		new Thread(() -> {
			try {
				loadSounds();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}, "sound loader").start();
	}

	private void loadSounds() throws IOException {
		ByteReader reader = openSoundFile();
		this.soundStarts = getSoundStarts(reader);
		soundPlayer.setSoundDataRetriever(new SoundDataRetriever(reader));
	}

	/**
	 * Sets the sound file lookup path.
	 *
	 * @param lookupPath
	 * 		The file path.
	 */
	public static void setLookupPath(File lookupPath) {
		SoundManager.lookupPath = lookupPath;
	}

	/**
	 * This class wraps an open {@link ByteReader} to a {@link ISoundDataRetriever}.
	 *
	 * @author Michael Zangl
	 */
	private static class SoundDataRetriever implements ISoundDataRetriever {
		private final ByteReader reader;

		/**
		 * Create a new {@link SoundDataRetriever}.
		 *
		 * @param reader
		 * 		The byte reader.
		 */
		SoundDataRetriever(ByteReader reader) {
			this.reader = reader;
		}

		@Override
		public synchronized short[] getSoundData(int soundStart) throws IOException {
			return SoundManager.getSoundData(reader, soundStart);
		}
	}

	/**
	 * Reads the sound data from a byte reader.
	 *
	 * @param reader
	 * 		The reader to read.
	 * @param start
	 * 		The sound start position.
	 * @return The read sound data.
	 * @throws IOException
	 * 		If that sound could not be read.
	 */
	protected static short[] getSoundData(ByteReader reader, int start) throws IOException {
		reader.skipTo(start);

		int length = reader.read32() / 2 - SOUND_META_LENGTH;
		reader.read32();
		reader.read32(); // mostly 22050
		reader.read32(); // mostly 44100
		reader.read32();

		return loadSound(reader, length);
	}

	private static short[] loadSound(ByteReader reader, int length) throws IOException {
		if (length < 0) {
			return new short[0];
		}
		short[] data = new short[length];
		for (int i = 0; i < length; i++) {
			data[i] = (short) reader.read16signed();
		}

		return data;
	}

	public void setMap(MapDrawContext map) {
		this.map = map;
		this.area = map.getScreenArea();
	}
}
