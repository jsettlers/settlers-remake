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

	public FlagsGrid(short width, short height) {
		this.width = width;

		this.blockedGrid = new BitSet(width * height);
		this.protectedGrid = new BitSet(width * height);
		this.markedGrid = new BitSet(width * height);
	}

	public boolean isBlocked(short x, short y) {
		return blockedGrid.get(x * width + y);
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
		this.blockedGrid.set(x * width + y, blocked);
		this.setProtected(x, y, blocked);
	}

	public boolean isMarked(short x, short y) {
		return this.markedGrid.get(x * width + y);
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid.set(x * width + y, marked);
	}

	public boolean isProtected(short x, short y) {
		return this.protectedGrid.get(x * width + y);
	}

	public void setProtected(short x, short y, boolean setProtected) {
		this.protectedGrid.set(x * width + y, setProtected);
	}

}
