package jsettlers.main;

import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.IMapDataProvider;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.main.JSettlersGame.Listener;

/**
 * This is the new main game class
 * 
 * @author michael
 */
public class ManagedJSettlers implements Listener {

	private ISettlersGameDisplay content;
	private JSettlersGame ongoingGame;

	public synchronized void start(ISettlersGameDisplay content) {
		this.content = content;
		showMainScreen();
	}

	private void showMainScreen() {
		content.showStartScreen(new StartConnector());
	}

	private static class RandomMapItem implements IMapItem {
		@Override
		public String getName() {
			return "test";
		}

		@Override
		public int getMinPlayers() {
			return 1;
		}

		@Override
		public int getMaxPlayers() {
			return 5;
		}

		@Override
		public IMapDataProvider createLoadableGame(int players, long seed) {
			return new RandomGameLoader(getName(), players, seed);
		}
	}

	private static class RandomGameLoader implements IMapDataProvider {
		private final String name;
		private final int players;
		private final long randomSeed;

		public RandomGameLoader(String name, int players, long randomSeed) {
			this.name = name;
			this.players = players;
			this.randomSeed = randomSeed;
		}

		@Override
		public IMapData getData() throws MapLoadException {
			RandomMapFile file = RandomMapFile.getByName(name);
			RandomMapEvaluator evaluator =
			        new RandomMapEvaluator(file.getInstructions(), players);
			evaluator.createMap(new Random(randomSeed));
			IMapData mapGrid = evaluator.getGrid();
			return mapGrid;
		}
	}

	private class StartConnector implements IStartScreenConnector {
		private final IMapItem[] MAPS = new IMapItem[] {
			new RandomMapItem()
		};

		@Override
		public IMapItem[] getMaps() {
			return MAPS;
		}

		@Override
		public ILoadableGame[] getLoadableGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IRecoverableGame[] getRecoverableGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public INetworkGame[] getNetworkGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setNetworkServer(String host) {
			// TODO Auto-generated method stub

		}

		@Override
		public void startNewGame(IGameSettings game) {
			if (ongoingGame != null) {
				ongoingGame.setListener(null);
				ongoingGame.stop();
			}
			IMapDataProvider provider =
			        game.getMap().createLoadableGame(game.getPlayerCount(),
			                123456L);
			ongoingGame = new JSettlersGame(content, provider, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
		}

		@Override
		public void loadGame(ILoadableGame load) {
			if (ongoingGame != null) {
				ongoingGame.setListener(null);
				ongoingGame.stop();
			}
			if (load instanceof SavedGame) {
				ongoingGame =
				        new JSettlersGame(content, (SavedGame) load, 123456L);
				ongoingGame.setListener(ManagedJSettlers.this);
				ongoingGame.start();
			} else {
				showMainScreen();
			}
		}

		@Override
		public void recoverNetworkGame(IRecoverableGame game) {
			// TODO Auto-generated method stub

		}

		@Override
		public void joinNetworkGame(INetworkGame game) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addNetworkGameListener(INetworkGameListener gameListener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeNetworkGameListener(INetworkGameListener gameListener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void startGameServer(IGameSettings game, String name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void exitGame() {
			System.exit(0);
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
