package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManageableBricklayer extends IManageable {

	/**
	 * 
	 * @param constructionSite
	 * @param bricklayerTargetPos
	 * @param direction
	 * @return Returns true if the request can be handled by this bricklayer.
	 */
	boolean setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction);

}
