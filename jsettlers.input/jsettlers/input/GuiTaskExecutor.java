package jsettlers.input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.task.ConvertGuiTask;
import jsettlers.input.task.DestroyBuildingGuiTask;
import jsettlers.input.task.EGuiAction;
import jsettlers.input.task.GeneralGuiTask;
import jsettlers.input.task.MovableGuiTask;
import jsettlers.input.task.MoveToGuiTask;
import jsettlers.input.task.SetBuildingPriorityGuiTask;
import jsettlers.input.task.SimpleGuiTask;
import jsettlers.input.task.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.network.task.ITask;
import network.ITaskExecutor;

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
			setWorkArea(task.getPosition(), task.getBuildingPos().x, task.getBuildingPos().y);
		}
			break;

		case BUILD: {
			GeneralGuiTask task = (GeneralGuiTask) guiTask;
			grid.constructBuildingAt(task.getPosition(), task.getType());
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
			ShortPoint2D buildingPos = ((DestroyBuildingGuiTask) guiTask).getPosition();
			((Building) grid.getBuildingAt(buildingPos.x, buildingPos.y)).kill();
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

		case SET_BUILDING_PRIORITY:
			setBuildingPriority((SetBuildingPriorityGuiTask) guiTask);
			break;
		}
	}

	private void setBuildingPriority(SetBuildingPriorityGuiTask guiTask) {
		ShortPoint2D pos = guiTask.getBuildingPosition();
		((Building) grid.getBuildingAt(pos.x, pos.y)).setPriority(guiTask.getNewPriority());
	}

	private void convertMovables(ConvertGuiTask guiTask) {
		for (Integer currID : guiTask.getSelection()) {
			NewMovable.getMovableByID(currID).convertTo(guiTask.getTargetType());
		}
		guiInterface.refreshSelection();
	}

	private void stopOrStartWorking(List<Integer> selectedMovables, boolean stop) {
		for (Integer currID : selectedMovables) {
			NewMovable.getMovableByID(currID).stopOrStartWorking(stop);
		}
	}

	private void killSelectedMovables(List<Integer> selectedMovables) {
		for (Integer currID : selectedMovables) {
			NewMovable curr = NewMovable.getMovableByID(currID);
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
	private void moveSelectedTo(ShortPoint2D pos, List<Integer> list) {
		if (list.size() == 1) {
			NewMovable currMovable = NewMovable.getMovableByID(list.get(0));
			if (currMovable != null)
				currMovable.moveTo(pos);
		} else if (!list.isEmpty()) {
			float radius = (float) (Math.sqrt(list.size() / 3.14f)) * 2;
			MapCircle mapCircle = new MapCircle(pos, radius);
			NewMovable leader = null;

			Iterator<ShortPoint2D> circleIter = mapCircle.iterator();
			int ctr = 0;
			for (Integer currID : list) {
				NewMovable currMovable = NewMovable.getMovableByID(currID);
				if (leader == null || ctr % 30 == 0) {
					leader = currMovable;
				}

				if (currMovable != null) {
					circleIter.next();
					currMovable.moveTo(circleIter.next());
				}
				ctr++;
			}
		}
	}

	private void setWorkArea(ShortPoint2D pos, short buildingX, short buildingY) {
		Building building = (Building) grid.getBuildingAt(buildingX, buildingY);

		if (building != null) {
			building.setWorkAreaCenter(pos);
		}
	}

}
