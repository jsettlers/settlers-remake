package jsettlers.logic.buildings;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.objects.MapObjectsManager;
import jsettlers.logic.stack.IRequestsStackGrid;

/**
 * This interface defines the methods needed by buildings to exist on a grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBuildingsGrid {

	/**
	 * Gives the height at the given position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @return height at given position.
	 */
	byte getHeightAt(ISPosition2D position);

	boolean setBuilding(ISPosition2D position, Building newBuilding); // FIXME create interface for Building to be used by the grid

	void setPlayerAt(ISPosition2D position, byte player);

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
	IHexMovable getMovable(ISPosition2D position);

	/**
	 * Positions the given movable at the given position.
	 * 
	 * @param position
	 *            position the movable should be positioned.
	 * @param movable
	 *            movable to be positioned.
	 */
	void placeNewMovable(ISPosition2D position, IHexMovable movable);

	MapObjectsManager getMapObjectsManager();

	IMovableGrid getMovableGrid();

	void requestDigger(FreeMapArea buildingArea, byte heightAvg);

	void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction);

	IRequestsStackGrid getRequestStackGrid();

	void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding);
}
