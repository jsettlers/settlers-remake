package jsettlers.main;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.input.GuiTaskExecutor;
import jsettlers.input.UIState;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.statistics.GameStatistics;
import jsettlers.logic.timer.MovableTimer;
import jsettlers.logic.timer.PartitionManagerTimer;
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

	private final long randomSeed;

	private boolean stopped = false;
	private final Object stopMutex = new Object();
	private final ISettlersGameDisplay content;
	private IGameCreator mapcreator;
	private final NetworkManager networkManager;
	private final byte playerNumber;

	private Listener listener;
	private MapInterfaceConnector gameConnector;
	private boolean started = false;

	public JSettlersGame(ISettlersGameDisplay content, IGameCreator mapCreator, long randomSeed, NetworkManager networkManager, byte playerNumber) {
		this.content = content;
		this.mapcreator = mapCreator;
		this.randomSeed = randomSeed;
		this.networkManager = networkManager;
		this.playerNumber = playerNumber;
	}

	/**
	 * Starts the game in a new thread. Returns immediately.
	 */
	public synchronized void start() {
		if (!started) {
			started = true;

			new Thread(null, new GameRunner(), "game", 128 * 1024).start();
		}
	}

	private class GameRunner implements Runnable, IMapInterfaceListener {

		@Override
		public void run() {
			ProgressConnector progress = content.showProgress();

			NetworkTimer gameTimer = NetworkTimer.get();
			gameTimer.setPausing(true);
			RandomSingleton.load(randomSeed);

			progress.setProgressState(EProgressState.LOADING_MAP, 0.1f);

			ImageProvider.getInstance().startPreloading();

			MainGrid grid;

			UIState uiState;
			try {
				grid = mapcreator.getMainGrid(playerNumber);
				uiState = mapcreator.getUISettings(playerNumber);
				mapcreator = null;
			} catch (MapLoadException e1) {
				e1.printStackTrace();
				listener.gameEnded();
				return;
			}

			gameTimer.setPausing(false);

			// load images
			progress.setProgressState(EProgressState.LOADING_IMAGES, 0.8f);

			final MapInterfaceConnector connector = content.showGameMap(grid.getGraphicsGrid(), new GameStatistics(NetworkTimer.get()));
			GuiInterface guiInterface = new GuiInterface(connector, networkManager, grid.getGuiInputGrid(), playerNumber);

			connector.addListener(this);
			connector.scrollTo(uiState.getScreenCenter(), false);

			grid.startThreads();
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

			NetworkTimer.get().setPausing(true);
			connector.stop();
			grid.stopThreads();
			guiInterface.stop();
			Timer100Milli.stop();
			MovableTimer.stop();
			PartitionManagerTimer.stop();
			NewMovable.dropAllMovables();
			Building.dropAllBuildings();
			networkManager.stop();

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
