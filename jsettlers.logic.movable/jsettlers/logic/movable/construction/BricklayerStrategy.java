package jsettlers.logic.movable.construction;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BricklayerStrategy extends PathableStrategy implements IManageableBricklayer {

	private IConstructableBuilding constructionSite;
	private ShortPoint2D bricklayerTargetPos;
	private EDirection lookDirection;
	private boolean wentThere;

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
			if (wentThere && constructionSite != null) {
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
		if (constructionSite != null) {
			if (!super.getPos().equals(bricklayerTargetPos)) {
				System.out.println("bricklayer error");
			}

			wentThere = true;
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
	protected void stopOrStartWorking(boolean stop) {
		// TODO implement stopping of work
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	@Override
	public void setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		this.constructionSite = constructionSite;
		this.bricklayerTargetPos = bricklayerTargetPos;
		this.lookDirection = direction;
		this.wentThere = false;
	}

}
