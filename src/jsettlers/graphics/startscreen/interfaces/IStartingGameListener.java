package jsettlers.graphics.startscreen.interfaces;

import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;

/**
 * @author michael
 * @author Andreas Eberle
 */
public interface IStartingGameListener {
	/**
	 * Notifies this listener of the current progress of the start. May only be
	 * called before {@link #startFinished(IStartedGame)} is called.
	 * 
	 * @param state
	 * @param progress
	 */
	void startProgressChanged(EProgressState state, float progress);

	/**
	 * Notifies the listener that a game was started and gives it access to the
	 * game data.
	 * 
	 * @param game
	 *            The game that was just started.
	 * @retrun A {@link MapInterfaceConnector} that can be used to access the
	 *         game afterwards.
	 */
	MapInterfaceConnector startFinished(IStartedGame game);

	void startFailed(EGameError errorType, Exception exception);
}
