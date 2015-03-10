package jsettlers.logic.map.newGrid.partition.manager.materials.interfaces;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.MaterialsManager;

/**
 * This interface describes a bearer that can be used by the {@link MaterialsManager} to give job orders.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManagerBearer extends ILocatable, Serializable {

	/**
	 * Sets a job to this {@link IManagerBearer} object. The job is to deliver the given offer tot the given request.
	 * 
	 * @param materialType
	 * @param offerPosition
	 * @param request
	 * @return true if the job can be handled, false if another bearer needs to be asked.
	 */
	boolean deliver(EMaterialType materialType, ShortPoint2D offerPosition, IMaterialRequest request);

}
