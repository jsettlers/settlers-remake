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

import java.util.List;

public final class Hashes {

	private final List<Long> hashes;

	public Hashes(List<Long> hashes) {
		this.hashes = hashes;
	}

	public int[] compareAndCreateMapping(Hashes other) {
		int[] mapping = new int[hashes.size()];

		for (int i1 = 0; i1 < hashes.size(); i1++) {
			Long h1 = hashes.get(i1);
			int i2 = i1 < other.hashes.size()
				&& h1.equals(other.hashes.get(i1)) ? i1 : other.hashes.indexOf(h1);
			mapping[i1] = i2;
			System.out.println(i1 + " -> " + i2);
		}

		return mapping;
	}

	public long hash() {
		long hashCode = 1L;
		long multiplier = 1L;
		for (Long hash : hashes) {
			multiplier *= 31L;
			hashCode += (hash + 27L) * multiplier;
		}
		return hashCode;
	}

	public List<Long> getHashes() {
		return hashes;
	}
}
