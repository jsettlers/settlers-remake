/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.input;

import java.util.LinkedList;
import java.util.List;

import jsettlers.algorithms.construction.ConstructionMarksThread;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IIDable;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectable;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.ChangeTradingRequestAction;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.action.SetMaterialDistributionSettingsAction;
import jsettlers.graphics.action.SetMaterialPrioritiesAction;
import jsettlers.graphics.action.SetMaterialProductionAction;
import jsettlers.graphics.action.SetMaterialStockAcceptedAction;
import jsettlers.graphics.action.SetTradingWaypointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.action.UpgradeSoldiersAction;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.UIState;
import jsettlers.input.tasks.ChangeTradingRequestGuiTask;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MovableGuiTask;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.SetBuildingPriorityGuiTask;
import jsettlers.input.tasks.SetMaterialDistributionSettingsGuiTask;
import jsettlers.input.tasks.SetMaterialPrioritiesGuiTask;
import jsettlers.input.tasks.SetMaterialProductionGuiTask;
import jsettlers.input.tasks.SetTradingWaypointGuiTask;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.input.tasks.UpgradeSoldiersGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.IDebugable;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * Class to handle the events provided by the user through jsettlers.graphics.
 *
 * @author Andreas Eberle
 */
public class GuiInterface implements IMapInterfaceListener, ITaskExecutorGuiInterface {

	private static final float SELECT_BY_TYPE_RADIUS = 30;

	private final IMapInterfaceConnector connector;

	private final IGameClock clock;
	private final ITaskScheduler taskScheduler;
	private final IGuiInputGrid grid;
	private final IGameStoppable gameStoppable;
	private final byte playerId;
	private final boolean multiplayer;
	private final ConstructionMarksThread constructionMarksCalculator;

	/**
	 * The current selection. This is updated by game logic.
	 */
	private SelectionSet currentSelection = new SelectionSet();

	public GuiInterface(IMapInterfaceConnector connector, IGameClock clock, ITaskScheduler taskScheduler, IGuiInputGrid grid,
			IGameStoppable gameStoppable, byte player,
			boolean multiplayer) {
		this.connector = connector;
		this.clock = clock;
		this.taskScheduler = taskScheduler;
		this.grid = grid;
		this.gameStoppable = gameStoppable;
		this.playerId = player;
		this.multiplayer = multiplayer;
		this.constructionMarksCalculator = new ConstructionMarksThread(grid.getConstructionMarksGrid(), clock, player);

		grid.getPlayer(player).setMessenger(connector);
		clock.setTaskExecutor(new GuiTaskExecutor(grid, this, playerId));
		connector.addListener(this);
	}

	@Override
	public void action(Action action) {
		if (action.getActionType() != EActionType.SCREEN_CHANGE) {
			System.out.println("action(Action): " + action.getActionType() + "   at game time: " + MatchConstants.clock().getTime());
		}

		switch (action.getActionType()) {
		case BUILD:
			this.setSelection(new SelectionSet());
			BuildAction buildAction = (BuildAction) action;
			EBuildingType buildingType = buildAction.getBuildingType();

			ShortPoint2D pos2 = grid.getConstructablePosition(buildAction.getPosition(), buildingType, playerId,
					InputSettings.USE_NEIGHBOR_POSITIONS_FOR_CONSTRUCTION);
			if (pos2 != null) {
				scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, pos2, buildingType));
			}

			System.out.println("build: " + buildingType);
			break;

		case SHOW_CONSTRUCTION_MARK: {
			buildingType = ((ShowConstructionMarksAction) action).getBuildingType();
			constructionMarksCalculator.setBuildingType(buildingType);
			break;
		}

		case DEBUG_ACTION:
			for (ISelectable curr : currentSelection) {
				if (curr instanceof IDebugable) {
					((IDebugable) curr).debug();
				}
			}
			break;

		case SPEED_TOGGLE_PAUSE:
			clock.invertPausing();
			break;

		case SPEED_SET_PAUSE:
			clock.setPausing(true);
			break;

		case SPEED_UNSET_PAUSE:
			clock.setPausing(false);
			break;

		case SPEED_SLOW:
			if (!multiplayer) {
				clock.setGameSpeed(0.5f);
			}
			break;
		case SPEED_FAST:
			if (!multiplayer) {
				clock.setGameSpeed(2.0f);
			}
			break;
		case SPEED_FASTER:
			if (!multiplayer) {
				clock.multiplyGameSpeed(1.2f);
			}
			break;
		case SPEED_SLOWER:
			if (!multiplayer) {
				clock.multiplyGameSpeed(1 / 1.2f);
			}
			break;
		case SPEED_NORMAL:
			if (!multiplayer) {
				clock.setGameSpeed(1.0f);
			}
			break;

