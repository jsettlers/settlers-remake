package jsettlers.main;

import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.main.JSettlersGame.GameEndedListener;
import networklib.client.OfflineTaskScheduler;

/**
 * This is the new main game class.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public class ManagedJSettlers implements GameEndedListener, IErrorDisplayer {

	private ISettlersGameDisplay display;
	private JSettlersGame ongoingGame;

	public synchronized void start(ISettlersGameDisplay content) {
		this.display = content;
		showMainScreen();
	}

	private synchronized void showMainScreen() {
		display.showStartScreen(new StartConnector(this));
	}

	public synchronized void startGame(IGameCreator gameCreator) {
		stop();

		// TODO: pass on player count
		OfflineTaskScheduler taskScheduler = new OfflineTaskScheduler();
		ongoingGame = new JSettlersGame(display, gameCreator, 123456L, taskScheduler, (byte) 0, false);
		ongoingGame.setGameEndedListener(ManagedJSettlers.this);
		ongoingGame.start();
	}

	public synchronized void stop() {
		if (ongoingGame != null) {
			ongoingGame.setGameEndedListener(null);
			ongoingGame.stop();
		}
	}

	/**
	 * Game ended from inside the game.
	 */
	@Override
	public void gameEnded() {
		ongoingGame.setGameEndedListener(null);
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

	@Override
	public void showError(String string) {
		System.out.println("Error: " + string);
		display.showErrorMessage(string);
	}

}
