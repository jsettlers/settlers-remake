package jsettlers.input.task;

import java.util.Iterator;
import java.util.List;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.input.IGuiInputGrid;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.movable.GotoJob;
import jsettlers.logic.movable.Movable;

public class TaskExecutor {
	private static TaskExecutor instance = null;
	private final IGuiInputGrid grid;

	private TaskExecutor(IGuiInputGrid grid) {
		this.grid = grid;
	}

	public static TaskExecutor get() {
		return instance;
	}

	public static void init(IGuiInputGrid grid) {
		if (instance == null)
			instance = new TaskExecutor(grid);
	}

	public void executeAction(SimpleGuiTask guiTask) {
		System.err.println("executeTask(GuiTask): " + guiTask.getGuiAction());
		switch (guiTask.getGuiAction()) {
		case SET_WORK_AREA: {
			WorkAreaGuiTask task = (WorkAreaGuiTask) guiTask;
			setWorkArea(task.getPosition(), task.getBuildingPos().getX(), task.getBuildingPos().getY());
		}
			break;

		case BUILD: {
			GeneralGuiTask task = (GeneralGuiTask) guiTask;
			Building building = Building.getBuilding(task.getType(), grid.getPlayerAt(task.getPosition()));
			building.constructAt(grid.getBuildingsGrid(), task.getPosition());
		}
			break;

		case MOVE_TO: {
			MoveToGuiTask task = (MoveToGuiTask) guiTask;
			moveSelectedTo(task.getPosition(), task.getSelection());
		}
			break;

		}
	}

	/**
	 * move the selected movables to the given position.
	 * 
	 * @param pos
	 *            position to move to
	 * @param list
	 */
	private void moveSelectedTo(ISPosition2D pos, List<Integer> list) {
		float radius = (float) (Math.sqrt(list.size() / 3.14f)) * 2;
		MapCircle mapCircle = new MapCircle(pos, radius);

		Iterator<ISPosition2D> circleIter = mapCircle.iterator();
		for (Integer currID : list) {
			Movable currMovable = Movable.getMovableByID(currID);
			if (currMovable != null) {
				GotoJob job = new GotoJob(circleIter.next());
				circleIter.next();
				currMovable.setGotoJob(job);
			}
		}
	}

	private void setWorkArea(ISPosition2D pos, short buildingX, short buildingY) {
		Building building = (Building) grid.getBuildingAt(buildingX, buildingY);

		if (building != null) {
			building.drawWorkAreaCircle(false);
			building.setWorkAreaCenter(pos);
			building.drawWorkAreaCircle(true);
		}
	}

}
