package jsettlers.input;

/**
 * This interface defines a method to stop a running game.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGameStoppable {
	/**
	 * Stops the running game. This method may be called multiple times. All but the first call will have no effect.
	 */
	void stopGame();
}
