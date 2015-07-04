package jsettlers.logic.buildings;

import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;

class BuildingMaterial implements IBuildingMaterial {

	private final EMaterialType materialType;
	private final int materialCount;
	private final boolean offering;

	BuildingMaterial(EMaterialType materialType, int stillRequired) {
		this(materialType, stillRequired, false);
	}

	BuildingMaterial(EMaterialType materialType, int stackSize, boolean offering) {
		this.materialType = materialType;
		this.materialCount = stackSize;
		this.offering = offering;
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	@Override
	public int getMaterialCount() {
		return materialCount;
	}

	@Override
	public boolean isOffering() {
		return offering;
	}
}
