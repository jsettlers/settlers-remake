package jsettlers.common.buildings;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.selectable.ISelectable;

public interface IBuilding extends IPlayerable, ISelectable, ILocatable {
	public EBuildingType getBuildingType();

	public boolean isOccupied();

	public ISPosition2D getPos();

	/**
	 * 
	 * @return -1 if no action img should be displayed<br>
	 *         idx of current action image
	 */
	public int getActionImgIdx();

	public float getConstructionState();

}
