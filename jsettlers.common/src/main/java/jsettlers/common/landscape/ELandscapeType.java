/*******************************************************************************
 * Copyright (c) 2015-2019
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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import jsettlers.common.Color;

public enum ELandscapeType {
	// DO NOT sort, order is important!
	GRASS(0, new Color(0xff156E15), false, false),
	DRY_GRASS(1, new Color(0xffAD8432), false, false),
	DESERT(18, new Color(0xffA09738), false, false),
	EARTH(2, new Color(0xffa2653e), false, false), // TODO: color
	MOUNTAIN(21, new Color(0xff5C5C5C), false, false),
	SNOW(24, new Color(0xffC0CDCF), false, true),
	SAND(31, new Color(0xffADAB00), false, false),
	/**
	 * Flattened grass (for buildings, paths, ...). Must behave exactly like normal grass does!
	 */
	FLATTENED(35, new Color(0xff105910), false, false),
	RIVER1(17, new Color(0xff4786FC), false, false),
	RIVER2(17, new Color(0xff4786FC), false, false),
	RIVER3(17, new Color(0xff4786FC), false, false),
	RIVER4(17, new Color(0xff4786FC), false, false),
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
	MOOR(7, new Color(0xff003F1C), false, true),
	MOORBORDER(8, new Color(0xff003F1C), false, true),
	MOORBORDEROUTER(9, new Color(0xff003F1C), false, false),
	FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	SHARP_FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	GRAVEL(230, new Color(0xff000000), false, false), // TODO: color
	DESERTBORDER(19, new Color(0xff949200), false, false),
	DESERTBORDEROUTER(20, new Color(0xff949200), false, false),
	SNOWBORDER(25, new Color(0xffd7fffe), false, true),
	SNOWBORDEROUTER(26, new Color(0xffd7fffe), false, false),
	MUD(4, new Color(0xff0e87cc), false, true), // TODO: color
	MUDBORDER(5, new Color(0xff0e87cc), false, true), // TODO: color
	MUDBORDEROUTER(6, new Color(0xff0e87cc), false, false), // TODO: color
	ROAD(34, new Color(0xff156E15), false, false), // TODO: color
	DRY_EARTH(36, new Color(0xffADAB00), false, false), // TODO: coor
	;

	public static final ELandscapeType[] VALUES = ELandscapeType.values();
	private static final Set<ELandscapeType> RIVERS = EnumSet.of(RIVER1, RIVER2, RIVER3, RIVER4);
	public static final Set<ELandscapeType> MOUNTAIN_TYPES = EnumSet.of(MOUNTAIN, MOUNTAINBORDER);
	private static final Set<ELandscapeType> FLAT_TYPES = EnumSet.of(MOOR, MOORBORDER, WATER1, WATER2, WATER3, WATER4, WATER5, WATER6, WATER7, WATER8);

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

	public static final Map<ELandscapeType, Set<ELandscapeType>> neighbors = new EnumMap<>(ELandscapeType.class);
	public static final Map<ELandscapeType, Set<ELandscapeType>> children = new EnumMap<>(ELandscapeType.class);
	public static final Map<ELandscapeType, ELandscapeType> roots = new EnumMap<>(ELandscapeType.class);

	static {
		// water hierarchy
		neighbors.put(WATER8, EnumSet.of(WATER8, WATER7));
		neighbors.put(WATER7, EnumSet.of(WATER8, WATER7, WATER6));
		neighbors.put(WATER6, EnumSet.of(WATER7, WATER6, WATER5));
		neighbors.put(WATER5, EnumSet.of(WATER6, WATER5, WATER4));
		neighbors.put(WATER4, EnumSet.of(WATER5, WATER4, WATER3));
		neighbors.put(WATER3, EnumSet.of(WATER4, WATER3, WATER2));
		neighbors.put(WATER2, EnumSet.of(WATER3, WATER2, WATER1));
		neighbors.put(WATER1, EnumSet.of(WATER2, WATER1, SAND, RIVER1, RIVER2, RIVER3, RIVER4));
		neighbors.put(SAND, EnumSet.of(WATER1, SAND, RIVER1, RIVER2, RIVER3, RIVER4, GRASS));

		// children of sand
		neighbors.put(RIVER1, EnumSet.of(SAND, RIVER1, RIVER2, RIVER3, RIVER4, GRASS, WATER1));
		neighbors.put(RIVER2, EnumSet.of(SAND, RIVER1, RIVER2, RIVER3, RIVER4, GRASS, WATER1));
		neighbors.put(RIVER3, EnumSet.of(SAND, RIVER1, RIVER2, RIVER3, RIVER4, GRASS, WATER1));
		neighbors.put(RIVER4, EnumSet.of(SAND, RIVER1, RIVER2, RIVER3, RIVER4, GRASS, WATER1));
		neighbors.put(GRASS, EnumSet.of(SAND, GRASS, RIVER1, RIVER2, RIVER3, RIVER4, FLATTENED, MOORBORDEROUTER, DRY_GRASS, DRY_EARTH, EARTH, MUDBORDEROUTER, MOUNTAINBORDEROUTER, DESERTBORDEROUTER, ROAD));

		// children of grass
		neighbors.put(ROAD, EnumSet.of(GRASS, ROAD));
		neighbors.put(FLATTENED, EnumSet.of(GRASS, FLATTENED));
		neighbors.put(DRY_GRASS, EnumSet.of(GRASS, DRY_GRASS, DESERT));
		neighbors.put(EARTH, EnumSet.of(GRASS, EARTH));
		neighbors.put(DRY_EARTH, EnumSet.of(GRASS, DRY_EARTH));

		// mountains
		neighbors.put(MOUNTAINBORDEROUTER, EnumSet.of(GRASS, MOUNTAINBORDEROUTER, MOUNTAINBORDER));
		neighbors.put(MOUNTAINBORDER, EnumSet.of(MOUNTAINBORDEROUTER, MOUNTAINBORDER, MOUNTAIN, GRAVEL));
		neighbors.put(GRAVEL, EnumSet.of(MOUNTAIN, GRAVEL));
		neighbors.put(MOUNTAIN, EnumSet.of(MOUNTAINBORDER, MOUNTAIN, SNOWBORDEROUTER, GRAVEL));
		neighbors.put(SNOWBORDEROUTER, EnumSet.of(MOUNTAIN, SNOWBORDEROUTER, SNOWBORDER));
		neighbors.put(SNOWBORDER, EnumSet.of(SNOWBORDEROUTER, SNOWBORDER, SNOW));
		neighbors.put(SNOW, EnumSet.of(SNOWBORDER, SNOW));

		// deserts
		neighbors.put(DESERTBORDEROUTER, EnumSet.of(GRASS, DESERTBORDEROUTER, DESERTBORDER));
		neighbors.put(DESERTBORDER, EnumSet.of(DESERTBORDEROUTER, DESERTBORDER, DESERT));
		neighbors.put(DESERT, EnumSet.of(DESERTBORDER, DESERT, DRY_GRASS, SHARP_FLATTENED_DESERT, FLATTENED_DESERT));
		neighbors.put(SHARP_FLATTENED_DESERT, EnumSet.of(DESERT, SHARP_FLATTENED_DESERT, FLATTENED_DESERT));
		neighbors.put(FLATTENED_DESERT, EnumSet.of(DESERT, FLATTENED_DESERT, SHARP_FLATTENED_DESERT));

		// moors
		neighbors.put(MOORBORDEROUTER, EnumSet.of(GRASS, MOORBORDEROUTER, MOORBORDER));
		neighbors.put(MOORBORDER, EnumSet.of(MOORBORDEROUTER, MOORBORDER, MOOR));
		neighbors.put(MOOR, EnumSet.of(MOORBORDER, MOOR));

		// muds
		neighbors.put(MUDBORDEROUTER, EnumSet.of(GRASS, MUDBORDEROUTER, MUDBORDER));
		neighbors.put(MUDBORDER, EnumSet.of(MUDBORDEROUTER, MUDBORDER, MUD));
		neighbors.put(MUD, EnumSet.of(MUDBORDER, MUD));

		// root node, should be first in neighbors set
		roots.put(WATER8, null);
		roots.put(WATER7, WATER8);
		roots.put(WATER6, WATER7);
		roots.put(WATER5, WATER6);
		roots.put(WATER4, WATER5);
		roots.put(WATER3, WATER4);
		roots.put(WATER2, WATER3);
		roots.put(WATER1, WATER2);
		roots.put(SAND, WATER1);

		roots.put(RIVER1, SAND);
		roots.put(RIVER2, SAND);
		roots.put(RIVER3, SAND);
		roots.put(RIVER4, SAND);
		roots.put(GRASS, SAND);

		roots.put(FLATTENED, GRASS);
		roots.put(DRY_GRASS, GRASS);
		roots.put(EARTH, GRASS);
		roots.put(ROAD, GRASS);
		roots.put(DRY_EARTH, GRASS);

		roots.put(MOUNTAINBORDEROUTER, GRASS);
		roots.put(MOUNTAINBORDER, MOUNTAINBORDEROUTER);
		roots.put(GRAVEL, MOUNTAIN);
		roots.put(MOUNTAIN, MOUNTAINBORDER);
		roots.put(SNOWBORDEROUTER, MOUNTAIN);
		roots.put(SNOWBORDER, SNOWBORDEROUTER);
		roots.put(SNOW, SNOWBORDER);

		// deserts
		roots.put(DESERTBORDEROUTER, GRASS);
		roots.put(DESERTBORDER, DESERTBORDEROUTER);
		roots.put(DESERT, DESERTBORDER);
		roots.put(SHARP_FLATTENED_DESERT, DESERT);
		roots.put(FLATTENED_DESERT, DESERT);

		// moors
		roots.put(MOORBORDEROUTER, GRASS);
		roots.put(MOORBORDER, MOORBORDEROUTER);
		roots.put(MOOR, MOORBORDER);

		// muds
		roots.put(MUDBORDEROUTER, GRASS);
		roots.put(MUDBORDER, MUDBORDEROUTER);
		roots.put(MUD, MUDBORDER);

		for(ELandscapeType type : VALUES) {
			EnumSet<ELandscapeType> empty = EnumSet.noneOf(ELandscapeType.class);
			for(ELandscapeType type2 : VALUES) {
				if(roots.get(type2) == type) empty.add(type2);
			}
			children.put(type, empty);
		}
	}

	public boolean isRoot(ELandscapeType type) {
		ELandscapeType temp_root = type;
		while(temp_root != null) {
			if(this == temp_root) return true;
			temp_root = roots.get(temp_root);
		}
		return false;
	}

	public ELandscapeType getDirectRoot() {
		return roots.get(this);
	}

	public Set<ELandscapeType> getDirectChildren() {
		return children.get(this);
	}

	public boolean isAllowedNeighbor(ELandscapeType type) {
		return neighbors.get(this).contains(type);
	}

	public boolean canHoldResource(EResourceType resource) {
		return (resource == EResourceType.FISH && this.isWater()) || (resource != EResourceType.FISH && MOUNTAIN_TYPES.contains(this));
	}

	public boolean isFlat() {
		return FLAT_TYPES.contains(this);
	}
}
