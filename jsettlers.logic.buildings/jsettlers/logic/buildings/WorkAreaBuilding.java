package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleBorder;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
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

	/**
	 * @param draw
	 *            true if the circle should be drawn<br>
	 *            false if it should be removed.
	 * @param center
	 * @param radius
	 */
	private void drawWorkAreaCircle(boolean draw) {
		ShortPoint2D center = getWorkAreaCenter();
		if (center != null) {
			short radius = getBuildingType().getWorkradius();
			IBuildingsGrid grid = super.getGrid();

			for (ShortPoint2D pos : getCircle(grid, center, radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 1.0f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .75f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.66f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .5f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.33f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .25f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0f);
			}
		}
	}

	private void addOrRemoveMarkObject(boolean draw, IBuildingsGrid grid, ShortPoint2D pos, float progress) {
		if (draw) {
			grid.getMapObjectsManager().addBuildingWorkAreaObject(pos, progress);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos.x, pos.y, EMapObjectType.WORKAREA_MARK);
		}
	}

	private MapShapeFilter getCircle(IBuildingsGrid grid, ShortPoint2D center, float radius) {
		MapCircle baseCircle = new MapCircle(center, radius);
		MapCircleBorder border = new MapCircleBorder(baseCircle);
		return new MapShapeFilter(border, grid.getWidth(), grid.getHeight());
	}
}
