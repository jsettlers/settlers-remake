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
package jsettlers.common.buildings;

import java.util.LinkedList;

import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.material.EMaterialType;

/**
 * This class calculates static data gained from configuration files. The class supplies the information what buildings can request a given
 * {@link EMaterialType}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialsOfBuildings {
	private static final EBuildingType[][] buildingsRequestingMaterial = new EBuildingType[EMaterialType.NUMBER_OF_MATERIALS][];

	static {
		@SuppressWarnings({ "unchecked" })
		LinkedList<EBuildingType>[] buildingsForMaterials = new LinkedList[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			buildingsForMaterials[i] = new LinkedList<EBuildingType>();
		}

		for (EBuildingType buildingType : EBuildingType.VALUES) {
			for (RelativeStack requestStack : buildingType.getRequestStacks()) {
				buildingsForMaterials[requestStack.getMaterialType().ordinal].add(buildingType);
			}
		}

		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			LinkedList<EBuildingType> currList = buildingsForMaterials[i];
			buildingsRequestingMaterial[i] = currList.toArray(new EBuildingType[currList.size()]);
		}
	}

	/**
	 * Gets an array of {@link EBuildingType}s that can request the given {@link EMaterialType}.
	 * <p />
	 * NOTE: The array MUST NOT be changed! For the sake of speed, no copy is created by this method!
	 * 
	 * @param material
	 *            {@link EMaterialType} to be checked.
	 * @return Returns an array of {@link EBuildingType}s that can request the given {@link EMaterialType}.
	 */
	public static EBuildingType[] getBuildingTypesRequestingMaterial(EMaterialType material) {
		return buildingsRequestingMaterial[material.ordinal];
	}

	private MaterialsOfBuildings() {
	}
}
