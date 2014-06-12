package jsettlers.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.startscreen.interfaces.EGameError;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
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
import jsettlers.logic.map.save.IGameCreator.MainGridWithUiSettings;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.statistics.GameStatistics;
import jsettlers.logic.timer.RescheduleTimer;
import networklib.client.OfflineNetworkConnector;
import networklib.client.interfaces.IGameClock;
import networklib.client.interfaces.INetworkConnector;
import networklib.synchronic.random.RandomSingleton;

/**
 * This class can start a Thread that loads and sets up a game and wait's for its termination.
 * 
 * @author Andreas Eberle
 */
public class JSettlersGame {
	private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private final Object stopMutex = new Object();

	private final IGameCreator mapcreator;
	private final long randomSeed;
	private final byte playerNumber;
	private final INetworkConnector networkConnector;
	private final boolean multiplayer;
	private final DataInputStream replayFileInputStream;

	private final GameRunner gameRunner;

	private boolean stopped = false;
	private boolean started = false;

	private JSettlersGame(IGameCreator mapCreator, long randomSeed, INetworkConnector networkConnector, byte playerNumber, boolean multiplayer,
			DataInputStream replayFileInputStream) {
		this.mapcreator = mapCreator;
		this.randomSeed = randomSeed;
		this.networkConnector = networkConnector;
		this.playerNumber = playerNumber;
		this.multiplayer = multiplayer;
		this.replayFileInputStream = replayFileInputStream;

		this.gameRunner = new GameRunner();
	}

	public JSettlersGame(IGameCreator mapCreator, long randomSeed, INetworkConnector networkConnector, byte playerNumber) {
		this(mapCreator, randomSeed, networkConnector, playerNumber, true, null);
	}

	/**
	 * Starts a single player game.
	 * 
	 * @param mapCreator
	 * @param randomSeed
	 * @param playerNumber
	 * @param replayFileInputStream
	 */
	private JSettlersGame(IGameCreator mapCreator, long randomSeed, byte playerNumber, DataInputStream replayFileInputStream) {
		this(mapCreator, randomSeed, new OfflineNetworkConnector(), playerNumber, false, replayFileInputStream);
	}

	/**
	 * Creates a new {@link JSettlersGame} object with an {@link OfflineNetworkConnector}.
	 * 
	 * @param mapCreator
	 * @param randomSeed
	 * @param playerNumber
	 */
	public JSettlersGame(IGameCreator mapCreator, long randomSeed, byte playerNumber) {
		this(mapCreator, randomSeed, playerNumber, null);
	}

	public static JSettlersGame loadFromReplayFile(File loadableReplayFile, INetworkConnector networkConnector) throws IOException {
		DataInputStream replayFileInputStream = new DataInputStream(new FileInputStream(loadableReplayFile));
		ReplayStartInformation replayStartInformation = new ReplayStartInformation();
		replayStartInformation.deserialize(replayFileInputStream);

		MapLoader mapCreator = MapList.getDefaultList().getMapById(replayStartInformation.getMapId());
		return new JSettlersGame(mapCreator, replayStartInformation.getRandomSeed(), networkConnector,
				(byte) replayStartInformation.getPlayerNumber(), false, replayFileInputStream);
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

	private class GameRunner implements Runnable, IStartingGame, IStartedGame, IGameStoppable {
		private IStartingGameListener startingGameListener;
		private MainGrid mainGrid;
		private GameStatistics statistics;
		private EProgressState progressState;
		private float progress;
		private IGameExitListener exitListener;
		private boolean gameRunning;

		@Override
		public void run() {
			try {
				updateProgressListener(EProgressState.LOADING, 0.1f);

				DataOutputStream replayFileStream = createReplayFileStream();

				IGameClock gameClock = MatchConstants.clock = networkConnector.getGameClock();
				gameClock.setReplayLogStream(replayFileStream);
				RandomSingleton.load(randomSeed);

				updateProgressListener(EProgressState.LOADING_MAP, 0.3f);
				Thread imagePreloader = ImageProvider.getInstance().startPreloading();

				MainGridWithUiSettings gridWithUiState = mapcreator.loadMainGrid(playerNumber);
				mainGrid = gridWithUiState.getMainGrid();
				UIState uiState = gridWithUiState.getUiState();

				RescheduleTimer.schedule(gameClock); // schedule timer

				updateProgressListener(EProgressState.LOADING_IMAGES, 0.7f);
				statistics = new GameStatistics(gameClock);
				mainGrid.startThreads();

				imagePreloader.join(); // Wait for ImageProvider to finish loading the images

				waitForStartingGameListener();

				updateProgressListener(EProgressState.WAITING_FOR_OTHER_PLAYERS, 0.98f);

				networkConnector.setStartFinished(true);
				waitForAllPlayersStartFinished(networkConnector);

				final IMapInterfaceConnector connector = startingGameListener.startFinished(this);
				connector.loadUIState(uiState.getUiStateData());

				GuiInterface guiInterface = new GuiInterface(connector, gameClock, networkConnector.getTaskScheduler(), mainGrid.getGuiInputGrid(),
						this, playerNumber, multiplayer);

				if (replayFileInputStream != null) {
					gameClock.loadReplayLogFromStream(replayFileInputStream);
				}

				gameClock.startExecution();
				gameRunning = true;

				synchronized (stopMutex) {
					while (!stopped) {
						try {
							stopMutex.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				networkConnector.shutdown();
				gameClock.stopExecution();
				connector.shutdown();
				mainGrid.stopThreads();
				guiInterface.stop();
				RescheduleTimer.stop();
				NewMovable.dropAllMovables();
				Building.dropAllBuildings();
			} catch (MapLoadException e) {
				e.printStackTrace();
				reportFail(EGameError.MAPLOADING_ERROR, e);
			} catch (Exception e) {
				e.printStackTrace();
				reportFail(EGameError.UNKNOWN_ERROR, e);
			}
			if (exitListener != null) {
				exitListener.gameExited(this);
			}
		}

		private DataOutputStream createReplayFileStream() throws IOException {
			final String dateAndMap = logDateFormat.format(new Date()) + "_" + mapcreator.getMapName();
			final String replayFilename = "logs/" + dateAndMap + "/" + dateAndMap + "_replay.log";
			DataOutputStream replayFileStream = new DataOutputStream(ResourceManager.writeFile(replayFilename));

			ReplayStartInformation replayInfo = new ReplayStartInformation(randomSeed, playerNumber, mapcreator.getMapName(), mapcreator.getMapID());
			replayInfo.serialize(replayFileStream);
			replayFileStream.flush();

			return replayFileStream;
		}

		/**
		 * Waits until the {@link #startingGameListener} has been set.
		 */
		private void waitForStartingGameListener() {
			while (startingGameListener == null) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
		}

		private void waitForAllPlayersStartFinished(INetworkConnector networkConnector) {
			while (!networkConnector.haveAllPlayersStartFinished()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
		}

		private void updateProgressListener(EProgressState progressState,
				float progress) {
			this.progressState = progressState;
			this.progress = progress;

			if (startingGameListener != null)
				startingGameListener.startProgressChanged(progressState, progress);
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
				startingGameListener.startProgressChanged(progressState, progress);
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

		@Override
		public void setGameExitListener(IGameExitListener exitListener) {
			this.exitListener = exitListener;
		}

		@Override
		public boolean isStartupFinished() {
			return gameRunning;
		}
	}
}
