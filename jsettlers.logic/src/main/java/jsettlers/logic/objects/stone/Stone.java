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
package jsettlers.logic.objects.stone;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;

/**
 * A stone on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public class Stone extends AbstractHexMapObject {
	private static final long serialVersionUID = 2470787539788090906L;

	private static final int MAX_CAPACITY = 12;
	public static final float DECOMPOSE_DELAY = 400;

	private byte leftCapacity = 0;

	public Stone() {
		this(MAX_CAPACITY);
	}

	public Stone(int capacity) {
		leftCapacity = (byte) capacity;
	}

	@Override
	public boolean cutOff() {
		if (!canBeCut()) {
			return false;
		}

		this.leftCapacity--;
		return true;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
	public float getStateProgress() {
		return leftCapacity;
	}

	@Override
	public RelativePoint[] getBlockedTiles() {
		return new RelativePoint[] { new RelativePoint(-1, -1), new RelativePoint(0, -1), new RelativePoint(-1, 0), new RelativePoint(0, 0),
				new RelativePoint(0, 1), new RelativePoint(1, 0), new RelativePoint(1, 1), };
	}

	@Override
	public boolean canBeCut() {
		return leftCapacity > 0;
	}

}
