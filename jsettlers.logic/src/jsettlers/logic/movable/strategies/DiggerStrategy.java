package jsettlers.logic.movable.strategies;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import jsettlers.network.synchronic.random.RandomSingleton;

public final class DiggerStrategy extends MovableStrategy implements IManageableDigger {
	private static final long serialVersionUID = 1581926355853324624L;

	private IDiggerRequester requester;
	private EDiggerState state = EDiggerState.JOBLESS;

	public DiggerStrategy(Movable movable) {
		super(movable);

		reportAsJobless();
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
				reportAsJobless();
				break;
			}
		case GOING_TO_POS:
			if (needsToBeWorkedOn(super.getPos())) {
				super.playAction(EAction.ACTION1, 1f);
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
		ShortPoint2D pos = super.getPos();
		super.getStrategyGrid().changeHeightTowards(pos.x, pos.y, requester.getAverageHeight());
	}

	private void goToDiggablePosition() {
		super.getStrategyGrid().setMarked(super.getPos(), false);
		ShortPoint2D diggablePos = getDiggablePosition();
		if (diggablePos != null) {
			if (super.goToPos(diggablePos)) {
				state = EDiggerState.GOING_TO_POS;
				super.getStrategyGrid().setMarked(diggablePos, true);
			} else {
				reportAsJobless();
			}
		} else {
			reportAsJobless();
		}
	}

	private ShortPoint2D getDiggablePosition() {
		RelativePoint[] blockedTiles = requester.getBuildingType().getProtectedTiles();
		ShortPoint2D buildingPos = requester.getPos();
		int offset = RandomSingleton.getInt(0, blockedTiles.length - 1);

		for (int i = 0; i < blockedTiles.length; i++) {
			ShortPoint2D pos = blockedTiles[(i + offset) % blockedTiles.length].calculatePoint(buildingPos);
			if (!super.getStrategyGrid().isMarked(pos) && needsToBeWorkedOn(pos)) {
				return pos;
			}
		}
		return null;
	}

	private boolean needsToBeWorkedOn(ShortPoint2D pos) {
		return needsToChangeHeight(pos) || isNotFlattened(pos);
	}

	private boolean isNotFlattened(ShortPoint2D pos) {
		return super.getStrategyGrid().getLandscapeTypeAt(pos.x, pos.y) != ELandscapeType.FLATTENED;
	}

	private boolean needsToChangeHeight(ShortPoint2D pos) {
		return super.getStrategyGrid().getHeightAt(pos) != requester.getAverageHeight();
	}

	private void reportAsJobless() {
		this.state = EDiggerState.JOBLESS;
		this.requester = null;
		super.getStrategyGrid().addJobless(this);
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (requester == null || requester.isDiggerRequestActive()) {
			return true;
		} else {
			if (state != EDiggerState.JOBLESS) {
				reportAsJobless();
			}

			if (pathTarget != null) {
				super.getStrategyGrid().setMarked(pathTarget, false);
			}
			return false;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (pathTarget != null) {
			super.getStrategyGrid().setMarked(pathTarget, false);
		}
		if (state == EDiggerState.JOBLESS) {
			super.getStrategyGrid().removeJobless(this);
		}

		state = EDiggerState.DEAD_OBJECT;
	}

	private static enum EDiggerState {
		JOBLESS,
		INIT_JOB,
		GOING_TO_POS,
		PLAYING_ACTION,

		DEAD_OBJECT
	}
}
