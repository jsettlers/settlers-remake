package jsettlers.logic.management.bearer.job;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.IJob;
import jsettlers.logic.management.MaterialJobPart;

public class BearerCarryJob implements IJob {
	private final MaterialJobPart offer;
	private final MaterialJobPart request;

	public BearerCarryJob(MaterialJobPart offer, MaterialJobPart request) {
		this.offer = offer;
		this.request = request;
	}

	@Override
	public ISPosition2D getFirstPos() {
		return offer.getPos();
	}

	public MaterialJobPart getOffer() {
		return offer;
	}

	public MaterialJobPart getRequest() {
		return request;
	}

}
