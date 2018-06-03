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
package jsettlers.logic.movable.strategies.specialists;

import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class GeologistStrategy extends MovableStrategy {
	private static final long serialVersionUID = 1L;

	private static final float ACTION1_DURATION = 1.4f;
	private static final float ACTION2_DURATION = 1.5f;

	private EGeologistState state = EGeologistState.JOBLESS;
	private ShortPoint2D centerPos;

	public GeologistStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			return;

		case GOING_TO_POS: {
			ShortPoint2D pos = movable.getPosition();

			if (centerPos == null) {
				this.centerPos = pos;
			}

			super.getGrid().setMarked(pos, false); // unmark the pos for the following check
			if (canWorkOnPos(pos)) {
				super.getGrid().setMarked(pos, true);
				super.playAction(EMovableAction.ACTION1, ACTION1_DURATION);
				state = EGeologistState.PLAYING_ACTION_1;
			} else {
				findWorkablePosition();
			}
		}
			break;

		case PLAYING_ACTION_1:
			super.playAction(EMovableAction.ACTION2, ACTION2_DURATION);
			state = EGeologistState.PLAYING_ACTION_2;
			break;

		case PLAYING_ACTION_2: {
			ShortPoint2D pos = movable.getPosition();
			super.getGrid().setMarked(pos, false);
			if (canWorkOnPos(pos)) {
				executeAction(pos);
			}

			findWorkablePosition();
		}
			break;
		}
	}

	private void findWorkablePosition() {
		ShortPoint2D closeWorkablePos = getCloseWorkablePos();

		if (closeWorkablePos != null && super.goToPos(closeWorkablePos)) {
			super.getGrid().setMarked(closeWorkablePos, true);
			this.state = EGeologistState.GOING_TO_POS;
			return;
		}
		centerPos = null;

		ShortPoint2D pos = movable.getPosition();
		if (super.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.RESOURCE_SIGNABLE)) {
			super.followPresearchedPath();
			this.state = EGeologistState.GOING_TO_POS;
			return;
		}

		this.state = EGeologistState.JOBLESS;
	}

	private ShortPoint2D getCloseWorkablePos() {
		MutablePoint2D bestNeighbourPos = new MutablePoint2D(-1, -1);
		MutableDouble bestNeighbourDistance = new MutableDouble(Double.MAX_VALUE); // distance from start point

		HexGridArea.streamBorder(movable.getPosition(), 2).filter((x, y) -> super.isValidPosition(x, y) && canWorkOnPos(x, y)).forEach((x, y) -> {
			double distance = ShortPoint2D.getOnGridDist(x - centerPos.x, y - centerPos.y);
			if (distance < bestNeighbourDistance.value) {
				bestNeighbourDistance.value = distance;
				bestNeighbourPos.x = x;
				bestNeighbourPos.y = y;
			}
		});

		if (bestNeighbourDistance.value != Double.MAX_VALUE) {
			return bestNeighbourPos.createShortPoint2D();
		} else {
			return null;
		}
	}

	private void executeAction(ShortPoint2D pos) {
		super.getGrid().executeSearchType(movable, pos, ESearchType.RESOURCE_SIGNABLE);
	}

	private boolean canWorkOnPos(ShortPoint2D pos) {
		return super.fitsSearchType(pos, ESearchType.RESOURCE_SIGNABLE);
	}

	private boolean canWorkOnPos(int x, int y) {
		return super.fitsSearchType(x, y, ESearchType.RESOURCE_SIGNABLE);
	}

	@Override
	protected boolean canBeControlledByPlayer() {
		return true;
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		this.state = EGeologistState.GOING_TO_POS;
		centerPos = null;

		super.getGrid().setMarked(oldPosition, false);

		if (oldTargetPos != null) {
			super.getGrid().setMarked(oldTargetPos, false);
		}
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		if (stop) {
			state = EGeologistState.JOBLESS;
		} else {
			state = EGeologistState.GOING_TO_POS;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (pathTarget != null) {
			super.getGrid().setMarked(pathTarget, false);
		} else {
			super.getGrid().setMarked(movable.getPosition(), false);
		}
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		state = EGeologistState.JOBLESS;
	}

	private enum EGeologistState {
		JOBLESS,
		GOING_TO_POS,
		PLAYING_ACTION_1,
		PLAYING_ACTION_2
	}
}
