package jsettlers.input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.input.task.ConvertGuiTask;
import jsettlers.input.task.DestroyBuildingGuiTask;
import jsettlers.input.task.EGuiAction;
import jsettlers.input.task.GeneralGuiTask;
import jsettlers.input.task.MovableGuiTask;
import jsettlers.input.task.MoveToGuiTask;
import jsettlers.input.task.SimpleGuiTask;
import jsettlers.input.task.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.movable.GotoJob;
import jsettlers.logic.movable.Movable;
import network.ITaskExecutor;
import network.task.ITask;

public class GuiTaskExecutor implements ITaskExecutor {
	private static GuiTaskExecutor instance = null;
	private final IGuiInputGrid grid;
	private final ITaskExecutorGuiInterface guiInterface;

	public GuiTaskExecutor(IGuiInputGrid grid, ITaskExecutorGuiInterface guiInterface) {
		this.grid = grid;
		this.guiInterface = guiInterface;
	}

	public static GuiTaskExecutor get() {
		return instance;
	}

	public static void init(IGuiInputGrid grid, ITaskExecutorGuiInterface guiInterface) {
		if (instance == null)
			instance = new GuiTaskExecutor(grid, guiInterface);
	}

	@Override
	public void executeTask(ITask iTask) {
		if (!(iTask instanceof SimpleGuiTask)) {
			return;
		}

		SimpleGuiTask guiTask = (SimpleGuiTask) iTask;

		System.out.println("executeTask(GuiTask): " + guiTask.getGuiAction());
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

		case QUICK_SAVE:
			try {
				grid.save();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;

		case DESTROY_BUILDING: {
			ISPosition2D buildingPos = ((DestroyBuildingGuiTask) guiTask).getPosition();
			((Building) grid.getBuildingAt(buildingPos.getX(), buildingPos.getY())).kill();
		}
			break;

		case DESTROY_MOVABLES:
			killSelectedMovables(((MovableGuiTask) guiTask).getSelection());
			break;

		case START_WORKING:
		case STOP_WORKING:
			stopOrStartWorking(((MovableGuiTask) guiTask).getSelection(), guiTask.getGuiAction() == EGuiAction.STOP_WORKING);
			break;

		case CONVERT:
			convertMovables((ConvertGuiTask) guiTask);
			break;

		}
	}

	private void convertMovables(ConvertGuiTask guiTask) {
		for (Integer currID : guiTask.getSelection()) {
			Movable.getMovableByID(currID).convertTo(guiTask.getTargetType());
		}
		guiInterface.refreshSelection();
	}

	private void stopOrStartWorking(List<Integer> selectedMovables, boolean stop) {
		for (Integer currID : selectedMovables) {
			Movable.getMovableByID(currID).stopOrStartWorking(stop);
		}
	}

	private void killSelectedMovables(List<Integer> selectedMovables) {
		for (Integer currID : selectedMovables) {
			Movable curr = Movable.getMovableByID(currID);
			if (curr != null)
				curr.kill();
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
		if (list.size() == 1) {
			Movable currMovable = Movable.getMovableByID(list.get(0));
			if (currMovable != null)
				currMovable.setGotoJob(currMovable, new GotoJob(pos));
		} else if (!list.isEmpty()) {
			float radius = (float) (Math.sqrt(list.size() / 3.14f)) * 2;
			MapCircle mapCircle = new MapCircle(pos, radius);
			Movable leader = null;

			Iterator<ISPosition2D> circleIter = mapCircle.iterator();
			int ctr = 0;
			for (Integer currID : list) {
				Movable currMovable = Movable.getMovableByID(currID);
				if (leader == null || ctr % 30 == 0) {
					leader = currMovable;
				}

				if (currMovable != null) {
					GotoJob job = new GotoJob(circleIter.next());
					circleIter.next();
					currMovable.setGotoJob(leader, job);
				}
				ctr++;
			}
		}
	}

	private void setWorkArea(ISPosition2D pos, short buildingX, short buildingY) {
		Building building = (Building) grid.getBuildingAt(buildingX, buildingY);

		if (building != null) {
			building.drawWorkAreaCircle(false);
			building.setWorkAreaCenter(pos);

			if (building.isSelected()) {
				building.drawWorkAreaCircle(true);
			}
		}
	}

}
