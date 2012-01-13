package jsettlers.main;

import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.main.JSettlersGame.Listener;

/**
 * This is the new main game class
 * 
 * @author michael
 */
public class ManagedJSettlers implements Listener, IGameStarter {

	private ISettlersGameDisplay content;
	private JSettlersGame ongoingGame;

	public synchronized void start(ISettlersGameDisplay content) {
		this.content = content;
		showMainScreen();
	}

	private void showMainScreen() {
		content.showStartScreen(new StartConnector(this));
	}

	/*
	 * private static class RandomMapItem implements IMapItem {
	 * @Override public String getName() { return "test"; }
	 * @Override public int getMinPlayers() { return 1; }
	 * @Override public int getMaxPlayers() { return 5; }
	 * @Override public IMapDataProvider createLoadableGame(int players, long
	 * seed) { return new RandomGameLoader(getName(), players, seed); } }
	 */

	// private static class RandomGameLoader implements IMapDataProvider {
	// private final String name;
	// private final int players;
	// private final long randomSeed;
	//
	// public RandomGameLoader(String name, int players, long randomSeed) {
	// this.name = name;
	// this.players = players;
	// this.randomSeed = randomSeed;
	// }
	//
	// @Override
	// public IMapData getData() throws MapLoadException {
	// RandomMapFile file = RandomMapFile.getByName(name);
	// RandomMapEvaluator evaluator =
	// new RandomMapEvaluator(file.getInstructions(), players);
	// evaluator.createMap(new Random(randomSeed));
	// IMapData mapGrid = evaluator.getGrid();
	// return mapGrid;
	// }
	// }

	/*
	 * (non-Javadoc)
	 * @see
	 * jsettlers.main.IGameStarter#startGame(jsettlers.graphics.startscreen.
	 * IStartScreenConnector.IGameSettings)
	 */
	@Override
	public void startGame(IGameSettings game) {
		if (ongoingGame != null) {
			ongoingGame.setListener(null);
			ongoingGame.stop();
		}

		IMapItem map = game.getMap();
		if (map instanceof IGameCreator) {
			IGameCreator creator = (IGameCreator) map;
			ongoingGame = new JSettlersGame(content, creator, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
		}
	}

	/**
	 * Game ended from inside the game.
	 */
	@Override
	public void gameEnded() {
		ongoingGame.setListener(null);
		ongoingGame = null;
		showMainScreen();
	}

	/**
	 * Sets the pause status of the ongoing game. Does noting if there is no
	 * game.
	 * 
	 * @param b
	 */
	public void setPaused(boolean b) {
		if (ongoingGame != null) {
			ongoingGame.setPaused(b);
		}
	}

	public boolean isPaused() {
		if (ongoingGame != null) {
			return ongoingGame.isPaused();
		}
		return false;
	}

	public String saveAndStopCurrentGame() {
		if (ongoingGame != null) {
			String id = ongoingGame.save();
			ongoingGame.stop();
			return id;
		} else {
			return null;
		}
	}

}
