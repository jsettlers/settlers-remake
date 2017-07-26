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
package jsettlers.common.landscape;

import java.util.EnumSet;
import java.util.Set;

import jsettlers.common.Color;

public enum ELandscapeType {
	// DO NOT sort, order is important!
	GRASS(0, new Color(0xff156E15), false, false),
	DRY_GRASS(1, new Color(0xffAD8432), false, false),
	DESERT(18, new Color(0xffA09738), false, false),
	EARTH(2, new Color(0xffa2653e), false, false), // TODO: color
	MOUNTAIN(21, new Color(0xff5C5C5C), false, false),
	SNOW(25, new Color(0xffC0CDCF), false, true),
	SAND(3, new Color(0xffADAB00), false, false),
	/**
	 * Flattened grass (for buildings, paths, ...). Must behave exactly like normal grass does!
	 */
	FLATTENED(35, new Color(0xff105910), false, false),
	RIVER1(10, new Color(0xff4786FC), false, false),
	RIVER2(10, new Color(0xff4786FC), false, false),
	RIVER3(10, new Color(0xff4786FC), false, false),
	RIVER4(10, new Color(0xff4786FC), false, false),
	MOUNTAINBORDER(22, new Color(0xff424142), false, false),
	MOUNTAINBORDEROUTER(23, new Color(0xff105910), false, false), // TODO: color
	WATER1(17, new Color(0xff1863F0), true, true),
	WATER2(16, new Color(0xff1562E0), true, true),
	WATER3(15, new Color(0xff1260D0), true, true),
	WATER4(14, new Color(0xff0E5CC8), true, true),
	WATER5(13, new Color(0xff0C53C0), true, true),
	WATER6(12, new Color(0xff084cB8), true, true),
	WATER7(11, new Color(0xff0443B0), true, true),
	WATER8(10, new Color(0xff003CAB), true, true),
	MOOR(8, new Color(0xff003F1C), false, true),
	MOORINNER(7, new Color(0xff003F1C), false, true),
	MOORBORDER(9, new Color(0xff003F1C), false, false),
	FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	SHARP_FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	GRAVEL(230, new Color(0xff000000), false, false), // TODO: color
	DESERTBORDER(19, new Color(0xff949200), false, false),
	DESERTBORDEROUTER(20, new Color(0xff949200), false, false),
	SNOWINNER(24, new Color(0xffd7fffe), false, true),
	SNOWBORDER(25, new Color(0xffd7fffe), false, false),
	MUD(5, new Color(0xff0e87cc), false, true), // TODO: color
	MUDINNER(4, new Color(0xff0e87cc), false, true), // TODO: color
	MUDBORDER(6, new Color(0xff0e87cc), false, false); // TODO: color

	public static final ELandscapeType[] VALUES = ELandscapeType.values();
	private static final Set<ELandscapeType> RIVERS = EnumSet.of(RIVER1, RIVER2, RIVER3, RIVER4);
	public static final Set<ELandscapeType> MOUNTAIN_TYPES = EnumSet.of(MOUNTAIN, MOUNTAINBORDER);

	public final int image;
	public final Color color;
	public final boolean isWater;
	public final boolean isBlocking;
	public final byte ordinal;

	ELandscapeType(int image, Color color, boolean isWater, boolean isBlocking) {
		this.image = image;
		this.color = color;
		this.isWater = isWater;
		this.isBlocking = isBlocking;
		this.ordinal = (byte) super.ordinal();
	}

	public final int getImageNumber() {
		return image;
	}

	/**
	 * Gets the base color of the landscape
	 * 
	 * @return The color the landscape has.
	 */
	public final Color getColor() {
		return color;
	}

	/**
	 * Checks if this landscape type is water (not river, just water that ships can swim on.).
	 * <p>
	 * To check for unwalkable land, also test if it is MOOR or SNOW
	 * 
	 * @return
	 */
	public final boolean isWater() {
		return isWater;
	}

	public final boolean isGrass() {
		return this == GRASS || this == FLATTENED;
	}

	public final boolean isRiver() {
		return RIVERS.contains(this);
	}
}
