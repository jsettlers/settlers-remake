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

import java.io.File;

import jsettlers.common.Color;

import static java8.util.J8Arrays.stream;

public enum DatFileType {
	RGB555(".7c003e01f.dat", new byte[]{
		(byte) 0x7c,
		0x00,
		0x00,
		(byte) 0xe0,
		0x03,}),
	RGB565(".f8007e01f.dat", new byte[]{
		(byte) 0xf8,
		0x00,
		0x00,
		(byte) 0xe0,
		0x07,}),;

	private final String fileSuffix;
	private final byte[] startMagic;

	DatFileType(String fileSuffix, byte[] startMagic) {
		this.fileSuffix = fileSuffix;
		this.startMagic = startMagic;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public byte[] getFileStartMagic() {
		return startMagic;
	}

	/**
	 * Converts a color in the current format to a rgba 5551 color.
	 */
	public short convertTo4444(int color) {
		if (this == RGB555) {
			color = (short) Color.convert555to4444(color);
		} else if (this == RGB565) {
			color = (short) Color.convert565to4444(color);
		}
		return (short) color;
	}

	public static DatFileType getForPath(File path) {
		return stream(values()).filter(v -> path.getName().endsWith(v.fileSuffix)).findFirst()
							   .orElseThrow(() -> new IllegalArgumentException("Could not determine type of " + path.getName()));
	}
}
