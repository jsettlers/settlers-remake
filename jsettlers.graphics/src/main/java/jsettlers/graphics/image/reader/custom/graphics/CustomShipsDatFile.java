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
package jsettlers.graphics.image.reader.custom.graphics;

import jsettlers.common.images.AnimationSequence;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.sequence.ArraySequence;
import jsettlers.graphics.image.sequence.Sequence;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.image.reader.DatFileReader;
import jsettlers.graphics.image.reader.EmptyDatFile;
import jsettlers.graphics.image.sequence.SequenceList;
import jsettlers.graphics.image.reader.WrappedAnimation;

class CustomShipsDatFile extends EmptyDatFile {
	private final DatFileReader fallback;
	private final ImageProvider imageProvider;

	CustomShipsDatFile(DatFileReader fallback, ImageProvider imageProvider) {
		this.fallback = fallback;
		this.imageProvider = imageProvider;
	}

	@Override
	public SequenceList<Image> getSettlers() {
		return new SequenceList<Image>() {

			private SequenceList<Image> fallbackSequence = fallback.getSettlers();

			@Override
			public int size() {
				return Math.max(30, fallbackSequence.size());
			}

			@Override
			public Sequence<Image> get(int index) {
				if (index == 0) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_hull_hull", 0, 6));
				} else if (index == 2) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_structures_structures", 0, 6));
				} else if (index == 28) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_sail_sail", 0, 6));

				} else if (index == 4) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_hull_hull", 0, 6));
				} else if (index == 6) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_structures_structures", 0, 6));
				} else if (index == 29) {
					return new WrappedAnimation(imageProvider, new AnimationSequence("cargo_ship_sail_sail", 0, 6));

				} else if (index < fallbackSequence.size()) {
					return fallbackSequence.get(index);
				} else {
					return ArraySequence.getNullSequence();
				}
			}
		};
	}
}
