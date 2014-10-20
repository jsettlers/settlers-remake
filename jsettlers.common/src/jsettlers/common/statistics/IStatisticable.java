package jsettlers.common.statistics;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

/**
 * This interface supplies statistical information about the game to the UI.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IStatisticable {
	/**
	 * Gets the game time.
	 * 
	 * @return The current game time in milliseconds.
	 */
	int getGameTime();

	int getNumberOf(EMaterialType materialType);

	int getNumberOf(EMovableType movableType);

	int getJoblessBearers();
}
