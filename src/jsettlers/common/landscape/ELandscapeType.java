package jsettlers.common.landscape;

public enum ELandscapeType {
	GRASS(0),
	DRY_GRASS(1),
	DESERT(3),
	EARTH(2),
	MOUNTAIN(21),
	SNOW(24),
	WATER(10),
	SAND(3),
	FLATTENED(35),
	RIVER1(10),
	RIVER2(10),
	RIVER3(10),
	RIVER4(10), MOUNTAINBORDER(21);

	private int image;

	ELandscapeType(int image) {
		this.image = image;
	}
	
	public int getImageNumber() {
	    return image;
    }

}
