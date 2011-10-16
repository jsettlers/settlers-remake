package jsettlers.common.buildings;

import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.selectable.ISelectable;

public interface IBuilding extends IMapObject, IPlayerable, ISelectable, ILocatable {
	public EBuildingType getBuildingType();

	public boolean isOccupied();

	/**
	 * 
	 * @return -1 if no action img should be displayed<br>
	 *         idx of current action image
	 */
	public int getActionImgIdx();

}
