package jsettlers.main.android.gameplay.controlsmenu.goods;

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.material.EMaterialType;

/**
 * Created by Tom Pratt on 25/09/2017.
 */

public class ProductionState {
	private final EMaterialType materialType;
	private final int quantity;
	private final float ratio;

	public ProductionState(EMaterialType materialType, IMaterialProductionSettings materialProductionSettings) {
		this.materialType = materialType;
		this.quantity = materialProductionSettings.getAbsoluteProductionRequest(materialType);
		this.ratio = materialProductionSettings.getUserConfiguredRelativeRequestValue(materialType);
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public int getQuantity() {
		return quantity;
	}

	public float getRatio() {
		return ratio;
	}
}
