package jsettlers.logic.management.bearer.job;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.IJob;

public class BearerToWorkerJob implements IJob {

	private final EMovableType movableType;

	/**
	 * creates a BearerToWorkerJob for Workers that don't need any tool for their job.
	 * 
	 * @param movableType
	 *            type of movable the bearer should convert to.
	 */
	public BearerToWorkerJob(EMovableType movableType) {
		this.movableType = movableType;
	}

	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public ISPosition2D getFirstPos() { // isn't needed here
		return null;
	}
}
