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
package jsettlers.common.material;

/**
 * Enum to define all material types.
 *
 * @author Andreas Eberle
 */
public enum EMaterialType {
	NO_MATERIAL((short) 0, (short) 0, (short) 0, false, -1, false),

	AXE((short) 46, (short) 3, (short) 153, true, 18, false),
	BOW((short) 63, (short) 14, (short) 114, true, 14, false),
	BLADE((short) 55, (short) 3, (short) 138, true, 16, false),
	BREAD((short) 49, (short) 3, (short) 186, true, 6, true),
	COAL((short) 34, (short) 3, (short) 144, true, 5, true),
	CROP((short) 50, (short) 3, (short) 180, true, 10, true),
	FISH((short) 47, (short) 3, (short) 189, true, 7, true),
	FISHINGROD((short) 66, (short) 3, (short) 141, true, 21, false),
	FLOUR((short) 48, (short) 3, (short) 183, true, 9, false),
	GOLD((short) 37, (short) 3, (short) 135, true, 24, false),
	GOLDORE((short) 36, (short) 3, (short) 150, true, 23, false),
	HAMMER((short) 51, (short) 3, (short) 126, true, 17, false),
	IRON((short) 42, (short) 3, (short) 132, true, 3, true),
	IRONORE((short) 39, (short) 3, (short) 147, true, 4, false),
	MEAT((short) 52, (short) 3, (short) 162, true, 8, true),
	PICK((short) 53, (short) 3, (short) 129, true, 19, false),
	PIG((short) 73, (short) 3, (short) 159, true, 11, false),
	PLANK((short) 33, (short) 3, (short) 168, true, 0, false),
	SAW((short) 54, (short) 3, (short) 177, true, 20, false),
	SCYTHE((short) 56, (short) 3, (short) 165, true, 22, false),
	SPEAR((short) 60, (short) 14, (short) 117, true, 15, false),
	STONE((short) 43, (short) 3, (short) 174, true, 1, false),
	SWORD((short) 59, (short) 14, (short) 111, true, 13, false),
	TRUNK((short) 41, (short) 3, (short) 171, true, 2, false),
	WATER((short) 77, (short) 3, (short) 156, true, 12, false),
	WINE((short) 69, (short) 14, (short) 123, true, 25, false),

	GEMS((short) 79, (short) 24, (short) 120, true, 26, false),
	SULFUR((short) 80, (short) 34, (short) 126, true, 27, false),
	RICE((short) 78, (short) 34, (short) 129, true, 28, false),
	KEG((short) 70, (short) 34, (short) 132, true, 29, false),

	BOX((short) 82, (short) 0, (short) 0, true, 30, false),

	// ammo for cannon (6 frames instead of 8)
	CANNON_AMMO((short) 86, (short) 0, (short) 0, false, -1, false),
	// ammo for ballista (6 frames instead of 8)
	BALLISTA_AMMO((short) 87, (short) 0, (short) 0, false, -1, false),
	// ammo for catapult (6 frames instead of 8)
	CATAPULT_AMMO((short) 88, (short) 0, (short) 0, false, -1, false),

	WHITE_BREAD((short) 0, (short) 0, (short) 0, false, -1, false),
	BASKET((short) 0, (short) 0, (short) 0, false, -1, false),
	TREE((short) 0, (short) 0, (short) 0, false, -1, false),
	PLANT((short) 0, (short) 0, (short) 0, false, -1, false);

	public static final EMaterialType[] values = EMaterialType.values();
	public static final int NUMBER_OF_MATERIALS = values.length;
	/**
	 * The droppable {@link EMaterialType}s in the order of their default priority.
	 */
	public static final EMaterialType[] DROPPABLE_MATERIALS;
	public static final int NUMBER_OF_DROPPABLE_MATERIALS;

	static {
		// calculate the number of droppable materials and build up an array in
		// the default priority order.
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
	private final boolean distributionConfigurable;

	EMaterialType(short stackIndex, short guiFile, short guiBase, boolean droppable, int defaultPrioIdx, boolean distributionConfigurable) {
		this.stackIndex = stackIndex;
		this.guiFile = guiFile;
		this.guiBase = guiBase;
		this.defaultPrioIdx = defaultPrioIdx;
		this.distributionConfigurable = distributionConfigurable;
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

	/**
	 *
	 * @return Returns true if this material can be dropped.
	 */
	public boolean isDroppable() {
		return droppable;
	}

	public boolean isDistributionConfigurable() {
		return distributionConfigurable;
	}

}
