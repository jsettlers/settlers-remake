package jsettlers.logic.algorithms.path;

import java.io.Serializable;

import jsettlers.common.movable.EDirection;
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

	private short currX;
	private short currY;

	private EDirection dir;
	private int idx = 1;

	private boolean finished;

	private ShortPoint2D targetPos;

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
		int length = oldPath.getLength() - oldPath.getStep() + pathPrefix.length + 2;
		pathX = new short[length];
		pathY = new short[length];

		int i;
		for (i = 0; i < pathPrefix.length; i++) {
			insertAt(i, pathPrefix[i].getX(), pathPrefix[i].getY());
		}

		for (; i < length; i++) {
			insertAt(i, oldPath.nextX(), oldPath.nextY());
			oldPath.goToNextStep();
		}

		initPath();
	}

	/**
	 * Creates a path of length 1 with that's just containing to the given position.<br>
	 * 
	 * @param position
	 *            the single path position.
	 */
	public Path(ShortPoint2D position) {
		this(1);
		insertAt(0, position.getX(), position.getY());
		initPath();
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

	/**
	 * returns the next x
	 * 
	 * @return -1 if {@link #isFinished()} returns true<br>
	 *         otherwise the next x coordinate
	 */
	public final short nextX() {
		if (isFinished()) {
			return -1;
		} else {
			return currX;
		}
	}

	/**
	 * returns the next y
	 * 
	 * @return -1 if {@link #isFinished()} returns true<br>
	 *         otherwise the next y coordinate
	 */
	public final short nextY() {
		if (isFinished()) {
			return -1;
		} else {
			return currY;
		}
	}

	public final boolean isFinished() {
		return finished;
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
		return pathX.length - 1;
	}

	/**
	 * increases the path counter
	 */
	public final void goToNextStep() {
		if (idx >= pathX.length) {
			finished = true;
			return;
		}

		currX = dir.getNextTileX(currX);
		currY = dir.getNextTileY(currY);

		if (currX == pathX[idx] && currY == pathY[idx]) {
			idx++;
			if (idx < pathX.length) {
				dir = EDirection.getDirectionOfMultipleSteps(pathX[idx] - pathX[idx - 1], pathY[idx] - pathY[idx - 1]);
			}
		}

	}

	public final void initPath() {
		currX = getFirstX();
		currY = getFirstY();

		if (pathX.length > 1) {
			dir = EDirection.getDirectionOfMultipleSteps(pathX[1] - pathX[0], pathY[1] - pathY[0]);
		}
	}

	public final ShortPoint2D getNextPos() {
		return new ShortPoint2D(nextX(), nextY());
	}

	public final ShortPoint2D getFirstPos() {
		return new ShortPoint2D(getFirstX(), getFirstY());
	}

	public final ShortPoint2D getTargetPos() {
		if (targetPos == null) {
			final int length = getLength();
			targetPos = new ShortPoint2D(pathX[length], pathY[length]);
		}
		return targetPos;
	}

	public int getStep() {
		return idx;
	}

	public ShortPoint2D getOverNextPos() {
		if (!isFinished()) {
			short x = dir.getNextTileX(currX);
			short y = dir.getNextTileY(currY);

			return new ShortPoint2D(x, y);
		} else {
			return null;
		}
	}
}
