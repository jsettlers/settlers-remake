package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.player.Player;

/**
 * A {@link Building} that has a work area that can be changed and drawn to the screen.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class WorkAreaBuilding extends Building {
	private static final long serialVersionUID = -5176169656248971550L;

	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_REQUESTERS = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };
	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_NON_REQUESTERS = new EPriority[] { EPriority.LOW, EPriority.STOPPED };

	private ShortPoint2D workAreaCenter;

	protected WorkAreaBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
		this.workAreaCenter = getBuildingType().getWorkcenter().calculatePoint(pos);
	}

	public final ShortPoint2D getWorkAreaCenter() {
		return workAreaCenter;
	}

	@Override
	public final void setWorkAreaCenter(ShortPoint2D newWorkAreaCenter) {
		int distance = super.getPos().getOnGridDistTo(newWorkAreaCenter);

		if (distance < Constants.BUILDINGS_MAX_WORKRADIUS_FACTOR * super.getBuildingType().getWorkradius()) {
			drawWorkAreaCircle(false);

			this.workAreaCenter = newWorkAreaCenter;

			if (isSelected()) {
				drawWorkAreaCircle(true);
			}
		}
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		drawWorkAreaCircle(selected);
	}

	@Override
	public void kill() {
		drawWorkAreaCircle(false);

		super.kill();
	}

	private void drawWorkAreaCircle(boolean draw) {
		super.getGrid().drawWorkAreaCircle(super.getPos(), workAreaCenter, super.getBuildingType().getWorkradius(), draw);
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		EPriority[] priorities = super.getSupportedPriorities();

		if (priorities.length == 0) {
			if (super.getStacks().isEmpty()) { // has no request stacks
				priorities = SUPPORTED_PRIORITIES_FOR_NON_REQUESTERS;
			} else {
				priorities = SUPPORTED_PRIORITIES_FOR_REQUESTERS;
			}
		}

		return priorities;
	}
}
