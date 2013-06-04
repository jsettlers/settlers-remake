package jsettlers.main;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.input.IGameStoppable;
import jsettlers.input.UIState;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.statistics.GameStatistics;
import jsettlers.logic.timer.MovableTimer;
import jsettlers.logic.timer.PartitionManagerTimer;
import jsettlers.logic.timer.Timer100Milli;
import networklib.client.OfflineTaskScheduler;
import networklib.client.interfaces.IGameClock;
import networklib.client.interfaces.ITaskScheduler;
import networklib.synchronic.random.RandomSingleton;

/**
 * This is a running jsettlers game. It can be started and then stopped once.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public class JSettlersGame {

	private final long randomSeed;

	private final Object stopMutex = new Object();
	private final ISettlersGameDisplay content;
	private final byte playerNumber;
	private final ITaskScheduler taskScheduler;
	private final boolean multiplayer;

	private IGameCreator mapcreator;

	private boolean stopped = false;
	private GameEndedListener listener;
	private MapInterfaceConnector gameConnector;
	private boolean started = false;

	public JSettlersGame(ISettlersGameDisplay content, IGameCreator mapCreator, long randomSeed, ITaskScheduler taskScheduler,
			byte playerNumber, boolean multiplayer) {
		this.content = content;
		this.mapcreator = mapCreator;
		this.randomSeed = randomSeed;
		this.taskScheduler = taskScheduler;
		this.playerNumber = playerNumber;
		this.multiplayer = multiplayer;
	}

	public JSettlersGame(ISettlersGameDisplay content, IGameCreator mapCreator, long randomSeed, byte playerNumber) {
		this(content, mapCreator, randomSeed, new OfflineTaskScheduler(), playerNumber, false);
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

	private class GameRunner implements Runnable, IGameStoppable {

		@Override
		public void run() {
			IGameClock gameClock = MatchConstants.clock = taskScheduler.getGameClock();

			ProgressConnector progress = content.showProgress();

			gameClock.setPausing(true);
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

			gameClock.setPausing(false);

			// load images
			progress.setProgressState(EProgressState.LOADING_IMAGES, 0.8f);

			final MapInterfaceConnector connector = content.showGameMap(grid.getGraphicsGrid(), new GameStatistics(gameClock));
			GuiInterface guiInterface = new GuiInterface(connector, gameClock, taskScheduler, grid.getGuiInputGrid(), this, playerNumber, multiplayer);

			connector.loadUIState(uiState.getUiStateData());

			grid.startThreads();
			gameClock.startExecution();

			gameConnector = connector;

			synchronized (stopMutex) {
				while (!stopped) {
					try {
						stopMutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			gameClock.setPausing(true);
			connector.stop();
			grid.stopThreads();
			guiInterface.stop();
			Timer100Milli.stop();
			MovableTimer.stop();
			PartitionManagerTimer.stop();
			NewMovable.dropAllMovables();
			Building.dropAllBuildings();

			listener.gameEnded();
		}

		@Override
		public void stopGame() {
			stop();
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
	 * @param The
	 *            listener that will be informed when the game ended.
	 */
	public void setGameEndedListener(GameEndedListener listener) {
		this.listener = listener;
	}

	public boolean isPaused() {
		return taskScheduler.getGameClock().isPausing();
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

	public static interface GameEndedListener {
		void gameEnded();
	}
}
