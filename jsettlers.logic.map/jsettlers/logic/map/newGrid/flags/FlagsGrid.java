package jsettlers.logic.map.newGrid.flags;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Grid that's storing the blocked information for fast access.
 * 
 * @author Andreas Eberle
 * 
 */
public class FlagsGrid implements Serializable {
	private static final long serialVersionUID = -413005884613149208L;

	private final short width;

	private final BitSet blockedGrid;
	private final BitSet markedGrid;
	private final BitSet protectedGrid;
	private final BitSet bordersGrid;

	public FlagsGrid(final short width, final short height) {
		this.width = width;

		this.blockedGrid = new BitSet(width * height);
		this.protectedGrid = new BitSet(width * height);
		this.markedGrid = new BitSet(width * height);
		this.bordersGrid = new BitSet(width * height);
	}

	public boolean isBlocked(short x, short y) {
		return blockedGrid.get(getIdx(x, y));
	}

	private final int getIdx(int x, int y) {
		return y * width + x;
	}

	/**
	 * sets this position blocked and protected.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param blocked
	 *            the position will be set to blocked and protected if blocked == true<br>
	 *            otherwise it will be set to unblocked and unprotected.
	 */
	public void setBlockedAndProtected(short x, short y, boolean blocked) {
		final int idx = getIdx(x, y);
		this.blockedGrid.set(idx, blocked);
		this.protectedGrid.set(idx, blocked);
	}

	public boolean isMarked(short x, short y) {
		return this.markedGrid.get(getIdx(x, y));
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid.set(getIdx(x, y), marked);
	}

	public boolean isProtected(short x, short y) {
		return this.protectedGrid.get(getIdx(x, y));
	}

	public void setProtected(short x, short y, boolean setProtected) {
		this.protectedGrid.set(getIdx(x, y), setProtected);
	}

	public boolean isBorderAt(short x, short y) {
		return this.bordersGrid.get(getIdx(x, y));
	}

	public void setBorderAt(short x, short y, boolean setProtected) {
		this.bordersGrid.set(getIdx(x, y), setProtected);
	}

}
