package jsettlers.common.buildings;

import java.util.List;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.selectable.ISelectable;
import jsettlers.common.sound.ISoundable;

/**
 * This is a normal building.
 * <p>
 * Buildings are map objects wit type {@link EMapObjectType#BUILDING}
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
	 * This is a mill building. An animation is shown when {@link #isWorking()} returns true.
	 * 
	 * @author michael
	 */
	static interface IMill extends IBuilding, ISoundable {
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
	static interface IOccupyed extends IBuilding {
		/**
		 * Gets a list of people occupying this building.
		 * 
		 * @return The list of people currently in the building.
		 */
		List<? extends IBuildingOccupyer> getOccupyers();

		/**
		 * Gets the number of soldiers the user has set to be requested at maximum for this building.
		 * 
		 * @param type
		 *            The type of slots.
		 * @return The number of soldiers we have at maximum.
		 */
		int getMaximumRequestedSoldiers(ESoldierType type);

		/**
		 * Sets the maximum number of requested soldiers for the given type. The number may be silently clamped by the logic depending on how much
		 * free space is available.
		 * 
		 * @param type
		 *            The building type.
		 * @param max
		 *            The maximum.
		 */
		void setMaximumRequestedSoldiers(ESoldierType type, int max);

		/**
		 * Gets the number of soldiers that are currently comming or already inside this building.
		 * 
		 * @param type
		 *            The type.
		 * @return The number of soldiers comming plus the number of soldiers already inside the building.
		 */
		int getCurrentlyCommingSoldiers(ESoldierType type);
	}

}
