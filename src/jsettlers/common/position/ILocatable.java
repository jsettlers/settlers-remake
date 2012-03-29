package jsettlers.common.position;

import java.util.List;

/**
 * Interface offering methods to get the {@link ILocatable} of a list of {@link ILocatable}s that's closest to a given target.<br>
 * It also specifies a heuristic for the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILocatable {
	ShortPoint2D getPos();

	static class Methods {
		/**
		 * returns the index of the {@link ILocatable} in the list that's closest to the target.
		 * 
		 * @param toBeCompared
		 *            list of {@link ILocatable} to be checked.
		 * @param target
		 *            target position
		 * @return index of the closest {@link ILocatable} on the list.
		 */
		public static int getNearest(List<? extends ILocatable> toBeCompared, ShortPoint2D target) {
			int closestIdx = 0;
			float closestDist = Short.MAX_VALUE;

			short tx = target.getX();
			short ty = target.getY();

			int idx = 0;
			for (ILocatable curr : toBeCompared) {
				ShortPoint2D currPos = curr.getPos();
				float currHeu = getHeuristic(currPos, tx, ty);
				if (currHeu < closestDist) {
					closestDist = currHeu;
					closestIdx = idx;
				}
				idx++;
			}

			return closestIdx;
		}

		public static float getHeuristic(ShortPoint2D pos, short tx, short ty) {
			return (float) Math.hypot(pos.getX() - tx, pos.getY() - ty);
		}
	}
}
