package jsettlers.main;

import jsettlers.common.network.IMatch;
import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.main.JSettlersGame.Listener;
import jsettlers.main.NetworkStarter.INetworkStartListener;
import jsettlers.network.client.ClientThread;
import jsettlers.network.server.match.MatchDescription;

/**
 * This is the new main game class
 * 
 * @author michael
 */
public class ManagedJSettlers implements Listener, IGameStarter,
        INetworkStartListener {

	private ISettlersGameDisplay content;
	private JSettlersGame ongoingGame;
	private NetworkConnectTask networkConnectTask;

	public synchronized void start(ISettlersGameDisplay content) {
		this.content = content;
		showMainScreen();
	}

	private synchronized void showMainScreen() {
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
	public synchronized void startGame(IGameSettings game) {
		stopOldStuff();

		IMapItem map = game.getMap();
		if (map instanceof IGameCreator) {
			IGameCreator creator = (IGameCreator) map;
			ongoingGame = new JSettlersGame(content, creator, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
		}
	}

	@Override
	public synchronized void loadGame(ILoadableGame load) {
		stopOldStuff();

		if (load instanceof IGameCreator) {
			IGameCreator creator = (IGameCreator) load;
			ongoingGame = new JSettlersGame(content, creator, 123456L);
			ongoingGame.setListener(ManagedJSettlers.this);
			ongoingGame.start();
		}
	}

	private synchronized void stopOldStuff() {
		if (ongoingGame != null) {
			ongoingGame.setListener(null);
			ongoingGame.stop();
		}

		// TODO: stop main screen
		if (networkConnectTask != null) {
			networkConnectTask.cancel();
		}
		networkConnectTask = null;
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

	@Override
	public synchronized void startNetworkGame(String server,
	        IMatchSettings gameSettings) {
		ProgressConnector connector = content.showProgress();
		connector.setProgressState(EProgressState.STARTING_SERVER);
		NetworkStarter starter = new NetworkStarter(server, gameSettings, this);
		starter.start(connector);
		networkConnectTask = starter;
	}

	@Override
	public void networkGameStartFailed(NetworkConnectTask starter) {
		if (starter == networkConnectTask) {
			networkConnectTask = null;
			showMainScreen();
		}
	}

	@Override
	public void networkGameStarted(NetworkConnectTask starter,
	        ClientThread clientThread, MatchDescription description) {
		if (starter == networkConnectTask) {
			networkConnectTask = null;
			System.out
			        .println("now the network game screen should be displayed.");
			
			NetworkScreenAdapter networkScreen = new NetworkScreenAdapter(clientThread, description);
			content.showNetworkScreen(networkScreen);
		}
	}

	@Override
	public void joinNetworkGame(String serverAddress, IMatch match) {
		ProgressConnector connector = content.showProgress();
		connector.setProgressState(EProgressState.JOINING_GAME);
		NetworkJoiner joiner = new NetworkJoiner(serverAddress, match, this);
		joiner.start();
		networkConnectTask = joiner;
	}

}
