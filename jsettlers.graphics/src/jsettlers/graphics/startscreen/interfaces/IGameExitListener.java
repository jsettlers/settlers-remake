package jsettlers.graphics.startscreen.interfaces;

/**
 * This listener gets notified when the game is exited.
 * 
 * @author michael
 */
public interface IGameExitListener {
	/**
	 * Called only once before the game is finally destroyed.
	 * 
	 * @param game
	 *            The game that was started before. Is valid during the call,
	 *            but should not be used afterwards.
	 */
	void gameExited(IStartedGame game);
}
