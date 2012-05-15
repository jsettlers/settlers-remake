package jsettlers.logic.algorithms.path.astar.supergrid;

import java.util.List;

import jsettlers.common.Color;
import jsettlers.common.position.ShortPoint2D;

/**
 * Interface for a grid that can be used by {@link SuperGridAStar}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISuperGridAStarGrid extends IDijkstraGrid {

	void setDebugColor(int x, int y, Color color);

	@Override
	boolean isBlocked(int x, int y);

	void setBlockedChangedListener(IBlockedChangedListener listener);

	/**
	 * Listener that needs to be informed if a position changes it's blocked state.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public static interface IBlockedChangedListener {
		void blockedChanged(List<ShortPoint2D> positions);

		void blockedChanged(int x, int y);
	}
}
