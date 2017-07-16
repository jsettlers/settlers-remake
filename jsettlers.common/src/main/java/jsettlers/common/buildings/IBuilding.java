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
package jsettlers.common.buildings;

import java.util.List;

import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.selectable.ISelectable;
import jsettlers.common.sound.ISoundable;

/**
 * This is a normal building.
 * <p>
 * Buildings are map objects with type {@link EMapObjectType#BUILDING}
 *
 * @author michael
 * @author Andreas Eberle
 */
public interface IBuilding extends IMapObject, IPlayerable, ISelectable, ILocatable {

	/**
	 * Gets the type definition for the building.
	 *
	 * @return The building type.
	 */
	EBuildingType getBuildingType();

	/**
	 *
	 * @return Returns the priority of this Building in receiving materials.
	 */
	EPriority getPriority();

	/**
	 * Gets the priorities supported for this building. They may change over time. Most buildings at least support "STOPPED" and "NORMAL"
	 */
	EPriority[] getSupportedPriorities();

	/**
	 *
	 * @return true if this building is occupied or does not need to be occupied.
	 */
	boolean isOccupied();

	/**
	 * Gets the materials this building produces or needs.
	 * <p>
	 * When a building is under construction, this is the list of materials this building needs to be build (stones an wood) and it produces.
	 * <p>
	 * When it's construction finished, it is a list of things it needs and it currently has.
	 * <p>
	 * Empty stacks are also in the list.
	 *
	 * @return A list of materials for this building.
	 */
	List<IBuildingMaterial> getMaterials();

	/**
	 * Gives information if the building cannot work.
	 *
	 * @return Return true if this building cannot work.
	 */
	boolean cannotWork();

	/**
	 * This is a mill building. An animation is shown when {@link #isWorking()} returns true.
	 *
	 * @author michael
	 */
	interface IMill extends IBuilding, ISoundable {
		/**
		 * If the woking animation of the mill should be shown.
		 *
		 * @return True if the mill is working.
		 */
		boolean isRotating();
	}

	/**
	 * This interface should be implemented by towers that can have occupying people in them.
	 *
	 * @author michael
	 */
	interface IOccupied extends IBuilding {
		/**
		 * Gets a list of people occupying this building.
		 *
		 * @return The list of people currently in the building.
		 */
		List<? extends IBuildingOccupier> getOccupiers();

		/**
		 * Gets the number of currently searched SOLDIERS.
		 *
		 * @param soldierClass
		 *            The class of soldier.
		 * @return The number of SOLDIERS currently searched.
		 */
		int getSearchedSoldiers(ESoldierClass soldierClass);

		/**
		 * Gets the number of SOLDIERS that are currently comming.
		 *
		 * @param soldierClass
		 *            The class of soldier
		 * @return The number of SOLDIERS comming.
		 */
		int getComingSoldiers(ESoldierClass soldierClass);
	}

	/**
	 * A {@link IResourceBuilding} provides an additional productivity field for the GUI.
	 *
	 * @author Michael Zangl
	 * @author Andreas Eberle
	 */
	interface IResourceBuilding extends IBuilding {
		/**
		 * Gets the productivity of this {@link IResourceBuilding}.
		 *
		 * @return The productivity in the interval [0,1].
		 */
		float getProductivity();

		/**
		 * Returns the remaining amount of the building's resource.
		 *
		 * @return The number of resources available.
		 */
		int getRemainingResourceAmount();
	}

	interface IStock extends IBuilding {
		IStockSettings getStockSettings();
	}

	interface ITrading extends IBuilding {
		/**
		 * Gets the amount of material requested for a given type.
		 *
		 * @param material
		 *            The material.
		 * @return The amount, which is 0 in most cases. {@link Integer#MAX_VALUE} indicates an infinite amount.
		 */
		int getRequestedTradingFor(EMaterialType material);

		/**
		 * Checks if this is a sea trading building.
		 *
		 * @return True for sea trading buildings.
		 */
		boolean isSeaTrading();
	}
}
