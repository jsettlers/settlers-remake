package jsettlers.logic.movable.specialists;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class ThiefStrategy extends PathableStrategy {
	private static final long serialVersionUID = 4744167559826750335L;
	private static final byte THIEF_SEARCH_RADIUS = 20;

	private ShortPoint2D startPosition = null;
	private EThiefState state = EThiefState.NOTHING_TO_DO;

	public ThiefStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	public final boolean needsPlayersGround() {
		return false;
	}

	@Override
	protected final boolean isGotoJobable() {
		return true;
	}

	@Override
	protected final boolean isPathStopable() {
		return true;
	}

	@Override
	protected final EMovableType getMovableType() {
		return EMovableType.THIEF;
	}

	@Override
	protected final boolean actionFinished() {
		if (!super.actionFinished()) {
			switch (state) {
			case TAKING_MATERIAL: {
				EMaterialType materialType = super.getGrid().stealMaterialAt(super.getPos());
				markStolen(super.getPos(), false);

				if (materialType != null) {
					super.setMaterial(materialType);
					super.calculatePathTo(startPosition);
					state = EThiefState.WALKING_BACK;
				} else {
					resetState();
					super.setAction(EAction.NO_ACTION, -1);
				}
			}
				break;
			case DROPPING_MATERIAL: {
				EMaterialType materialType = super.getMaterial();
				if (materialType != null) {
					super.getGrid().pushMaterial(super.getPos(), materialType, true);
					super.setMaterial(EMaterialType.NO_MATERIAL);
				}
				resetState();
				super.setAction(EAction.NO_ACTION, -1);
			}
				break;

			default:
				super.setAction(EAction.NO_ACTION, -1);
				break;
			}
		}
		return true;
	}

	@Override
	protected final boolean noActionEvent() {
		if (!super.noActionEvent()) {
			// nothing to do
		}

		return true;
	}

	@Override
	protected final void pathFinished() {
		switch (state) {
		case GOING_TO:
			this.state = EThiefState.WALKING_TO_MATERIAL;
			super.calculateDijkstraPath(super.getPos(), THIEF_SEARCH_RADIUS, ESearchType.FOREIGN_MATERIAL);

			if (super.isFollowingPath()) {
				markStolen(super.getTargetPos(), true);
			}
			break;
		case WALKING_TO_MATERIAL:
			if (super.getGrid().canPop(super.getPos(), null)) {
				super.setAction(EAction.ACTION1, Constants.MOVABLE_TAKE_DROP_DURATION);
				this.state = EThiefState.TAKING_MATERIAL;
			} else {
				initWalkBack();
			}
			break;

		case WALKING_BACK:
			if (super.getMaterial() != EMaterialType.NO_MATERIAL) {
				if (super.getGrid().getPlayerAt(super.getPos()) == super.getPlayer()) {
					this.state = EThiefState.DROPPING_MATERIAL;
					super.setAction(EAction.ACTION1, 0);
				}
			} else {
				resetState();
				super.setAction(EAction.NO_ACTION, -1);
			}
			break;

		default:
			System.out.println("state should not happen here (ThiefStrategy.pathFinished()) " + state);
		}
	}

	private final void initWalkBack() {
		super.calculatePathTo(startPosition);
		this.state = EThiefState.WALKING_BACK;
	}

	@Override
	protected final void pathRequestFailed() {
		resetState();
	}

	private final void resetState() {
		startPosition = null;
		state = EThiefState.NOTHING_TO_DO;
	}

	@Override
	protected final void doPreGotoJobActions() {
		switch (state) {
		case NOTHING_TO_DO:
		case GOING_TO:
		case DROPPING_MATERIAL:
			this.startPosition = super.getPos();
			this.state = EThiefState.GOING_TO;
			break;
		case WALKING_TO_MATERIAL:
			markStolen(super.getTargetPos(), false);
			this.state = EThiefState.GOING_TO;
			break;
		case TAKING_MATERIAL:
			markStolen(super.getPos(), false);
			this.state = EThiefState.GOING_TO;
			break;

		case WALKING_BACK:
			break;
		}
	}

	private final void markStolen(ShortPoint2D pos, boolean mark) {
		super.getGrid().getMapObjectsManager().markStolen(pos.getX(), pos.getY(), mark);
	}

	@Override
	protected final void convertActionEvent() {
		switch (state) {
		case WALKING_TO_MATERIAL:
			markStolen(super.getTargetPos(), false);
			break;
		case TAKING_MATERIAL:
			markStolen(super.getPos(), false);
			break;
		}
	}

	private static enum EThiefState {
		GOING_TO,
		WALKING_TO_MATERIAL,
		TAKING_MATERIAL,
		NOTHING_TO_DO,
		WALKING_BACK,
		DROPPING_MATERIAL,

		;
	}

}
