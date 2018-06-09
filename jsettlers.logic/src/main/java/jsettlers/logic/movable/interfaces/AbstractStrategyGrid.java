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
package jsettlers.logic.movable.interfaces;

import java.io.Serializable;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.movable.MovableStrategy;
import jsettlers.logic.player.Player;

/**
 * Defines methods needed by the {@link MovableStrategy}.
 *
 * @author Andreas Eberle
 *
 */
public abstract class AbstractStrategyGrid implements Serializable {
	private static final long serialVersionUID = 5560951888790865783L;

	public abstract void addJobless(IManageableBearer bearer);

	public abstract void removeJobless(IManageableBearer bearer);

	public abstract void addJobless(IManageableWorker worker);

	public abstract void removeJobless(IManageableWorker worker);

	public abstract void addJobless(IManageableDigger digger);

	public abstract void removeJobless(IManageableDigger digger);

	public abstract void addJobless(IManageableBricklayer bricklayer);

	public abstract void removeJobless(IManageableBricklayer bricklayer);

	/**
	 * Take a material from the stack at given position of given {@link EMaterialType}.
	 *
	 * @param pos
	 * @param materialType
	 * @return true if the material was available<br>
	 *         false otherwise.
	 */
	public abstract boolean takeMaterial(ShortPoint2D pos, EMaterialType materialType);

	/**
	 * Drop a material of given type at given position.
	 *
	 * @param pos
	 * @param materialType
	 * @param offer
	 * @param forced
	 *            If true, the material will be put on the grid on the closest free location.
	 */
	public abstract boolean dropMaterial(ShortPoint2D pos, EMaterialType materialType, boolean offer, boolean forced);

	/**
	 *
	 * @param position
	 * @param searchType
	 * @return in what direction you have to look from the given position to directly look at the given search type<br>
	 *         or null if the search type isn't a neighbor of the given position.
	 */
	public abstract EDirection getDirectionOfSearched(ShortPoint2D position, ESearchType searchType);

	/**
	 * Checks if the given position fits the given search type.
	 *
	 * @param pathCalculateable
	 *            path requester
	 * @param x
	 *            x coordinate of position to check the given {@link ESearchType}.
	 * @param y
	 *            y coordinate of position to check the given {@link ESearchType}.
	 * @param searchType
	 *            search type to be checked
	 * @return true if the search type fits the given position.
	 */
	public abstract boolean fitsSearchType(IPathCalculatable pathCalculateable, int x, int y, ESearchType searchType);

	/**
	 *
	 * @param pathCalculatable
	 *            requester
	 * @param position
	 *            Position to execute the given {@link ESearchType}.
	 * @param searchType
	 * @return true if the given position can be used to execute the search type.<br>
	 *         false if it can not
	 */
	public abstract boolean executeSearchType(IPathCalculatable pathCalculatable, ShortPoint2D position, ESearchType searchType);

	public abstract EMaterialType popToolProductionRequest(ShortPoint2D pos);

	public abstract void placePigAt(ShortPoint2D pos, boolean place);

	/**
	 *
	 * @param position
	 * @return true if there is a pig at given pos<br>
	 *         false otherwise.
	 */
	public abstract boolean hasPigAt(ShortPoint2D position);

	/**
	 *
	 * @param position
	 * @return true if there is a pig on given position.
	 */
	public abstract boolean isPigAdult(ShortPoint2D position);

	public abstract boolean feedDonkeyAt(ShortPoint2D position);

	/**
	 * Show smoke or remove it at the given position.
	 *
	 * @param position
	 *            position to let the smoke appear.
	 * @param smokeOn
	 *            if true, smoke will be turned on, <br>
	 *            if false, it will be turned of.
	 */
	public abstract void placeSmoke(ShortPoint2D position, boolean smokeOn);

	/**
	 * checks if there can be put any more materials on the given position.
	 *
	 * @param position
	 * @return
	 */
	public abstract boolean canPushMaterial(ShortPoint2D position);

	/**
	 * Checks if the given {@link EMaterialType} can be popped from the given position.
	 *
	 * @param position
	 * @param material
	 * @return
	 */
	public abstract boolean canTakeMaterial(ShortPoint2D position, EMaterialType material);

	public abstract byte getHeightAt(ShortPoint2D position);

	public abstract boolean isMarked(ShortPoint2D position);

	public abstract void setMarked(ShortPoint2D position, boolean marked);

	/**
	 * Changes the height of the given position towards the given targetHeight and changes the landscape type to {@link ELandscapeType}.FLATTENED
	 *
	 * @param x
	 * @param y
	 * @param targetHeight
	 */
	public abstract void changeHeightTowards(int x, int y, byte targetHeight);

	/**
	 * Changes the player at the given position to the given player.
	 *
	 * @param pos
	 * @param player
	 */
	public abstract void changePlayerAt(ShortPoint2D pos, Player player);

	/**
	 * Gets the landscape type at the given position.
	 *
	 * @param x
	 *            x coordinate of the position to get the landscape type.
	 * @param y
	 *            y coordinate of the position to get the landscape type.
	 * @return {@link ELandscapeType} at the given position.
	 */
	public abstract ELandscapeType getLandscapeTypeAt(int x, int y);

	/**
	 * Searches for an enemy around the position of the given movable in it's search radius.
	 *
	 * @param centerPos
	 *            The center position to start the search.
	 * @param movable
	 *            The movable searching an enemy.
	 * @param minSearchRadius
	 *            The minimum radius of the search for enemy attackables.
	 * @param maxSearchRadius
	 *            The maximum radius of the search for enemy attackables.
	 * @param includeTowers
	 *            If true, towers are included in the search, if false, only movables are searched.
	 * @return The closest enemy or null if none exists in the search radius.
	 */
	public abstract IAttackable getEnemyInSearchArea(ShortPoint2D centerPos, IAttackable movable, short minSearchRadius, short maxSearchRadius,
													 boolean includeTowers);

	/**
	 * Adds an arrow object to the map flying from
	 *
	 * @param attackedPos
	 *            Attacked position.
	 * @param shooterPos
	 *            Position of the shooter.
	 * @param shooterPlayerId
	 *            The id of the attacking player.
	 * @param hitStrength
	 *            Strength of the hit.
	 */
	public abstract void addArrowObject(ShortPoint2D attackedPos, ShortPoint2D shooterPos, byte shooterPlayerId, float hitStrength);

	public abstract boolean hasNoMovableAt(int x, int y);

	/**
	 *
	 * @param position
	 *            The position to be checked.
	 * @return true if the position is on the grid, not blocked and free of other movables. <br>
	 *         false otherwise.
	 */
	public abstract boolean isFreePosition(int x, int y);

	public abstract boolean tryTakingResource(ShortPoint2D position, EResourceType resource);

	public abstract ILogicMovable getMovableAt(int x, int y);
}
