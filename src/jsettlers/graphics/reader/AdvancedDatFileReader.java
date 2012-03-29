package jsettlers.graphics.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.MultiImageMap;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.Torso;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.reader.translator.DatBitmapTranslator;
import jsettlers.graphics.reader.translator.GuiTranslator;
import jsettlers.graphics.reader.translator.LandscapeTranslator;
import jsettlers.graphics.reader.translator.SettlerTranslator;
import jsettlers.graphics.reader.translator.ShadowTranslator;
import jsettlers.graphics.reader.translator.TorsoTranslator;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is an advanced dat file reader. It can read the file, but it only reads
 * needed sequences.
 * <p>
 * The format of a dat file is (all numbers in little endian):
 * <table>
 * <tr>
 * <td>Bytes 0..47:</td>
 * <td>Always the same</td>
 * </tr>
 * <tr>
 * <td>Bytes 48 .. 51:</td>
 * <td>file size</td>
 * </tr>
 * <tr>
 * <td>Bytes 52 .. 55:</td>
 * <td>Unknown Pointer, seems not to be a sequence.</td>
 * </tr>
 * <tr>
 * <td>Bytes 56 .. 59:</td>
 * <td>Start position of landscape sequence pointers.</td>
 * </tr>
 * <tr>
 * <td>Bytes 60 .. 63:</td>
 * <td>Unneeded Pointer</td>
 * </tr>
 * <tr>
 * <td>Bytes 64 .. 67:</td>
 * <td>Settler/Building/.. pointers</td>
 * </tr>
 * <tr>
 * <td>Bytes 68 .. 71:</td>
 * <td>Torso pointers</td>
 * </tr>
 * <tr>
 * <td>Bytes 72 .. 75:</td>
 * <td>Position after above</td>
 * </tr>
 * <tr>
 * <td>Bytes 76 .. 79:</td>
 * <td>Position after above</td>
 * </tr>
 * <tr>
 * <td>Bytes 80 .. 83:</td>
 * <td>Something, seems to be like 52..55</td>
 * </tr>
 * <tr>
 * <td>Bytes 84 .. 87:</td>
 * <td>{04 19 00 00}</td>
 * </tr>
 * <tr>
 * <td>Bytes 88 .. 91:</td>
 * <td>{0c 00 00 00}</td>
 * </tr>
 * <tr>
 * <td>Bytes 92 .. 95:</td>
 * <td>{00 00 00 00}</td>
 * </tr>
 * <tr>
 * <td>e.g. Bytes 102 .. 103:</td>
 * <td>Image count of image sequences for one type</td>
 * </tr>
 * <tr>
 * <td>e.g. Bytes 104 .. 107:</td>
 * <td>Start position of fist image sequence list.</td>
 * </tr>
 * </table>
 * 
 * @author michael
 */
public class AdvancedDatFileReader implements DatFileSet {
	/**
	 * Every dat file seems to have to start with this sequence.
	 */
	private static final byte[] FILE_START = {
	        0x04,
	        0x13,
	        0x04,
	        0x00,
	        0x0c,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x54,
	        0x00,
	        0x00,
	        0x00,
	        0x20,
	        0x00,
	        0x00,
	        0x00,
	        0x40,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x10,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x7c,
	        0x00,
	        0x00,
	        (byte) 0xe0,
	        0x03,
	        0x00,
	        0x00,
	        0x1f,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00
	};

	private static final byte[] FILE_HEADER_END = {
	        0x04,
	        0x19,
	        0x00,
	        0x00,
	        0x0c,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00,
	        0x00
	};

	static final int SEQUENCE_TYPE_COUNT = 6;

	static final int ID_SETTLERS = 0x106;

	static final int ID_TORSOS = 0x3112;

	static final int ID_LANDSCAPE = 0x2412;

	static final int ID_SHADOWS = 0x5982;

	// fullscreen images
	static final int ID_GUIS = 0x11306;

	public static final DatBitmapTranslator<SettlerImage> SETTLER_TRANSLATOR =
	        new SettlerTranslator();

