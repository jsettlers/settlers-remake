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
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

public class TreeObjectContainer implements ObjectContainer, IMapObject {

	@Override
	public MapDataObject getMapObject() {
		return MapTreeObject.getInstance();
	}

	public static ObjectContainer getInstance() {
		return new TreeObjectContainer();
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.TREE_ADULT;
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return new RelativePoint[] {
				new RelativePoint(0, 0),
				new RelativePoint(1, 0),
				new RelativePoint(1, 1),
				new RelativePoint(0, 1),
				new RelativePoint(-1, 0),
				new RelativePoint(-1, -1),
				new RelativePoint(0, -1),
		};
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}
}
