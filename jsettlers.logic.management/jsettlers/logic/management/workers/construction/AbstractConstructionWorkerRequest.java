package jsettlers.logic.management.workers.construction;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.management.workers.AbstractWorkerRequest;

public abstract class AbstractConstructionWorkerRequest extends AbstractWorkerRequest {

	public AbstractConstructionWorkerRequest(EMovableType movableType, byte player) {
		super(movableType, player);
	}

}
