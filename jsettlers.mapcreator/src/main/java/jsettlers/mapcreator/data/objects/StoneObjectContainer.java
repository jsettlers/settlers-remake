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
package jsettlers.mapcreator.data.objects;

import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

public class StoneObjectContainer implements ObjectContainer, IMapObject {
	private final StoneMapDataObject peer;

	public StoneObjectContainer(int stones) {
		this(StoneMapDataObject.getInstance(stones));
	}

	public StoneObjectContainer(StoneMapDataObject object) {
		this.peer = object;
	}

	@Override
	public MapDataObject getMapObject() {
		return peer;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
	public float getStateProgress() {
		return peer.getCapacity();
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
				new RelativePoint(0, 0),
				// inner circle
				new RelativePoint(1, 0),
				new RelativePoint(1, 1),
				new RelativePoint(0, 1),
				new RelativePoint(-1, 0),
				new RelativePoint(-1, -1),
				new RelativePoint(0, -1),
				// outer circle
				new RelativePoint(2, 0),
				new RelativePoint(1, 0),
				new RelativePoint(2, 2),
				new RelativePoint(1, 2),
				new RelativePoint(0, 2),
				new RelativePoint(-1, 1),
				new RelativePoint(-2, 0),
				new RelativePoint(-2, -1),
				new RelativePoint(-2, -2),
				new RelativePoint(-1, -2),
				new RelativePoint(0, -2),
				new RelativePoint(1, -1),
		};
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}
}
