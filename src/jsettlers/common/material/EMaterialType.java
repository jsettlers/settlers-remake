package jsettlers.common.material;

/**
 * enum to define all material types.
 * 
 * @author Andreas Eberle
 */
public enum EMaterialType {
	NO_MATERIAL((short) 0, (short) 0, (short) 0, false, -1),

	AXE((short) 46, (short) 3, (short) 153, true, 18),
	BOW((short) 63, (short) 14, (short) 114, true, 14),
	BLADE((short) 55, (short) 3, (short) 138, true, 16),
	BREAD((short) 49, (short) 3, (short) 186, true, 6),
	COAL((short) 34, (short) 3, (short) 144, true, 5),
	CROP((short) 50, (short) 3, (short) 180, true, 10),
	FISH((short) 47, (short) 3, (short) 189, true, 7),
	FISHINGROD((short) 66, (short) 3, (short) 141, true, 21),
	FLOUR((short) 48, (short) 3, (short) 183, true, 9),
	GOLD((short) 36, (short) 3, (short) 135, true, 24),
	GOLDORE((short) 35, (short) 3, (short) 150, true, 23),
	HAMMER((short) 51, (short) 3, (short) 126, true, 17),
	IRON((short) 42, (short) 3, (short) 132, true, 3),
	IRONORE((short) 39, (short) 3, (short) 147, true, 4),
	MEAT((short) 52, (short) 3, (short) 162, true, 8),
	PICK((short) 53, (short) 3, (short) 129, true, 19),
	PIG((short) 73, (short) 3, (short) 159, true, 11),
	PLANK((short) 33, (short) 3, (short) 168, true, 0),
	SAW((short) 54, (short) 3, (short) 177, true, 20),
	SCYTHE((short) 56, (short) 3, (short) 165, true, 22),
	SPEAR((short) 60, (short) 14, (short) 117, true, 15),
	STONE((short) 43, (short) 3, (short) 174, true, 1),
	SWORD((short) 59, (short) 14, (short) 111, true, 13),
	TRUNK((short) 41, (short) 3, (short) 171, true, 2),
	WATER((short) 77, (short) 3, (short) 156, true, 12),
	WINE((short) 69, (short) 14, (short) 123, true, 25),

	WHITE_BREAD((short) 0, (short) 0, (short) 0, false, -1),
	BASKET((short) 0, (short) 0, (short) 0, false, -1),
	TREE((short) 0, (short) 0, (short) 0, false, -1),
	PLANT((short) 0, (short) 0, (short) 0, false, -1);

	public static final EMaterialType[] values = EMaterialType.values();
	public static final int NUMBER_OF_MATERIALS = values.length;
	/**
	 * The droppable {@link EMaterialType}s in the order of their default priority.
	 */
	public static final EMaterialType[] DROPPABLE_MATERIALS;
	public static final int NUMBER_OF_DROPPABLE_MATERIALS;

	static {
		// calculate the number of droppable materials and build up an array in the default priority order.
		int numberOfDroppable = 0;
		for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
			if (values[i].droppable) {
				numberOfDroppable++;
			}
		}
		NUMBER_OF_DROPPABLE_MATERIALS = numberOfDroppable;
		DROPPABLE_MATERIALS = new EMaterialType[numberOfDroppable];
		for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
			if (values[i].droppable) {
				DROPPABLE_MATERIALS[values[i].defaultPrioIdx] = values[i];
			}
		}
	}

	public final byte ordinal;

	private final short stackIndex;
	private final short guiBase;
	private final short guiFile;
	private final boolean droppable;
	private final int defaultPrioIdx;

	EMaterialType(short stackIndex, short guiFile, short guiBase, boolean droppable, int defaultPrioIdx) {
		this.stackIndex = stackIndex;
		this.guiFile = guiFile;
		this.guiBase = guiBase;
		this.defaultPrioIdx = defaultPrioIdx;
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
