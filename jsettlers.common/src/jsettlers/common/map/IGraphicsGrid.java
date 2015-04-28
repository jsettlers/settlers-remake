/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.map;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.partition.IPartitionSettings;
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
	 * @return width of map
	 */
	short getWidth();

	/**
	 * @return height of map.
	 */
	short getHeight();

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
	 * @param debugColorMode
	 *            {@link EDebugColorModes} enum that defines what should be printed.
	 * @return debug color to be drawn at the given position (in 16bit rgba format) or -1 if no color should be drawn.
	 */
	int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode);

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
	 *         otherwise: the id of the player occupying this position.
	 */
	byte getPlayerIdAt(int x, int y);

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

	/**
	 * Gets the next x coordinate that might contain a drawable Object.
	 * 
	 * @param x
	 * @param y
	 * @param maxX
	 *            the maximum x that needs to be searched.
	 * @return a value bigger than x, might be outside the map.
	 */
	int nextDrawableX(int x, int y, int maxX);

	/**
	 * Gets the current settings of the partition at the given position.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return Returns an object that gives access to the settings of the partition at the given position.<br>
	 *         For convenience during testing, the given value might be null.
	 */
	IPartitionSettings getPartitionSettings(int x, int y);
}
