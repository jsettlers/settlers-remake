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
package jsettlers.common.utils;

import java8.util.Optional;
import jsettlers.common.utils.interfaces.ICoordinateWithRadiusConsumer;
import jsettlers.common.utils.interfaces.ICoordinateWithRadiusFunction;

/**
 * Created by Andreas Eberle on 06.01.2017.
 */
public class VisitorConsumerUtils {

	public static <T> ICoordinateWithRadiusFunction<Optional<T>> visitor(ICoordinateWithRadiusConsumer consumer) {
		return (x, y, radius) -> {
			consumer.accept(x, y, radius);
			return Optional.empty();
		};
	}

	public static <T> ICoordinateWithRadiusFunction<Optional<T>> filterBounds(int width, int height,
			ICoordinateWithRadiusFunction<Optional<T>> consumer) {
		return (x, y, radius) -> {
			if (0 <= x && x < width && 0 <= y && y < height) {
				return consumer.apply(x, y, radius);
			}
			return Optional.empty();
		};
	}
}
