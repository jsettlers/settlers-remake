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
package jsettlers.logic.movable.strategies;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

public class BricklayerStrategy extends MovableStrategy implements IManageableBricklayer {
	private static final long serialVersionUID = 7032795807942301297L;
	private static final float BRICKLAYER_ACTION_DURATION = 1f;

	private EBricklayerState state = EBricklayerState.JOBLESS;
	private IConstructableBuilding constructionSite;
	private ShortPoint2D bricklayerTargetPos;
	private EDirection lookDirection;

	public BricklayerStrategy(Movable movable) {
		super(movable);
		jobFinished();
	}

	@Override
	public boolean setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		if (state == EBricklayerState.JOBLESS) {
			this.constructionSite = constructionSite;
			this.bricklayerTargetPos = bricklayerTargetPos;
			this.lookDirection = direction;
			this.state = EBricklayerState.INIT_JOB;
			return true;
		} else {
			return false;
		}
	}

	private void jobFinished() {
		this.state = EBricklayerState.JOBLESS;
		this.bricklayerTargetPos = null;
		this.constructionSite = null;
		this.lookDirection = null;
		reportJobless();
	}

	private void reportJobless() {
		super.getGrid().addJobless(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			break;

		case INIT_JOB:
			if (constructionSite.isBricklayerRequestActive() && super.goToPos(bricklayerTargetPos)) {
				this.state = EBricklayerState.GOING_TO_POS;
			} else {
				jobFinished();
			}
			break;

		case GOING_TO_POS:
			super.lookInDirection(lookDirection);
			state = EBricklayerState.BUILDING;
		case BUILDING:
			tryToBuild();
			break;

		case DEAD_OBJECT:
			break;
		}
	}

	private void tryToBuild() {
		if (constructionSite.isBricklayerRequestActive() && constructionSite.tryToTakeMaterial()) {
			super.playAction(EMovableAction.ACTION1, BRICKLAYER_ACTION_DURATION);
		} else {
			jobFinished();
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (constructionSite == null || constructionSite.isBricklayerRequestActive()) {
			return true;
		} else {
			jobFinished();
			return false;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (state == EBricklayerState.JOBLESS) {
			super.getGrid().removeJobless(this);
		} else {
			abortJob();
		}

		state = EBricklayerState.DEAD_OBJECT;
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		if (constructionSite != null) {
			abortJob();
			jobFinished(); // this job is done for us
		}
	}

	private void abortJob() {
		constructionSite.bricklayerRequestFailed(bricklayerTargetPos, lookDirection);
	}

	private enum EBricklayerState {
		JOBLESS,
		INIT_JOB,
		GOING_TO_POS,
		BUILDING,

		DEAD_OBJECT
	}

}
