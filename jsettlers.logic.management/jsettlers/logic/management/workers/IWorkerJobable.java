package jsettlers.logic.management.workers;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;

public interface IWorkerJobable<T extends AbstractWorkerRequest> extends ILocatable, IPlayerable {

	void setWorkerRequest(T curr);

	Enum<EMovableType> getMovableType();

}
