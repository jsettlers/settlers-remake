package jsettlers.main;

import random.RandomSingleton;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.input.GuiInterface;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.timer.Timer100Milli;
import network.INetworkManager;
import network.NullNetworkManager;

/**
 * This is a running jsettlers game. It can be started and then stopped once.
 * 
 * @author michael
 */
public class JSettlersGame {

	private final IGameSettings map;
	private final long randomSheed;

	private boolean stopped = false;
	private Object stopMutex = new Object();
	private final ISettlersGameDisplay content;

	private Listener listener;

	/**
	 * Creates a new game by a given random mapname.
	 * 
	 * @param map
	 *            The name of the random map and some settings
	 */
	public JSettlersGame(ISettlersGameDisplay content, IGameSettings map, long randomSheed) {
		this.content = content;
		this.map = map;
		this.randomSheed = randomSheed;
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
			INetworkManager manager = new NullNetworkManager();

			ProgressConnector progress = content.showProgress();

			RandomSingleton.load(randomSheed);

			Timer100Milli.start();

			progress.setProgressState(EProgressState.LOADING_MAP);

			/** random map */
			RandomMapFile file = RandomMapFile.getByName(map.getMap().getName());
			RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), (byte) map.getPlayerCount());
			evaluator.createMap(RandomSingleton.get());
			MapGrid mapGrid = evaluator.getGrid();
			
			MainGrid grid = MainGrid.create(mapGrid);

			progress.setProgressState(EProgressState.LOADING_IMAGES);

			MapInterfaceConnector connector =
			        content.showGameMap(grid.getGraphicsGrid(), null);
			new GuiInterface(connector, manager, grid.getGuiInputGrid());

			connector.addListener(this);
			connector.scrollTo(mapGrid.getStartPoint(0), false);
			manager.startGameTimer();

			// TODO: allow user to stop game before this happens.
			synchronized (stopMutex) {
				while (!stopped) {
					try {
						stopMutex.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			manager.close();
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
}
