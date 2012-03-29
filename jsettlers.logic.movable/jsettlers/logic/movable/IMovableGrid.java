package jsettlers.logic.movable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStar;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.map.newGrid.MainGrid.MovableNeighborIterator;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import synchronic.timer.NetworkTimer;

/**
 * This interface defines all methods needed by the movables to interact with the grid.
 * 
 * @author Andreas Eberle
 */
public interface IMovableGrid {
	/**
	 * The given movable left the given position
	 * 
	 * @param position
	 *            position to be left
	 * @param movable
	 *            movable that left
	 */
	void movableLeft(ShortPoint2D position, IHexMovable movable);

	/**
	 * The given movable entered the given position.
	 * 
	 * @param position
	 *            position to be entered
	 * @param movable
	 *            movable that entered
	 */
	void movableEntered(ShortPoint2D position, IHexMovable movable);

	/**
	 * This method returns the {@link MapObjectsManager} of the grid.
	 * 
	 * @return {@link MapObjectsManager} of the grid.
	 */
	MapObjectsManager getMapObjectsManager();

	/**
	 * This method returns the movable located at the given position.
	 * 
	 * @param position
	 *            position of the returned movable.
	 * @return the movable currently located at the given position<br>
	 *         or null if no movable is located at that position.
	 */
	IHexMovable getMovable(ShortPoint2D position);

	/**
	 * Checks if the given position is blocked.
	 * 
	 * @param x
	 *            x coordinate.
	 * @param y
	 *            y coordinate.
	 * @return true if the position is blocked<br>
	 *         false otherwise
	 */
	boolean isBlocked(short x, short y);

	/**
	 * Checks if the given position is blocked for the given {@link IPathCalculateable}.
	 * 
	 * @param pathCalculateable
	 *            the actor for whom it should be checked, if the position can be entered by himself.
	 * @param x
	 *            x coordinate.
	 * @param y
	 *            y coordinate.
	 * @return true if the position is blocked<br>
	 *         false otherwise
	 */
	boolean isBlocked(IPathCalculateable pathCalculateable, short x, short y);

	/**
	 * Places the given material at the given position.
	 * 
	 * @param position
	 *            position the material should be placed
	 * @param materialType
	 *            {@link EMaterialType} of the material to be placed
	 * @param offer
	 *            if true, the pushed material will be offered to the manager<br>
	 *            if false it won't be offered to the manager
	 * @return true if the material had been placed,<br>
	 *         false otherwise.
	 */
	boolean pushMaterial(ShortPoint2D position, EMaterialType materialType, boolean offer);

	/**
	 * Takes a material of the given type from the given position.
	 * 
	 * @param position
	 *            position the material should be taken from.
	 * @param materialType
	 *            {@link EMaterialType} of the material that should be taken.
	 * @return true if it was possible to take the material.<br>
	 *         false if it was not possible.
	 */
	boolean popMaterial(ShortPoint2D position, EMaterialType materialType);

	/**
	 * Gives the {@link ELandscapeType} at the given position.
	 * 
	 * @param position
	 *            position to be looked for.
	 * @return {@link ELandscapeType} that's currently set to the given position.
	 */
	ELandscapeType getLandscapeTypeAt(ShortPoint2D position);

	/**
	 * Gives the height at the given position.
	 * 
	 * @param position
	 *            position to get the height for.
	 * @return height at the given position.
	 */
	byte getHeightAt(ShortPoint2D position);

	/**
	 * Changes the height of the landscape at the given position.
	 * 
	 * @param position
	 *            position the height should be changed.
	 * @param delta
	 *            delta the height should be changed at the given position.
	 */
	void changeHeightAt(ShortPoint2D position, byte delta);

	/**
	 * Sets a marker to this position.
	 * 
	 * @param position
	 *            position the marker should be set.
	 * @param marked
	 *            if true, the mark will be set,<br>
	 *            if false, the mark will be removed.
	 */
	void setMarked(ShortPoint2D position, boolean marked);

	/**
	 * Checks if the exists a mark for this position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @return true if there is a marking<br>
	 *         false if there is none.
	 */
	boolean isMarked(ShortPoint2D position);

	/**
	 * Checks if the given position is on the grid.
	 * 
	 * @param position
	 *            position to be checked
	 * @return true if the given position is on the grid<br>
	 *         false otherwise.
	 */
	boolean isInBounds(ShortPoint2D position);

	/**
	 * Gives the player occupying the given position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @return player currently occupying the given position.
	 */
	byte getPlayerAt(ShortPoint2D position);

