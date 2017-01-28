/*******************************************************************************
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.utils.coordinates;

import java8.util.Optional;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.Mutable;
import jsettlers.common.utils.mutables.MutableInt;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andreas Eberle on 12.01.2017.
 */
public abstract class CoordinateStream implements Serializable {

	public <T> Optional<T> iterateForResult(ICoordinateFunction<Optional<T>> function) {
		Mutable<Optional<T>> result = new Mutable<>(Optional.empty());
		iterate((x, y) -> {
			Optional<T> currResult = function.apply(x, y);
			if (currResult.isPresent()) {
				result.object = currResult;
				return false;
			} else {
				return true;
			}
		});
		return result.object;
	}

	/**
	 * @param function
	 *            Function called for every value of the stream. If the function returns <code>true</code> the iteration is continued. If it returns
	 *            <code>false</code> the iteration is stopped.
	 * @return <code>true</code> if the iteration has not been aborted (the function never returned <code>false</code>)
	 */
	public abstract boolean iterate(IBooleanCoordinateFunction function);

	public CoordinateStream filter(ICoordinatePredicate predicate) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				return CoordinateStream.this.iterate((x, y) -> !predicate.test(x, y) || function.apply(x, y));
			}
		};
	}

	public CoordinateStream filterBounds(int width, int height) {
		return filterBounds(0, 0, width, height);
	}

	/**
	 * @param xStart
	 *            inclusive
	 * @param yStart
	 *            inclusive
	 * @param xEnd
	 *            exclusive
	 * @param yEnd
	 *            exclusive
	 * @return
	 */
	public CoordinateStream filterBounds(int xStart, int yStart, int xEnd, int yEnd) {
		return filter((x, y) -> xStart <= x && x < xEnd && yStart <= y && y < yEnd);
	}

	public void forEach(ICoordinateConsumer consumer) {
		iterate((x, y) -> {
			consumer.accept(x, y);
			return true;
		});
	}

	public CoordinateStream getEvery(int getIndex) {
		if (getIndex <= 0) {
			throw new IllegalArgumentException("parameter must be greater 0");
		}

		MutableInt counter = new MutableInt(0);
		return filter(((x, y) -> counter.value++ % getIndex == 0));
	}

	public CoordinateStream skipFirst(int numberOfSkips) {
		MutableInt counter = new MutableInt(0);
		return filter((x, y) -> counter.value++ >= numberOfSkips);
	}

	public int count() {
		MutableInt counter = new MutableInt(0);
		forEach((x, y) -> counter.value++);
		return counter.value;
	}

	public boolean isEmpty() {
		return iterate((x, y) -> false);
	}

	public boolean contains(int searchedX, int searchedY) {
		return !iterate((x, y) -> !(x == searchedX && y == searchedY));
	}

	public static CoordinateStream fromList(final List<ShortPoint2D> positions) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				for (ShortPoint2D position : positions) {
					if (!function.apply(position.x, position.y)) {
						return false;
					}
				}
				return true;
			}
		};
	}
}
