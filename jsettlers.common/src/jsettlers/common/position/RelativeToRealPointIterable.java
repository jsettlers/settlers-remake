/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.common.position;

import java.util.Iterator;

/**
 * Iterates over an array of relative positions using a reference point.
 * 
 * @author Andreas Eberle
 *
 */
public class RelativeToRealPointIterable implements Iterable<ShortPoint2D> {

	private final RelativePoint[] relativePositions;
	private final ShortPoint2D relationPosition;

	/**
	 * Create a new {@link RelativeToRealPointIterable}.
	 * 
	 * @param relativePositions
	 *            The positions to iterate over
	 * @param relationPosition
	 *            The reference point to use.
	 */
	public RelativeToRealPointIterable(RelativePoint[] relativePositions, ShortPoint2D relationPosition) {
		this.relativePositions = relativePositions;
		this.relationPosition = relationPosition;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new Iterator<ShortPoint2D>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < relativePositions.length;
			}

			@Override
			public ShortPoint2D next() {
				return relativePositions[index++].calculatePoint(relationPosition);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
