/*******************************************************************************
 * Copyright (c) 2015, 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import java8.util.Optional;
import jsettlers.ai.highlevel.AiExecutor;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.statistics.IGameTimeProvider;
import jsettlers.input.GuiInterface;
import jsettlers.input.IGameStoppable;
import jsettlers.input.PlayerState;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.trading.HarborBuilding;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.map.loading.IGameCreator;
import jsettlers.logic.map.loading.IGameCreator.MainGridWithUiSettings;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.logic.timer.RescheduleTimer;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.network.client.interfaces.INetworkConnector;

/**
 * This class can start a Thread that loads and sets up a game and wait's for its termination.
 *
 * @author Andreas Eberle
 */
public class JSettlersGame {
	private static final SimpleDateFormat LOG_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
	private final Object stopMutex = new Object();

	private final IGameCreator mapCreator;
	private final long randomSeed;
	private final byte playerId;
	private final PlayerSetting[] playerSettings;
	private final INetworkConnector networkConnector;
	private final boolean multiplayer;
	private final DataInputStream replayFileInputStream;

	private final GameRunner gameRunner;

	private boolean started = false;
	private boolean stopped = false;
	private boolean shutdownFinished;

	private PrintStream systemErrorStream;
	private PrintStream systemOutStream;

	private JSettlersGame(IGameCreator mapCreator, long randomSeed, INetworkConnector networkConnector, byte playerId,
			PlayerSetting[] playerSettings, boolean controlAll, boolean multiplayer, DataInputStream replayFileInputStream) {
		configureLogging(mapCreator);

		System.out.println("OS version: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version"));
		System.out.println("Java version: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
		System.out.println("JsettlersGame(): seed: " + randomSeed + " playerId: " + playerId + " availablePlayers: "
				+ Arrays.toString(playerSettings) + " multiplayer: " + multiplayer + " mapCreator: " + mapCreator);

		if (mapCreator == null) {
			throw new IllegalArgumentException("No mapCreator provided (mapCreator==null).");
		}

		this.mapCreator = mapCreator;
		this.randomSeed = randomSeed;
		this.networkConnector = networkConnector;
		this.playerId = playerId;
		this.playerSettings = playerSettings;
		this.multiplayer = multiplayer;
		this.replayFileInputStream = replayFileInputStream;

		MatchConstants.ENABLE_ALL_PLAYER_FOG_OF_WAR = controlAll;
		MatchConstants.ENABLE_ALL_PLAYER_SELECTION = controlAll;
		MatchConstants.ENABLE_FOG_OF_WAR_DISABLING = controlAll;
		MatchConstants.ENABLE_DEBUG_COLORS = controlAll;

		this.gameRunner = new GameRunner();
	}

	/**
	 * @param mapCreator
	 * @param randomSeed
	 * @param networkConnector
	 * @param playerId
	 */
	public JSettlersGame(IGameCreator mapCreator, long randomSeed, INetworkConnector networkConnector, byte playerId, PlayerSetting[] playerSettings) {
		this(mapCreator, randomSeed, networkConnector, playerId, playerSettings, CommonConstants.CONTROL_ALL, true, null);
	}

	/**
	 * Creates a new {@link JSettlersGame} object with an {@link OfflineNetworkConnector}.
	 *
	 * @param mapCreator
	 * @param randomSeed
	 * @param playerId
	 */
	public JSettlersGame(IGameCreator mapCreator, long randomSeed, byte playerId, PlayerSetting[] playerSettings) {
		this(mapCreator, randomSeed, new OfflineNetworkConnector(), playerId, playerSettings, CommonConstants.CONTROL_ALL, false, null);
	}

