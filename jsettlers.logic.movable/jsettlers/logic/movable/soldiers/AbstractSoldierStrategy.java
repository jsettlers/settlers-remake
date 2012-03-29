package jsettlers.logic.movable.soldiers;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.movable.GotoJob;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import jsettlers.logic.movable.soldiers.behaviors.ISoldierBehaviorable;
import jsettlers.logic.movable.soldiers.behaviors.SoldierBehavior;

public abstract class AbstractSoldierStrategy extends PathableStrategy implements IBuildingOccupyableMovable, ISoldierBehaviorable {
	private static final long serialVersionUID = 9000857936712315432L;

	private final EMovableType type;

	private SoldierBehavior behavior;

	private Movable leader;

	protected AbstractSoldierStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable);
		this.type = type;
		this.behavior = SoldierBehavior.getDefaultSoldierBehavior(this);
		super.setAction(EAction.NO_ACTION, -1);
	}

	@Override
	protected final void gotHitEvent() {
		super.abortPath();
	}

	protected final boolean isInTower() {
		return SoldierBehavior.isInTower(behavior);
	}

	@Override
	protected final boolean noActionEvent() {
		if (!super.noActionEvent()) {
			calculateAction();
		}
		return true;
	}

	private final void calculateAction() {
		behavior = behavior.calculate(super.getPos(), this);
	}

	@Override
	protected final boolean actionFinished() {
		if (!super.actionFinished()) {
			calculateAction();
		}

		return true;
	}

	@Override
	protected final void pathFinished() {
		calculateAction();
	}

	@Override
	public final boolean needsPlayersGround() {
		return false;
	}

	@Override
	public final EMovableType getMovableType() {
		return type;
	}

	@Override
	protected final boolean isGotoJobable() {
		return SoldierBehavior.isGotoJobable(behavior);
	}

	@Override
	protected final void pathRequestFailed() {
		behavior.pathRequestFailed();
	}

	@Override
	protected final void pathAbortedEvent() {
		behavior = SoldierBehavior.getDefaultSoldierBehavior(this);
	}

	@Override
	protected final boolean isPathStopable() {
		return SoldierBehavior.isPathStopable(behavior);
	}

	@Override
	public final void setOccupyableBuilding(IOccupyableBuilding building) {
		behavior = SoldierBehavior.getGoToTowerBehavior(this, building);
	}

	@Override
	public final void leaveOccupyableBuilding(ShortPoint2D pos) {
		this.setPos(pos);
		super.setVisible(true);
		super.setDontMove(false);

		behavior = SoldierBehavior.getDefaultSoldierBehavior(this);
	}

	@Override
	public final void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	@Override
	protected final void killedEvent() {
		behavior.killedEvent(this.type);
	}

	@Override
	public final boolean canOccupyBuilding() {
		return !isInTower();
	}

	@Override
	protected final boolean checkGoStepPrecondition() {
		return behavior.checkGoStepPrecondition();
	}

	@Override
	public final void goToTile(ShortPoint2D newPos) {
		super.goToTile(newPos);
	}

	@Override
	public final void setGotoJob(Movable leader, GotoJob job) {
		this.leader = leader;
		super.setGotoJob(leader, job);
	}

	@Override
	protected boolean executingGotoJobAction() {
		if (super.getMovable() == leader) {
			this.behavior = SoldierBehavior.getDefaultSoldierBehavior(this);
			leader = null;
			return true;
		} else {
			this.behavior = SoldierBehavior.getFlockingBehavior(this, leader);
			leader = null;
			return false;
		}
	}

	@Override
	public final IMovableGrid getGrid() {
		return super.getGrid();
	}

	@Override
	public final void setAction(EAction action, float duration) {
		super.setAction(action, duration);
	}

	@Override
	public final void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	@Override
	public final IBuildingOccupyableMovable getBuildingOccupier() {
		return this;
	}

	@Override
	public final void calculatePathTo(ShortPoint2D target) {
		super.calculatePathTo(target);
	}
}
