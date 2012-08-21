package jsettlers.logic.newmovable.strategies;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public class BricklayerStrategy extends NewMovableStrategy implements IManageableBricklayer {
	private static final long serialVersionUID = 7032795807942301297L;
	private static final float BRICKLAYER_ACTION_DURATION = 1f;

	private EBricklayerState state = EBricklayerState.JOBLESS;
	private IConstructableBuilding constructionSite;
	private ShortPoint2D bricklayerTargetPos;
	private EDirection lookDirection;

	public BricklayerStrategy(NewMovable movable) {
		super(movable);
		makeJobless();
	}

	@Override
	public void setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		this.constructionSite = constructionSite;
		this.bricklayerTargetPos = bricklayerTargetPos;
		this.lookDirection = direction;

		this.state = EBricklayerState.INIT_JOB;
	}

	private void makeJobless() {
		this.state = EBricklayerState.JOBLESS;
		this.bricklayerTargetPos = null;
		this.constructionSite = null;
		this.lookDirection = null;
		super.getStrategyGrid().addJobless(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			break;

		case INIT_JOB:
			if (super.goToPos(bricklayerTargetPos)) {
				this.state = EBricklayerState.GOING_TO_POS;
			} else {
				makeJobless();
			}
			break;

		case GOING_TO_POS:
			super.lookInDirection(lookDirection);
			state = EBricklayerState.BUILDING;
		case BUILDING:
			tryToBuild();
			break;
		}
	}

	private void tryToBuild() {
		if (constructionSite.tryToTakeMaterial()) {
			super.playAction(EAction.ACTION1, BRICKLAYER_ACTION_DURATION);
		} else {
			makeJobless();
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (constructionSite == null || !constructionSite.isConstructionFinished()) {
			return true;
		} else {
			makeJobless();
			return false;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (state == EBricklayerState.JOBLESS) {
			super.getStrategyGrid().removeJobless(this);
		}
	}

	private static enum EBricklayerState {
		JOBLESS,
		INIT_JOB,
		GOING_TO_POS,
		BUILDING,
	}

}