		case FAST_FORWARD:
			if (!multiplayer) {
				clock.fastForward();
			}
			break;

		case SELECT_POINT:
			handleSelectPointAction((PointAction) action);
			break;

		case SELECT_AREA:
			selectArea((SelectAreaAction) action);
			break;

		case DESELECT:
			deselect();
			break;

		case SELECT_POINT_TYPE:
			selectPointType((PointAction) action);
			break;

		case MOVE_TO: {
			PointAction moveToAction = (PointAction) action;

			if (currentSelection.getSelectionType() == ESelectionType.BUILDING && currentSelection.getSize() == 1) {
				setBuildingWorkArea(moveToAction.getPosition());

			} else {
				moveTo(moveToAction.getPosition());
			}
			break;
		}

		case SET_WORK_AREA:
			setBuildingWorkArea(((PointAction) action).getPosition());
			break;

		case DESTROY:
			destroySelected();
			break;

		case STOP_WORKING:
			stopOrStartWorkingAction(true);
			break;
		case START_WORKING:
			stopOrStartWorkingAction(false);
			break;

		case SHOW_SELECTION:
			showSelection();
			break;

		case SCREEN_CHANGE:
			constructionMarksCalculator.setScreen(((ScreenChangeAction) action).getScreenArea());
			break;

		case TOGGLE_DEBUG:
			grid.resetDebugColors();
			break;

		case TOGGLE_FOG_OF_WAR:
			if (MatchConstants.ENABLE_FOG_OF_WAR_DISABLING) {
				grid.toggleFogOfWar();
			}
			break;

		case SAVE:
			taskScheduler.scheduleTask(new SimpleGuiTask(EGuiAction.QUICK_SAVE, playerId));
			break;

		case CONVERT:
			sendConvertAction((ConvertAction) action);
			break;

		case SET_BUILDING_PRIORITY:
			setBuildingPriority(((SetBuildingPriorityAction) action).getNewPriority());
			break;

		case SET_MATERIAL_DISTRIBUTION_SETTINGS: {
			SetMaterialDistributionSettingsAction a = (SetMaterialDistributionSettingsAction) action;
			taskScheduler.scheduleTask(new SetMaterialDistributionSettingsGuiTask(playerId, a.getManagerPosition(), a.getMaterialType(), a
					.getProbabilities()));
			break;
		}

		case SET_MATERIAL_PRIORITIES: {
			SetMaterialPrioritiesAction a = (SetMaterialPrioritiesAction) action;
			taskScheduler.scheduleTask(new SetMaterialPrioritiesGuiTask(playerId, a.getPosition(), a.getMaterialTypeForPriority()));
			break;
		}

		case SET_MATERIAL_STOCK_ACCEPTED: {
			SetMaterialStockAcceptedAction a = (SetMaterialStockAcceptedAction) action;
			// TODO @Andreas: implement this.
			System.err.println("Not implemented: " + a);
			break;
		}

