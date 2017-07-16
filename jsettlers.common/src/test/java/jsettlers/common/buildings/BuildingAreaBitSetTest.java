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
package jsettlers.common.buildings;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Test;

import jsettlers.common.position.RelativePoint;

/**
 * Test for class {@link BuildingAreaBitSet}.
 * 
 * @author Andreas Eberle
 * 
 */
public class BuildingAreaBitSetTest {

	@Test
	public void testJumpMaps() {
		final boolean[][] blockedMap = {
				{ true, true, true, true, false, false, false },
				{ true, true, false, true, false, true, false },
				{ true, true, true, true, false, true, false },
				{ true, false, true, true, false, false, false },
				{ false, false, false, false, false, false, false },
				{ true, true, false, false, true, true, true },
				{ true, false, false, false, false, false, false } };
		RelativePoint[] relativePoints = BuildingAreaUtils.createRelativePoints(blockedMap);

		BuildingAreaBitSet buildingArea = new BuildingAreaBitSet(relativePoints);

		assertEquals(blockedMap.length, buildingArea.height);
		assertEquals(blockedMap[0].length, buildingArea.width);

		assertBlockedEquals(blockedMap, buildingArea.bitSet, buildingArea.width);

		final short[][] xJumps = {
				{ 1, 2, 3, 4, 0, 0, 0 },
				{ 1, 2, 0, 1, 0, 1, 0 },
				{ 1, 2, 3, 4, 0, 1, 0 },
				{ 1, 0, 1, 2, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 2, 0, 0, 1, 2, 3 },
				{ 1, 0, 0, 0, 0, 0, 0 } };

		assertArraysEquals(xJumps, buildingArea.xJumps, buildingArea.width);

		final short[][] yJumps = {
				{ 1, 1, 1, 1, 0, 0, 0 },
				{ 2, 2, 0, 2, 0, 1, 0 },
				{ 3, 3, 1, 3, 0, 2, 0 },
				{ 4, 0, 2, 4, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 1, 0, 0, 1, 1, 1 },
				{ 2, 0, 0, 0, 0, 0, 0 } };

		assertArraysEquals(yJumps, buildingArea.yJumps, buildingArea.width);
	}

	private void assertArraysEquals(short[][] array2d, short[] arrayFlat, short width) {
		for (int y = 0; y < array2d.length; y++) {
			for (int x = 0; x < array2d[y].length; x++) {
				assertEquals("(" + x + "|" + y + ") ", array2d[y][x], arrayFlat[x + y * width]);
			}
		}
	}

	private void assertBlockedEquals(boolean[][] blockedMap, BitSet bitSet, short width) {
		for (int y = 0; y < blockedMap.length; y++) {
			for (int x = 0; x < blockedMap[y].length; x++) {
				assertEquals(blockedMap[y][x], bitSet.get(x + y * width));
			}
		}
	}
}
