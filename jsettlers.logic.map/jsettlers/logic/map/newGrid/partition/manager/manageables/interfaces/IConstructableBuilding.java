package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.player.IPlayerable;

public interface IConstructableBuilding extends IWorkerBuilding, IPlayerable {
	boolean tryToTakeMaterial();

	boolean isConstructionFinished();
}