		case SET_MATERIAL_PRODUCTION: {
			SetMaterialProductionAction a = (SetMaterialProductionAction) action;
			taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, a.getPosition(), a.getMaterialType(), a.getProductionType(), a
					.getRatio()));
			break;
		}

		case NEXT_OF_TYPE:
			selectNextOfType();
			break;

		case UPGRADE_SOLDIERS: {
			UpgradeSoldiersAction a = (UpgradeSoldiersAction) action;
			taskScheduler.scheduleTask(new UpgradeSoldiersGuiTask(playerId, a.getSoldierType()));
			break;
		}

		case CHANGE_TRADING_REQUEST: {
			ISelectable selected = currentSelection.getSingle();
			if (selected instanceof Building) {
				ChangeTradingRequestAction a = (ChangeTradingRequestAction) action;
				scheduleTask(new ChangeTradingRequestGuiTask(EGuiAction.CHANGE_TRADING, playerId, ((Building) selected).getPos(), a.getMaterial(),
						a.getAmount(), a.isRelative()));
			}
			break;
		}

		case SET_TRADING_WAYPOINT: {
			ISelectable selected = currentSelection.getSingle();
			if (selected instanceof Building) {
				SetTradingWaypointAction a = (SetTradingWaypointAction) action;
				scheduleTask(new SetTradingWaypointGuiTask(EGuiAction.SET_TRADING_WAYPOINT, playerId, ((Building) selected).getPos(),
						a.getWaypoint(), a.getPosition()));
			}
		}

		case ABORT:
			break;

		case EXIT:
			gameStoppable.stopGame();
			break;

		default:
			System.out.println("WARNING: GuiInterface.action() called, but event can't be handled... (" + action.getActionType() + ")");
		}
	}

	private void selectNextOfType() {
		if (currentSelection.getSize() != 1) {
			return;
		}

		if (currentSelection.getSelectionType() == ESelectionType.BUILDING) {
			Building building = (Building) currentSelection.get(0);
			EBuildingType buildingType = building.getBuildingType();
			Building first = null;
			Building next = null;
			boolean buildingFound = false;

			for (Building currBuilding : Building.getAllBuildings()) {
				if (currBuilding == building) {
					buildingFound = true;
				} else {
					if (currBuilding.getBuildingType() == buildingType && currBuilding.getPlayerId() == playerId) {
						if (first == null) {
							first = currBuilding;
						}
						if (buildingFound) {
							next = currBuilding;
							break;
						}
					}
				}
			}

			if (next != null) {
				setSelection(new SelectionSet(next));
			} else if (first != null) {
				setSelection(new SelectionSet(first));
			}
		}
	}

	private void setBuildingWorkArea(ShortPoint2D workAreaPosition) {
		ISelectable selected = currentSelection.getSingle();
		if (selected instanceof Building) {
			scheduleTask(new WorkAreaGuiTask(EGuiAction.SET_WORK_AREA, playerId, workAreaPosition, ((Building) selected).getPos()));
		}
	}

	private void sendConvertAction(ConvertAction action) {
		List<ISelectable> convertables = new LinkedList<ISelectable>();
		switch (action.getTargetType()) {
		case BEARER:
			for (ISelectable curr : currentSelection) {
				if (curr instanceof IMovable) {
					EMovableType currType = ((IMovable) curr).getMovableType();
					if (currType == EMovableType.THIEF || currType == EMovableType.PIONEER || currType == EMovableType.GEOLOGIST) {
						convertables.add(curr);
						if (convertables.size() >= action.getAmount()) {
							break;
						}
					}
				}
			}
			break;
		case PIONEER:
		case GEOLOGIST:
		case THIEF:
			for (ISelectable curr : currentSelection) {
				if (curr instanceof IMovable) {
					EMovableType currType = ((IMovable) curr).getMovableType();
					if (currType == EMovableType.BEARER) {
						convertables.add(curr);
						if (convertables.size() >= action.getAmount()) {
							break;
						}
					}
				}
			}
			break;
		default:
			System.out.println("WARNING: can't handle convert to this movable type: " + action.getTargetType());
			return;
		}

		if (convertables.size() > 0) {
			taskScheduler.scheduleTask(new ConvertGuiTask(playerId, getIDsOfIterable(convertables), action.getTargetType()));
		}
	}

	private void destroySelected() {
		if (currentSelection == null || currentSelection.getSize() == 0) {
			return;
		} else if (currentSelection.getSize() == 1 && currentSelection.iterator().next() instanceof Building) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, ((Building) currentSelection.iterator().next()).getPos()));
		} else {
			taskScheduler.scheduleTask(new MovableGuiTask(EGuiAction.DESTROY_MOVABLES, playerId, getIDsOfSelected()));
		}
		setSelection(new SelectionSet());
	}

	private void setBuildingPriority(EPriority newPriority) {
		if (currentSelection != null && currentSelection.getSize() == 1 && currentSelection.iterator().next() instanceof Building) {
			taskScheduler
					.scheduleTask(new SetBuildingPriorityGuiTask(playerId, ((Building) currentSelection.iterator().next()).getPos(), newPriority));
		}
	}

	private void showSelection() {
		int x = 0;
		int y = 0;
		int count = 0;
		for (ISelectable member : currentSelection) {
			if (member instanceof ILocatable) {
				x += ((ILocatable) member).getPos().x;
				y += ((ILocatable) member).getPos().y;
				count++;
			}
		}
		System.out.println("locatable: " + count);
		if (count > 0) {
			ShortPoint2D point = new ShortPoint2D(x / count, y / count);
			connector.scrollTo(point, false);
		}
	}

	/**
	 * @param stop
	 *            if true the members of currentSelection will stop working<br>
	 *            if false, they will start working
	 */
	private void stopOrStartWorkingAction(boolean stop) {
		taskScheduler.scheduleTask(new MovableGuiTask(stop ? EGuiAction.STOP_WORKING : EGuiAction.START_WORKING, playerId, getIDsOfSelected()));
	}

	private void moveTo(ShortPoint2D pos) {
		List<Integer> selectedIds = getIDsOfSelected();
		scheduleTask(new MoveToGuiTask(playerId, pos, selectedIds));
	}

	private final List<Integer> getIDsOfSelected() {
		return getIDsOfIterable(currentSelection);
	}

	private final static List<Integer> getIDsOfIterable(Iterable<? extends ISelectable> iterable) {
		List<Integer> selectedIds = new LinkedList<Integer>();

		for (ISelectable curr : iterable) {
			if (curr instanceof IIDable) {
				selectedIds.add(((IIDable) curr).getID());
			}
		}
		return selectedIds;
	}

	private void selectArea(SelectAreaAction action) {
		SelectionSet selectionSet = new SelectionSet();

		for (ShortPoint2D curr : new MapShapeFilter(action.getArea(), grid.getWidth(), grid.getHeight())) {
			IGuiMovable movable = grid.getMovable(curr.x, curr.y);
			if (movable != null && canSelectPlayer(movable.getPlayerId())) {
				selectionSet.add(movable);
			}
			IBuilding building = grid.getBuildingAt(curr.x, curr.y);
			if (building != null && canSelectPlayer(building.getPlayerId())) {
				selectionSet.add(building);
			}
		}

		setSelection(selectionSet);
	}

	private boolean canSelectPlayer(byte playerIdOfSelected) {
		return MatchConstants.ENABLE_ALL_PLAYER_SELECTION || playerIdOfSelected == playerId;
	}

	private void deselect() {
		setSelection(new SelectionSet());
	}

	private void handleSelectPointAction(PointAction action) {
		ShortPoint2D pos = action.getPosition();

		// only for debugging
		grid.postionClicked(pos.x, pos.y);

		// check what's to do
		ISelectable selected = getSelectableAt(pos);
		if (selected != null) {
			setSelection(new SelectionSet(selected));
		} else {
			setSelection(new SelectionSet());
		}

	}

	private void scheduleTask(SimpleGuiTask guiTask) {
		taskScheduler.scheduleTask(guiTask);
	}

	private ISelectable getSelectableAt(ShortPoint2D pos) {
		if (grid.isInBounds(pos)) {
			short x = pos.x;
			short y = pos.y;

			IGuiMovable selectableMovable = getSelectableMovable(x, y);
			if (selectableMovable != null) {
				return selectableMovable;
			} else {
				// search buildings
				IBuilding building = grid.getBuildingAt(pos.x, pos.y);
				if (building != null && canSelectPlayer(building.getPlayerId())) {
					return building;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	private IGuiMovable getSelectableMovable(short x, short y) {
		IGuiMovable m1 = grid.getMovable(x, y);
		IGuiMovable m3 = grid.getMovable((short) (x + 1), (short) (y + 1));
		IGuiMovable m2 = grid.getMovable((x), (short) (y + 1));
		IGuiMovable m4 = grid.getMovable((short) (x + 1), (short) (y + 2));

		if (m1 != null && canSelectPlayer(m1.getPlayerId())) {
			return m1;
		} else if (m2 != null && canSelectPlayer(m2.getPlayerId())) {
			return m2;
		} else if (m3 != null && canSelectPlayer(m3.getPlayerId())) {
			return m3;
		} else if (m4 != null && canSelectPlayer(m4.getPlayerId())) {
			return m4;
		} else {
			return null;
		}
	}

	private void selectPointType(PointAction action) {
		ShortPoint2D actionPosition = action.getPosition();
		IGuiMovable centerSelectable = getSelectableMovable(actionPosition.x, actionPosition.y);

		if (centerSelectable == null) { // nothing found at the location
			setSelection(new SelectionSet());
			return;
		}

		List<ISelectable> selected = new LinkedList<ISelectable>();
		selected.add(centerSelectable);

		for (ShortPoint2D pos : new MapCircle(actionPosition, SELECT_BY_TYPE_RADIUS)) {
			IGuiMovable movable = grid.getMovable(pos.x, pos.y);
			if (movable != null && movable.getMovableType() == centerSelectable.getMovableType() && canSelectPlayer(movable.getPlayerId())) {
				selected.add(movable);
			}
		}

		setSelection(new SelectionSet(selected));
	}

	/**
	 * Sets the selection.
	 *
	 * @param selection
	 *            The selected items. Not null!
	 */
	private void setSelection(SelectionSet selection) {
		currentSelection.clear();

		selection.setSelected(true);
		this.connector.setSelection(selection);
		this.currentSelection = selection;
	}

	@Override
	public void refreshSelection() {
		connector.setSelection(null);
		connector.setSelection(currentSelection);
	}

	@Override
	public UIState getUIState() {
		return connector.getUIState();
	}

	/**
	 * Shuts down used threads.
	 */
	public void stop() {
		constructionMarksCalculator.cancel();
		connector.removeListener(this);
	}

}
