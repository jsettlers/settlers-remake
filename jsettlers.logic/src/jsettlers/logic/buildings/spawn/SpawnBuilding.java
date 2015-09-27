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
package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

/**
 * Abstract parent class for buildings that spawn new movables.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class SpawnBuilding extends Building {
	private static final long serialVersionUID = 7584783336566602225L;

	private static final int PRODUCE_PERIOD = 2000;
	private byte produced = 0;

	protected SpawnBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected int constructionFinishedEvent() {
		return PRODUCE_PERIOD;
	}

	@Override
	protected final int subTimerEvent() {
		int rescheduleDelay;
		Movable movableAtDoor = super.getGrid().getMovable(super.getDoor());

		if (movableAtDoor == null) {
			movableAtDoor = new Movable(super.getGrid().getMovableGrid(), getMovableType(), getDoor(), super.getPlayer());
			produced++;

			if (produced < getProduceLimit()) {
				rescheduleDelay = PRODUCE_PERIOD;
			} else {
				rescheduleDelay = -1; // remove from scheduling
			}
		} else {
			rescheduleDelay = 100; // door position wasn't free, so check more often
		}

		movableAtDoor.leavePosition();

		return rescheduleDelay;
	}

	protected abstract EMovableType getMovableType();

	protected abstract byte getProduceLimit();

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	public final boolean isOccupied() {
		return true;
	}

	@Override
	public boolean cannotWork() {
		return produced >= getProduceLimit();
	}
}
