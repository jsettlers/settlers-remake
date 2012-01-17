package jsettlers.common.landscape;

import jsettlers.common.Color;

public enum ELandscapeType {
	// DO NOT sort, order is important!
	GRASS(0, new Color(0x105910), false),
	DRY_GRASS(1, new Color(0x105910), false),
	DESERT(3, new Color(0x949200), false),
	EARTH(2, new Color(0xa2653e), false), //TODO: color
	MOUNTAIN(21, new Color(0x424142), false),
	SNOW(24, new Color(0xd7fffe), false),
	SAND(3, new Color(0x949200), false),
	/**
	 * Flattened grass (for buildings, paths, ...).
	 * Must behave exactly like normal grass does!
	 */
	FLATTENED(35, new Color(0x105910), false),
	RIVER1(10, new Color(0x000073), false), 
	RIVER2(10, new Color(0x000073), false), 
	RIVER3(10, new Color(0x000073), false), 
	RIVER4(10, new Color(0x000073), false), 
	MOUNTAINBORDER(21, new Color(0x424142), false),
	MOUNTAINBORDEROUTER(21, new Color(0x105910), false), //TODO: color
	WATER1(17, new Color(0x000073), true),
	WATER2(16, new Color(0x000073), true),
	WATER3(15, new Color(0x000073), true),
	WATER4(14, new Color(0x000073), true),
	WATER5(13, new Color(0x000073), true),
	WATER6(12, new Color(0x000073), true),
	WATER7(11, new Color(0x000073), true),
	WATER8(10, new Color(0x000073), true),
	MOOR(7, new Color(0x0e87cc), false), //TODO: color
	MOORINNER(7, new Color(0x0e87cc), false), //TODO: color
	MOORBORDER(9, new Color(0x0e87cc), false), //TODO: color
	FLATTENED_DESERT(217, new Color(0x949200), false),
	SHARP_FLATTENED_DESERT(217, new Color(0x949200), false),
	GRAVEL(230, new Color(0x00), false), ; //TODO: color

	private final int image;
	private final Color color;
	private final boolean isWater;

	ELandscapeType(int image, Color color, boolean isWater) {
		this.image = image;
		this.color = color;
		this.isWater = isWater;
	}

	public int getImageNumber() {
		return image;
	}

	/**
	 * Gets the base color of the landscape
	 * 
	 * @return The color the landscape has.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Checks if this landscape type is water (not river, just water that ships
	 * can swim on.).
	 * <p>
	 * To check for unwalkable land, also test if it is MOOR or SNOW
	 * 
	 * @return
	 */
	public boolean isWater() {
		return isWater;
	}

	public boolean isGrass() {
	    return this == GRASS || this == FLATTENED;
    }
}
