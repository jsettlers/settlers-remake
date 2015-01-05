package jsettlers.graphics.action;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} is used to set the distribution settings for a material in a manager.
 * 
 * @author Andreas Eberle
 */
public class SetMaterialDistributionSettingsAction extends Action {

	private final ShortPoint2D managerPosition;
	private final EMaterialType materialType;
	private final float[] probabilities;

	public SetMaterialDistributionSettingsAction(ShortPoint2D managerPosition,
			EMaterialType materialType, float[] probabilities) {
		super(EActionType.SET_MATERIAL_DISTRIBUTION_SETTINGS);
		this.managerPosition = managerPosition;
		this.materialType = materialType;
		this.probabilities = probabilities;
	}

	/**
	 * @return Returns a position occupied by the manager this settings shall be used for.
	 */
	public ShortPoint2D getManagerPosition() {
		return managerPosition;
	}

	/**
	 * @return Returns the {@link EMaterialType} this settings shall be used for.
	 */
	public EMaterialType getMaterialType() {
		return materialType;
	}

	/**
	 * @return Returns the new distribution probabilities. The values correspond to the {@link EBuildingType} given by
	 *         MaterialsOfBuildings.getBuildingTypesRequestingMaterial( {@link #getMaterialType()}).
	 * @see MaterialsOfBuildings#getBuildingTypesRequestingMaterial(EMaterialType)
	 */
	public float[] getProbabilities() {
		return probabilities;
	}
}