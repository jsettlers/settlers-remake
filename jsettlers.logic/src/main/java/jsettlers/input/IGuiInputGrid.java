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

import java.io.IOException;

import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.menu.UIState;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.FerryEntrance;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.player.Player;

import java8.util.Optional;

/**
 * This interface defines the methods needed by the GUI to interact with the grid.
 *
 * @author Andreas Eberle
 */
public interface IGuiInputGrid {

	short getWidth();

	short getHeight();

	IBuilding getBuildingAt(int x, int y);

	boolean isInBounds(ShortPoint2D position);

	void resetDebugColors();

	IGuiMovable getMovable(int x, int y);

	/**
	 * Gets a position where the building can be constructed some points around pos.
	 *
	 * @param position
	 *            THe position
	 * @param type
	 *            The type of the building
	 * @param player
	 *            The player that wants to construct the building.
	 * @return <code>null</code> if no position was found, the position otherwise.
	 */
	Optional<ShortPoint2D> getConstructablePosition(ShortPoint2D position, EBuildingType type, byte player);

	/**
	 * Saves the map with the given {@link UIState}.
	 *
	 * @param playerId
	 * @param uiState
	 * @throws IOException
	 * @throws InterruptedException
	 */
	void save(Byte playerId, UIState uiState) throws IOException, InterruptedException;

	void toggleFogOfWar();

	AbstractConstructionMarkableMap getConstructionMarksGrid();

	/**
	 * Positions a new building of the given type at the given position.
	 *
	 * @param position
	 *            Position the new building will be placed. <br>
	 *            NOTE: There will be no validation if this position is allowed! This must be done prior to this call.
	 * @param type
	 *            {@link EBuildingType} of the new building.
	 * @param playerId
	 *            The player constructing the building.
	 */
	void constructBuildingAt(ShortPoint2D position, EBuildingType type, byte playerId);

	/**
	 * This method can be used to print debug output when the given position is clicked by the user.
	 *
	 * @param x
	 *            x coordinate of the position.
	 * @param y
	 *            y coordinate of the position.
	 */
	void positionClicked(int x, int y);

	/**
	 * Sets the distribution settings for the given materialType in the manager at the given managerPosition.
	 *
	 * @param managerPosition
	 *            The position of the manger to set the given settings.
	 * @param materialType
	 *            The {@link EMaterialType} of the material the given settings shall be used for.
	 * @param buildingType
	 *            The building type for which this setting should be set.
	 * @param ratio
	 *            The ratio that should be set for given materialType and buildingType.
	 *
	 */
	void setMaterialDistributionSettings(ShortPoint2D managerPosition, EMaterialType materialType, EBuildingType buildingType, float ratio);

	/**
	 * Sets the material priorities setting in the given manager at the given managerPosition.
	 *
	 * @param managerPosition
	 *            The position of the manger to set the given settings.
	 * @param materialTypeForPriority
	 *            The {@link EMaterialType}s for the priorities. The first element has the highest priority, the last one has the lowest.
	 */
	void setMaterialPrioritiesSettings(ShortPoint2D managerPosition, EMaterialType[] materialTypeForPriority);

	short getBlockedPartition(int x, int y);

	boolean isBlocked(int x, int y);

	Player getPlayer(byte playerId);

	byte getNumberOfPlayers();

	MaterialProductionSettings getMaterialProductionAt(ShortPoint2D position);

	void setAcceptedStockMaterial(ShortPoint2D position, EMaterialType materialType, boolean accepted);

	FerryEntrance ferryAtPosition(ShortPoint2D position, byte playerId);

	boolean isNavigable(int x, int y);
}
