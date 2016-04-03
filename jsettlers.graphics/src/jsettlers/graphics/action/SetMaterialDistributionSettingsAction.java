/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.action;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.menu.action.EActionType;
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