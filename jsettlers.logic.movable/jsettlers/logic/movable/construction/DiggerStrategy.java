package jsettlers.logic.movable.construction;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class DiggerStrategy extends PathableStrategy implements IManageableDigger {

	private boolean wentThere = false;
	private FreeMapArea buildingArea;
	private byte targetHeight;

	public DiggerStrategy(IMovableGrid grid, Movable movable) {
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
			if (buildingArea != null) {
				ISPosition2D pos = getDiggablePosition();
				if (pos != null) {
					super.calculatePathTo(pos);
				} else {
					buildingArea = null;
					super.getGrid().addJobless(this);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void pathRequestFailed() {
		if (buildingArea != null) {
			// TODO rerequest the worker request
			buildingArea = null;
			super.getGrid().addJobless(this);
		}
		super.setAction(EAction.NO_ACTION, -1);
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (buildingArea != null) {
				executeDigg();
				tryToDigg();
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	private void executeDigg() {
		super.getGrid().changeHeightAt(super.getPos(), (byte) (Math.signum(targetHeight - super.getGrid().getHeightAt(super.getPos()))));
		super.getGrid().changeLandscapeAt(super.getPos(), ELandscapeType.FLATTENED);
		super.getGrid().setMarked(super.getPos(), false);
	}

	private ISPosition2D getDiggablePosition() {
		for (ISPosition2D pos : buildingArea) {
			if (needsToChangeHeight(pos) && !super.getGrid().isMarked(pos)) {
				return pos;
			}
		}
		return null;
	}

	private boolean needsToChangeHeight(ISPosition2D pos) {
		if (super.getGrid().getHeightAt(pos) != targetHeight) {
			return true;
		}
		return false;
	}

	@Override
	protected void pathFinished() {
		if (buildingArea != null) {
			wentThere = true;
			tryToDigg();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	private void tryToDigg() {
		if (needsToChangeHeight(super.getPos()) && wentThere) {
			super.setAction(EAction.ACTION1, 1);
		} else if (buildingArea != null) {
			ISPosition2D diggablePos = getDiggablePosition();
			if (diggablePos != null) {
				this.wentThere = false;
				super.getGrid().setMarked(diggablePos, true);
				super.calculatePathTo(diggablePos);
			} else {
				super.setAction(EAction.NO_ACTION, -1);
				this.buildingArea = null;
				super.getGrid().addJobless(this);
			}
		}
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.DIGGER;
	}

	@Override
	public void setDiggerJob(FreeMapArea buildingArea, byte targetHeight) {
		this.buildingArea = buildingArea;
		this.targetHeight = targetHeight;
		this.wentThere = false;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) { // diggers don't stop working
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

}
