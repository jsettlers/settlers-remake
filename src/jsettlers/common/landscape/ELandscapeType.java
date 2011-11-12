package jsettlers.common.landscape;

import jsettlers.common.Color;


public enum ELandscapeType {
	GRASS(0, new Color(0x3f9b0b)),
	DRY_GRASS(1, new Color(0x5cac2d)),
	DESERT(3, new Color(0xccad60)),
	EARTH(2, new Color(0xa2653e)),
	MOUNTAIN(21, new Color(0x59656d)),
	SNOW(24, new Color(0xd7fffe)),
	WATER(10, new Color(0x0e87cc)),
	SAND(3, new Color(0xccad60)),
	FLATTENED(35, new Color(0xcfaf7b)),
	RIVER1(10, new Color(0x0e87cc)),
	RIVER2(10, new Color(0x0e87cc)),
	RIVER3(10, new Color(0x0e87cc)),
	RIVER4(10, new Color(0x0e87cc)), 
	MOUNTAINBORDER(21, new Color(0x59656d));
	
	private final int image;
	private final Color color;

	ELandscapeType(int image, Color color) {
		this.image = image;
		this.color = color;
	}
	
	public int getImageNumber() {
	    return image;
    }

	/**
	 * Gets the base color of the landscape
	 * @return The color the landscape has.
	 */
	public Color getColor() {
	    return color;
    }

}
