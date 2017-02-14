package jsettlers.common.map.partition;

import jsettlers.common.material.EMaterialType;

public interface IStockSettings {
	EMaterialType[] ACCEPTABLE_MATERIALS = EMaterialType.DROPPABLE_MATERIALS;
	int NUMMBER_OF_ACCEPTABLE_MATERIALS = ACCEPTABLE_MATERIALS.length;

	boolean isAccepted(EMaterialType materialType);
}
