package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;

public interface IStartedGame {
	/**
	 * Gets the grid that should be displayed to the user.
	 * @return
	 */
	IGraphicsGrid getMap();
	
	/**
	 * Gets the statistics of the current player for which the UI is.
	 * @return
	 */
    IStatisticable getPlayerStatistics();
    
    /**
     * Gets the number of the player that is currently playing.
     * @return
     */
    int getPlayer();
}
