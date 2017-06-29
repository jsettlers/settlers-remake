/*
 * Copyright (c) 2015 - 2017
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
 */

package jsettlers.logic.map.loading.data;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.common.position.ShortPoint2D;

public interface IMapData {

	int getWidth();

	int getHeight();

	ELandscapeType getLandscape(int x, int y);

	MapDataObject getMapObject(int x, int y);

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