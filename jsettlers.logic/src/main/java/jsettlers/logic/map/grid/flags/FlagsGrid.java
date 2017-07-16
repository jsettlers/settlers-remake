/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.grid.flags;

import java.io.Serializable;
import java.util.BitSet;

import jsettlers.algorithms.partitions.IBlockingProvider;

/**
 * Grid that's storing the blocked information for fast access.
 *
 * @author Andreas Eberle
 *
 */
public final class FlagsGrid implements Serializable, IBlockingProvider, IProtectedProvider {
	private static final long serialVersionUID = -413005884613149208L;

	private final short width;

	private final BitSet blockedGrid;
	private final BitSet markedGrid;
	private final BitSet protectedGrid;

	private IProtectedChangedListener protectedChangedListener = null;

	public FlagsGrid(final short width, final short height) {
		this.width = width;

		this.blockedGrid = new BitSet(width * height);
		this.protectedGrid = new BitSet(width * height);
		this.markedGrid = new BitSet(width * height);
	}

	@Override
	public boolean isBlocked(int x, int y) {
		return blockedGrid.get(x + y * width);
	}

	/**
	 * Sets this position's blocked and protected.
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param blockedAndProtected
	 *            the position will be set to blocked and protected if blocked == true<br>
	 *            otherwise it will be set to unblocked and unprotected.
	 */
	public void setBlockedAndProtected(int x, int y, boolean blockedAndProtected) {
		setBlockedAndProtected(x, y, blockedAndProtected, blockedAndProtected);
	}

	/**
	 * Sets this position's blocked and protected.
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param newBlocked
	 *            new blocked value of this position
	 * @param newProtected
	 *            new protected value of this position
	 */
	public void setBlockedAndProtected(int x, int y, boolean newBlocked, boolean newProtected) {
		final int idx = x + y * width;
		boolean oldProtected = this.protectedGrid.get(idx);

		this.blockedGrid.set(idx, newBlocked);
		this.protectedGrid.set(idx, newProtected);

		if (protectedChangedListener != null && oldProtected != newProtected) {
			this.protectedChangedListener.protectedChanged(x, y, newProtected);
		}
	}

	public boolean isMarked(int x, int y) {
		return this.markedGrid.get(x + y * width);
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid.set(x + y * width, marked);
	}

	@Override
	public boolean isProtected(int x, int y) {
		return this.protectedGrid.get(x + y * width);
	}

	public void setProtected(int x, int y, boolean newProtected) {
		this.protectedGrid.set(x + y * width, newProtected);

		if (protectedChangedListener != null) {
			this.protectedChangedListener.protectedChanged(x, y, newProtected);
		}
	}

	public boolean isPioneerBlocked(int x, int y) {
		int index = x + y * width;
		return blockedGrid.get(index) || protectedGrid.get(index);
	}

	@Override
	public void setProtectedChangedListener(IProtectedChangedListener protectedChangedListener) {
		this.protectedChangedListener = protectedChangedListener;
	}
}
