package jsettlers.common.buildings;

import java.util.LinkedList;

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

		for (EBuildingType building : EBuildingType.values) {
			for (RelativeStack stack : building.getRequestStacks()) {
				if (stack.requiredForBuild() == 0) { // if it's not a stack used for constructing the building
					buildingsForMaterials[stack.getMaterialType().ordinal].add(building);
				}
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
