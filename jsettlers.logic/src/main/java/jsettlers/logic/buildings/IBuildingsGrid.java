/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.buildings;

import jsettlers.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.stack.IRequestsStackGrid;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.grid.objects.MapObjectsManager;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.player.Player;

/**
 * This interface defines the methods needed by buildings to exist on a grid.
 *
 * @author Andreas Eberle
 */
public interface IBuildingsGrid {

	/**
	 * Gives the height at the given position.
	 *
	 * @param position
	 *            position to be checked.
	 * @return height at given position.
	 */
	byte getHeightAt(ShortPoint2D position);

	boolean setBuilding(ShortPoint2D position, Building newBuilding); // FIXME create interface for Building to be used by the grid

	/**
	 * Gives the width of the grid.
	 *
	 * @return width of the grid.
	 */
	short getWidth();

	/**
	 * Gives the height of the grid.
	 *
	 * @return height of the grid,
	 */
	short getHeight();

	/**
	 * Gives the movable currently located at the given position.
	 *
	 * @param position
	 *            position to be checked.
	 * @return the movable currently located at the given position<br>
	 *         or null if no movable is located at the given position.
	 */
	ILogicMovable getMovable(ShortPoint2D position);

	MapObjectsManager getMapObjectsManager();

	AbstractMovableGrid getMovableGrid();

	void requestDiggers(IDiggerRequester requester, byte amount);

	void requestBricklayer(Building building, ShortPoint2D position, EDirection direction);

	IRequestsStackGrid getRequestStackGrid();

	void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding);

	void requestSoldierable(IBarrack barrack);

	void setBlocked(FreeMapArea buildingArea, boolean blocked);

	void removeBuildingAt(ShortPoint2D pos);

	void pushMaterialsTo(ShortPoint2D position, EMaterialType type, byte numberOf);

	void setDock(DockPosition dockPosition, Player player);

	void removeDock(DockPosition dockPosition);

	/**
	 * @return dijkstra algorithm to be used by buildings.
	 */
	DijkstraAlgorithm getDijkstra();

	/**
	 * Occupies the given area for the given player.
	 *
	 * @param player
	 * @param influencingArea
	 */
	void occupyAreaByTower(Player player, MapCircle influencingArea, final FreeMapArea groundArea);

	/**
	 * Frees the area occupied by the tower at the given position.
	 *
	 * @param towerPosition
	 */
	void freeAreaOccupiedByTower(ShortPoint2D towerPosition);

	/**
	 * Changes the player of the tower at the given position to the given new player. The given groundArea will always become occupied by the new player.
	 *
	 * @param towerPosition
	 * @param newPlayer
	 * @param groundArea
	 */
	void changePlayerOfTower(ShortPoint2D towerPosition, Player newPlayer, final FreeMapArea groundArea);

	/**
	 * Checks if the given relative area has the flattened landscape type and the given height.
	 *
	 * @param position
	 * @param positions
	 * @param expectedHeight
	 * @return Returns true if the area has the given height and the landscape type {@link ELandscapeType}.FLATTENED.
	 */
	boolean isAreaFlattenedAtHeight(ShortPoint2D position, RelativePoint[] positions, byte expectedHeight);

	/**
	 * @param buildingPosition
	 * @param workAreaCenter
	 * @param radius
	 * @param draw
	 *            If true, the work area circle is drawn,<br>
	 *            if false, it is removed.
	 */
	void drawWorkAreaCircle(ShortPoint2D buildingPosition, ShortPoint2D workAreaCenter, short radius, boolean draw);

	/**
	 * Draws the trading path.
	 *
	 * @param start
	 *            The market position.
	 * @param waypoints
	 *            The waypoints. May contain null elements.
	 * @param draw
	 *            If true, the line is drawn,<br>
	 *            if false, it is removed.
	 */
	void drawTradingPathLine(ShortPoint2D start, ShortPoint2D[] waypoints, boolean draw);

	short getPartitionIdAt(ShortPoint2D pos);

	boolean tryTakingResource(ShortPoint2D position, EResourceType resource);

	int getAmountOfResource(EResourceType resource, Iterable<ShortPoint2D> positions);

	MaterialProductionSettings getMaterialProductionAt(int x, int y);

	ShortPoint2D getClosestReachablePosition(ShortPoint2D start, ShortPoint2D target, boolean needsPlayersGround, boolean isShip, IPlayer player, short targetRadius);

	boolean isCoastReachable(ShortPoint2D position);

	DockPosition findValidDockPosition(ShortPoint2D requestedDockPosition, ShortPoint2D buildingPosition, int maxRadius);
}
