package jsettlers.logic.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;

public interface IRequestsStackGrid {

	void request(ISPosition2D position, EMaterialType materialType, byte priority);

	boolean hasMaterial(ISPosition2D position, EMaterialType materialType);

	void popMaterial(ISPosition2D position, EMaterialType materialType);

	int getStackSize(ISPosition2D position, EMaterialType materialType);

}
