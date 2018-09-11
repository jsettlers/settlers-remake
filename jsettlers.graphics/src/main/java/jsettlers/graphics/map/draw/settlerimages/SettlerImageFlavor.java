/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.graphics.map.draw.settlerimages;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;

import java.util.Objects;

public final class SettlerImageFlavor {

	public static final SettlerImageFlavor NONE = new SettlerImageFlavor(null, null, null, null);
	private final EMovableType type;
	private final EMovableAction action;
	private final EMaterialType material;
	private final EDirection direction;

	public SettlerImageFlavor(EMovableType type, EMovableAction action, EMaterialType material, EDirection direction) {
		this.type = type;
		this.action = action;
		this.material = material;
		this.direction = direction;
	}

	static SettlerImageFlavor createFromMovable(IMovable movable) {
		return new SettlerImageFlavor(movable.getMovableType(), movable.getAction(), movable.getMaterial(), movable.getDirection());
	}

	public EMovableType getType() {
		return type;
	}

	public EMovableAction getAction() {
		return action;
	}

	public EMaterialType getMaterial() {
		return material;
	}

	public EDirection getDirection() {
		return direction;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SettlerImageFlavor that = (SettlerImageFlavor) o;

		if (type != that.type) return false;
		if (action != that.action) return false;
		if (material != that.material) return false;
		return direction == that.direction;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (action != null ? action.hashCode() : 0);
		result = 31 * result + (material != null ? material.hashCode() : 0);
		result = 31 * result + (direction != null ? direction.hashCode() : 0);
		return result;
	}

	int calculatePriority() {
		int priority = 1;// more than 0.
		if (getType() != null) {
			priority += 10;
		}
		if (getAction() != null) {
			priority += 100;
		}
		if (getMaterial() != null) {
			priority += 1000;
		}
		if (getDirection() != null) {
			priority += 10000;
		}
		return priority;
	}
}
