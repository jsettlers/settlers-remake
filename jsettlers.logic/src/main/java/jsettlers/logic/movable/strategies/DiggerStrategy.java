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
package jsettlers.logic.movable.strategies;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

public final class DiggerStrategy extends MovableStrategy implements IManageableDigger {
	private static final long serialVersionUID = 1581926355853324624L;

	private IDiggerRequester requester;
	private EDiggerState state = EDiggerState.JOBLESS;

	public DiggerStrategy(Movable movable) {
		super(movable);

		reportJobless();
	}

	@Override
	public boolean setDiggerJob(IDiggerRequester requester) {
		if (state == EDiggerState.JOBLESS) {
			this.requester = requester;
			this.state = EDiggerState.INIT_JOB;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			break;

		case INIT_JOB:
			goToDiggablePosition();
			break;

		case PLAYING_ACTION:
			executeDigg();
			if (!requester.isDiggerRequestActive()) {
				super.getGrid().setMarked(movable.getPosition(), false);
				reportJobless();
				break;
			}
		case GOING_TO_POS:
			if (needsToBeWorkedOn(movable.getPosition())) {
				super.playAction(EMovableAction.ACTION1, 1f);
				this.state = EDiggerState.PLAYING_ACTION;
			} else {
				goToDiggablePosition();
			}
			break;

		case DEAD_OBJECT:
			break;
		}
	}

	private void executeDigg() {
		ShortPoint2D pos = movable.getPosition();
		super.getGrid().changeHeightTowards(pos.x, pos.y, requester.getAverageHeight());
	}

	private void goToDiggablePosition() {
		super.getGrid().setMarked(movable.getPosition(), false);
		ShortPoint2D diggablePos = getDiggablePosition();
		if (diggablePos != null) {
			if (super.goToPos(diggablePos)) {
				state = EDiggerState.GOING_TO_POS;
				super.getGrid().setMarked(diggablePos, true);
			} else {
				reportJobless();
			}

		} else if (allPositionsFlattened()) { // all positions are flattened => building is finished
			reportJobless();

		} // else { not all positions are finished, so wait if one becomes unmarked or all are finished => do nothing
	}

	private boolean allPositionsFlattened() {
		for (RelativePoint relativePosition : requester.getBuildingType().getProtectedTiles()) {
			if (needsToBeWorkedOn(relativePosition.calculatePoint(requester.getPosition()))) {
				return false;
			}
		}
		return true;
	}

	private ShortPoint2D getDiggablePosition() {
		RelativePoint[] blockedTiles = requester.getBuildingType().getProtectedTiles();
		ShortPoint2D buildingPos = requester.getPosition();
		int offset = MatchConstants.random().nextInt(blockedTiles.length);

		for (int i = 0; i < blockedTiles.length; i++) {
			ShortPoint2D pos = blockedTiles[(i + offset) % blockedTiles.length].calculatePoint(buildingPos);
			if (!super.getGrid().isMarked(pos) && needsToBeWorkedOn(pos)) {
				return pos;
			}
		}
		return null;
	}

	private boolean needsToBeWorkedOn(ShortPoint2D pos) {
		return needsToChangeHeight(pos) || isNotFlattened(pos);
	}

	private boolean isNotFlattened(ShortPoint2D pos) {
		return super.getGrid().getLandscapeTypeAt(pos.x, pos.y) != ELandscapeType.FLATTENED;
	}

	private boolean needsToChangeHeight(ShortPoint2D pos) {
		return super.getGrid().getHeightAt(pos) != requester.getAverageHeight();
	}

	private void reportJobless() {
		this.state = EDiggerState.JOBLESS;
		this.requester = null;
		super.getGrid().addJobless(this);
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (requester == null || requester.isDiggerRequestActive()) {
			return true;
		} else {
			if (state != EDiggerState.JOBLESS) {
				reportJobless();
			}

			if (pathTarget != null) {
				super.getGrid().setMarked(pathTarget, false);
			}
			return false;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (pathTarget != null) {
			super.getGrid().setMarked(pathTarget, false);
		}

		switch (state) {
		case JOBLESS:
			super.getGrid().removeJobless(this);
			break;
		case PLAYING_ACTION:
			super.getGrid().setMarked(movable.getPosition(), false);
			break;
		default:
			break;
		}

		if (requester != null) {
			abortJob();
		}

		state = EDiggerState.DEAD_OBJECT;
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		if (requester != null) {
			super.getGrid().setMarked(pathTarget, false);
			abortJob();
			reportJobless();
		}
	}

	private void abortJob() {
		requester.diggerRequestFailed();
	}

	private enum EDiggerState {
		JOBLESS,
		INIT_JOB,
		GOING_TO_POS,
		PLAYING_ACTION,

		DEAD_OBJECT
	}
}
