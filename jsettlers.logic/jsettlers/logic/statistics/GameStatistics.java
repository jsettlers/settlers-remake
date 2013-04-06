package jsettlers.logic.statistics;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;
import synchronic.timer.NetworkTimer;

/**
 * This class supplies the UI with statistics of the game.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameStatistics implements IStatisticable {

	private NetworkTimer gameTimer;

	public GameStatistics(NetworkTimer gameTimer) {
		this.gameTimer = gameTimer;
	}

	@Override
	public int getGameTime() {
		return gameTimer.getGameTime();
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
