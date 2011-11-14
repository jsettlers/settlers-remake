package jsettlers.logic.stack;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;

public interface IRequestsStackGrid extends Serializable {

	void request(ISPosition2D position, EMaterialType materialType, byte priority);

	boolean hasMaterial(ISPosition2D position, EMaterialType materialType);

	void popMaterial(ISPosition2D position, EMaterialType materialType);

	byte getStackSize(ISPosition2D position, EMaterialType materialType);

}
