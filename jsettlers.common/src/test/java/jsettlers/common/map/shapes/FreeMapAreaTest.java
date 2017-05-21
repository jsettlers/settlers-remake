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
package jsettlers.common.map.shapes;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class FreeMapAreaTest {

	@Test
	public void test() {
		List<ShortPoint2D> positions = new LinkedList<>();
		positions.add(new ShortPoint2D(1, 1));
		positions.add(new ShortPoint2D(2, 2));
		positions.add(new ShortPoint2D(3, 3));
		positions.add(new ShortPoint2D(2, 1));
		positions.add(new ShortPoint2D(1, 2));
		positions.add(new ShortPoint2D(1, 3));
		positions.add(new ShortPoint2D(3, 1));

		FreeMapArea area = new FreeMapArea(positions);

		boolean[][] expected = { { false, false, false, false, false }, { false, true, true, true, false }, { false, true, true, false, false },
				{ false, true, false, true, false }, { false, false, false, false, false } };

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				assertEquals(expected[y][x], area.contains(new ShortPoint2D(x, y)));
			}
		}
	}
}
