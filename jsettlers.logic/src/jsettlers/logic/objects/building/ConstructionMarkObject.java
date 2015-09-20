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
package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;

/**
 * This map objects represent a construction marking used to show the user where he is able to construct a building.<br>
 * 
 * @see EMapObjectType.CONSTRUCTION_MARK
 * 
 * @author Andreas Eberle
 * 
 */
public final class ConstructionMarkObject extends AbstractHexMapObject {
	private static final long serialVersionUID = 4420024473109760614L;

	private float constructionValue;

	public ConstructionMarkObject(byte constructionValue) {
		this.setConstructionValue(constructionValue);
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.CONSTRUCTION_MARK;
	}

	@Override
	public float getStateProgress() {
		return constructionValue;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	public void setConstructionValue(byte constructionValue) {
		assert constructionValue >= 0 : "construction value must be >= 0";
		this.constructionValue = ((float) constructionValue) / Byte.MAX_VALUE;
	}

}
