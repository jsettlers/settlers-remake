package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This is the map data of a map that is beeing created by the editor.
 * 
 * @author michael
 */
public class MapData {
	private final int width;
	private final int height;

	private final ELandscapeType[][] landscapes;

	public MapData(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
			        "width and height must be positive");
		}

		if (width > Short.MAX_VALUE || height > Short.MAX_VALUE) {
			throw new IllegalArgumentException(
			        "width and height must be less than "
			                + (Short.MAX_VALUE + 1));
		}

		this.width = width;
		this.height = height;
		this.landscapes = new ELandscapeType[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landscapes[x][y] = ELandscapeType.GRASS;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ELandscapeType getLandscapeAt(int x, int y) {
		return landscapes[x][y];
	}
}
