package jsettlers.logic.algorithms.path;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * A path a movable can follow.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Path implements Serializable {
	private static final long serialVersionUID = 1869164120660594918L;

	private final short[] pathX;
	private final short[] pathY;
	private short walkIdx = 1;

	public Path(int length) {
		pathX = new short[length + 1];
		pathY = new short[length + 1];
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
	public void insertAt(int idx, short x, short y) {
		pathX[idx] = x;
		pathY[idx] = y;
	}

	/**
	 * returns the next x
	 * 
	 * @return -1 if {@link #isFinished()} returns true<br>
	 *         otherwise the next x coordinate
	 */
	public short nextX() {
		if (isFinished()) {
			return -1;
		} else {
			return pathX[walkIdx];
		}
	}

	/**
	 * returns the next y
	 * 
	 * @return -1 if {@link #isFinished()} returns true<br>
	 *         otherwise the next y coordinate
	 */
	public short nextY() {
		if (isFinished()) {
			return -1;
		} else {
			return pathY[walkIdx];
		}
	}

	public boolean isFinished() {
		return walkIdx >= pathX.length;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (short idx = 0; idx < pathX.length; idx++) {
			res.append("(" + pathX[idx] + "|" + pathY[idx] + ")");
		}
		return res.toString();
	}

	public short getFirstX() {
		return pathX[1];
	}

	public short getFirstY() {
		return pathY[1];
	}

	public short getTargetX() {
		return pathX[pathX.length - 1];
	}

	public short getTargetY() {
		return pathY[pathY.length - 1];
	}

	public final int getLength() {
		return pathX.length - 1;
	}

	/**
	 * increases the path counter
	 */
	public void goToNextStep() {
		walkIdx++;
	}

	public ShortPoint2D getNextPos() {
		return new ShortPoint2D(pathX[walkIdx], pathY[walkIdx]);
	}

	public ShortPoint2D getFirstPos() {
		return new ShortPoint2D(pathX[1], pathY[1]);
	}

	public ShortPoint2D getTargetPos() {
		final int length = getLength();
		return new ShortPoint2D(pathX[length], pathY[length]);
	}
}
