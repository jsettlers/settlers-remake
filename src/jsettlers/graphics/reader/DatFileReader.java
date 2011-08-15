package jsettlers.graphics.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.image.Torso;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.reader.translator.DatBitmapTranslator;
import jsettlers.graphics.reader.translator.GuiTranslator;
import jsettlers.graphics.reader.translator.LandscapeTranslator;
import jsettlers.graphics.reader.translator.SettlerTranslator;
import jsettlers.graphics.reader.translator.ShadowTranslator;
import jsettlers.graphics.reader.translator.TorsoTranslator;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is a reader that reads a dat file.
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
public class DatFileReader implements DatFileSet {
	/**
	 * Every dat file seems to have to start with this sequence.
	 */
	private static final byte[] FILE_START =
	        { 0x04, 0x13, 0x04, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
	                0x00, 0x54, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x40,
	                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00,
	                0x00, 0x00, 0x7c, 0x00, 0x00, (byte) 0xe0, 0x03, 0x00,
	                0x00, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static final byte[] FILE_HEADER_END =
	        { 0x04, 0x19, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
	                0x00 };

	private static final int SEQUENCE_TYPE_COUNT = 6;

	private static final int ID_SETTLERS = 0x106;

	private static final int ID_TORSOS = 0x3112;

	private static final int ID_LANDSCAPE = 0x2412;

	private static final int ID_SHADOWS = 0x5982;

	// fullscreen images
	private static final int ID_GUIS = 0x11306;

	private static final DatBitmapTranslator<SettlerImage> SETTLER_TRANSLATOR =
	        new SettlerTranslator();

	private static final DatBitmapTranslator<Torso> TORSO_TRANSLATOR =
	        new TorsoTranslator();

	private static final DatBitmapTranslator<LandscapeImage> LANDSCAPE_TRANSLATOR =
	        new LandscapeTranslator();

	private static final DatBitmapTranslator<ShadowImage> SHADOW_TRANSLATOR =
	        new ShadowTranslator();

	private static final DatBitmapTranslator<GuiImage> GUI_TRANSLATOR =
	        new GuiTranslator();

	private ArrayList<Sequence<SettlerImage>> settlerSequences =
	        new ArrayList<Sequence<SettlerImage>>();

	private ArrayList<Sequence<Torso>> torsoSequences =
	        new ArrayList<Sequence<Torso>>();

	private ArrayList<Sequence<ShadowImage>> shadowSequences =
	        new ArrayList<Sequence<ShadowImage>>();


	private List<Sequence<SettlerImage>> unmodifiableSettlers;
	private List<Sequence<Torso>> unmodifiableTorsos;
	private List<Sequence<LandscapeImage>> unmodifiableLandscapes;
	private List<Sequence<GuiImage>> unmodifiableGuis;

	private List<Sequence<ShadowImage>> unmodifiableShadows;

	/**
	 * Creates a new dat file reader for the given file.
	 * 
	 * @param file
	 *            The file to read from.
	 * @throws IOException
	 *             If an read error occurred.
	 */
	public DatFileReader(File file) throws IOException {
		// System.out.println("reading file: " + file);
		ByteReader reader = null;
		try {
			reader = new ByteReader(new FileInputStream(file));
			readFromReader(file, reader);

		} catch (IOException e) {
			if (reader != null) {
				reader.close();
			}
			throw e;
		}
		// reader.debug();

		addTorsosToSettlers();

		this.unmodifiableSettlers =
		        Collections.unmodifiableList(this.settlerSequences);
		this.unmodifiableTorsos =
		        Collections.unmodifiableList(this.torsoSequences);
		this.unmodifiableShadows =
		        Collections.unmodifiableList(this.shadowSequences);
	}

	/**
	 * Adds a link to the matching torso to each settler that has one.
	 */
	private void addTorsosToSettlers() {
		int torsoOffset =
		        this.settlerSequences.size() - this.torsoSequences.size();
		for (int i = 0; i < this.torsoSequences.size(); i++) {
			Sequence<Torso> torsos = this.torsoSequences.get(i);
			Sequence<SettlerImage> settlers =
			        this.settlerSequences.get(torsoOffset + i);
			setTorsoSequenceForSettlers(settlers, torsos);
		}
	}

	private void setTorsoSequenceForSettlers(Sequence<SettlerImage> settlers,
	        Sequence<Torso> torsos) {
		for (int i = 0; i < settlers.length(); i++) {
			SettlerImage image = settlers.getImage(i);
			image.setTorso(torsos.getImage(i));
		}
	}

	private void readFromReader(File file, ByteReader reader)
	        throws IOException {

		int[] sequenceIndexStarts =
		        readSequenceIndexStarts(file.length(), reader);

		for (int i = 0; i < SEQUENCE_TYPE_COUNT; i++) {
			// System.out.println("Loading sequence stream " + i);
			try {
				readSequencesAt(reader, sequenceIndexStarts[i]);
			} catch (IOException e) {
				System.err.println("Error while loading sequence"
				        + ": " + e.getMessage());
			}
		}
		reader.close();
	}

	private int[] readSequenceIndexStarts(long filelength, ByteReader reader)
	        throws IOException {
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
	 * reads all sequences at a given position.
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
			this.settlerSequences =
			        getSequenceList(reader, sequenceIndexPointers,
			                SETTLER_TRANSLATOR);
		} else if (sequenceType == ID_TORSOS) {
			this.torsoSequences =
			        getSequenceList(reader, sequenceIndexPointers,
			                TORSO_TRANSLATOR);

		} else if (sequenceType == ID_LANDSCAPE) {
			this.unmodifiableLandscapes =
			        getLandscapeImageList(reader, sequenceIndexPointers);

		} else if (sequenceType == ID_SHADOWS) {
			this.shadowSequences =
			        getSequenceList(reader, sequenceIndexPointers,
			                SHADOW_TRANSLATOR);

		} else if (sequenceType == ID_GUIS) {
			this.unmodifiableGuis =
			        getGuiImageList(reader, sequenceIndexPointers);
		}
	}

	
	/**
	 * Loads a list of landscape images as list of sequences.
	 * @param reader The reader to read from.
	 * @param imagePointers The positions of the images.
	 * @return The sequences. 
	 * @throws IOException if an op error occured.
	 */
    @SuppressWarnings({ "cast", "unchecked" })
    private List<Sequence<LandscapeImage>> getLandscapeImageList(
            ByteReader reader, int[] imagePointers) throws IOException {
		Sequence<LandscapeImage>[] sequences = (Sequence<LandscapeImage>[]) new Sequence[imagePointers.length];
	    for (int i = 0; i < sequences.length; i++) {
	    	reader.skipTo(imagePointers[i]);
	    	LandscapeImage image = DatBitmapReader.getImage(LANDSCAPE_TRANSLATOR, reader);
	    	sequences[i] = new Sequence<LandscapeImage>(new LandscapeImage[] {image});
	    }
	    return Arrays.asList(sequences);
    }

