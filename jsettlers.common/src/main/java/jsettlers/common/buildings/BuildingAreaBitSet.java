/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import java.util.BitSet;

import jsettlers.common.position.RelativePoint;

/**
 * This class defines the area for a building using a rectangular bitset.
 *
 * @author Andreas Eberle
 */
public final class BuildingAreaBitSet {
	/**
	 * The bit set for the area. It has a size of width*height. For each relative (x,y) point the bit
	 * x + width * y is set if that position is contained in this set.
	 */
	public final BitSet bitSet;
	/**
	 * One position that is contained in this set.
	 */
	public final RelativePoint aPosition;
	/**
	 * The (maximum) width of this set.
	 */
	public final short width;
	/**
	 * The (maximum) height of the area of this set.
	 */
	public final short height;
	/**
	 * Minimum X value in this set.
	 */
	public final short minX;
	/**
	 * Minimum Y value in this set.
	 */
	public final short minY;
	/**
	 * Maximum X value in this set.
	 */
	public final short maxX;
	/**
	 * Maximum Y value in this set.
	 */
	public final short maxY;
	/**
	 * Number of positions contained in this set.
	 */
	public final int numberOfPositions;

	/**
	 * A jump table used by the construction mark algorithm.
	 */
	public final short[] xJumps;
	/**
	 * A jump table used by the construction mark algorithm.
	 */
	public final short[] yJumps;

	/**
	 * Creates a new area bit set.
	 *
	 * @param protectedTiles The points that should be contained in this set.
	 */
	public BuildingAreaBitSet(RelativePoint[] protectedTiles) {
		short minX = protectedTiles[0].getDx();
		short maxX = protectedTiles[0].getDx();
		short minY = protectedTiles[0].getDy();
		short maxY = protectedTiles[0].getDy();
		for (int i = 0; i < protectedTiles.length; i++) {
			minX = min(minX, protectedTiles[i].getDx());
			maxX = max(maxX, protectedTiles[i].getDx());
			minY = min(minY, protectedTiles[i].getDy());
			maxY = max(maxY, protectedTiles[i].getDy());
		}

		this.aPosition = protectedTiles[0];
		this.numberOfPositions = protectedTiles.length;

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		this.width = (short) (maxX - minX + 1);
		this.height = (short) (maxY - minY + 1);

		this.bitSet = new BitSet(width * height);

		for (int i = 0; i < protectedTiles.length; i++) {
			set(protectedTiles[i].getDx(), protectedTiles[i].getDy());
		}

		// calculate jump tables
		this.xJumps = new short[width * height];
		calculateXJumps();
		this.yJumps = new short[width * height];
		calculateYJumps();
	}

	private void calculateXJumps() {
		for (int y = 0; y < height; y++) {
			short jumpsCtr = 0;
			for (int x = 0; x < width; x++) {
				final int index = x + y * width;

				if (bitSet.get(index)) { // if position blocked
					xJumps[index] = ++jumpsCtr;
				} else { // if it's free, let the default value (0) in the array and reset jumpsCtr
					jumpsCtr = 0;
				}
			}
		}
	}

	private void calculateYJumps() {
		for (int x = 0; x < width; x++) {
			short jumpsCtr = 0;
			for (int y = 0; y < height; y++) {
				final int index = x + y * width;

				if (bitSet.get(index)) { // if position blocked
					yJumps[index] = ++jumpsCtr;
				} else { // if it's free, let the default value (0) in the array and reset jumpsCtr
					jumpsCtr = 0;
				}
			}
		}
	}

	public void set(short x, short y) {
		this.bitSet.set(index(x - minX, y - minY));
	}

	public boolean get(short x, short y) {
		return this.bitSet.get(index(x - minX, y - minY));
	}

	private int index(int relativeX, int relativeY) {
		return relativeX + width * relativeY;
	}

	private static short max(short first, short second) {
		if (first > second) {
			return first;
		} else {
			return second;
		}
	}

	private static short min(short first, short second) {
		if (first < second) {
			return first;
		} else {
			return second;
		}
	}

	public void setCenter(short xJump, short yJump) {
		int centerIndex = -minX - minY * width;
		xJumps[centerIndex] = xJump;
		yJumps[centerIndex] = yJump;
	}
}
