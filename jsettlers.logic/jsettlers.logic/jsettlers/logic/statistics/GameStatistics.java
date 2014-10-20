package jsettlers.logic.statistics;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;
import networklib.client.interfaces.IGameClock;

/**
 * This class supplies the UI with statistics of the game.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameStatistics implements IStatisticable {

	private IGameClock gameClock;

	public GameStatistics(IGameClock gameTimer) {
		this.gameClock = gameTimer;
	}

	@Override
	public int getGameTime() {
		return gameClock.getTime();
	}

	@Override
	public int getNumberOf(EMaterialType materialType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOf(EMovableType movableType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getJoblessBearers() {
		// TODO Auto-generated method stub
		return 0;
	}

}