    @SuppressWarnings({ "cast", "unchecked" })
    private List<Sequence<GuiImage>> getGuiImageList(
            ByteReader reader, int[] imagePointers) throws IOException {
		Sequence<GuiImage>[] sequences = (Sequence<GuiImage>[]) new Sequence[imagePointers.length];
	    for (int i = 0; i < sequences.length; i++) {
	    	reader.skipTo(imagePointers[i]);
	    	GuiImage image = DatBitmapReader.getImage(GUI_TRANSLATOR, reader);
	    	sequences[i] = new Sequence<GuiImage>(new GuiImage[] {image});
	    }
	    return Arrays.asList(sequences);
    }
	private <T extends Image> ArrayList<Sequence<T>> getSequenceList(
	        ByteReader reader, int[] sequenceStarts,
	        DatBitmapTranslator<T> translator) throws IOException {
		ArrayList<Sequence<T>> sequences =
		        new ArrayList<Sequence<T>>(sequenceStarts.length);
		for (int pointer : sequenceStarts) {
			Sequence<T> sequence = getSequenceAt(reader, pointer, translator);

			sequences.add(sequence);
		}
		return sequences;
	}

	private static final byte[] START =
	        new byte[] { 0x02, 0x14, 0x00, 0x00, 0x08, 0x00, 0x00 };

	/**
	 * Creates the frame sequence by reading the data from the reader.
	 * <p>
	 * The data format must be:
	 * <p>
	 * The first 7 bytes have to be equal to: 0x02, 0x14, 0x00, 0x00, 0x08,
	 * 0x00, 0x00
	 * <p>
	 * The next byte must be the number of frames that follow.
	 * 
	 * @param reader
	 * @param position
	 * @param translator
	 * @throws IOException
	 * @throws IOException
	 *             if an io error occurred
	 */
	@SuppressWarnings("unchecked")
	private <T extends Image> Sequence<T> getSequenceAt(ByteReader reader,
	        int position, DatBitmapTranslator<T> translator) throws IOException {
		reader.skipTo(position);

		reader.assumeToRead(START);
		int frameCount = reader.read8();

		long[] framePositions = new long[frameCount];
		for (int i = 0; i < frameCount; i++) {
			framePositions[i] = reader.read32() + position;
		}

		T[] images = (T[]) new Image[frameCount];
		for (int i = 0; i < frameCount; i++) {
			reader.skipTo(framePositions[i]);
			T image = DatBitmapReader.getImage(translator, reader);
			images[i] = image;
		}

		return new Sequence<T>(images);
	}

	@Override
	public List<Sequence<Torso>> getTorsos() {
		return this.unmodifiableTorsos;
	}

	@Override
	public List<Sequence<SettlerImage>> getSettlers() {
		return this.unmodifiableSettlers;
	}

	@Override
	public List<Sequence<LandscapeImage>> getLandscapes() {
		return this.unmodifiableLandscapes;
	}

	@Override
	public List<Sequence<GuiImage>> getGuis() {
		return this.unmodifiableGuis;
	}

	/**
	 * Gets the dat file set for this file.
	 * 
	 * @return The file set.
	 */
	public DatFileSet getDatFileSet() {
		return this;
	}

	@Override
	public List<Sequence<ShadowImage>> getShadows() {
		return this.unmodifiableShadows;
	}
}
