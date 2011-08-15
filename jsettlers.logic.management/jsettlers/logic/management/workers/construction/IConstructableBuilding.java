package jsettlers.logic.management.workers.construction;

import jsettlers.common.player.IPlayerable;
import jsettlers.logic.management.workers.IWorkerBuilding;

public interface IConstructableBuilding extends IWorkerBuilding, IPlayerable {
	boolean tryToTakeMaterial();
}
