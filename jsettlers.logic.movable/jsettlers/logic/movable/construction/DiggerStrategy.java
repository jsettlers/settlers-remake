package jsettlers.logic.movable.construction;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import random.RandomSingleton;

/**
 * Movable strategy for diggers.
 * 
 * @author Andreas Eberle
 * 
 */
public class DiggerStrategy extends PathableStrategy implements IManageableDigger {
	private static final long serialVersionUID = -4662839529813216429L;

	private boolean wentThere = false;

	private IDiggerRequester requester;

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
			if (requester != null) {
				ShortPoint2D pos = getDiggablePosition();
				if (pos != null) {
					super.calculatePathTo(pos);
				} else {
					requester = null;
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
		if (requester != null) {
			cancelRequest();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (requester != null) {
				executeDigg();
				tryToDigg();
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	private void executeDigg() {
		super.getGrid().changeHeightAt(super.getPos(), (byte) (Math.signum(requester.getAverageHeight() - super.getGrid().getHeightAt(super.getPos()))));
		super.getGrid().changeLandscapeAt(super.getPos(), ELandscapeType.FLATTENED);
		super.getGrid().setMarked(super.getPos(), false);
	}

	private ShortPoint2D getDiggablePosition() {
		RelativePoint[] blockedTiles = requester.getBuildingType().getBlockedTiles();
		ShortPoint2D buildingPos = requester.getPos();
		int offset = RandomSingleton.getInt(0, blockedTiles.length - 1);

		for (int i = 0; i < blockedTiles.length; i++) {
			ShortPoint2D pos = blockedTiles[(i + offset) % blockedTiles.length].calculatePoint(buildingPos);
			if (needsToChangeHeight(pos) && !super.getGrid().isMarked(pos)) {
				return pos;
			}
		}
		return null;
	}

	private boolean needsToChangeHeight(ShortPoint2D pos) {
		if (super.getGrid().getHeightAt(pos) != requester.getAverageHeight()) {
			return true;
		}
		return false;
	}

	@Override
	protected void pathFinished() {
		if (requester != null) {
			wentThere = true;
			tryToDigg();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	private void tryToDigg() {
		if (requester.isRequestActive()) {
			if (needsToChangeHeight(super.getPos()) && wentThere) {
				super.setAction(EAction.ACTION1, 1);
			} else if (requester != null) {
				ShortPoint2D diggablePos = getDiggablePosition();
				if (diggablePos != null) {
					this.wentThere = false;
					super.getGrid().setMarked(diggablePos, true);
					super.calculatePathTo(diggablePos);
				} else {
					cancelRequest();
				}
			}
		} else {
			cancelRequest();
		}
	}

	private void cancelRequest() {
		super.setAction(EAction.NO_ACTION, -1);
		this.requester = null;
		super.getGrid().addJobless(this);
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.DIGGER;
	}

	@Override
	public void setDiggerJob(IDiggerRequester requester) {
		this.requester = requester;
		this.wentThere = false;
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
	protected boolean checkGoStepPrecondition() {
		return requester == null || requester.isRequestActive();
	}

	@Override
	protected void pathAbortedEvent() {
		if (requester != null) {
			cancelRequest();
		} else {
			System.err.println("bricklayer abort path but that should not happen here!");
			super.setAction(EAction.NO_ACTION, -1);
		}

	}
}
