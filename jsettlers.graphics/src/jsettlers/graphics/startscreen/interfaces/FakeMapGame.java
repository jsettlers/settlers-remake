package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;

/**
 * This is a simple implementation of {@link IStartedGame} that allows you to supply a map as a game.
 * 
 * @author michael
 */
public class FakeMapGame implements IStartedGame {

	public final class NullStatistics implements IStatisticable {
		@Override
		public int getGameTime() {
			return 0;
		}

		@Override
		public int getNumberOf(EMovableType movableType) {
			return 0;
		}

		@Override
		public int getNumberOf(EMaterialType materialType) {
			return 0;
		}

		@Override
		public int getJoblessBearers() {
			return 0;
		}
	}

	private final IGraphicsGrid map;

	public FakeMapGame(IGraphicsGrid map) {
		this.map = map;
	}

	@Override
	public IGraphicsGrid getMap() {
		return map;
	}

	@Override
	public IStatisticable getPlayerStatistics() {
		return new NullStatistics();
	}

	@Override
	public void setGameExitListener(IGameExitListener exitListener) {
	}

}
