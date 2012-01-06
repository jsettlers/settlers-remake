package jsettlers.graphics.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import jsettlers.graphics.reader.bytereader.ByteReader;
import go.graphics.sound.SoundPlayer;

public class SoundManager {

	private static final int SEQUENCE_N = 118;
	private final SoundPlayer player;

	public SoundManager(SoundPlayer player) {
		this.player = player;
		try {
			this.loadSounds();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The lookup paths for the dat files.
	 */
	private static ArrayList<File> lookupPaths = new ArrayList<File>();
	private int[] playerids;
	private boolean initialized = false;

	private void loadSounds() throws FileNotFoundException, IOException {
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

		if (sndfile == null) {
			throw new IOException("Sound file not found.");
		}

		ByteReader reader = new ByteReader(new RandomAccessFile(sndfile, "r"));

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

		//int filesize = reader.read32();

		reader.skipTo(0x24);
		int[] seqheaderstarts = new int[SEQUENCE_N];
		for (int i = 0; i < SEQUENCE_N; i++) {
			seqheaderstarts[i] = reader.read32();
		}

		playerids = new int[SEQUENCE_N];
		for (int i = 0; i < SEQUENCE_N; i++) {
			reader.skipTo(seqheaderstarts[i]);
			reader.read32();
			int leftstart = reader.read32() + 20;
			int rightstart = reader.read32() + 20;
			int length = (rightstart - leftstart - 20) / 2;

			playerids[i] = player.load(loadSound(reader, leftstart, rightstart, length));
		}
	}
	
	private static short[] loadSound(ByteReader reader, int leftstart, int rightstart, int length) throws IOException {
		if (length < 0) {
			return new short[0];
		}
		short[] data = new short[length * 2];
		reader.skipTo(leftstart);
		for (int i = 0; i < length; i++) {
			data[i * 2] = (short) reader.read16signed();
		}

		reader.skipTo(rightstart);
		for (int i = 0; i < length; i++) {
			data[i * 2 + 1] = (short) reader.read16signed();
		}
		
		return data;
	}

	public void playSound(int soundid) {
		initialize();

		if (playerids != null && soundid >= 0 && soundid < SEQUENCE_N) {
			player.playSound(playerids[soundid], 1, 1);
		}
	}

	private void initialize() {
	    if (!initialized) {
			try {
				this.loadSounds();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			initialized = true;
		}
    }

	public static void addLookupPath(File file) {
		synchronized (lookupPaths) {
			lookupPaths.add(file);
		}
	}
}
