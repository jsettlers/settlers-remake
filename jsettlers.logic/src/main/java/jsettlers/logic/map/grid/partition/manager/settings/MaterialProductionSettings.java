/*
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
 */
package jsettlers.logic.map.grid.partition.manager.settings;

import java.io.Serializable;

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.grid.partition.manager.settings.RelativeSettings.OrdinalToTypeMapper;

public class MaterialProductionSettings implements IMaterialProductionSettings, Serializable {
	private static final int MAXIMUM_ABSOLUTE_REQUEST_VALUE = 99;
	private static final OrdinalToTypeMapper<EMaterialType> ordinalToTypeMapper = ordinal -> EMaterialType.VALUES[ordinal];

	private final RelativeSettings<EMaterialType> relativeWeaponRequests = new RelativeSettings<>(EMaterialType.NUMBER_OF_MATERIALS, ordinalToTypeMapper, false);
	private final RelativeSettings<EMaterialType> relativeToolRequests = new RelativeSettings<>(EMaterialType.NUMBER_OF_MATERIALS, ordinalToTypeMapper, false);

	private final RelativeSettings<EMaterialType> absoluteWeaponRequests = new RelativeSettings<>(EMaterialType.NUMBER_OF_MATERIALS, ordinalToTypeMapper, true, 0, MAXIMUM_ABSOLUTE_REQUEST_VALUE);
	private final RelativeSettings<EMaterialType> absoluteToolRequests = new RelativeSettings<>(EMaterialType.NUMBER_OF_MATERIALS, ordinalToTypeMapper, true, 0, MAXIMUM_ABSOLUTE_REQUEST_VALUE);

	public MaterialProductionSettings() {
		relativeWeaponRequests.setUserValue(EMaterialType.SWORD, 1f);
		relativeWeaponRequests.setUserValue(EMaterialType.BOW, 0.7f);
		relativeWeaponRequests.setUserValue(EMaterialType.SPEAR, 0.3f);
	}

	private RelativeSettings<EMaterialType> getRelativeSettingsForType(EMaterialType type) {
		return EMaterialType.WEAPONS.contains(type) ? relativeWeaponRequests : relativeToolRequests;
	}

	private RelativeSettings<EMaterialType> getAbsoluteSettingsForType(EMaterialType type) {
		return EMaterialType.WEAPONS.contains(type) ? absoluteWeaponRequests : absoluteToolRequests;
	}

	public void setUserConfiguredRelativeRequestValue(EMaterialType type, float userValue) {
		getRelativeSettingsForType(type).setUserValue(type, userValue);
	}

	@Override
	public float getUserConfiguredRelativeRequestValue(EMaterialType type) {
		return getRelativeSettingsForType(type).getUserValue(type);
	}

	@Override
	public float getRelativeRequestProbability(EMaterialType type) {
		return getRelativeSettingsForType(type).getProbability(type);
	}

	public void setAbsoluteProductionRequest(EMaterialType type, int count) {
		getAbsoluteSettingsForType(type).setUserValue(type, count);
	}

	public void increaseAbsoluteProductionRequest(EMaterialType type) {
		getAbsoluteSettingsForType(type).changeUserValue(type, +1);
	}

	public void decreaseAbsoluteProductionRequest(EMaterialType type) {
		getAbsoluteSettingsForType(type).changeUserValue(type, -1);
	}

	@Override
	public int getAbsoluteProductionRequest(EMaterialType type) {
		return (int) getAbsoluteSettingsForType(type).getUserValue(type);
	}

	public EMaterialType getWeaponToProduce() {
		EMaterialType weapon = absoluteWeaponRequests.drawRandom();
		if (weapon != null) {
			return weapon;
		} else {
			return relativeWeaponRequests.drawRandom();
		}
	}

	public EMaterialType drawRandomAbsolutelyRequestedTool() {
		return absoluteToolRequests.drawRandom();
	}

	public EMaterialType drawRandomRelativelyRequestedTool() {
		return relativeToolRequests.drawRandom();
	}
}
