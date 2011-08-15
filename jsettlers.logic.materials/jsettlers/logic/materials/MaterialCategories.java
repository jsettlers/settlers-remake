package jsettlers.logic.materials;

import jsettlers.common.material.EMaterialType;

/**
 * defines categories of MaterialTypes
 * 
 * @author Andreas Eberle
 * 
 */
public enum MaterialCategories {
	TOOLS(EMaterialType.AXE, EMaterialType.FISHINGROD, EMaterialType.HAMMER, EMaterialType.PICK, EMaterialType.SAW, EMaterialType.SCYTHE),
	WEAPONS(EMaterialType.SWORD, EMaterialType.BOW, EMaterialType.SPEAR);

	private final EMaterialType[] types;

	MaterialCategories(EMaterialType... types) {
		this.types = types;
	}

	public boolean is(EMaterialType type) {
		for (EMaterialType currType : types) {
			if (currType == type)
				return true;
		}

		return false;
	}

	/**
	 * 
	 * @param type
	 *            MaterialType to be checked.
	 * @return -1 if the MaterialType isn't in this category<br>
	 *         the index of the MaterialType in this category if it is found.
	 */
	public byte getIndex(EMaterialType type) {
		byte idx = 0;
		for (EMaterialType currType : types) {
			if (currType == type)
				return idx;
			idx++;
		}

		return -1;
	}

	public byte getNumberOfElements() {
		return (byte) types.length;
	}

	/**
	 * @param idx
	 *            index of the material type
	 * @return EMaterialType at given index in this category
	 */
	public EMaterialType getType(int idx) {
		return types[idx];
	}
}