	public static final DatBitmapTranslator<Torso> TORSO_TRANSLATOR =
	        new TorsoTranslator();

	public static final DatBitmapTranslator<LandscapeImage> LANDSCAPE_TRANSLATOR =
	        new LandscapeTranslator();

	static final DatBitmapTranslator<ShadowImage> SHADOW_TRANSLATOR =
	        new ShadowTranslator();

	static final DatBitmapTranslator<GuiImage> GUI_TRANSLATOR =
	        new GuiTranslator();

	private ByteReader reader = null;
	private final File file;

	/**
	 * This is a list of file positions where the settler sequences start.
	 */
	private int[] settlerstarts;

	/**
	 * A list of loaded settler sequences.
	 */
	private Sequence<Image>[] settlersequences = null;
	/**
	 * An array with the same length as settlers.
	 */
	private int[] torsostarts;
	/**
	 * An array with the same length as settlers.
	 */
	private int[] shadowstarts;

	/**
	 * A list of loaded landscae images.
	 */
	private LandscapeImage[] landscapeimages = null;
	private Sequence<LandscapeImage> landscapesequence =
	        new LandscapeImageSequence();
	private int[] landscapestarts;

	private GuiImage[] guiimages = null;
	private int[] guistarts;
	private Sequence<GuiImage> guisequence = new GuiImageSequence();

	private final SequenceList<Image> directSettlerList;

	private static final byte[] START = new byte[] {
	        0x02, 0x14, 0x00, 0x00, 0x08, 0x00, 0x00
	};

	public AdvancedDatFileReader(File file) {
		this.file = file;
		directSettlerList = new DirectSettlerSequenceList();
	}

	/**
	 * Initializes the reader, reads the index.
	 */
	@SuppressWarnings("unchecked")
	public void initialize() {
		try {
			try {
				reader = new ByteReader(new RandomAccessFile(file, "r"));
				initFromReader(file, reader);

			} catch (IOException e) {
				if (reader != null) {
					reader.close();
					reader = null;
				}
				throw e;
			}
		} catch (Exception e) {
		}
		initializeNullFile();

		landscapeimages = new LandscapeImage[landscapestarts.length];

		guiimages = new GuiImage[guistarts.length];

		settlersequences = new Sequence[settlerstarts.length];

		int torsodifference = settlerstarts.length - torsostarts.length;
		if (torsodifference != 0) {
			int[] oldtorsos = torsostarts;
			torsostarts = new int[settlerstarts.length];
			for (int i = 0; i < oldtorsos.length; i++) {
				torsostarts[i + torsodifference] = oldtorsos[i];
			}
			for (int i = 0; i < torsodifference; i++) {
				torsostarts[i] = -1;
			}
		}

		int shadowdifference = settlerstarts.length - shadowstarts.length;
		if (shadowstarts.length < settlerstarts.length) {
			int[] oldshadows = shadowstarts;
			shadowstarts = new int[settlerstarts.length];
			for (int i = 0; i < oldshadows.length; i++) {
				shadowstarts[i + shadowdifference] = oldshadows[i];
			}
			for (int i = 0; i < shadowdifference; i++) {
				torsostarts[i] = -1;
			}
		}
	}

