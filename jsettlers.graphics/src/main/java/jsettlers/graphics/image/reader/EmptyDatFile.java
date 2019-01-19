/*
 * Copyright (c) 2018
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

import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.reader.bytereader.ByteReader;
import jsettlers.graphics.image.reader.translator.DatBitmapTranslator;
import jsettlers.graphics.image.reader.translator.LandscapeTranslator;
import jsettlers.graphics.image.sequence.ArraySequence;
import jsettlers.graphics.image.sequence.Sequence;
import jsettlers.graphics.image.sequence.SequenceList;

public class EmptyDatFile implements DatFileReader {
	@Override
	public SequenceList<Image> getSettlers() {
		return new SequenceList<Image>() {
			@Override
			public Sequence<Image> get(int index) {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}
		};
	}

	@Override
	public Sequence<SingleImage> getLandscapes() {
		return new ArraySequence<>(new SingleImage[0]);
	}

	@Override
	public Sequence<SingleImage> getGuis() {
		return new ArraySequence<>(new SingleImage[0]);
	}

	@Override
	public DatBitmapTranslator<SingleImage> getLandscapeTranslator() {
		return new LandscapeTranslator(DatFileType.RGB555);
	}

	@Override
	public ByteReader getReaderForLandscape(int index) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void generateImageMap(int width, int height, int[] sequences, String id, String name) throws IOException {
		throw new UnsupportedOperationException();
	}

}
