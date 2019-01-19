/*
 * Copyright (c) 2015 - 2018
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
 */
package jsettlers.graphics.image.reader;

import java.io.IOException;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.reader.bytereader.ByteReader;
import jsettlers.graphics.image.reader.translator.HeaderType;
import jsettlers.graphics.image.reader.translator.DatBitmapTranslator;

/**
 * This class is capable of reading an image from the given stram.
 *
 * @param <T>
 *            The image type.
 * @author michael
 */
public final class DatBitmapReader<T extends Image> {

	// private final short[] data;

	/**
	 * Creates a new reader that starts to read fom the given bytereader at its current position and uses the translator to convert the image.
	 *
	 * @param translator
	 *            The translator that translates the image data.
	 * @param reader
	 *            The reader to read from.
	 * @throws IOException
	 *             If an io error occurred.
	 */
	// private DatBitmapReader(DatBitmapTranslator<T> translator, ByteReader
	// reader)
	// throws IOException {
	// this.data = uncompressImage(reader, translator);
	// }

	/**
	 * Assumes that there is an iamge starting at the beginning of reader, and reads its contents and creates the image from the stream of compressed
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
	 * If the alignment of the next short is uneven, then a 0-byte for padding is inserted.
	 * <p>
	 * Then the image data starts with the first meta short. <br>
	 * A meta short: <br>
	 * The first bit states that after drawing the line that follows this short, a linebreak should be inserted. <br>
	 * The next 7 bit state the number of pixels that should be skipped before drawing the line. <br>
	 * The next 8 bit state the line length (l)
	 * <p>
	 * Then a sequence of l short follows, each of them in a 5-5-5-color format.
	 * <p>
	 * Then a new meta short comes, until the end of the image is reached (a linebreak so that we get out of the image space)
	 * <p>
	 * This method initializes data, width, height and offset.
	 *
	 * @param reader
	 * @param translator
	 * @param
	 * @return The short array given, or null if the short array was not big enough.
	 * @throws IOException
	 */
	public static <T extends Image> void uncompressImage(ByteReader reader,
			DatBitmapTranslator<T> translator, ImageMetadata metadata,
			ImageArrayProvider array) throws IOException {
		long currentPos = reader.getReadBytes();
		HeaderType headerType = translator.getHeaderType();

		if (headerType == HeaderType.DISPLACED) {
			reader.assumeToRead(new byte[] {
					0x0c, 0, 0, 0
			});
		}
		metadata.width = reader.read16();
		metadata.height = reader.read16();
		if (headerType == HeaderType.DISPLACED) {
			metadata.offsetX = reader.read16signed();
			metadata.offsetY = reader.read16signed();
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

		array.startImage(metadata.width, metadata.height);

		try {
			readCompressedData(reader, translator, metadata.width,
					metadata.height, array);
		} catch (Throwable e) {
			System.err.println("Error while loading image starting at "
					+ currentPos
					+ ". There is an error/overflow somewhere around "
					+ reader.getReadBytes()
					+ ". Error was: "
					+ e.getMessage());
			throw new IOException("Error uncompressing image", e);
		}
	}

	/**
	 * Reads the compressed data.
	 *
	 * @param reader
	 * @param translator
	 * @return
	 * @throws IOException
	 */
	private static <T extends Image> void readCompressedData(
			ByteReader reader, DatBitmapTranslator<T> translator, int width,
			int lines, ImageArrayProvider array) throws IOException {
		short transparent = translator.getTransparentColor();
		// TODO: buffer the buffer but be thread safe!
		short[] lineBuffer = new short[width];

		for (int i = 0; i < lines; i++) {
			boolean newLine = false;

			int x = 0;
			while (!newLine) {
				int currentMeta = reader.read16();

				int sequenceLength = currentMeta & 0xff;
				int skip = (currentMeta & 0x7f00) >> 8;
				newLine = (currentMeta & 0x8000) != 0;

				int skipend = x + skip;
				while (x < skipend) {
					lineBuffer[x] = transparent;
					x++;
				}

				int readPartEnd = x + sequenceLength;
				while (x < readPartEnd) {
					lineBuffer[x] = translator.readUntransparentColor(reader);
					x++;
				}
			}

			array.writeLine(lineBuffer, x);
		}
	}

	// private void fillRestOfLine(DatBitmapTranslator<T> translator,
	// short[] pixels, int x, int y) {
	// int currentx = x;
	// while (currentx < this.width) {
	// pixels[y * this.width + currentx] =
	// translator.getTransparentColor();
	// currentx++;
	// }
	// }
	//
	// private int readPixels(ByteReader reader,
	// DatBitmapTranslator<T> translator, short[] pixels, int x, int y,
	// int sequenceLength) throws IOException {
	// for (int i = 0; i < sequenceLength; i++) {
	// int currentx = i + x;
	// if (currentx >= this.width) {
	// throw new IllegalArgumentException("The image line " + y
	// + " exceeded width!");
	// }
	// pixels[y * this.width + currentx] =
	// translator.readUntransparentColor(reader);
	// }
	// return x;
	// }

	// private int skipGivenBytes(DatBitmapTranslator<T> translator, int x, int
	// skip) {
	// for (int i = 0; i < skip; i++) {
	// int currentx = i + x;
	// // if (currentx >= this.width) {
	// // throw new IllegalArgumentException(
	// // "Skipped out of image at line " + y + "!");
	// // }
	// lineBuffer[currentx] =
	// translator.getTransparentColor();
	// }
	// return x;
	// }

	// @Override
	// public ShortBuffer getData() {
	// return ShortBuffer.wrap(this.data);
	// }
	//
	// @Override
	// public int getWidth() {
	// return this.width;
	// }
	//
	// @Override
	// public int getHeight() {
	// return this.height;
	// }
	//
	// @Override
	// public int getOffsetX() {
	// return this.offsetX;
	// }
	//
	// @Override
	// public int getOffsetY() {
	// return this.offsetY;
	// }

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
			DatBitmapTranslator<T> translator, ByteReader reader, String name)
			throws IOException {
		ImageMetadata metadata = new ImageMetadata();
		ShortArrayWriter array = new ShortArrayWriter();
		uncompressImage(reader, translator, metadata, array);
		return translator.createImage(metadata, array.getArray(), name);
	}
}
