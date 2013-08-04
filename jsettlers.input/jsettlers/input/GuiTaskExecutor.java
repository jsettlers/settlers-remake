package jsettlers.input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.GeneralGuiTask;
import jsettlers.input.tasks.MovableGuiTask;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.SetBuildingPriorityGuiTask;
import jsettlers.input.tasks.SetMaterialDistributionSettingsGuiTask;
import jsettlers.input.tasks.SetMaterialPrioritiesGuiTask;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.newmovable.NewMovable;
import networklib.client.task.packets.TaskPacket;
import networklib.synchronic.timer.ITaskExecutor;

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
	public void executeTask(TaskPacket iTask) {
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
				grid.save(guiInterface.getUIState());
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

		case SET_MATERIAL_DISTRIBUTION_SETTINGS: {
			SetMaterialDistributionSettingsGuiTask task = (SetMaterialDistributionSettingsGuiTask) guiTask;
			grid.setMaterialDistributionSettings(task.getManagerPosition(), task.getMaterialType(), task.getProbabilities());
		}
			break;

		case SET_MATERIAL_PRIORITIES: {
			SetMaterialPrioritiesGuiTask task = (SetMaterialPrioritiesGuiTask) guiTask;
			grid.setMaterialPrioritiesSetting(task.getManagerPosition(), task.getMaterialTypeForPriority());
		}
			break;

		default:
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
	 * Move the selected {@link NewMovable} to the given position.
	 * 
	 * @param targetPosition
	 *            position to move to
	 * @param movableIds
	 *            A list of the id's of the movables.
	 */
	private void moveSelectedTo(ShortPoint2D targetPosition, List<Integer> movableIds) {
		if (movableIds.size() == 1) {
			NewMovable currMovable = NewMovable.getMovableByID(movableIds.get(0));
			if (currMovable != null)
				currMovable.moveTo(targetPosition);
		} else if (!movableIds.isEmpty()) {
			// float radius = (float) (Math.sqrt(movableIds.size() / 3.14f)) * 2;
			// MapCircle mapCircle = new MapCircle(targetPosition, radius);
			//
			// Iterator<ShortPoint2D> circleIter = mapCircle.iterator();
			// for (Integer currID : movableIds) {
			// NewMovable currMovable = NewMovable.getMovableByID(currID);
			//
			// if (currMovable != null) {
			// circleIter.next();
			// currMovable.moveTo(circleIter.next());
			// }
			// }

			short radius = 1;
			short ringsWithoutSuccessCtr = 0; // used to stop the loop
			Iterator<ShortPoint2D> posIterator = new HexBorderArea(targetPosition, radius).iterator();

			for (Integer currMovableId : movableIds) {
				NewMovable currMovable = NewMovable.getMovableByID(currMovableId);

				ShortPoint2D currTargetPos;

				do {
					if (!posIterator.hasNext()) {
						ringsWithoutSuccessCtr++;
						if (ringsWithoutSuccessCtr > 5) {
							return; // the rest of the movables can't be sent to the target.
						}

						radius++;
						posIterator = new HexBorderArea(targetPosition, radius).iterator();
					}

					currTargetPos = posIterator.next();
				} while (!canMoveTo(currMovable, currTargetPos));

				ringsWithoutSuccessCtr = 0;
				currMovable.moveTo(currTargetPos);
			}
		}
	}

	private boolean canMoveTo(NewMovable movable, ShortPoint2D potentialTargetPos) {
		return grid.isInBounds(potentialTargetPos) && !grid.isBlocked(potentialTargetPos)
				&& grid.getBlockedPartition(movable.getPos()) == grid.getBlockedPartition(potentialTargetPos);
	}

	private void setWorkArea(ShortPoint2D pos, short buildingX, short buildingY) {
		Building building = (Building) grid.getBuildingAt(buildingX, buildingY);

		if (building != null) {
			building.setWorkAreaCenter(pos);
		}
	}

}
