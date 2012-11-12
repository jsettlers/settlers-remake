package jsettlers.logic.map.newGrid.flags;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Grid that's storing the blocked information for fast access.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FlagsGrid implements Serializable {
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

	public boolean isBlocked(int x, int y) {
		return blockedGrid.get(x + y * width);
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
		final int idx = x + y * width;
		this.blockedGrid.set(idx, blocked);
		this.protectedGrid.set(idx, blocked);
	}

	public boolean isMarked(int x, int y) {
		return this.markedGrid.get(x + y * width);
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid.set(x + y * width, marked);
	}

	public boolean isProtected(int x, int y) {
		return this.protectedGrid.get(x + y * width);
	}

	public void setProtected(short x, short y, boolean setProtected) {
		this.protectedGrid.set(x + y * width, setProtected);
	}

	public boolean isBorderAt(int x, int y) {
		return this.bordersGrid.get(x + y * width);
	}

	public void setBorderAt(short x, short y, boolean setProtected) {
		this.bordersGrid.set(x + y * width, setProtected);
	}
}
