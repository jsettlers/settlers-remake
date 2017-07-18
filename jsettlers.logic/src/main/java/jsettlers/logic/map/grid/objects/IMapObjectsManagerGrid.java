/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.logic.map.grid.objects;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.objects.arrow.IArrowAttackableGrid;
import jsettlers.logic.player.Player;

public interface IMapObjectsManagerGrid extends Serializable, IArrowAttackableGrid {
	AbstractHexMapObject getMapObject(int x, int y, EMapObjectType mapObjectType);

	void setLandscape(int x, int y, ELandscapeType landscapeType);

	void addMapObject(int x, int y, AbstractHexMapObject mapObject);

	boolean isBlocked(int x, int y);

	void setBlocked(int x, int y, boolean blocked);

	boolean isProtected(int x, int y);

	void setProtected(int x, int y, boolean protect);

	boolean removeMapObject(int x, int y, AbstractHexMapObject mapObject);

	short getWidth();

	short getHeight();

	boolean isInBounds(int x, int y);

	EResourceType getResourceTypeAt(int x, int y);

	byte getResourceAmountAt(int x, int y);

	boolean isBuildingAreaAt(short x, short y);

	boolean hasMapObjectType(int x, int y, EMapObjectType... mapObjectTypes);

	void spawnDonkey(ShortPoint2D position, Player player);
}
