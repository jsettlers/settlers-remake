package jsettlers.common.map;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;

public interface IMapData {

	int getWidth();

	int getHeight();

	ELandscapeType getLandscape(int x, int y);

	MapObject getMapObject(int x, int y);

	byte getLandscapeHeight(int x, int y);

	/**
	 * Gets the start point of the given player. Always returns a valid point.
	 * 
	 * @param player
	 * @return
	 */
	ShortPoint2D getStartPoint(int player);

	int getPlayerCount();

	EResourceType getResourceType(short x, short y);

	/**
	 * Gets the amount of resources for a given position. In range 0..127
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	byte getResourceAmount(short x, short y);

	/**
	 * Gets the id of the blocked partition of the given position.
	 * 
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return The id of the blocked partition the given position belongs to.
	 */
	short getBlockedPartition(short x, short y);

}