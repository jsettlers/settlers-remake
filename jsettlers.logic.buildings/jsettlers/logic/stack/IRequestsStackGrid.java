package jsettlers.logic.stack;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

public interface IRequestsStackGrid extends Serializable {

	void request(IMaterialRequester requester, EMaterialType materialType, byte priority);

	boolean hasMaterial(ShortPoint2D position, EMaterialType materialType);

	void popMaterial(ShortPoint2D position, EMaterialType materialType);

	byte getStackSize(ShortPoint2D position, EMaterialType materialType);

	void releaseRequestsAt(ShortPoint2D position, EMaterialType materialType);

}
