package jsettlers.logic.algorithms.path;

import java.io.Serializable;

import jsettlers.common.position.ISPosition2D;

public class Path implements Serializable {
	private static final long serialVersionUID = 1869164120660594918L;

	private final ISPosition2D[] path;

	private short walkIdx = 1;

	public Path(int length) {
		path = new ISPosition2D[length + 1];
	}

	/**
	 * sets the given position to the given index of the path
	 * 
	 * @param idx
	 *            NOTE: this must be in the integer interval [0, pathlength -1]!
	 * @param step
	 *            NOTE: this mustn't be null!
	 */
	public void insertAt(int idx, ISPosition2D step) {
		assert step != null : "PATH POINT == NULL";

		path[idx] = step;
	}

	/**
	 * returns the next step and increases the steps counter
	 * 
	 * @return null if {@link #isFinished()} returns true<br>
	 *         otherwise the next step
	 */
	public ISPosition2D nextStep() {
		if (isFinished()) {
			return null;
		} else {
			return path[walkIdx++];
		}
	}

	public boolean isFinished() {
		return walkIdx >= path.length;// || walkIdx >= insertIdx;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (ISPosition2D curr : path) {
			res.append(curr.toString());
		}
		return res.toString();
	}

	public ISPosition2D getFirst() {
		return path[1];
	}

	public ISPosition2D getTargetPos() {
		return path[path.length - 1];
	}

	public int getLength() {
		return path.length;
	}
}
