package jsettlers.common.material;

/**
 * enum to define all material types.
 * 
 * @author Andreas Eberle
 */
public enum EMaterialType {
	NO_MATERIAL((short) 0, (short) 0, (short) 0, false),

	AXE((short) 46, (short) 3, (short) 153, true),
	BOW((short) 63, (short) 14, (short) 114, true),
	BLADE((short) 55, (short) 3, (short) 138, true),
	BREAD((short) 49, (short) 3, (short) 186, true),
	COAL((short) 34, (short) 3, (short) 144, true),
	CROP((short) 50, (short) 3, (short) 180, true),
	FISH((short) 47, (short) 3, (short) 189, true),
	FISHINGROD((short) 66, (short) 3, (short) 141, true),
	FLOUR((short) 48, (short) 3, (short) 183, true),
	GOLD((short) 36, (short) 3, (short) 135, true),
	GOLDORE((short) 35, (short) 3, (short) 150, true),
	HAMMER((short) 51, (short) 3, (short) 126, true),
	IRON((short) 42, (short) 3, (short) 132, true),
	IRONORE((short) 39, (short) 3, (short) 147, true),
	MEAT((short) 52, (short) 3, (short) 162, true),
	PICK((short) 53, (short) 3, (short) 129, true),
	PIG((short) 73, (short) 3, (short) 159, true),
	PLANK((short) 33, (short) 3, (short) 168, true),
	SAW((short) 54, (short) 3, (short) 177, true),
	SCYTHE((short) 56, (short) 3, (short) 165, true),
	SPEAR((short) 60, (short) 14, (short) 117, true),
	STONE((short) 43, (short) 3, (short) 174, true),
	SWORD((short) 59, (short) 14, (short) 111, true),
	TRUNK((short) 41, (short) 3, (short) 171, true),
	WATER((short) 77, (short) 3, (short) 156, true),
	WINE((short) 69, (short) 14, (short) 123, true),

	WHITE_BREAD((short) 0, (short) 0, (short) 0, false),
	BASKET((short) 0, (short) 0, (short) 0, false),
	TREE((short) 0, (short) 0, (short) 0, false),
	PLANT((short) 0, (short) 0, (short) 0, false), ;

	public static final EMaterialType[] values = EMaterialType.values();

	private final short stackIndex;
	private final short guiBase;
	private final short guiFile;
	private final boolean droppable;
	public final byte ordinal;

	EMaterialType(short stackIndex, short guiFile, short guiBase, boolean droppable) {
		this.stackIndex = stackIndex;
		this.guiFile = guiFile;
		this.guiBase = guiBase;
		this.ordinal = (byte) super.ordinal();
		this.droppable = droppable;
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

	/**
	 * Gets the gui icon base. That is the index of the small image for this material in the gui file 2.
	 * 
	 * @return The index.
	 */
	public int getGuiIconBase() {
		return guiBase;
	}

	public short getGuiFile() {
		return guiFile;
	}

	public boolean isDroppable() {
		return droppable;
	}

}
