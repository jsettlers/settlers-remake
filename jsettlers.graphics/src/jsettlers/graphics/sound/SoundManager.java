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
	private static final int SEQUENCE_N = 118;
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

	public SoundManager(SoundPlayer player) {
		this.player = player;
		initialize();
	}

	private void loadSounds() throws FileNotFoundException, IOException {
		ByteReader reader = openSoundFile();

		this.soundStarts = getSoundStarts(reader);
		player.setSoundDataRetriever(new SoundDataRetriever(reader));
	}

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

	protected static ByteReader openSoundFile() throws IOException,
			FileNotFoundException {
		File sndfile = getSoundFile();

		if (sndfile == null) {
			throw new IOException("Sound file not found.");
		}

		RandomAccessFile randomAccessFile = new RandomAccessFile(sndfile, "r");
		ByteReader reader = new ByteReader(randomAccessFile);

		reader.assumeToRead(new byte[] {
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
		});

		reader.skipTo(0x24);
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

	public static void addLookupPath(File file) {
		synchronized (lookupPaths) {
			lookupPaths.add(file);
		}
	}

	protected static class SoundDataRetriever implements ISoundDataRetriever {

		private final ByteReader reader;

		public SoundDataRetriever(ByteReader reader) {
			this.reader = reader;
		}

		@Override
		public synchronized short[] getSoundData(int soundStart)
				throws IOException {
			return SoundManager.getSoundData(reader, soundStart);
		}
	}

	protected static short[] getSoundData(ByteReader reader, int start)
			throws IOException {
		reader.skipTo(start);

		int length = reader.read32() / 2 - 16;
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
