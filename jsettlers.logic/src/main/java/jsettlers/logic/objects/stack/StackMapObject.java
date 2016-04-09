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
package jsettlers.logic.objects.stack;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;

/**
 * This is a stack object that can be located on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public final class StackMapObject extends AbstractHexMapObject implements IStackMapObject {
	private static final long serialVersionUID = -5471566113368524172L;

	private final byte materialType;
	private byte size;
	private byte markedStolen = 0;

	public StackMapObject(EMaterialType materialType, byte size) {
		this.materialType = materialType.ordinal;
		this.size = size;
	}

	public final boolean isEmpty() {
		return size == 0;
	}

	public final boolean isFull() {
		return size >= Constants.STACK_SIZE;
	}

	public final void incrementStolenMarks() {
		markedStolen++;
	}

	public final void decrementStolenMarks() {
		markedStolen--;
	}

	/**
	 * 
	 * @return true if the stack has a material that is not already marked by a thief.<br>
	 *         false if all materials are already marked by thiefs.
	 */
	public final boolean hasUnstolen() {
		return markedStolen < size;
	}

	public final boolean hasStolenMarks() {
		return markedStolen > 0;
	}

	public final void increment() {
		size++;
	}

	public final void decrement() {
		size--;
	}

	@Override
	public final EMapObjectType getObjectType() {
		return EMapObjectType.STACK_OBJECT;
	}

	@Override
	public final float getStateProgress() {
		return 0;
	}

	@Override
	public final EMaterialType getMaterialType() {
		return EMaterialType.VALUES[materialType];
	}

	@Override
	public final byte getSize() {
		return size;
	}

	@Override
	public final boolean cutOff() {
		return false;
	}

	@Override
	public final boolean canBeCut() {
		return false;
	}

}
