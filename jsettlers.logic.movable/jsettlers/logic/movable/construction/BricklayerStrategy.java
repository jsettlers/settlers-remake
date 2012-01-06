package jsettlers.logic.movable.construction;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

/**
 * Movable strategy for bricklayers.
 * 
 * @author Andreas Eberle
 * 
 */
public class BricklayerStrategy extends PathableStrategy implements IManageableBricklayer {
	private static final long serialVersionUID = -5076159171851148099L;

	private IConstructableBuilding constructionSite;
	private ISPosition2D bricklayerTargetPos;
	private EDirection lookDirection;
	public boolean startedGoing;

	public BricklayerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
		grid.addJobless(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (constructionSite != null) {
				super.calculatePathTo(bricklayerTargetPos);
				startedGoing = true;
			}
		}
		return true;
	}

	@Override
	protected void pathRequestFailed() {
		if (constructionSite != null) {
			constructionSite = null;
			bricklayerTargetPos = null;
			lookDirection = null;
			super.getGrid().addJobless(this);
		}
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (startedGoing && constructionSite != null) {
				tryToBuild();
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	private void tryToBuild() {
		if (constructionSite.tryToTakeMaterial()) {
			super.setAction(EAction.ACTION1, 1);
		} else {
			constructionSite = null;
			bricklayerTargetPos = null;
			lookDirection = null;
			super.getGrid().addJobless(this);
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	protected void pathFinished() {
		if (constructionSite != null && startedGoing) {
			super.setDirection(lookDirection);
			tryToBuild();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BRICKLAYER;
	}

	@Override
	protected boolean isPathStopable() {
		return false;
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	@Override
	public void setBricklayerJob(IConstructableBuilding constructionSite, ISPosition2D bricklayerTargetPos, EDirection direction) {
		if (!constructionSite.isConstructionFinished()) {
			this.constructionSite = constructionSite;
			this.bricklayerTargetPos = bricklayerTargetPos;
			this.lookDirection = direction;
			this.startedGoing = false;
		} else {
			super.getGrid().addJobless(this);
		}
	}

	@Override
	protected boolean checkGoStepPrecondition() {
		return constructionSite == null || !constructionSite.isConstructionFinished();
	}

	@Override
	protected void pathAbortedEvent() {
		if (constructionSite.isConstructionFinished()) {
			constructionSite = null;
			bricklayerTargetPos = null;

			super.setAction(EAction.NO_ACTION, -1);
			super.getGrid().addJobless(this);
		} else {
			System.err.println("bricklayer abort path but that should not happen here!");
			super.setAction(EAction.NO_ACTION, -1);
		}

	}
}
