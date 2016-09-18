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

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;

/**
 * Enum to define all material types.
 *
 * @author Andreas Eberle
 */
public enum EMaterialType {
	AXE((short) 46, 3, 153, true, 18, false),
	BOW((short) 63, 14, 114, true, 14, false),
	BLADE((short) 55, 3, 138, true, 16, false),
	BREAD((short) 49, 3, 186, true, 6, true),
	COAL((short) 34, 3, 144, true, 5, true),
	CROP((short) 50, 3, 180, true, 10, true),
	FISH((short) 47, 3, 189, true, 7, true),
	FISHINGROD((short) 66, 3, 141, true, 21, false),
	FLOUR((short) 48, 3, 183, true, 9, false),
	GOLD((short) 37, 3, 135, true, 24, false),
	GOLDORE((short) 36, 3, 150, true, 23, false),
	HAMMER((short) 51, 3, 126, true, 17, false),
	IRON((short) 42, 3, 132, true, 3, true),
	IRONORE((short) 39, 3, 147, true, 4, false),
	MEAT((short) 52, 3, 162, true, 8, true),
	PICK((short) 53, 3, 129, true, 19, false),
	PIG((short) 73, 3, 159, true, 11, false),
	PLANK((short) 33, 3, 168, true, 0, false),
	SAW((short) 54, 3, 177, true, 20, false),
	SCYTHE((short) 56, 3, 165, true, 22, false),
	SPEAR((short) 60, 14, 117, true, 15, false),
	STONE((short) 43, 3, 174, true, 1, false),
	SWORD((short) 59, 14, 111, true, 13, false),
	TRUNK((short) 41, 3, 171, true, 2, false),
	WATER((short) 77, 3, 156, true, 12, false),
	WINE((short) 69, 14, 123, true, 25, false),

	// Now the non-droppable materials

	NO_MATERIAL((short) 0, 0, 0, false, -1, false),

	MEAD((short) 90, 14, 126, false, -1, true),
	HONEY((short) 89, 14, 129, false, -1, true),
	GEMS((short) 79, 24, 120, false, -1, false),
	SULFUR((short) 80, 34, 126, false, -1, false),
	RICE((short) 78, 34, 129, false, -1, false),
	KEG((short) 70, 34, 132, false, -1, false),
	BOX((short) 82, 0, 0, false, -1, false),

	// ammo for cannon (6 frames instead of 8)
	CANNON_AMMO((short) 86, 0, 0, false, -1, false),
	// ammo for ballista (6 frames instead of 8)
	BALLISTA_AMMO((short) 87, 0, 0, false, -1, false),
	// ammo for catapult (6 frames instead of 8)
	CATAPULT_AMMO((short) 88, 0, 0, false, -1, false),

	WHITE_BREAD((short) 0, 0, 0, false, -1, false),
	BASKET((short) 0, 0, 0, false, -1, false),
	TREE((short) 0, 0, 0, false, -1, false),
	PLANT((short) 0, 0, 0, false, -1, false);

	public static final EMaterialType[] VALUES = EMaterialType.values();
	public static final int NUMBER_OF_MATERIALS = VALUES.length;
	/**
	 * The droppable {@link EMaterialType}s in the order of their default priority.
	 */
	public static final EMaterialType[] DROPPABLE_MATERIALS;
	public static final int NUMBER_OF_DROPPABLE_MATERIALS;

	static {
		// calculate the number of droppable materials and build up an array in
		// the default priority order. (not all materials have priority index but may be dropped)
		int numberOfDroppable = 0;
		for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
			if (VALUES[i].droppable && VALUES[i].defaultPrioIdx >= 0) {
				numberOfDroppable++;
			}
		}
		NUMBER_OF_DROPPABLE_MATERIALS = numberOfDroppable;
		DROPPABLE_MATERIALS = new EMaterialType[numberOfDroppable];
		for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
			if (VALUES[i].droppable && VALUES[i].defaultPrioIdx >= 0) {
				DROPPABLE_MATERIALS[VALUES[i].defaultPrioIdx] = VALUES[i];
			}
		}
	}

	public final byte ordinal;

	private final short stackIndex;
	private final boolean droppable;
	private final int defaultPrioIdx;
	private final boolean distributionConfigurable;
	private final OriginalImageLink imageLink;

	EMaterialType(short stackIndex, int guiFile, int guiBase, boolean droppable, int defaultPrioIdx, boolean distributionConfigurable) {
		this.stackIndex = stackIndex;
		this.defaultPrioIdx = defaultPrioIdx;
		this.distributionConfigurable = distributionConfigurable;
		this.ordinal = (byte) super.ordinal();
		this.droppable = droppable;
		imageLink = new OriginalImageLink(EImageLinkType.GUI, guiFile, guiBase, 0);
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
	 * Gets an icon that is used in the GUI for this material.
	 *
	 * @return An image link to the icon.
	 */
	public ImageLink getIcon() {
		return imageLink;
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
