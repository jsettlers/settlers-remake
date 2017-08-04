/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.logic.map.loading.original.data;

import jsettlers.common.material.EMaterialType;

/**
 * The stack type for the map
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EOriginalMapStackType {

	NOT_A_STACK(null), // - 0 is not defined

	PLANK(EMaterialType.PLANK),
	STONE(EMaterialType.STONE),
	TRUNK(EMaterialType.TRUNK),
	BREAD(EMaterialType.BREAD),
	COAL(EMaterialType.COAL),
	GOLDORE(EMaterialType.GOLDORE),
	IRONORE(EMaterialType.IRONORE),
	FISH(EMaterialType.FISH),
	CROP(EMaterialType.CROP),
	GOLD(EMaterialType.GOLD),
	IRON(EMaterialType.IRON),
	SPADE(EMaterialType.BLADE),
	HAMMER(EMaterialType.HAMMER),
	AXE(EMaterialType.AXE),
	PICK(EMaterialType.PICK),
	SAW(EMaterialType.SAW),
	FISHINGROD(EMaterialType.FISHINGROD),
	SWORD(EMaterialType.SWORD),
	BOW(EMaterialType.BOW),
	SPEAR(EMaterialType.SPEAR),
	WINE(EMaterialType.WINE),
	FLOUR(EMaterialType.FLOUR),
	PIG(EMaterialType.PIG),
	MEAT(EMaterialType.MEAT),
	SULFUR(EMaterialType.SULFUR),
	WATER(EMaterialType.WATER),
	RICE(EMaterialType.RICE),
	GEMS(EMaterialType.GEMS),
	KEG(EMaterialType.KEG),
	Schiesspulver(null),
	unknown(null),
	SCYTHE(EMaterialType.SCYTHE),
	Reisschnaps(null),
	Met(null),
	HONEY(EMaterialType.HONEY),

	/**
	 * has to be the last item
	 */
	END_OF_LIST(null);
	
	private static final EOriginalMapStackType[] VALUES = EOriginalMapStackType.values();

	public final EMaterialType value;

	EOriginalMapStackType(EMaterialType value) {
		this.value = value;
	}

	public static EOriginalMapStackType getTypeByInt(int type) {
		if (type < 0 || type >= VALUES.length) {
			return NOT_A_STACK;
		} else {
			return VALUES[type];
		}
	}

}