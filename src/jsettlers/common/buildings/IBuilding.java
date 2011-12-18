package jsettlers.common.buildings;

import java.util.List;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.selectable.ISelectable;

/**
 * This is a normal building.
 * <p>
 * Buildings are map objects wit type {@link EMapObjectType#BUILDING}
 * 
 * @author michael
 */
public interface IBuilding extends IMapObject, IPlayerable, ISelectable, ILocatable {

	/**
	 * Gets the type definition for the building.
	 * 
	 * @return The building type.
	 */
	public EBuildingType getBuildingType();

	/**
	 * This is a mill building. An animation is shown when {@link #isWorking()} returns true.
	 * 
	 * @author michael
	 */
	interface IMill extends IBuilding {
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
	interface IOccupyed extends IBuilding {
		List<? extends IBuildingOccupyer> getOccupyers();
	}

	/**
	 * if the building is currently working
	 * 
	 * @return true if working is enabled (no matter if it really works)
	 */
	public boolean isWorking();
}
