package jsettlers.graphics;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector;

/**
 * This class defines the default methods that are used to display a game and
 * its controls on the screen.
 * 
 * @author michael
 */
public interface ISettlersGameDisplay {
	public ProgressConnector showProgress();
	
	public void showStartScreen(IStartScreenConnector connector);
	
	/**
	 * Shows the map on the screen.
	 * <p>
	 * This method also sets up the draw context of the map and returns a
	 * {@link MapInterfaceConnector} that can be accessed to change the view.
	 * 
	 * @param map
	 *            The map to display.
	 * @param playerStatistics
	 *            the statistics to be displayed. (can be null) <br>
	 *            TODO @Michael use player statistics
	 * @return The connector to access the view and add event listenrs
	 * @see MapInterfaceConnector
	 */
	public MapInterfaceConnector showGameMap(
	        IGraphicsGrid map, IStatisticable playerStatistics);

	/**
	 * Shows the network screen (when a network game is started)
	 * @param networkScreen
	 */
	public void showNetworkScreen(INetworkScreenAdapter networkScreen);
}
