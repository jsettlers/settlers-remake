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

package jsettlers.graphics.map.controls.original.panel.content.buildings;

import java.util.Arrays;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;

/**
 * Created by Andreas Eberle on 02.05.2017.
 */
public enum EBuildingsCategory {
	BUILDINGS_CATEGORY_NORMAL(
			EBuildingType.LUMBERJACK,
			EBuildingType.SAWMILL,
			EBuildingType.STONECUTTER,
			EBuildingType.FORESTER,
			EBuildingType.IRONMINE,
			EBuildingType.IRONMELT,
			EBuildingType.GOLDMINE,
			EBuildingType.GOLDMELT,
			EBuildingType.COALMINE,
			EBuildingType.TOOLSMITH,
			EBuildingType.CHARCOAL_BURNER),
	BUILDINGS_CATEGORY_FOOD(
			EBuildingType.FISHER,
			EBuildingType.FARM,
			EBuildingType.PIG_FARM,
			EBuildingType.MILL,
			EBuildingType.SLAUGHTERHOUSE,
			EBuildingType.BAKER,
			EBuildingType.WATERWORKS,
			EBuildingType.DONKEY_FARM,
			EBuildingType.WINEGROWER),
	BUILDINGS_CATEGORY_MILITARY(
			EBuildingType.TOWER,
			EBuildingType.BIG_TOWER,
			EBuildingType.CASTLE,
			EBuildingType.LOOKOUT_TOWER,
			EBuildingType.WEAPONSMITH,
			EBuildingType.BARRACK,
			EBuildingType.DOCKYARD,
			EBuildingType.HOSPITAL),
	BUILDINGS_CATEGORY_SOCIAL(
			EBuildingType.SMALL_LIVINGHOUSE,
			EBuildingType.MEDIUM_LIVINGHOUSE,
			EBuildingType.BIG_LIVINGHOUSE,
			EBuildingType.STOCK,
			EBuildingType.MARKET_PLACE,
			EBuildingType.HARBOR,
			EBuildingType.TEMPLE,
			EBuildingType.BIG_TEMPLE);

	public static final EBuildingsCategory[] VALUES = values();
	public static final int NUMBER_OF_VALUES = VALUES.length;

	public final List<EBuildingType> buildingTypes;

	EBuildingsCategory(EBuildingType... buildingTypes) {
		this.buildingTypes = Arrays.asList(buildingTypes);
	}
}
