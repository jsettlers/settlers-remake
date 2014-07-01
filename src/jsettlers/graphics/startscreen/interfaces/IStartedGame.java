package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;

/**
 * This interface represents a started game offering the methods the UI needs.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IStartedGame {
	/**
	 * Gets the grid that should be displayed to the user.
	 * 
	 * @return
	 */
	IGraphicsGrid getMap();

	/**
	 * Gets the statistics of the current player for which the UI is.
	 * 
	 * @return
	 */
	IStatisticable getPlayerStatistics();

	void setGameExitListener(IGameExitListener exitListener);
}