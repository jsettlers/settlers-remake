package jsettlers.logic.map.newGrid.landscape;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This grid stores the height and the {@link ELandscapeType} of every position.
 * 
 * @author Andreas Eberle
 * 
 */
public class LandscapeGrid implements Serializable {
	private static final long serialVersionUID = -751261669662036483L;

	private final byte[][] heightGrid;
	private final ELandscapeType[][] landscapeGrid;

	public LandscapeGrid(short width, short height) {
		this.heightGrid = new byte[width][height];
		this.landscapeGrid = new ELandscapeType[width][height];
	}

	public byte getHeightAt(short x, short y) {
		return heightGrid[x][y];
	}

	public void setHeightAt(short x, short y, byte height) {
		this.heightGrid[x][y] = height;
	}

	public ELandscapeType getLandscapeTypeAt(short x, short y) {
		return landscapeGrid[x][y];
	}

	public void setLandscapeTypeAt(short x, short y, ELandscapeType landscapeType) {
		this.landscapeGrid[x][y] = landscapeType;
	}

	public void changeHeightAt(short x, short y, byte delta) {
		this.heightGrid[x][y] += delta;
	}
}
