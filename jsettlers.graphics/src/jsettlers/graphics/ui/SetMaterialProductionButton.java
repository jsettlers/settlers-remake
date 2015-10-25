package jsettlers.graphics.ui;

import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.SetMaterialProductionAction;

/**
 * @author codingberlin
 */
public class SetMaterialProductionButton extends Button {

	private final SetMaterialProductionAction.PositionSupplyer positionSupplyer;
	private final EMaterialType materialType;
	private final SetMaterialProductionAction.EMaterialProductionType productionType;

	public SetMaterialProductionButton(
			SetMaterialProductionAction.PositionSupplyer positionSupplyer,
			EMaterialType materialType,
			SetMaterialProductionAction.EMaterialProductionType productionType) {
		super(null);
		this.positionSupplyer = positionSupplyer;
		this.materialType = materialType;
		this.productionType = productionType;
	}

	public Action getAction() {
		return new SetMaterialProductionAction(positionSupplyer.getCurrentPosition(), materialType, productionType, 0);
	}
}
