package jsettlers.network.client.interfaces;

/**
 * This interface supplies a method to check if the game is pausing.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPausingSupplier {

	/**
	 * 
	 * @return Returns true if the game's clock is pausing<br>
	 *         false otherwise.
	 */
	boolean isPausing();
}
