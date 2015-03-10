package jsettlers.logic.map.newGrid.landscape;

/**
 * This interface defines a method that lets you walk on the given ground.
 * 
 * @author michael
 *
 */
public interface IWalkableGround {

	/**
	 * Walks on a given landscape type. Flatens it if needed.
	 */
	public abstract void walkOn(int x, int y);

}