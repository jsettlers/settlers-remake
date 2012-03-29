package jsettlers.common.map;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;

public interface IMapData {

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract ELandscapeType getLandscape(int x, int y);

	public abstract MapObject getMapObject(int x, int y);

	public abstract byte getLandscapeHeight(int x, int y);

	/**
	 * Gets the start point of the given player. Always returns a valid point.
	 * 
	 * @param player
	 * @return
	 */
	public abstract ShortPoint2D getStartPoint(int player);
	
	public abstract int getPlayerCount();

}