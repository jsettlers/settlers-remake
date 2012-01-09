package jsettlers.main;

import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.startscreen.IStartScreenConnector;
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

	private class StartConnector implements IStartScreenConnector {

		private final IMapItem[] MAPS = new IMapItem[] { new MapItem() };

		@Override
		public IMapItem[] getMaps() {
			return MAPS;
		}

		private class MapItem implements IMapItem {
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
			ongoingGame = new JSettlersGame(content, game, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
		}

		@Override
		public void loadGame(ILoadableGame load) {
			if (ongoingGame != null) {
				ongoingGame.setListener(null);
				ongoingGame.stop();
			}
			ongoingGame = new JSettlersGame(content, load, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
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
	 * Sets the pause status of the ongoing game. Does noting if there is no game.
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
