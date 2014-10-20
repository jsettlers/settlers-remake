package jsettlers.common.landscape;

import jsettlers.common.Color;

public enum ELandscapeType {
	// DO NOT sort, order is important!
	GRASS(0, new Color(0xff105910), false, false),
	DRY_GRASS(1, new Color(0xff105910), false, false),
	DESERT(3, new Color(0xff949200), false, false),
	EARTH(2, new Color(0xffa2653e), false, false), // TODO: color
	MOUNTAIN(21, new Color(0xff424142), false, false),
	SNOW(24, new Color(0xffd7fffe), false, true),
	SAND(3, new Color(0xff949200), false, false),
	/**
	 * Flattened grass (for buildings, paths, ...). Must behave exactly like normal grass does!
	 */
	FLATTENED(35, new Color(0xff105910), false, false),
	RIVER1(10, new Color(0xff000073), false, false),
	RIVER2(10, new Color(0xff000073), false, false),
	RIVER3(10, new Color(0xff000073), false, false),
	RIVER4(10, new Color(0xff000073), false, false),
	MOUNTAINBORDER(21, new Color(0xff424142), false, false),
	MOUNTAINBORDEROUTER(21, new Color(0xff105910), false, false), // TODO: color
	WATER1(17, new Color(0xff000073), true, true),
	WATER2(16, new Color(0xff000073), true, true),
	WATER3(15, new Color(0xff000073), true, true),
	WATER4(14, new Color(0xff000073), true, true),
	WATER5(13, new Color(0xff000073), true, true),
	WATER6(12, new Color(0xff000073), true, true),
	WATER7(11, new Color(0xff000073), true, true),
	WATER8(10, new Color(0xff000073), true, true),
	MOOR(7, new Color(0xff0e87cc), false, true), // TODO: color
	MOORINNER(7, new Color(0xff0e87cc), false, true), // TODO: color
	MOORBORDER(9, new Color(0xff0e87cc), false, false), // TODO: color
	FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	SHARP_FLATTENED_DESERT(217, new Color(0xff949200), false, false),
	GRAVEL(230, new Color(0xff000000), false, false); // TODO: color

	public static final ELandscapeType[] values = ELandscapeType.values();

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
}
