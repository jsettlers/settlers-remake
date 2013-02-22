package jsettlers.logic.map.newGrid.partition.manager.materials.interfaces;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.MaterialsManager;

/**
 * This interface defines methods needed by the {@link MaterialsManager} to get jobless movables to give them jobs.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IJoblessSupplier extends Serializable {

	/**
	 * States if this {@link IJoblessSupplier} has no more jobless left.
	 * 
	 * @return Returns true if there are no jobless left <br>
	 *         false otherwise.
	 */
	boolean isEmpty();

	/**
	 * This method returns the jobless closest to the given position.
	 * 
	 * @param position
	 *            The position is used as the center of the search for a jobless.
	 * 
	 * @return Returns the jobless closest to the given position<br>
	 *         or null if none has been found.
	 */
	IManagerBearer removeJoblessCloseTo(ShortPoint2D position);

}
