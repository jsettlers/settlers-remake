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
package jsettlers.algorithms.path;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * A path a movable can follow.
 * 
 * @author Andreas Eberle
 * 
 */
public class Path implements Serializable {
	private static final long serialVersionUID = 1869164120660594918L;

	private final short[] pathX;
	private final short[] pathY;

	private int idx = -1;

	public Path(int length) {
		pathX = new short[length];
		pathY = new short[length];
	}

	/**
	 * Concatenates a path and a prefix of {@link ShortPoint2D} objects.
	 * 
	 * @param oldPath
	 *            The path to be appended to the prefix.
	 * @param pathPrefix
	 *            The path prefix. NOTE: The prefix must start with the current position of the movable!
	 */
	public Path(Path oldPath, ShortPoint2D... pathPrefix) {
		int length = (oldPath.getLength() - (oldPath.idx + 1)) + pathPrefix.length;
		pathX = new short[length];
		pathY = new short[length];

		int i;
		for (i = 0; i < pathPrefix.length; i++) {
			insertAt(i, pathPrefix[i].x, pathPrefix[i].y);
		}

		for (; i < length; i++) {
			insertAt(i, oldPath.nextX(), oldPath.nextY());
			oldPath.goToNextStep();
		}
	}

	/**
	 * Creates a path of length 1 with that's just containing to the given position.<br>
	 * 
	 * @param position
	 *            the single path position.
	 */
	public Path(ShortPoint2D position) {
		this(1);
		insertAt(0, position.x, position.y);
	}

	/**
	 * sets the given position to the given index of the path
	 * 
	 * @param idx
	 *            NOTE: this must be in the integer interval [0, pathlength -1]!
	 * @param x
	 *            x position of the step
	 * @param y
	 *            y position of the step
	 */
	public final void insertAt(int idx, short x, short y) {
		pathX[idx] = x;
		pathY[idx] = y;
	}

	public boolean hasNextStep() {
		return idx + 1 < pathX.length;
	}

	public final short nextX() {
		return pathX[idx + 1];
	}

	public final short nextY() {
		return pathY[idx + 1];
	}

	public final ShortPoint2D getNextPos() {
		return new ShortPoint2D(nextX(), nextY());
	}

	public final boolean isFinished() {
		return idx >= pathX.length;
	}

	@Override
	public final String toString() {
		StringBuffer res = new StringBuffer();
		for (short idx = 0; idx < pathX.length; idx++) {
			res.append("(" + pathX[idx] + "|" + pathY[idx] + ")");
		}
		return res.toString();
	}

	public final short getFirstX() {
		return pathX[0];
	}

	public final short getFirstY() {
		return pathY[0];
	}

	public final short getTargetX() {
		return pathX[pathX.length - 1];
	}

	public final short getTargetY() {
		return pathY[pathY.length - 1];
	}

	public final int getLength() {
		return pathX.length;
	}

	/**
	 * increases the path counter
	 */
	public final void goToNextStep() {
		idx++;
	}

	public final ShortPoint2D getFirstPos() {
		return new ShortPoint2D(getFirstX(), getFirstY());
	}

	public final ShortPoint2D getTargetPosition() {
		int lastIdx = pathX.length - 1;
		return new ShortPoint2D(pathX[lastIdx], pathY[lastIdx]);
	}

	public int getStep() {
		return idx;
	}

	public boolean hasOverNextStep() {
		return idx + 2 < pathX.length;
	}

	public ShortPoint2D getOverNextPos() {
		return new ShortPoint2D(pathX[idx + 2], pathY[idx + 2]);
	}
}
