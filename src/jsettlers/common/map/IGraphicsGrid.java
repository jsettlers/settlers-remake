package jsettlers.common.map;

import jsettlers.common.Color;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

/**
 * This interface specifies the methods needed by jsettlers.graphics to draw the grid and all it's content.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGraphicsGrid {

	/**
	 * @return height of map.
	 */
	short getHeight();

	/**
	 * @return width of map
	 */
	short getWidth();

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return {@link IMovable} object at the given position or null if there is none.
	 */
	IMovable getMovableAt(int x, int y);

	/**
	 * Gets the first map object that is placed on the given position. There may be more map objects that can be retained by using the
	 * {@link IMapObject#getNextObject()} method.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return an {@link IMapObject} that's at the given position or null if there is none.
	 */
	IMapObject getMapObjectsAt(int x, int y);

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return height at the given position.
	 */
	byte getHeightAt(int x, int y);

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return {@link ELandscapeType} at the given position.
	 */
	ELandscapeType getLandscapeTypeAt(int x, int y);

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return {@link Color} object that should be drawn as debug color at the given position or null if no debug color should be drawn.
	 */
	Color getDebugColorAt(int x, int y);

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return true if this position is a border position.
	 */
	boolean isBorder(int x, int y);

	/**
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return -1 if this position has no player (for example when it is not occupied)<br>
	 *         otherwise: the player number occupying this position.
	 */
	byte getPlayerAt(int x, int y);

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return visibility value defined by the fog of war algorithm
	 */
	byte getVisibleStatus(int x, int y);

	boolean isFogOfWarVisible(int x, int y);

	/**
	 * This method can be used to set a {@link IGraphicsBackgroundListener} to this {@link IGraphicsGrid}. <br>
	 * 
	 * @see IGraphicsBackgroundListener
	 * 
	 * @param backgroundListener
	 *            listener to be set.
	 */
	void setBackgroundListener(IGraphicsBackgroundListener backgroundListener);
}
