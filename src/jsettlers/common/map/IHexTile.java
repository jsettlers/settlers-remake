package jsettlers.common.map;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.IStack;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;

/**
 * This is a tile of the hex map.
 * 
 * @author michael
 */
public interface IHexTile extends IPlayerable, ISPosition2D {

	/**
	 * The distance between two tiles in x direction (pixels).
	 */
	public static final int X_DISTANCE = 16;
	/**
	 * The distance between two tiles in y direction (pixels).
	 */
	public static final int Y_DISTANCE = 9;

	/**
	 * Gets the movable that is standing on the tile or walking towards the
	 * tile.
	 * 
	 * @return The movable or <code>null</code>
	 */
	public IMovable getMovable();

	/**
	 * If there is a building centered around this tile, it returns the
	 * building.
	 * 
	 * @return The building or <code>null</code>
	 */
	public IBuilding getBuilding();

	/**
	 * Gets the first map object that is placed on this tile. There may be more
	 * map objects that can be retained by using the
	 * {@link IMapObject#getNextObject()} method.
	 * 
	 * @return The map object or<code>null</code>
	 */
	public IMapObject getHeadMapObject();

	/**
	 * Gets the landscape type of the tile.
	 * 
	 * @return Some landscape type but never <code>null</code>
	 */
	public ELandscapeType getLandscapeType();

	/**
	 * 0 is the see level.
	 * 
	 * @return height of this tile
	 */
	public byte getHeight();

	/**
	 * higher value is worse.<br>
	 * value in range of [0, 127]
	 * 
	 * @return value in range of [0, 127]
	 */
	public byte getConstructionMark();

	/**
	 * Gets the base stack of this tile.
	 * 
	 * @return THe stack or <code>null</code>
	 */
	public IStack getStack();

	public Color getDebugColor();

}
