/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.logic.movable.strategies.ships;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

public abstract class ShipStrategy extends MovableStrategy {
	private static final long serialVersionUID = 8360707186573364992L;

	private final EMovableType movableType;

	public ShipStrategy(Movable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
	}

	@Override
	protected void action() {
	}

	public EMovableType getMovableType() {
		return movableType;
	}

	public Movable getMovable() {
		return movable;
	}

	public void setSelected(boolean selected) {
		movable.setSelected(selected);
	}

	@Override
	protected boolean canBeControlledByPlayer() {
		return true;
	}

	private ShortPoint2D getRandomFreePosition(ShortPoint2D pos1, ShortPoint2D pos2) {
		boolean pos1Free = getGrid().isFreeShipPosition(pos1);
		boolean pos2Free = getGrid().isFreeShipPosition(pos2);

		if (pos1Free && pos2Free) {
			return MatchConstants.random().nextBoolean() ? pos1 : pos2;
		} else if (pos1Free) {
			return pos1;
		} else if (pos2Free) {
			return pos2;
		} else {
			return null;
		}
	}
}
