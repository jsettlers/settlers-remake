package jsettlers.common.landscape;

import jsettlers.common.Color;

public enum ELandscapeType {
	// DO NOT sort, order is important!
	GRASS(0, new Color(0x3f9b0b), false),
	DRY_GRASS(1, new Color(0x5cac2d), false),
	DESERT(3, new Color(0xccad60), false),
	EARTH(2, new Color(0xa2653e), false),
	MOUNTAIN(21, new Color(0x59656d), false),
	SNOW(24, new Color(0xd7fffe), false),
	SAND(3, new Color(0xccad60), false),
	FLATTENED(35, new Color(0xcfaf7b), false),
	RIVER1(10, new Color(0x0e87cc), false),
	RIVER2(10, new Color(0x0e87cc), false),
	RIVER3(10, new Color(0x0e87cc), false),
	RIVER4(10, new Color(0x0e87cc), false),
	MOUNTAINBORDER(21, new Color(0x59656d), false),
	MOUNTAINBORDEROUTER(21, new Color(0x59656d), false),
	WATER1(17, new Color(0x0e87cc), true),
	WATER2(16, new Color(0x0e87cc), true),
	WATER3(15, new Color(0x0e87cc), true),
	WATER4(14, new Color(0x0e87cc), true),
	WATER5(13, new Color(0x0e87cc), true),
	WATER6(12, new Color(0x0e87cc), true),
	WATER7(11, new Color(0x0e87cc), true),
	WATER8(10, new Color(0x0e87cc), true),
	MOOR(10, new Color(0x0e87cc), false), ;

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
}
