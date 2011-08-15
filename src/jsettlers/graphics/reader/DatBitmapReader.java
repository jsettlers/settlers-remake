package jsettlers.graphics.reader;

import java.io.IOException;
import java.nio.ShortBuffer;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.ImageDataPrivider;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.reader.translator.DatBitmapTranslator;
import jsettlers.graphics.reader.translator.HeaderType;

/**
 * This class is capable of reading an image from the given stram.
 * 
 * @param <T>
 *            The image type.
 * @author michael
 */
public final class DatBitmapReader<T extends Image> implements
        ImageDataPrivider {

	private int width;
	private int height;
	private int offsetX;
	private int offsetY;
	private final short[] data;

	/**
	 * Creates a new reader that starts to read fom the given bytereader at its
	 * current position and uses the translator to convert the image.
	 * 
	 * @param translator
	 *            The translator that translates the image data.
	 * @param reader
	 *            The reader to read from.
	 * @throws IOException
	 *             If an io error occurred.
	 */
	private DatBitmapReader(DatBitmapTranslator<T> translator, ByteReader reader)
	        throws IOException {
		this.data = uncompressImage(reader, translator);
	}

	/**
	 * Assumes that there is an iamge starting at the beginning of reader, and
	 * reads its contents and creates the image from the stream of compressed
	 * data.
	 * <p>
	 * The data format is as follows:
	 * <p>
	 * The first bytes are 0x0c, 0x0, 0x0, 0x0
	 * <p>
	 * The next short (little endian) is the width of the image. <br>
	 * The next short the height. <br>
	 * The next (signed) short the x offset. <br>
	 * The next (signed) short the y offset. <br>
	 * <p>
	 * If the alignment of the next short is uneven, then a 0-byte for padding
	 * is inserted.
	 * <p>
	 * Then the image data starts with the first meta short. <br>
	 * A meta short: <br>
	 * The first bit states that after drawing the line that follows this short,
	 * a linebreak should be inserted. <br>
	 * The next 7 bit state the number of pixels that should be skipped before
	 * drawing the line. <br>
	 * The next 8 bit state the line length (l)
	 * <p>
	 * Then a sequence of l short follows, each of them in a 5-5-5-color format.
	 * <p>
	 * Then a new meta short comes, until the end of the image is reached (a
	 * linebreak so that we get out of the image space)
	 * <p>
	 * This method initializes data, width, height and offset.
	 * 
	 * @param reader
	 * @param translator
	 * @return
	 * @throws IOException
	 */
	private short[] uncompressImage(ByteReader reader,
	        DatBitmapTranslator<T> translator) throws IOException {
		long currentPos = reader.getReadBytes();
		HeaderType headerType = translator.getHeaderType();

		if (headerType == HeaderType.DISPLACED) {
			reader.assumeToRead(new byte[] { 0x0c, 0, 0, 0 });
		}
		this.width = reader.read16();
		this.height = reader.read16();
		if (headerType == HeaderType.DISPLACED) {
			this.offsetX = reader.read16signed();
			this.offsetY = reader.read16signed();
		} else if (headerType == HeaderType.GUI) {
			// mysterious bytes
			reader.read16();
			reader.read16();
		} else {
			// mysterious bytes?
			reader.read16();
		}

		if (reader.getReadBytes() % 2 == 1) {
			// uneven position => padding.
			reader.read8();
		}

		if (this.width == 0 || this.height == 0) {
			this.width = 1;
			this.height = 1;
			return new short[] { translator.getTransparentColor() };
		}

		short[] newData;
		try {
			newData = readCompressedData(reader, translator);
		} catch (IOException e) {
			System.err.println("Error while loading image starting at "
			        + currentPos
			        + ". There is an error/overflow somewhere around "
			        + reader.getReadBytes());
			newData = new short[this.width * this.height];
		}
		return newData;
	}

	/**
	 * Reads the compressed data.
	 * 
	 * @param reader
	 * @param translator
	 * @return
	 * @throws IOException
	 */
	private short[] readCompressedData(ByteReader reader,
	        DatBitmapTranslator<T> translator) throws IOException {
		short[] pixels = new short[this.width * this.height];

		int x = 0;
		int y = this.height - 1;

		while (y >= 0) {
			int currentMeta = reader.read16();

			int sequenceLength = currentMeta & 0xff;
			int skip = (currentMeta & 0x7f00) >> 8;
			boolean newLine = (currentMeta & 0x8000) != 0;

			skipGivenBytes(translator, pixels, x, y, skip);
			x += skip;

			readPixels(reader, translator, pixels, x, y, sequenceLength);
			x += sequenceLength;

			if (newLine) {
				fillRestOfLine(translator, pixels, x, y);
				x = 0;
				y--;
			}

		}
		return pixels;
	}

	private void fillRestOfLine(DatBitmapTranslator<T> translator,
	        short[] pixels, int x, int y) {
		int currentx = x;
		while (currentx < this.width) {
			pixels[y * this.width + currentx] =
			        translator.getTransparentColor();
			currentx++;
		}
	}

	private int readPixels(ByteReader reader,
	        DatBitmapTranslator<T> translator, short[] pixels, int x, int y,
	        int sequenceLength) throws IOException {
		for (int i = 0; i < sequenceLength; i++) {
			int currentx = i + x;
			if (currentx >= this.width) {
				throw new IllegalArgumentException("The image line " + y
				        + " exceeded width!");
			}
			pixels[y * this.width + currentx] =
			        translator.readUntransparentColor(reader);
		}
		return x;
	}

	private int skipGivenBytes(DatBitmapTranslator<T> translator,
	        short[] pixels, int x, int y, int skip) {
		for (int i = 0; i < skip; i++) {
			int currentx = i + x;
			if (currentx >= this.width) {
				throw new IllegalArgumentException(
				        "Skipped out of image at line " + y + "!");
			}
			pixels[y * this.width + currentx] =
			        translator.getTransparentColor();
		}
		return x;
	}

	@Override
	public ShortBuffer getData() {
		return ShortBuffer.wrap(this.data);
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getOffsetX() {
		return this.offsetX;
	}

	@Override
	public int getOffsetY() {
		return this.offsetY;
	}

	/**
	 * Gets an image form the reader.
	 * 
	 * @param <T>
	 *            The image type.
	 * @param translator
	 *            A translator for the given type.
	 * @param reader
	 *            The reader to read from.
	 * @return The read image.
	 * @throws IOException
	 *             If an read error occurred.
	 */
	public static <T extends Image> T getImage(
	        DatBitmapTranslator<T> translator, ByteReader reader)
	        throws IOException {
		DatBitmapReader<T> translated =
		        new DatBitmapReader<T>(translator, reader);
		return translator.createImage(translated);
	}
}
