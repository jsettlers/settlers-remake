/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.input;

import static java8.util.stream.StreamSupport.stream;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableInt;
import jsettlers.input.tasks.ChangeTowerSoldiersGuiTask;
import jsettlers.input.tasks.ChangeTradingRequestGuiTask;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MovableGuiTask;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.SetAcceptedStockMaterialGuiTask;
import jsettlers.input.tasks.SetBuildingPriorityGuiTask;
import jsettlers.input.tasks.SetMaterialDistributionSettingsGuiTask;
import jsettlers.input.tasks.SetMaterialPrioritiesGuiTask;
import jsettlers.input.tasks.SetMaterialProductionGuiTask;
import jsettlers.input.tasks.SetTradingWaypointGuiTask;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.input.tasks.UpgradeSoldiersGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.buildings.others.StockBuilding;
import jsettlers.logic.buildings.trading.TradingBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.synchronic.timer.ITaskExecutor;

import java8.util.Objects;
import java8.util.Optional;
import java8.util.stream.Collectors;

/**
 *
 * @author Andreas Eberle
 *
 */
public class GuiTaskExecutor implements ITaskExecutor {
	private static GuiTaskExecutor instance = null;
	private final IGuiInputGrid grid;
	private final ITaskExecutorGuiInterface guiInterface;
	private final byte playerId;

	public GuiTaskExecutor(IGuiInputGrid grid, ITaskExecutorGuiInterface guiInterface, byte playerId) {
		this.grid = grid;
		this.guiInterface = guiInterface;
		this.playerId = playerId;
	}

	public static GuiTaskExecutor get() {
		return instance;
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
			break;
		}

		case BUILD: {
			ConstructBuildingTask task = (ConstructBuildingTask) guiTask;
			grid.constructBuildingAt(task.getPosition(), task.getType(), task.getPlayerId());
			break;
		}

		case MOVE_TO: {
			MoveToGuiTask task = (MoveToGuiTask) guiTask;
			moveSelectedTo(task.getPosition(), task.getSelection());
			break;
		}

		case QUICK_SAVE:
			save();
			break;

