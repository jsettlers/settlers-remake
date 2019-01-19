/*******************************************************************************
 * Copyright (c) 2015 - 2018
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

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import java8.util.Optional;
import java8.util.function.BiFunction;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import jsettlers.algorithms.construction.ConstructionMarksThread;
import jsettlers.common.action.BuildAction;
import jsettlers.common.action.ChangeTradingRequestAction;
import jsettlers.common.action.ConvertAction;
import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.action.PointAction;
import jsettlers.common.action.ScreenChangeAction;
import jsettlers.common.action.SelectAreaAction;
import jsettlers.common.action.SetAcceptedStockMaterialAction;
import jsettlers.common.action.SetBuildingPriorityAction;
import jsettlers.common.action.SetDockAction;
import jsettlers.common.action.SetMaterialDistributionSettingsAction;
import jsettlers.common.action.SetMaterialPrioritiesAction;
import jsettlers.common.action.SetMaterialProductionAction;
import jsettlers.common.action.SetTradingWaypointAction;
import jsettlers.common.action.ShowConstructionMarksAction;
import jsettlers.common.action.SoldierAction;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.EPriority;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.menu.UIState;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.EShipType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.movable.IIDable;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectable;
import jsettlers.input.tasks.ChangeTowerSoldiersGuiTask;
import jsettlers.input.tasks.ChangeTowerSoldiersGuiTask.EChangeTowerSoldierTaskType;
import jsettlers.input.tasks.ChangeTradingRequestGuiTask;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MovableGuiTask;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.OrderShipGuiTask;
import jsettlers.input.tasks.SetAcceptedStockMaterialGuiTask;
import jsettlers.input.tasks.SetBuildingPriorityGuiTask;
import jsettlers.input.tasks.SetDockGuiTask;
import jsettlers.input.tasks.SetMaterialDistributionSettingsGuiTask;
import jsettlers.input.tasks.SetMaterialPrioritiesGuiTask;
import jsettlers.input.tasks.SetMaterialProductionGuiTask;
import jsettlers.input.tasks.SetTradingWaypointGuiTask;
import jsettlers.input.tasks.SimpleBuildingGuiTask;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.input.tasks.UpgradeSoldiersGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IDockBuilding;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.IDebugable;
import jsettlers.logic.player.Player;
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

	private final IGameClock              clock;
	private final ITaskScheduler          taskScheduler;
	private final IGuiInputGrid           grid;
	private final IGameStoppable          gameStoppable;
	private final byte                    playerId;
	private final boolean                 multiplayer;
	private final ConstructionMarksThread constructionMarksCalculator;
	private final Timer                   refreshSelectionTimer;

	/**
	 * The current selection. This is updated by game logic.
	 */
	private SelectionSet currentSelection = new SelectionSet();

	public GuiInterface(IMapInterfaceConnector connector, IGameClock clock, ITaskScheduler taskScheduler, IGuiInputGrid grid, IGameStoppable gameStoppable, byte playerId, boolean multiplayer) {
		this.connector = connector;
		this.clock = clock;
		this.taskScheduler = taskScheduler;
		this.grid = grid;
		this.gameStoppable = gameStoppable;
		this.playerId = playerId;
		this.multiplayer = multiplayer;
		this.constructionMarksCalculator = new ConstructionMarksThread(grid.getConstructionMarksGrid(), clock, playerId);

		this.refreshSelectionTimer = new Timer("refreshSelectionTimer");
		this.refreshSelectionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				refreshSelection();
			}
		}, 1000, 1000);

		Player player = grid.getPlayer(playerId);
		if (player != null) {
			player.setMessenger(connector);
		}
		clock.setTaskExecutor(new GuiTaskExecutor(grid, this, this.playerId));
		connector.addListener(this);
	}

	@Override
	public void action(IAction action) {
		if (action.getActionType() != EActionType.SCREEN_CHANGE) {
			System.out.println("action(Action): " + action.getActionType() + "   at game time: " + MatchConstants.clock().getTime());
		}

		switch (action.getActionType()) {
			case BUILD:
				handleBuildAction((BuildAction) action);
				break;

			case SHOW_CONSTRUCTION_MARK:
				constructionMarksCalculator.setBuildingType(((ShowConstructionMarksAction) action).getBuildingType());
				break;

			case DEBUG_ACTION:
				for (final ISelectable curr : currentSelection) {
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
					clock.setGameSpeed(5.0f);
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

			case FILTER_WOUNDED:
				filterWounded();
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
				final PointAction moveToAction = (PointAction) action;

				if (currentSelection.getSelectionType() == ESelectionType.BUILDING) {
					Building building = (Building) currentSelection.getSingle();
					if (building == null) {
						return;
					}

					if (building instanceof IDockBuilding) {
						setDock(moveToAction.getPosition());
					} else {
						setBuildingWorkArea(moveToAction.getPosition());
					}
				} else {
					moveTo(moveToAction.getPosition());
				}
				break;
			}

			case SHOW_MESSAGE: {
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
				final SetMaterialDistributionSettingsAction a = (SetMaterialDistributionSettingsAction) action;
				taskScheduler.scheduleTask(new SetMaterialDistributionSettingsGuiTask(playerId, a.getManagerPosition(), a.getMaterialType(), a.getBuildingType(), a.getRatio()));
				break;
			}

			case SET_MATERIAL_PRIORITIES: {
				final SetMaterialPrioritiesAction a = (SetMaterialPrioritiesAction) action;
				taskScheduler.scheduleTask(new SetMaterialPrioritiesGuiTask(playerId, a.getPosition(), a.getMaterialTypeForPriority()));
				break;
			}

			case SET_MATERIAL_STOCK_ACCEPTED: {
				final SetAcceptedStockMaterialAction a = (SetAcceptedStockMaterialAction) action;
				taskScheduler.scheduleTask(new SetAcceptedStockMaterialGuiTask(playerId, a.getPosition(), a.getMaterial(), a.shouldAccept(), a
					.isLocalSetting()));
				break;
			}

			case SET_MATERIAL_PRODUCTION: {
				final SetMaterialProductionAction a = (SetMaterialProductionAction) action;
				taskScheduler.scheduleTask(new SetMaterialProductionGuiTask(playerId, a.getPosition(), a.getMaterialType(), a.getProductionType(), a
					.getRatio()));
				break;
			}

			case NEXT_OF_TYPE:
				selectNextOfType();
				break;

			case UPGRADE_SOLDIERS: {
				final SoldierAction a = (SoldierAction) action;
				taskScheduler.scheduleTask(new UpgradeSoldiersGuiTask(playerId, a.getSoldierType()));
				break;
			}

			case CHANGE_TRADING_REQUEST: {
				final ISelectable selected = currentSelection.getSingle();
				if (selected instanceof Building) {
					final ChangeTradingRequestAction a = (ChangeTradingRequestAction) action;
					scheduleTask(new ChangeTradingRequestGuiTask(EGuiAction.CHANGE_TRADING, playerId, ((Building) selected).getPosition(), a.getMaterial(),
																 a.getAmount(), a.isRelative()
					));
				}
				break;
			}

			case SET_TRADING_WAYPOINT: {
				final ISelectable selected = currentSelection.getSingle();
				if (selected instanceof Building) {
					final SetTradingWaypointAction a = (SetTradingWaypointAction) action;
					scheduleTask(new SetTradingWaypointGuiTask(EGuiAction.SET_TRADING_WAYPOINT, playerId, ((Building) selected).getPosition(),
															   a.getWaypointType(), a.getPosition()
					));
				}
				break;
			}

			case SET_DOCK: {
				setDock(((SetDockAction) action).getPosition());
				break;
			}

			case SOLDIERS_ALL:
				requestSoldiers(EChangeTowerSoldierTaskType.FULL, null);
				break;
			case SOLDIERS_ONE:
				requestSoldiers(EChangeTowerSoldierTaskType.ONE, null);
				break;
			case SOLDIERS_LESS:
				requestSoldiers(EChangeTowerSoldierTaskType.LESS, ((SoldierAction) action).getSoldierType());
				break;
			case SOLDIERS_MORE:
				requestSoldiers(EChangeTowerSoldierTaskType.MORE, ((SoldierAction) action).getSoldierType());
				break;

			case ABORT:
				break;

			case EXIT:
				gameStoppable.stopGame();
				break;

			case MAKE_FERRY:
				orderShip(EShipType.FERRY);
				break;

			case MAKE_CARGO_SHIP:
				orderShip(EShipType.CARGO_SHIP);
				break;

			case UNLOAD_FERRIES:
				unloadFerries();
				break;

			default:
				System.out.println("WARNING: GuiInterface.action() called, but event can't be handled... (" + action.getActionType() + ")");
		}
	}

	private void handleBuildAction(BuildAction buildAction) {
		this.setSelection(new SelectionSet());
		EBuildingType buildingType = buildAction.getBuildingType();

		Optional<ShortPoint2D> position = grid.getConstructablePosition(buildAction.getPosition(), buildingType, playerId);
		position.ifPresent(pos -> scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, pos, buildingType)));
		System.out.println("build " + buildingType + " at " + position);
	}

	private void requestSoldiers(EChangeTowerSoldierTaskType taskType, ESoldierType soldierType) {
		ISelectable selectable = currentSelection.getSingle();
		if (selectable instanceof OccupyingBuilding) {
			OccupyingBuilding building = ((OccupyingBuilding) selectable);
			scheduleTask(new ChangeTowerSoldiersGuiTask(playerId, building.getPosition(), taskType, soldierType));
		}
	}

	private void selectNextOfType() {
		if (currentSelection.getSize() != 1) {
			return;
		}

		if (currentSelection.getSelectionType() == ESelectionType.BUILDING) {
			final Building building = (Building) currentSelection.get(0);
			final EBuildingType buildingType = building.getBuildingType();
			Building first = null;
			Building next = null;
			boolean buildingFound = false;

			for (final Building currBuilding : Building.getAllBuildings()) {
				if (currBuilding == building) {
					buildingFound = true;
				} else {
					if (currBuilding.getBuildingType() == buildingType && currBuilding.getPlayer().getPlayerId() == playerId) {
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

	public void setDock(ShortPoint2D requestedDockPosition) {
		final ISelectable selected = currentSelection.getSingle();
		if (!(selected instanceof IDockBuilding)) {
			return;
		}

		IDockBuilding building = (IDockBuilding) selected;

		if (building.canDockBePlaced(requestedDockPosition)) {
			taskScheduler.scheduleTask(new SetDockGuiTask(playerId, building, requestedDockPosition));
		} else {
			connector.playSound(116, 1); // this dock position is not at the coast
		}
	}

	private void setBuildingWorkArea(ShortPoint2D workAreaPosition) {
		final ISelectable selected = currentSelection.getSingle();
		if (selected instanceof Building) {
			scheduleTask(new WorkAreaGuiTask(EGuiAction.SET_WORK_AREA, playerId, workAreaPosition, ((Building) selected).getPosition()));
		}
	}

	private void sendConvertAction(ConvertAction action) {
		final List<ISelectable> convertables = new LinkedList<>();
		switch (action.getTargetType()) {
			case BEARER:
				for (final ISelectable curr : currentSelection) {
					if (curr instanceof IMovable) {
						final EMovableType currType = ((IMovable) curr).getMovableType();
						if (currType == EMovableType.PIONEER) {
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
				for (final ISelectable curr : currentSelection) {
					if (curr instanceof IMovable) {
						final EMovableType currType = ((IMovable) curr).getMovableType();
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
			taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, ((Building) currentSelection.iterator().next()).getPosition()));
		} else {
			taskScheduler.scheduleTask(new MovableGuiTask(EGuiAction.DESTROY_MOVABLES, playerId, getIDsOfSelected()));
		}
		setSelection(new SelectionSet());
	}

	private void setBuildingPriority(EPriority newPriority) {
		if (currentSelection != null && currentSelection.getSize() == 1 && currentSelection.iterator().next() instanceof Building) {
			taskScheduler
				.scheduleTask(new SetBuildingPriorityGuiTask(playerId, ((Building) currentSelection.iterator().next()).getPosition(), newPriority));
		}
	}

	private void showSelection() {
		int x = 0;
		int y = 0;
		int count = 0;
		for (final ISelectable member : currentSelection) {
			if (member instanceof ILocatable) {
				x += ((ILocatable) member).getPosition().x;
				y += ((ILocatable) member).getPosition().y;
				count++;
			}
		}
		System.out.println("locatable: " + count);
		if (count > 0) {
			final ShortPoint2D point = new ShortPoint2D(x / count, y / count);
			connector.scrollTo(point, false);
		}
	}

	/**
	 * @param stop
	 * 		if true the members of currentSelection will stop working<br>
	 * 		if false, they will start working
	 */
	private void stopOrStartWorkingAction(boolean stop) {
		taskScheduler.scheduleTask(new MovableGuiTask(stop ? EGuiAction.STOP_WORKING : EGuiAction.START_WORKING, playerId, getIDsOfSelected()));
	}

	private void moveTo(ShortPoint2D pos) {
		final List<Integer> selectedIds = getIDsOfSelected();
		scheduleTask(new MoveToGuiTask(playerId, pos, selectedIds));
	}

	private void orderShip(EShipType shipType) {
		DockyardBuilding dockyard = (DockyardBuilding) currentSelection.get(0);
		taskScheduler.scheduleTask(new OrderShipGuiTask(playerId, dockyard, shipType));
	}

	private void unloadFerries() {
		taskScheduler.scheduleTask(new MovableGuiTask(EGuiAction.UNLOAD_FERRY, playerId, getIDsOfSelected()));
	}

	private List<Integer> getIDsOfSelected() {
		return getIDsOfIterable(currentSelection);
	}

	private static List<Integer> getIDsOfIterable(Iterable<? extends ISelectable> iterable) {
		final List<Integer> selectedIds = new LinkedList<>();

		for (final ISelectable curr : iterable) {
			if (curr instanceof IIDable) {
				selectedIds.add(((IIDable) curr).getID());
			}
		}
		return selectedIds;
	}

	private void selectArea(SelectAreaAction action) {
		final SelectionSet selectionSet = new SelectionSet();

		action.getArea().stream().filterBounds(grid.getWidth(), grid.getHeight()).forEach((x, y) -> {
			final IGuiMovable movable = grid.getMovable(x, y);
			if (movable != null && canSelectPlayer(movable.getPlayer().getPlayerId())) {
				selectionSet.add(movable);
			}
			final IBuilding building = grid.getBuildingAt(x, y);
			if (building != null && canSelectPlayer(building.getPlayer().getPlayerId())) {
				selectionSet.add(building);
			}
		});

		setSelection(selectionSet);
	}

	private boolean canSelectPlayer(IPlayer player) {
		return canSelectPlayer(player.getPlayerId());
	}

	private boolean canSelectPlayer(byte playerIdOfSelected) {
		return MatchConstants.ENABLE_ALL_PLAYER_SELECTION || playerIdOfSelected == playerId;
	}

	private void filterWounded() {
		if (currentSelection.getSelectionType() == ESelectionType.BUILDING) {
			return;
		}

		final List<ISelectable> wounded = currentSelection.stream().filter(ISelectable::isWounded).collect(Collectors.toList());
		setSelection(new SelectionSet(wounded));
	}

	private void deselect() {
		setSelection(new SelectionSet());
	}

	private void handleSelectPointAction(PointAction action) {
		final ShortPoint2D pos = action.getPosition();

		// only for debugging
		grid.positionClicked(pos.x, pos.y);

		// check what's to do
		final ISelectable selected = getSelectableAt(pos);
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
			final IBuilding building = grid.getBuildingAt(pos.x, pos.y);
			if (building != null && canSelectPlayer(building.getPlayer().getPlayerId())) {
				return building;

			} else {
				final IGuiMovable selectableMovable = getSelectableMovable(pos.x, pos.y).orElse(null);
				if (selectableMovable != null) {
					return selectableMovable;
				}
			}
		}

		return null;
	}

	private Optional<IGuiMovable> getSelectableMovable(short selectedX, short selectedY) {
		return HexGridArea.stream(selectedX, selectedY, 0, 2).iterateForResult((x, y) -> {
			IGuiMovable movable = grid.getMovable(x, y);
			if (movable != null && canSelectPlayer(movable.getPlayer())) {
				return Optional.of(movable);
			} else {
				return Optional.empty();
			}
		});
	}

	private void selectPointType(PointAction action) {
		final ShortPoint2D actionPosition = action.getPosition();
		final IGuiMovable selectedMovable = getSelectableMovable(actionPosition.x, actionPosition.y).orElse(null);

		if (selectedMovable == null) { // nothing found at the location
			setSelection(new SelectionSet());
			return;
		}

		EMovableType selectedType = selectedMovable.getMovableType();
		byte selectedPlayerId = selectedMovable.getPlayer().getPlayerId();

		Set<EMovableType> selectableTypes;
		if (selectedType.isSwordsman()) {
			selectableTypes = EMovableType.SWORDSMEN;
		} else if (selectedType.isPikeman()) {
			selectableTypes = EMovableType.PIKEMEN;
		} else if (selectedType.isBowman()) {
			selectableTypes = EMovableType.BOWMEN;
		} else {
			selectableTypes = EnumSet.of(selectedType);
		}

		final List<ISelectable> selected = new LinkedList<>();

		MapCircle.stream(actionPosition, SELECT_BY_TYPE_RADIUS)
				 .filterBounds(grid.getWidth(), grid.getHeight())
				 .forEach((x, y) -> {
					 final IGuiMovable movable = grid.getMovable(x, y);
					 if (movable != null && selectableTypes.contains(movable.getMovableType()) && selectedPlayerId == movable.getPlayer().getPlayerId()) {
						 selected.add(movable);
					 }
				 });
		setSelection(new SelectionSet(selected));
	}

	/**
	 * Sets the selection.
	 *
	 * @param selection
	 * 		The selected items. Not null!
	 */
	private void setSelection(SelectionSet selection) {
		currentSelection.setSelected(false);

		selection.setSelected(true);
		connector.setSelection(selection);
		currentSelection = selection;
	}

	private void refreshSelection() {
		updateSelection(SelectionSet::createFromFilteredSelectionSetKeepingType);
	}

	@Override
	public void renewSelection() {
		updateSelection(SelectionSet::createFromFilteredSelectionSetUpdatingType);
	}

	private void updateSelection(BiFunction<SelectionSet, Predicate<ISelectable>, SelectionSet> updateFunction) {
		if (!currentSelection.isEmpty()) {
			SelectionSet newSelection = updateFunction.apply(currentSelection, selectable -> selectable.isSelected() && canSelectPlayer(selectable.getPlayer()));

			if (currentSelection.getSize() != newSelection.getSize() || currentSelection.getSelectionType() != newSelection.getSelectionType()) {
				setSelection(newSelection);
			}
		}
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
		refreshSelectionTimer.cancel();
	}
}
