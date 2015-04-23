package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;

public interface IConstructableBuilding extends IPlayerable {
	boolean tryToTakeMaterial();

	ShortPoint2D calculateRealPoint(short dx, short dy);

	EBuildingType getBuildingType();

	boolean isBricklayerRequestActive();

	void bricklayerRequestFailed(ShortPoint2D bricklayerTargetPos, EDirection lookDirection);
}
