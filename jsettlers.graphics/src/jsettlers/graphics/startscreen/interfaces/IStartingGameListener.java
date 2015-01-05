package jsettlers.graphics.startscreen.interfaces;

import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.progress.EProgressState;

/**
 * @author michael
 * @author Andreas Eberle
 */
public interface IStartingGameListener {
	/**
	 * Notifies this listener of the current progress of the start. May only be called before {@link #startFinished(IStartedGame)} is called.
	 * 
	 * @param state
	 * @param progress
	 */
	void startProgressChanged(EProgressState state, float progress);

	/**
	 * Notifies the listener that a game was started and gives it access to the game data.
	 * 
	 * @param game
	 *            The game that was just started.
	 * @retrun A {@link IMapInterfaceConnector} that can be used to access the game afterwards.
	 */
	IMapInterfaceConnector startFinished(IStartedGame game);

	void startFailed(EGameError errorType, Exception exception);
}
