package jsettlers.logic.newmovable.interfaces;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.newmovable.NewMovableStrategy;

/**
 * Defines methods needed by the {@link NewMovableStrategy}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IStrategyGrid {

	void addJobless(IManageableBearer bearer);

	void removeJobless(IManageableBearer bearer);

	void addJobless(IManageableWorker worker);

	void removeJobless(IManageableWorker worker);

	void addJobless(IManageableDigger digger);

	void removeJobless(IManageableDigger digger);

	void addJobless(IManageableBricklayer bricklayer);

	void removeJobless(IManageableBricklayer bricklayer);

	/**
	 * Take a material from the stack at given position of given {@link EMaterialType}.
	 * 
	 * @param pos
	 * @param materialType
	 * @return true if the material was available<br>
	 *         false otherwise.
	 */
	boolean takeMaterial(ShortPoint2D pos, EMaterialType materialType);

	/**
	 * Drop a material of given type at given position.
	 * 
	 * @param pos
	 * @param materialType
	 */
	boolean dropMaterial(ShortPoint2D pos, EMaterialType materialType, boolean offer);

	float getResourceAmountAround(short x, short y, EResourceType type);

	/**
	 * 
	 * @param position
	 * @param searchType
	 * @return in what direction you have to look from the given position to directly look at the given search type<br>
	 *         or null if the search type isn't a neighbor of the given position.
	 */
	EDirection getDirectionOfSearched(ShortPoint2D position, ESearchType searchType);

	/**
	 * 
	 * @param pos
	 * @param searchType
	 * @return true if the given position can be used to execute the search type.<br>
	 *         false if it can not
	 */
	boolean executeSearchType(ShortPoint2D pos, ESearchType searchType);

	/**
	 * Checks if the given position fits the given search type.
	 * 
	 * @param pathCalculateable
	 *            path requester
	 * @param pos
	 *            position
	 * @param searchType
	 *            search type to be checked
	 * @return true if the search type fits the given position.
	 */
	boolean fitsSearchType(IPathCalculateable pathCalculateable, ShortPoint2D pos, ESearchType searchType);

	EMaterialType popToolProductionRequest(ShortPoint2D pos);

	void placePigAt(ShortPoint2D pos, boolean place);

	/**
	 * 
	 * @param position
	 * @return true if there is a pig at given pos<br>
	 *         false otherwise.
	 */
	boolean hasPigAt(ShortPoint2D position);

	/**
	 * 
	 * @param position
	 * @return true if there is a pig on given position.
	 */
	boolean isPigAdult(ShortPoint2D position);

	/**
	 * Show smoke or remove it at the given position.
	 * 
	 * @param position
	 *            position to let the smoke appear.
	 * @param smokeOn
	 *            if true, smoke will be turned on, <br>
	 *            if false, it will be turned of.
	 */
	void placeSmoke(ShortPoint2D position, boolean smokeOn);

	/**
	 * checks if there can be put any more materials on the given position.
	 * 
	 * @param position
	 * @return
	 */
	boolean canPushMaterial(ShortPoint2D position);

	/**
	 * Checks if the given {@link EMaterialType} can be popped from the given position.
	 * 
	 * @param position
	 * @param material
	 * @return
	 */
	boolean canPop(ShortPoint2D position, EMaterialType material);

	byte getHeightAt(ShortPoint2D position);

	boolean isMarked(ShortPoint2D position);

	void setMarked(ShortPoint2D position, boolean marked);

	/**
	 * Changes the height of the given position towards the given targetHeight and changes the landscape type to {@link ELandscapeType}.FLATTENED
	 * 
	 * @param position
	 * @param targetHeight
	 */
	void changeHeightTowards(short x, short y, byte targetHeight);

	void changePlayerAt(ShortPoint2D pos, byte player);

	/**
	 * Gets the landscape type at the given position.
	 * 
	 * @param x
	 *            x coordinate of the position to get the landscape type.
	 * @param y
	 *            y coordinate of the position to get the landscape type.
	 * @return {@link ELandscapeType} at the given position.
	 */
	ELandscapeType getLandscapeTypeAt(short x, short y);

}
