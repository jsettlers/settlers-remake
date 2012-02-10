package jsettlers.common.material;

/**
 * enum to define all material types.
 * 
 * @author Andreas Eberle
 */
public enum EMaterialType {
	NO_MATERIAL((short) 0, (short) 0, (short) 0),

	AXE((short) 46, (short) 3, (short) 153),
	BOW((short) 63, (short) 14, (short) 114),
	BLADE((short) 55, (short) 3, (short) 138),
	BREAD((short) 49, (short) 3, (short) 186),
	COAL((short) 34, (short) 3, (short) 144),
	CROP((short) 50, (short) 3, (short) 180),
	FISH((short) 47, (short) 3, (short) 189),
	FISHINGROD((short) 66, (short) 3, (short) 141),
	FLOUR((short) 48, (short) 3, (short) 183),
	GOLD((short) 36, (short) 3, (short) 135),
	GOLDORE((short) 35, (short) 3, (short) 150),
	HAMMER((short) 51, (short) 3, (short) 126),
	IRON((short) 42, (short) 3, (short) 132),
	IRONORE((short) 39, (short) 3, (short) 147),
	MEAT((short) 52, (short) 3, (short) 162),
	PICK((short) 53, (short) 3, (short) 129),
	PIG((short) 73, (short) 3, (short) 159),
	PLANK((short) 33, (short) 3, (short) 168),
	SAW((short) 54, (short) 3, (short) 177),
	SCYTHE((short) 56, (short) 3, (short) 165),
	SPEAR((short) 60, (short) 14, (short) 117),
	STONE((short) 43, (short) 3, (short) 174),
	SWORD((short) 59, (short) 14, (short) 111),
	TRUNK((short) 41, (short) 3, (short) 171),
	WATER((short) 77, (short) 3, (short) 156),
	WINE((short) 69, (short) 14, (short) 123),

	WHITE_BREAD((short) 0, (short) 0, (short) 0),
	BASKET((short) 0, (short) 0, (short) 0),
	TREE((short) 0, (short) 0, (short) 0),
	PLANT((short) 0, (short) 0, (short) 0);

	public static final EMaterialType[] values = EMaterialType.values();

	private final short stackIndex;
	private final short guiBase;
	private final short guiFile;
	public final byte ordinal;

	EMaterialType(short stackIndex, short guiFile, short guiBase) {
		this.stackIndex = stackIndex;
		this.guiFile = guiFile;
		this.guiBase = guiBase;
		this.ordinal = (byte) super.ordinal();
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

}
