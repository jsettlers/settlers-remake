package jsettlers.logic.buildings;

import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;

class BuildingMaterial implements IBuildingMaterial {

	private final EMaterialType materialType;
	private final int stackSize;
	private final boolean offering;
	private final short stillRequired;

	BuildingMaterial(EMaterialType materialType, int stackSize, short stillRequired) {
		this.materialType = materialType;
		this.stackSize = stackSize;
		this.offering = false;
		this.stillRequired = stillRequired;
	}

	BuildingMaterial(EMaterialType materialType, int stackSize, boolean offering) {
		this.materialType = materialType;
		this.stackSize = stackSize;
		this.offering = offering;
		this.stillRequired = offering ? 0 : Short.MAX_VALUE;
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	@Override
	public int getStackSize() {
		return stackSize;
	}

	@Override
	public boolean isOffering() {
		return offering;
	}

	@Override
	public short getRequiredMaterials() {
		return stillRequired;
	}
}
