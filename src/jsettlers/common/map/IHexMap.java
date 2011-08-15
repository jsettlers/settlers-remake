package jsettlers.common.map;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ISPosition2D;

public interface IHexMap {

	/**
	 * preview image to be displayed if the field can be used to build the given EBuildingType
	 * 
	 * @return null if no preview building should be displayed<br>
	 *         EBuildingType otherwise.
	 */
	public EBuildingType getConstructionPreviewBuilding();

	public short getWidth();

	public short getHeight();

	/**
	 * Gets a tile from the map at a given position.
	 * 
	 * @param pos
	 *            The position.
	 * @return The tile or <code>null</code> if the coordinates are outside the grid.
	 */
	public IHexTile getTile(ISPosition2D pos);

	/**
	 * Short version of {@link #getTile(ISPosition2D)}, for better heap usage.
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return The tile or <code>null</code> if the coordinates are outside the grid.
	 */
	public IHexTile getTile(short x, short y);
}