	public static JSettlersGame loadFromReplayFile(ReplayUtils.IReplayStreamProvider loadableReplayFile, INetworkConnector networkConnector, ReplayStartInformation replayStartInformation)
			throws MapLoadException {
		try {
			DataInputStream replayFileInputStream = new DataInputStream(loadableReplayFile.openStream());
			replayStartInformation.deserialize(replayFileInputStream);

			MapLoader mapCreator = loadableReplayFile.getMap(replayStartInformation);
			return new JSettlersGame(mapCreator, replayStartInformation.getRandomSeed(), networkConnector, (byte) replayStartInformation.getPlayerId(),
					replayStartInformation.getReplayablePlayerSettings(), true, false, replayFileInputStream);
		} catch (IOException e) {
			throw new MapLoadException("Could not deserialize " + loadableReplayFile, e);
		}
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
			printEndgameStatistic();
			stopped = true;
			stopMutex.notifyAll();
		}
	}

	// TODO remove me when an EndgameStatistic screen exists.
	private void printEndgameStatistic() {
		PartitionsGrid partitionsGrid = gameRunner.getMainGrid().getPartitionsGrid();
		System.out.println("Endgame statistic:");
		for (byte playerId = 0; playerId < partitionsGrid.getNumberOfPlayers(); playerId++) {
			Player player = partitionsGrid.getPlayer(playerId);
			if (player != null) {
				System.out.println("Player " + playerId + ": " + player.getEndgameStatistic());
			}
		}
	}

	protected OutputStream createReplayWriteStream() throws IOException {
		final String replayFilename = getLogFile(mapCreator, "_replay.log");
		return ResourceManager.writeUserFile(replayFilename);
	}

	public class GameRunner implements Runnable, IStartingGame, IStartedGame, IGameStoppable {
		private IStartingGameListener startingGameListener;
		private MainGrid mainGrid;
		private GameTimeProvider gameTimeProvider;
		private EProgressState progressState;
		private float progress;
		private IGameExitListener exitListener;
		private boolean gameRunning;
		private AiExecutor aiExecutor;

		@Override
		public void run() {
			try {
				if (startingGameListener != null) {
					startingGameListener.startingLoadingGame();
				}
				updateProgressListener(EProgressState.LOADING, 0.1f);

				clearState();
				MatchConstants.init(networkConnector.getGameClock(), randomSeed);
				try {
					MatchConstants.clock().setReplayLogStream(createReplayFileStream());
				} catch (IOException e) {
					// TODO: log that we do not have write access to resources.
					System.out.println("Cannot write jsettlers.integration.replay file.");
				}

				updateProgressListener(EProgressState.LOADING_MAP, 0.3f);

				MainGridWithUiSettings gridWithUiState = mapCreator.loadMainGrid(playerSettings);
				mainGrid = gridWithUiState.getMainGrid();
				PlayerState playerState = gridWithUiState.getPlayerState(playerId);

				RescheduleTimer.schedule(MatchConstants.clock()); // schedule timer

				updateProgressListener(EProgressState.LOADING_IMAGES, 0.7f);
				gameTimeProvider = new GameTimeProvider(MatchConstants.clock());

				mainGrid.initForPlayer(playerId, playerState.getFogOfWar());
				mainGrid.startThreads();

				waitForStartingGameListener();
				startingGameListener.waitForPreloading();

				updateProgressListener(EProgressState.WAITING_FOR_OTHER_PLAYERS, 0.98f);

				if (replayFileInputStream != null) {
					MatchConstants.clock().loadReplayLogFromStream(replayFileInputStream);
				}

				networkConnector.setStartFinished(true);
				waitForAllPlayersStartFinished(networkConnector);

				final IMapInterfaceConnector connector = startingGameListener.preLoadFinished(this);
				GuiInterface guiInterface = new GuiInterface(connector, MatchConstants.clock(), networkConnector.getTaskScheduler(),
						mainGrid.getGuiInputGrid(), this, playerId, multiplayer);
				connector.loadUIState(playerState.getUiState()); // This is required after the GuiInterface instantiation so that
				// ConstructionMarksThread has it's mapArea variable initialized via the EActionType.SCREEN_CHANGE event.

				aiExecutor = new AiExecutor(playerSettings, mainGrid, networkConnector.getTaskScheduler());
				networkConnector.getGameClock().schedule(aiExecutor, (short) 10000);

				MatchConstants.clock().startExecution(); // WARNING: GAME CLOCK IS STARTED!
				// NO CONFIGURATION AFTER THIS POINT! =================================
				gameRunning = true;

				startingGameListener.startFinished();

				synchronized (stopMutex) {
					while (!stopped) {
						try {
							stopMutex.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				networkConnector.shutdown();
				mainGrid.stopThreads();
				connector.shutdown();
				guiInterface.stop();
				clearState();

				System.setErr(systemErrorStream);
				System.setOut(systemOutStream);

			} catch (MapLoadException e) {
				e.printStackTrace();
				reportFail(EGameError.MAPLOADING_ERROR, e);
			} catch (Exception e) {
				e.printStackTrace();
				reportFail(EGameError.UNKNOWN_ERROR, e);
			} finally {
				shutdownFinished = true;
				if (exitListener != null) {
					exitListener.gameExited(this);
				}
			}
		}

		public AiExecutor getAiExecutor() {
			return aiExecutor;
		}

		private DataOutputStream createReplayFileStream() throws IOException {
			DataOutputStream replayFileStream = new DataOutputStream(createReplayWriteStream());

			ReplayStartInformation replayInfo = new ReplayStartInformation(randomSeed, mapCreator.getMapName(), mapCreator.getMapId(), playerId,
					playerSettings);
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
					Thread.sleep(5L);
				} catch (InterruptedException e) {
				}
			}
		}

		private void waitForAllPlayersStartFinished(INetworkConnector networkConnector) {
			while (!networkConnector.haveAllPlayersStartFinished()) {
				try {
					Thread.sleep(5L);
				} catch (InterruptedException e) {
				}
			}
		}

		private void updateProgressListener(EProgressState progressState, float progress) {
			this.progressState = progressState;
			this.progress = progress;

			if (startingGameListener != null) {
				startingGameListener.startProgressChanged(progressState, progress);
			}
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

		// METHODS of IStartedGame
		// ======================================================
		@Override
		public IGraphicsGrid getMap() {
			return mainGrid.getGraphicsGrid();
		}

		@Override
		public IGameTimeProvider getGameTimeProvider() {
			return gameTimeProvider;
		}

		@Override
		public IInGamePlayer getInGamePlayer() {
			return mainGrid.getPartitionsGrid().getPlayer(playerId);
		}

		@Override
		public boolean isShutdownFinished() {
			return shutdownFinished;
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

		public MainGrid getMainGrid() {
			return mainGrid;
		}
	}

	private void configureLogging(final IGameCreator mapcreator) {
		try {
			systemErrorStream = System.err;
			systemOutStream = System.out;

			if (!CommonConstants.ENABLE_CONSOLE_LOGGING) {
				PrintStream outLogStream = new PrintStream(ResourceManager.writeUserFile(getLogFile(mapcreator, "_out.log")));
				System.setOut(outLogStream);
				System.setErr(outLogStream);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error setting up logging.", ex);
		}
	}

	private static String getLogFile(IGameCreator mapcreator, String suffix) {
		final String dateAndMap = getLogDateFormatter().format(new Date()) + "_" + mapcreator.getMapName().replace(" ", "_");
		final String logFolder = "logs/" + dateAndMap + "/";

		return logFolder + dateAndMap + suffix;
	}

	private static DateFormat getLogDateFormatter() {
		return LOG_DATE_FORMATTER;
	}

	public static void clearState() {
		RescheduleTimer.stopAndClear();
		Movable.resetState();
		Building.clearState();
		MarketBuilding.clearState();
		HarborBuilding.clearState();
		MatchConstants.clearState();
	}
}
