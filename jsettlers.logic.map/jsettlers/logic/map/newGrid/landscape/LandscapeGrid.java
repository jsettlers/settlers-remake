package jsettlers.logic.map.newGrid.landscape;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This grid stores the height and the {@link ELandscapeType} of every position.
 * 
 * @author Andreas Eberle
 * 
 */
public class LandscapeGrid {
	private final byte[][] heightGrid;
	private final ELandscapeType[][] landscapeGrid;

	public LandscapeGrid(short width, short height) {
		this.heightGrid = new byte[width][height];
		this.landscapeGrid = new ELandscapeType[width][height];
	}

	public byte getHeightAt(short x, short y) {
		return heightGrid[x][y];
	}

	public void setHeight(short x, short y, byte height) {
		this.heightGrid[x][y] = height;
	}

	public ELandscapeType getLandscapeType(short x, short y) {
		return landscapeGrid[x][y];
	}

	public void setLandscapeType(short x, short y, ELandscapeType landscapeType) {
		this.landscapeGrid[x][y] = landscapeType;
	}
}
