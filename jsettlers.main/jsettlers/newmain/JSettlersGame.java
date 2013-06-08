package jsettlers.newmain;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.IStartingGameListener;
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
 * This class can start a Thread that loads and sets up a game and wait's for
 * its termination.
 * 
 * @author Andreas Eberle
 */
public class JSettlersGame {

	private final Object stopMutex = new Object();

	private final long randomSeed;
	private final byte playerNumber;
	private final ITaskScheduler taskScheduler;
	private final boolean multiplayer;
	private final IGameCreator mapcreator;

	private final GameRunner gameRunner;

	private boolean stopped = false;
	private boolean started = false;

	public JSettlersGame(IGameCreator mapCreator, long randomSeed,
			ITaskScheduler taskScheduler, byte playerNumber, boolean multiplayer) {
		this.mapcreator = mapCreator;
		this.randomSeed = randomSeed;
		this.taskScheduler = taskScheduler;
		this.playerNumber = playerNumber;
		this.multiplayer = multiplayer;

		this.gameRunner = new GameRunner();
	}

	/**
	 * Creates a new {@link JSettlersGame} object with an
	 * {@link OfflineTaskScheduler}.
	 * 
	 * @param mapCreator
	 * @param randomSeed
	 * @param playerNumber
	 */
	public JSettlersGame(IGameCreator mapCreator, long randomSeed,
			byte playerNumber) {
		this(mapCreator, randomSeed, new OfflineTaskScheduler(), playerNumber,
				false);
	}

	/**
	 * Starts the game in a new thread. Returns immediately.
	 * 
	 * @return
	 */
	public synchronized IStartingGame start() {
		if (!started) {
			started = true;
			new Thread(null, gameRunner, "GameThread", 128 * 1024).start();
		}
		return gameRunner;
	}

	public void stop() {
		synchronized (stopMutex) {
			stopped = true;
			stopMutex.notifyAll();
		}
	}

	private class GameRunner implements Runnable, IStartingGame, IStartedGame,
			IGameStoppable {
		private IStartingGameListener startingGameListener;
		private MainGrid mainGrid;
		private GameStatistics statistics;
		private EProgressState progressState;
		private float progress;

		@Override
		public void run() {
			try {
				informProgressListener(EProgressState.LOADING, 0.1f);

				IGameClock gameClock = MatchConstants.clock = taskScheduler
						.getGameClock();
				RandomSingleton.load(randomSeed);

				informProgressListener(EProgressState.LOADING_MAP, 0.3f);
				Thread imagePreloader = ImageProvider.getInstance()
						.startPreloading();

				mainGrid = mapcreator.getMainGrid(playerNumber);
				UIState uiState = mapcreator.getUISettings(playerNumber);

				informProgressListener(EProgressState.LOADING_IMAGES, 0.7f);
				statistics = new GameStatistics(gameClock);
				mainGrid.startThreads();

				imagePreloader.join(); // Wait for ImageProvider to finish
										// loading the images
				// TODO @Andreas Eberle: Wait for startingGameListener to be
				// set.

				final MapInterfaceConnector connector = startingGameListener
						.startFinished(this);
				connector.loadUIState(uiState.getUiStateData());

				GuiInterface guiInterface = new GuiInterface(connector,
						gameClock, taskScheduler, mainGrid.getGuiInputGrid(),
						this, playerNumber, multiplayer);
				gameClock.startExecution();

				synchronized (stopMutex) {
					while (!stopped) {
						try {
							stopMutex.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				gameClock.stopExecution();
				connector.stop();
				mainGrid.stopThreads();
				guiInterface.stop();
				Timer100Milli.stop();
				MovableTimer.stop();
				PartitionManagerTimer.stop();
				NewMovable.dropAllMovables();
				Building.dropAllBuildings();

				MatchConstants.clock = null;
			} catch (MapLoadException e) {
				e.printStackTrace();
				reportFail(EGameError.MAPLOADING_ERROR, e);
			} catch (Exception e) {
				e.printStackTrace();
				reportFail(EGameError.UNKNOWN_ERROR, e);
			}
		}

		private void informProgressListener(EProgressState progressState,
				float progress) {
			this.progressState = progressState;
			this.progress = progress;
			if (startingGameListener != null)
				startingGameListener.startProgressChanged(progressState,
						progress);
		}

		private void reportFail(EGameError gameError, Exception e) {
			if (startingGameListener != null)
				startingGameListener.startFailed(gameError, e);
		}

		// METHODS of IStartingGame
		// ====================================================
		@Override
		public void setListener(IStartingGameListener startingGameListener) {
			this.startingGameListener = startingGameListener;
			if (startingGameListener != null)
				startingGameListener.startProgressChanged(progressState,
						progress);
		}

		@Override
		public void abort() {
			stop();
		}

		// METHODS of IStartedGame
		// ======================================================
		@Override
		public IGraphicsGrid getMap() {
			return mainGrid.getGraphicsGrid();
		}

		@Override
		public IStatisticable getPlayerStatistics() {
			return statistics;
		}

		@Override
		public int getPlayer() {
			return playerNumber;
		}

		@Override
		public void stopGame() {
			stop();
		}
	}
}
