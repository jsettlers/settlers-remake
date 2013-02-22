package jsettlers.logic.stack;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.MaterialRequestObject;

/**
 * This interface defines the methods a grid must supply that it can be used by a {@link RequestStack}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IRequestsStackGrid extends Serializable {

	/**
	 * Requests the given {@link EMaterialType} with the conditions supplied through the {@link MaterialRequestObject}.
	 * 
	 * @param materialType
	 *            The {@link EMaterialType} to be requested.
	 * @param requestObject
	 *            The {@link MaterialRequestObject} specifying the requests conditions like amount and so on.
	 */
	void request(EMaterialType materialType, MaterialRequestObject requestObject);

	boolean hasMaterial(ShortPoint2D position, EMaterialType materialType);

	byte getStackSize(ShortPoint2D position, EMaterialType materialType);

	/**
	 * This method creates a new offer for every material of the given {@link EMaterialType} that is currently located at the given position.
	 * 
	 * @param position
	 * @param materialType
	 */
	void createOffersForAvailableMaterials(ShortPoint2D position, EMaterialType materialType);

	/**
	 * Pops a materials of the given type from the given location.
	 * 
	 * @param position
	 *            The location to pop the material.
	 * @param materialType
	 *            The {@link EMaterialType} type to be popped.
	 */
	void popMaterial(ShortPoint2D position, EMaterialType materialType);
}
