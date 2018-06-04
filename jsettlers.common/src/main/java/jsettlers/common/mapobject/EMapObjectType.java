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
package jsettlers.common.mapobject;

import java.util.EnumSet;
import java.util.Set;

public enum EMapObjectType {
	TREE_GROWING,
	TREE_ADULT,
	TREE_DEAD,

	CORN_GROWING,
	CORN_ADULT,
	CORN_DEAD,

	WINE_GROWING,
	WINE_HARVESTABLE,
	WINE_DEAD,
	WINE_BOWL,

	WAVES,
	STONE,
	/**
	 * This is the arrow object type.
	 * <p>
	 * Map objects with type arrow must implement {@link IArrowMapObject}
	 */
	ARROW,
	/**
	 * A Ghost (disappearing settler).
	 */
	GHOST,
	/**
	 * A working area mark. A progress of 0 means the outer ring, 1 means the inner ring. Currently, there are 4 rings.
	 */
	WORKAREA_MARK(false),
	/**
	 * The coal-mark for mountains. 0 means few coal, 1 is a lot.
	 */
	FOUND_COAL,
	FOUND_IRON,
	FOUND_GOLD,
	FOUND_GEMSTONE,
	FOUND_BRIMSTONE,
	FOUND_NOTHING,
	/**
	 * The sign that marks the center of a building site.
	 */
	BUILDINGSITE_SIGN,
	/**
	 * Building site borders.
	 */
	BUILDINGSITE_POST,

	/**
	 * in front of a door
	 */
	FLAG_DOOR,

	/**
	 * on top of the roof
	 */
	FLAG_ROOF,

	/**
	 * the rest of a stone that can not be cut any more.
	 */
	CUT_OFF_STONE,

	/**
	 * Type of objects used to view the user where a building can be build.
	 * <p />
	 * The value of the construction mark is given by {@link IMapObject} .getStateProgress().<br>
	 * The value ranges from 0 to 1 where 0 is best and 1 is worst.<br>
	 * If there is no marking at a position, the building can not be constructed there.
	 */
	CONSTRUCTION_MARK(false),

	/**
	 * Type to represent material stacks.
	 * <p />
	 * {@link IMapObject}s of this type must implement {@link IStackMapObject}.
	 */
	STACK_OBJECT,

	/**
	 * Type to represent a Building
	 * <p />
	 * {@link IMapObject}s of this type must implement {@link IBuildingMapObject}.
	 */
	BUILDING,

	/**
	 * Type to represent the Building shown when placing for construction.
	 */
	PLACEMENT_BUILDING,

	/**
	 * Simple smoke
	 */
	SMOKE,

	/**
	 * A pig. Progress is ignored.
	 */
	PIG,
	/**
	 * A donkey (in the donkey farm)
	 */
	DONKEY,

	/**
	 * cloud of smoke when a building get's torn down.
	 */
	BUILDING_DECONSTRUCTION_SMOKE,
	PLANT_DECORATION,
	DESERT_DECORATION,
	DOCK,

	/**
	 * Animated fish in the water.
	 */
	FISH_DECORATION,

	/**
	 * Doesn't need to be drawn. <br>
	 * Must implement {@link IAttackableTowerMapObject}.
	 */
	ATTACKABLE_TOWER,

	/**
	 * doesn't need to be drawn.
	 */
	INFORMABLE_MAP_OBJECT,

	FERRY,
	CARGO_SHIP;

	public static final EMapObjectType[] VALUES = EMapObjectType.values();
	public final byte ordinal;
	/**
	 * If true, objects of this type should be saved. If false, they should not be persisted.
	 */
	public final boolean persistent;

	public static final Set<EMapObjectType> TO_BE_REMOVED_WHEN_FLATTENED = EnumSet.of(
			EMapObjectType.ARROW,
			EMapObjectType.CORN_GROWING,
			EMapObjectType.CORN_ADULT,
			EMapObjectType.CORN_DEAD,
			EMapObjectType.WINE_GROWING,
			EMapObjectType.WINE_HARVESTABLE,
			EMapObjectType.WINE_DEAD,
			EMapObjectType.CUT_OFF_STONE,
			EMapObjectType.DESERT_DECORATION,
			EMapObjectType.PLANT_DECORATION,
			EMapObjectType.TREE_DEAD);

	EMapObjectType() {
		this(true);
	}

	EMapObjectType(boolean persistent) {
		this.ordinal = (byte) super.ordinal();
		this.persistent = persistent;
	}
}
