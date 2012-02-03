package jsettlers.main;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.input.GuiTaskExecutor;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.timer.MovableTimer;
import jsettlers.logic.timer.Timer100Milli;
import network.NetworkManager;
import random.RandomSingleton;
import synchronic.timer.NetworkTimer;

/**
 * This is a running jsettlers game. It can be started and then stopped once.
 * 
 * @author michael
 */
public class JSettlersGame {

	private final long randomSheed;

	private boolean stopped = false;
	private final Object stopMutex = new Object();
	private final ISettlersGameDisplay content;
	private final IGameCreator mapcreator;
	private final NetworkManager networkManager;
	private final byte playerNumber;

	private Listener listener;
	private MapInterfaceConnector gameConnector;

	public JSettlersGame(ISettlersGameDisplay content, IGameCreator map, long randomSheed, NetworkManager networkManager, byte playerNumber) {
		this.content = content;
		this.mapcreator = map;
		this.randomSheed = randomSheed;
		this.networkManager = networkManager;
		this.playerNumber = playerNumber;
	}

	/**
	 * Starts the game in a new thread. Returns immeadiately
	 */
	public void start() {
		new Thread(new GameRunner()).start();
	}

	private class GameRunner implements Runnable, IMapInterfaceListener {

		@Override
		public void run() {
			ProgressConnector progress = content.showProgress();

			RandomSingleton.load(randomSheed);

			NetworkTimer.get().setPausing(true);
			Timer100Milli.start();

			progress.setProgressState(EProgressState.LOADING_MAP);

			ImageProvider.getInstance().startPreloading();

			MainGrid grid;

			UIState uiState;
			try {
				grid = mapcreator.getMainGrid();
				uiState = mapcreator.getUISettings(playerNumber);
			} catch (MapLoadException e1) {
				e1.printStackTrace();
				listener.gameEnded();
				return;
			}

			NetworkTimer.get().setPausing(false);

			/** random map */
			// RandomMapFile file =
			// RandomMapFile.getByName(mapSettings.getMap().getName());
			// RandomMapEvaluator evaluator = new
			// RandomMapEvaluator(file.getInstructions(), (byte)
			// mapSettings.getPlayerCount());
			// evaluator.createMap(RandomSingleton.get());
			// IMapData mapGrid = evaluator.getGrid();

			// GameSerializer gameSerializer = new GameSerializer();
			// try {
			// grid = gameSerializer.load();
			// } catch (Exception e) {
			// e.printStackTrace();
			// grid = null;
			// }

			progress.setProgressState(EProgressState.LOADING_IMAGES);

			final MapInterfaceConnector connector = content.showGameMap(grid.getGraphicsGrid(), null);
			GuiInterface guiInterface = new GuiInterface(connector, networkManager, grid.getGuiInputGrid(), playerNumber);

			connector.addListener(this);
			connector.loadUIState(uiState);

			networkManager.startGameTimer(new GuiTaskExecutor(grid.getGuiInputGrid(), guiInterface));

			gameConnector = connector;

			synchronized (stopMutex) {
				while (!stopped) {
					try {
						stopMutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			networkManager.stop();
			Timer100Milli.stop();
			MovableTimer.stop();
			grid.stopGame();

			listener.gameEnded();
		}

		@Override
		public void action(Action action) {
			if (action.getActionType() == EActionType.EXIT) {
				stop();
			}
		}

	}

	public void stop() {
		synchronized (stopMutex) {
			stopped = true;
			stopMutex.notifyAll();
		}
	}

	/**
	 * Defines a listener for this game.
	 * 
	 * @param managedJSettlers
	 */
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public interface Listener {
		void gameEnded();
	}

	public boolean isPaused() {
		return true; // TODO: how do we know that the game is paused
	}

	public void setPaused(boolean b) {
		if (gameConnector != null) {
			gameConnector.fireAction(new Action(b ? EActionType.SPEED_SET_PAUSE : EActionType.SPEED_UNSET_PAUSE));
		}
	}

	public String save() {
		gameConnector.fireAction(new Action(EActionType.SAVE));
		return "savegame";
	}
}
