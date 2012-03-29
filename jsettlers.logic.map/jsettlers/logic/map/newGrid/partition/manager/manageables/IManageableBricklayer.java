package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;

public interface IManageableBricklayer extends IManageable {

	void setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction);

}
