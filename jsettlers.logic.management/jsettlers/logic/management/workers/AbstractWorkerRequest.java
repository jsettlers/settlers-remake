package jsettlers.logic.management.workers;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.management.AbstractJobPart;

public abstract class AbstractWorkerRequest extends AbstractJobPart {

	protected final EMovableType movableType;

	public AbstractWorkerRequest(EMovableType movableType, byte player) {
		super(player);
		this.movableType = movableType;
	}

	public EMovableType getWorkerType() {
		return movableType;
	}

}
