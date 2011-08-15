package jsettlers.logic.management.bearer.job;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.MaterialJobPart;

public class BearerToWorkerWithMaterialJob extends BearerToWorkerJob {

	private final MaterialJobPart offer;

	/**
	 * creates a BearerToWorkerJob for Workers that need a tool for their job.
	 * 
	 * @param offer
	 *            offer of the needed tool
	 * @param movableType
	 */
	public BearerToWorkerWithMaterialJob(MaterialJobPart offer, EMovableType movableType) {
		super(movableType);
		this.offer = offer;

		assert movableType.getTool() == offer.getMaterialType() : "this movabletype(" + movableType + ") can't be create with that tool("
				+ offer.getMaterialType() + ")";
	}

	public MaterialJobPart getOffer() {
		return offer;
	}

	@Override
	public ISPosition2D getFirstPos() { // isn't needed here
		return offer.getPos();
	}
}