	private void initFromReader(File file, ByteReader reader)
	        throws IOException {
		int[] sequenceIndexStarts =
		        readSequenceIndexStarts(file.length(), reader);

		for (int i = 0; i < SEQUENCE_TYPE_COUNT; i++) {
			try {
				readSequencesAt(reader, sequenceIndexStarts[i]);
			} catch (IOException e) {
				System.err.println("Error while loading sequence" + ": "
				        + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static int[] readSequenceIndexStarts(long filelength,
	        ByteReader reader) throws IOException {
		reader.assumeToRead(FILE_START);
		int fileSize = reader.read32();

		if (fileSize != filelength) {
			throw new IOException(
			        "The length stored in the dat file is not the file length.");
		}

		// ignore unknown bytes.
		reader.read32();

		// read settler image pointer
		int[] sequenceIndexStarts = new int[SEQUENCE_TYPE_COUNT];
		for (int i = 0; i < SEQUENCE_TYPE_COUNT; i++) {
			sequenceIndexStarts[i] = reader.read32();
		}

		// ignore unknown bytes.
		reader.read32();
		reader.assumeToRead(FILE_HEADER_END);
		return sequenceIndexStarts;
	}

	/**
	 * reads all sequence starts at a given position.
	 * <p>
	 * Does not align torsos and shadows.
	 * 
	 * @param reader
	 *            The reader to read from.
	 * @param sequenceIndexStart
	 *            The position to start at.
	 * @param type
	 *            The type of the sequence
	 * @throws IOException
	 *             if an read error occurred.
	 */
	private void readSequencesAt(ByteReader reader, int sequenceIndexStart)
	        throws IOException {
		// read data index 0
		reader.skipTo(sequenceIndexStart);

		int sequenceType = reader.read32();

		int byteCount = reader.read16();
		int pointerCount = reader.read16();

		if (byteCount != pointerCount * 4 + 8) {
			throw new IOException("Sequence index block length ("
			        + pointerCount + ") and " + "bytecount (" + byteCount
			        + ") are not consistent.");
		}

		int[] sequenceIndexPointers = new int[pointerCount];
		for (int i = 0; i < pointerCount; i++) {
			sequenceIndexPointers[i] = reader.read32();
		}

		if (sequenceType == ID_SETTLERS) {
			settlerstarts = sequenceIndexPointers;
		} else if (sequenceType == ID_TORSOS) {
			torsostarts = sequenceIndexPointers;

		} else if (sequenceType == ID_LANDSCAPE) {
			landscapestarts = sequenceIndexPointers;

		} else if (sequenceType == ID_SHADOWS) {
			shadowstarts = sequenceIndexPointers;

		} else if (sequenceType == ID_GUIS) {
			guistarts = sequenceIndexPointers;
		}
	}

	private void initializeNullFile() {
		if (settlerstarts == null) {
			settlerstarts = new int[0];
		}
		if (torsostarts == null) {
			torsostarts = new int[0];
		}
		if (shadowstarts == null) {
			shadowstarts = new int[0];
		}
		if (landscapestarts == null) {
			landscapestarts = new int[0];
		}
		if (guistarts == null) {
			guistarts = new int[0];
		}
	}

	private void initializeIfNeeded() {
		if (settlersequences == null) {
			initialize();
		}
	}

	@Override
	public SequenceList<Image> getSettlers() {
		return directSettlerList;
	}

	private static final Sequence<Image> NULL_SETTLER_SEQUENCE =
	        new ArraySequence<Image>(new SettlerImage[0]);

	private class DirectSettlerSequenceList implements SequenceList<Image> {

		@Override
		public Sequence<Image> get(int index) {
			initializeIfNeeded();
			if (settlersequences[index] == null) {
				settlersequences[index] = NULL_SETTLER_SEQUENCE;
				try {
					System.out.println("Loading Sequence number " + index);

					loadSettlers(index);
				} catch (Exception e) {
				}
			}
			return settlersequences[index];
		}

		@Override
		public int size() {
			initializeIfNeeded();
			return settlersequences.length;
		}
	}

	private synchronized void loadSettlers(int index) throws IOException {

		int position = settlerstarts[index];
		long[] framePositions = readSequenceHeader(position);

		SettlerImage[] images = new SettlerImage[framePositions.length];
		for (int i = 0; i < framePositions.length; i++) {
			reader.skipTo(framePositions[i]);
			images[i] = DatBitmapReader.getImage(SETTLER_TRANSLATOR, reader);
		}

		int torsoposition = torsostarts[index];
		if (torsoposition >= 0) {
			long[] torsoPositions = readSequenceHeader(torsoposition);
			for (int i = 0; i < torsoPositions.length
			        && i < framePositions.length; i++) {
				reader.skipTo(torsoPositions[i]);
				Torso torso =
				        DatBitmapReader.getImage(TORSO_TRANSLATOR, reader);
				images[i].setTorso(torso);
			}
		}

		settlersequences[index] = new ArraySequence<Image>(images);
	}

	private long[] readSequenceHeader(int position) throws IOException {
		reader.skipTo(position);

		reader.assumeToRead(START);
		int frameCount = reader.read8();

		long[] framePositions = new long[frameCount];
		for (int i = 0; i < frameCount; i++) {
			framePositions[i] = reader.read32() + position;
		}
		return framePositions;
	}

	@Override
	public Sequence<LandscapeImage> getLandscapes() {
		return landscapesequence;
	}

	@Override
	public Sequence<GuiImage> getGuis() {
		return guisequence;
	}

	/**
	 * This landscape image list loads the landscape images.
	 * 
	 * @author michael
	 */
	private class LandscapeImageSequence implements Sequence<LandscapeImage> {
		/**
		 * Forces a get of the image.
		 */
		@Override
		public LandscapeImage getImage(int index) {
			initializeIfNeeded();
			if (landscapeimages[index] == null) {
				loadLandscapeImage(index);
			}
			return landscapeimages[index];
		}

		@Override
		public int length() {
			initializeIfNeeded();
			return landscapeimages.length;
		}

		@Override
		public SingleImage getImageSafe(int index) {
			initializeIfNeeded();
			if (index < 0 || index >= length()) {
				return NullImage.getInstance();
			} else {
				if (landscapeimages[index] == null) {
					loadLandscapeImage(index);
				}
				return landscapeimages[index];
			}
		}
	}

	public ByteReader getReaderForLandscape(int index) throws IOException {
		initializeIfNeeded();
		reader.skipTo(landscapestarts[index]);
		return reader;
	}

	private void loadLandscapeImage(int index) {
		try {
			reader.skipTo(landscapestarts[index]);
			LandscapeImage image =
			        DatBitmapReader.getImage(LANDSCAPE_TRANSLATOR, reader);
			landscapeimages[index] = image;
		} catch (IOException e) {
			landscapeimages[index] = NullImage.getForLandscape();
		}
	}

	/**
	 * This landscape image list loads the landscape images.
	 * 
	 * @author michael
	 */
	private class GuiImageSequence implements Sequence<GuiImage> {
		/**
		 * Forces a get of the image.
		 */
		@Override
		public GuiImage getImage(int index) {
			initializeIfNeeded();
			if (guiimages[index] == null) {
				loadGuiImage(index);
			}
			return guiimages[index];
		}

		@Override
		public int length() {
			initializeIfNeeded();
			return guiimages.length;
		}

		@Override
		public SingleImage getImageSafe(int index) {
			initializeIfNeeded();
			if (index < 0 || index >= length()) {
				return NullImage.getInstance();
			} else {
				if (guiimages[index] == null) {
					loadGuiImage(index);
				}
				return guiimages[index];
			}
		}
	}

	private void loadGuiImage(int index) {
		try {
			reader.skipTo(guistarts[index]);
			GuiImage image = DatBitmapReader.getImage(GUI_TRANSLATOR, reader);
			guiimages[index] = image;
		} catch (IOException e) {
			guiimages[index] = NullImage.getForGui();
		}
	}

	public long[] getSettlerPointers(int seqindex) throws IOException {
		initializeIfNeeded();
		return readSequenceHeader(settlerstarts[seqindex]);
	}

	public long[] getTorsoPointers(int seqindex) throws IOException {
		initializeIfNeeded();
		int position = torsostarts[seqindex];
		if (position >= 0) {
			return readSequenceHeader(position);
		} else {
			return null;
		}
	}

	/**
	 * Gets a reader positioned at the given settler
	 * 
	 * @param pointer
	 * @return
	 * @throws IOException
	 */
	public ByteReader getReaderForPointer(long pointer) throws IOException {
		initializeIfNeeded();
		reader.skipTo(pointer);
		return reader;
	}

	public void generateImageMap(int width, int height, int[] sequences,
	        String id) throws IOException {
		initializeIfNeeded();

		MultiImageMap map = new MultiImageMap(width, height, id);
		if (!map.hasCache()) {
			map.addSequences(this, sequences, settlersequences);
			map.writeCache();
		}
	}
}