	/**
	 * Sets the player that should now occupy the given position.
	 * 
	 * @param position
	 *            position the player occupied
	 * @param player
	 *            player that is now occupying the given position.
	 */
	void changePlayerAt(ShortPoint2D position, byte player);

	/**
	 * Checks if the given position fits the given search type.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param searchType
	 *            {@link ESearchType} to be checked.
	 * @param pathCalculateable
	 *            {@link IPathCalculateable} that want's to go there.
	 * @return true if the position fits the searchType.<br>
	 *         false if it doesn't.
	 */
	boolean fitsSearchType(ShortPoint2D position, ESearchType searchType, IPathCalculateable pathCalculateable);

	/**
	 * Executes the given searchType
	 * 
	 * @param position
	 *            position to execute the searchType.
	 * @param searchType
	 *            {@link ESearchType} to be executed.
	 * @return true if the execution was successful<br>
	 *         false if it wasn't.
	 */
	boolean executeSearchType(ShortPoint2D position, ESearchType searchType);

	/**
	 * Checks if the given {@link EMaterialType} can be popped from the given position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param material
	 *            {@link EMaterialType} to be checked.
	 * @return true if it can be popped<br>
	 *         false otherwise.
	 */
	boolean canPop(ShortPoint2D position, EMaterialType material);

	/**
	 * Checks if the given {@link EMaterialType} can be pushed to the given position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @return true if it can be pushed to the given position<br>
	 *         false otherwise.
	 */
	boolean canPush(ShortPoint2D position);

	/**
	 * Gives a {@link HexAStar} algorithm.
	 * <p />
	 * NOTE: This {@link HexAStar} can only be used in a {@link NetworkTimer} synchronous way.
	 * 
	 * @return {@link IAStar} algorithm.
	 */
	IAStar getAStar();

	/**
	 * Gives a {@link DijkstraAlgorithm} algorithm.
	 * <p />
	 * NOTE: This {@link DijkstraAlgorithm} can only be used in a {@link NetworkTimer} synchronous way.
	 * 
	 * @return {@link DijkstraAlgorithm} algorithm.
	 */
	DijkstraAlgorithm getDijkstra();

	/**
	 * Gives a {@link InAreaFinder} algorithm.
	 * <p />
	 * NOTE: This {@link InAreaFinder} can only be used in a {@link NetworkTimer} synchronous way.
	 * 
	 * @return {@link InAreaFinder} algorithm.
	 */
	InAreaFinder getInAreaFinder();

	void addJobless(IManageableBearer bearer);

	void addJobless(IManageableWorker worker);

	void addJobless(IManageableBricklayer bricklayer);

	void addJobless(IManageableDigger digger);

	void changeLandscapeAt(ShortPoint2D pos, ELandscapeType type);

	/**
	 * Places or removes a smoke object.
	 * 
	 * @param pos
	 *            The position to place the object.
	 * @param place
	 *            If the object should be placed (true) or removed (false)
	 */
	void placeSmoke(ShortPoint2D pos, boolean place);

	boolean isProtected(short x, short y);

	void placePig(ShortPoint2D pos, boolean place);

	boolean isPigThere(ShortPoint2D pos);

	boolean isPigAdult(ShortPoint2D pos);

	boolean isEnforcedByTower(ShortPoint2D pos);

	boolean isAllowedForMovable(short x, short y, IPathCalculateable pathCalculatable);

	EResourceType getResourceTypeAt(short x, short y);

	byte getResourceAmountAt(short x, short y);

	boolean canAddRessourceSign(ShortPoint2D pos);

	/**
	 * Returns a {@link EMaterialType} available at the given position.
	 * 
	 * @param pos
	 *            position to be checked.
	 * @return an available {@link EMaterialType} at this position. <br>
	 *         or null if no material is available
	 */
	EMaterialType getMaterialTypeAt(ShortPoint2D pos);

	/**
	 * try to steal a material at the given position
	 * 
	 * @param pos
	 *            at this position the thief tries to steal something
	 * @return the {@link EMaterialType} of the stolen material<br>
	 *         or null if none had been stolen.
	 */
	EMaterialType stealMaterialAt(ShortPoint2D pos);

	EMaterialType popToolProduction(ShortPoint2D pos);

	/**
	 * gets the amount of resources around a given position. 1 means: full, 0 means nothing.
	 * 
	 * @param x
	 *            The x coordinate of the position
	 * @param y
	 *            The y coordinate of the position
	 * @param type
	 *            The type of resources to search for
	 * @return The amount.
	 */
	float getResourceAmountAround(short x, short y, EResourceType type);

	MovableNeighborIterator getNeighborsIterator(ShortPoint2D pos, byte radius);
}
