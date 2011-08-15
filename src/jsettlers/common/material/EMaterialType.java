package jsettlers.common.material;

/**
 * enum to define all material types.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EMaterialType {
	NO_MATERIAL((short) 0),

	AXE((short) 46),
	BOW((short) 63),
	BLADE((short) 55),
	BREAD((short) 49),
	COAL((short) 34),
	CROP((short) 50),
	FISH((short) 47),
	FISHINGROD((short) 66),
	FLOUR((short) 48),
	GOLD((short) 36),
	GOLDORE((short) 35),
	HAMMER((short) 51),
	IRON((short) 42),
	IRONORE((short) 39),
	MEAT((short) 52),
	PICK((short) 53),
	PIG((short) 73),
	PLANK((short) 33),
	SAW((short) 54),
	SCYTHE((short) 56),
	SPEAR((short) 60),
	STONE((short) 43),
	SWORD((short) 59),
	TRUNK((short) 41),
	WATER((short) 77),
	WINE((short) 69),

	WHITE_BREAD((short) 0),
	BASKET((short) 0),
	TREE((short) 0),
	PLANT((short) 0);

	private final short stackIndex;

	EMaterialType(short stackIndex) {
		this.stackIndex = stackIndex;
	}

	/**
	 * gets the index of the material for stacks. <br>
	 * used for jsettlers.graphics
	 * 
	 * @return
	 */
	public short getStackIndex() {
		return stackIndex;
	}

}