		case DESTROY_BUILDING: {
			ShortPoint2D buildingPos = ((DestroyBuildingGuiTask) guiTask).getPosition();
			Building building = ((Building) grid.getBuildingAt(buildingPos.x, buildingPos.y));
			if (building != null) {
				building.kill();
			}
			break;
		}

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
			grid.setMaterialDistributionSettings(task.getManagerPosition(), task.getMaterialType(), task.getBuildingType(), task.getRatio());
			break;
		}

		case SET_MATERIAL_PRIORITIES: {
			SetMaterialPrioritiesGuiTask task = (SetMaterialPrioritiesGuiTask) guiTask;
			grid.setMaterialPrioritiesSettings(task.getManagerPosition(), task.getMaterialTypeForPriority());
		}
			break;

		case UPGRADE_SOLDIERS: {
			UpgradeSoldiersGuiTask task = (UpgradeSoldiersGuiTask) guiTask;
			grid.getPlayer(task.getPlayerId()).getMannaInformation().upgrade(task.getSoldierType());
			break;
		}

		case CHANGE_TRADING: {
			ChangeTradingRequestGuiTask task = (ChangeTradingRequestGuiTask) guiTask;
			ShortPoint2D buildingPos = task.getBuildingPos();
			IBuilding building = grid.getBuildingAt(buildingPos.x, buildingPos.y);
			if (building instanceof TradingBuilding) {
				((TradingBuilding) building).changeRequestedMaterial(task.getMaterial(), task.getAmount(), task.isRelative());
			}
			break;
		}

		case SET_TRADING_WAYPOINT: {
			SetTradingWaypointGuiTask task = (SetTradingWaypointGuiTask) guiTask;
			ShortPoint2D buildingPos = task.getBuildingPos();
			IBuilding building = grid.getBuildingAt(buildingPos.x, buildingPos.y);
			if (building instanceof TradingBuilding) {
				((TradingBuilding) building).setWaypoint(task.getWaypointType(), task.getPosition());
			}
			break;
		}

		case SET_MATERIAL_PRODUCTION: {
			SetMaterialProductionGuiTask task = (SetMaterialProductionGuiTask) guiTask;
			MaterialProductionSettings materialProduction = grid.getMaterialProductionAt(task.getPosition());

			switch (task.getProductionType()) {
			case INCREASE:
				materialProduction.increaseAbsoluteProductionRequest(task.getMaterialType());
				break;
			case DECREASE:
				materialProduction.decreaseAbsoluteProductionRequest(task.getMaterialType());
				break;
			case SET_PRODUCTION:
				materialProduction.setAbsoluteProductionRequest(task.getMaterialType(), (int) task.getRatio());
				break;
			case SET_RATIO:
				materialProduction.setUserConfiguredRelativeRequestValue(task.getMaterialType(), task.getRatio());
				break;
			}
			break;
		}

		case CHANGE_TOWER_SOLDIERS:
			changeTowerSoldiers((ChangeTowerSoldiersGuiTask) guiTask);
			break;

		case SET_ACCEPTED_STOCK_MATERIAL:
			setAcceptedStockMaterial((SetAcceptedStockMaterialGuiTask) guiTask);
			break;

		default:
			break;

		}
	}

	private void setAcceptedStockMaterial(SetAcceptedStockMaterialGuiTask guiTask) {
		ShortPoint2D taskPosition = guiTask.getPosition();

		if (guiTask.isLocal()) {
			IBuilding building = grid.getBuildingAt(taskPosition.x, taskPosition.y);
			if (building != null && building instanceof StockBuilding) {
				StockBuilding stock = (StockBuilding) building;
				stock.setAcceptedMaterial(guiTask.getMaterialType(), guiTask.isAccepted());
			}

		} else {
			grid.setAcceptedStockMaterial(taskPosition, guiTask.getMaterialType(), guiTask.isAccepted());
		}
	}

	private void changeTowerSoldiers(ChangeTowerSoldiersGuiTask soldierTask) {
		ShortPoint2D buildingPosition = soldierTask.getBuildingPos();
		OccupyingBuilding occupyingBuilding = (OccupyingBuilding) grid.getBuildingAt(buildingPosition.x, buildingPosition.y);

		switch (soldierTask.getTaskType()) {
		case FULL:
			occupyingBuilding.requestSoldiers();
			break;
		case MORE:
			occupyingBuilding.requestSoldier(soldierTask.getSoldierType());
			break;
		case ONE:
			occupyingBuilding.releaseSoldiers();
			break;
		case LESS:
			occupyingBuilding.releaseSoldier(soldierTask.getSoldierType());
			break;
		}
	}

	private void save() {
		try {
			grid.save(playerId, guiInterface.getUIState());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setBuildingPriority(SetBuildingPriorityGuiTask guiTask) {
		ShortPoint2D pos = guiTask.getBuildingPosition();
		Building building = ((Building) grid.getBuildingAt(pos.x, pos.y));
		if (building != null) {
			building.setPriority(guiTask.getNewPriority());
		}
	}

	private void convertMovables(ConvertGuiTask guiTask) {
		for (Integer currID : guiTask.getSelection()) {
			ILogicMovable movable = Movable.getMovableByID(currID);
			if (movable != null) {
				movable.convertTo(guiTask.getTargetType());
			}
		}
		guiInterface.refreshSelection();
	}

	private void stopOrStartWorking(List<Integer> selectedMovables, boolean stop) {
		for (Integer currID : selectedMovables) {
			ILogicMovable movable = Movable.getMovableByID(currID);
			if (movable != null) {
				movable.stopOrStartWorking(stop);
			}
		}
	}

	private void killSelectedMovables(List<Integer> selectedMovables) {
		for (Integer currID : selectedMovables) {
			ILogicMovable curr = Movable.getMovableByID(currID);
			if (curr != null) {
				curr.kill();
			}
		}
	}

	/**
	 * Move the selected {@link Movable} to the given position.
	 *
	 * @param targetPosition
	 *            position to move to
	 * @param movableIds
	 *            A list of the id's of the movables.
	 */
	private void moveSelectedTo(ShortPoint2D targetPosition, List<Integer> movableIds) {
		if (movableIds.size() == 1) {
			ILogicMovable currMovable = Movable.getMovableByID(movableIds.get(0));
			if (currMovable != null) {
				currMovable.moveTo(targetPosition);
			}
		} else if (!movableIds.isEmpty()) {
			sendMovablesNew(targetPosition, movableIds);
		}
	}

	private void sendMovablesNew(ShortPoint2D targetPosition, List<Integer> movableIds) {
		List<ILogicMovable> movables = stream(movableIds).map(Movable::getMovableByID).filter(Objects::nonNull).collect(Collectors.toList());
		if (movables.isEmpty()) {
			return;
		}

		for (int radius = 1, ringsWithoutSuccessCtr = 0; ringsWithoutSuccessCtr <= 5 && !movables.isEmpty(); radius++) {
			MutableInt numberOfSendMovables = new MutableInt(0);

			HexGridArea
					.streamBorder(targetPosition.x, targetPosition.y, radius)
					.filterBounds(grid.getWidth(), grid.getHeight())
					.getEvery(2)
					.forEach((x, y) -> {
						Optional<ILogicMovable> movableOptional = removeMovableThatCanMoveTo(movables, x, y);

						movableOptional.ifPresent(movable -> {
							movable.moveTo(new ShortPoint2D(x, y));
							numberOfSendMovables.value++;
						});
					});

			if (numberOfSendMovables.value > 0) {
				ringsWithoutSuccessCtr = 0;
			} else {
				ringsWithoutSuccessCtr++;
			}
		}
	}

	private Optional<ILogicMovable> removeMovableThatCanMoveTo(List<ILogicMovable> movables, int x, int y) {
		for (Iterator<ILogicMovable> iterator = movables.iterator(); iterator.hasNext();) {
			ILogicMovable movable = iterator.next();
			if (canMoveTo(movable, x, y)) {
				iterator.remove();
				return Optional.of(movable);
			}
		}
		return Optional.empty();
	}

	private boolean canMoveTo(ILogicMovable movable, int x, int y) {
		return !grid.isBlocked(x, y) && grid.getBlockedPartition(movable.getPos().x, movable.getPos().y) == grid.getBlockedPartition(x, y);
	}

	private void setWorkArea(ShortPoint2D pos, short buildingX, short buildingY) {
		Building building = (Building) grid.getBuildingAt(buildingX, buildingY);

		if (building != null) {
			building.setWorkAreaCenter(pos);
		}
	}
}
