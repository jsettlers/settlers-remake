/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.grid.partition.manager.settings;

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.buildings.stack.multi.StockSettings;
import jsettlers.logic.map.grid.partition.manager.PartitionManager;

/**
 * This class bundles all settings for the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionManagerSettings implements IPartitionSettings, Serializable {
	private static final long serialVersionUID = -6269898822727665606L;

	private static final MaterialDistributionSettings[] defaultSettings = new MaterialDistributionSettings[EMaterialType.NUMBER_OF_MATERIALS];
	private static final boolean[] INITIAL_STOCK_SETTINGS = new boolean[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];

	static {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			defaultSettings[i] = new MaterialDistributionSettings(EMaterialType.VALUES[i]);
		}

		INITIAL_STOCK_SETTINGS[EMaterialType.GOLD.ordinal] = true; // GOLD is active by default
	}

	private final EMaterialType[] materialTypeForPriorities;
	private final MaterialDistributionSettings[] settingsOfMaterials;
	private final MaterialProductionSettings materialProductionSettings;
	private final StockSettings stockSettings;

	public PartitionManagerSettings() {
		materialTypeForPriorities = new EMaterialType[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		System.arraycopy(EMaterialType.DROPPABLE_MATERIALS, 0, materialTypeForPriorities, 0, EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS);

		settingsOfMaterials = new MaterialDistributionSettings[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.VALUES[i];

			if (materialType.isDistributionConfigurable()) {
				settingsOfMaterials[i] = new MaterialDistributionSettings(materialType);
			} else {
				settingsOfMaterials[i] = defaultSettings[i];
			}
		}

		materialProductionSettings = new MaterialProductionSettings();
		stockSettings = new StockSettings(INITIAL_STOCK_SETTINGS);
	}

	@Override
	public EMaterialType getMaterialTypeForPriority(int priorityIdx) {
		return materialTypeForPriorities[priorityIdx];
	}

	@Override
	public MaterialDistributionSettings getDistributionSettings(final EMaterialType materialType) {
		return settingsOfMaterials[materialType.ordinal];
	}

	/**
	 * Sets the setting for the priorities of the droppable {@link EMaterialType}s.
	 * 
	 * @param materialTypeForPriority
	 *            An array of all droppable {@link EMaterialType}s. The first element has the highest priority, the last one has the lowest.
	 */
	public void setMaterialPriorities(EMaterialType[] materialTypeForPriority) {
		assert this.materialTypeForPriorities.length == materialTypeForPriority.length;

		for (int i = 0; i < materialTypeForPriority.length; i++) {
			this.materialTypeForPriorities[i] = materialTypeForPriority[i];
		}
	}

	@Override
	public MaterialProductionSettings getMaterialProductionSettings() {
		return materialProductionSettings;
	}

	public void setAcceptedStockMaterial(EMaterialType materialType, boolean accepted) {
		stockSettings.setAccepted(materialType, accepted);
	}

	@Override
	public StockSettings getStockSettings() {
		return stockSettings;
	}

	public void setMaterialDistributionSettings(EMaterialType materialType, EBuildingType buildingType, float ratio) {
		getDistributionSettings(materialType).setUserConfiguredDistributionValue(buildingType, ratio);
	}
}
