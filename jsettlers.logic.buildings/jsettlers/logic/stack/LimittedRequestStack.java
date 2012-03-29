package jsettlers.logic.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

public class LimittedRequestStack extends RequestStack {
	private static final long serialVersionUID = -8778043264123900036L;

	private final short requestedAmount;
	private short stillToBeRequested = 0;
	private short poppedMaterials = 0;

	public LimittedRequestStack(IRequestsStackGrid grid, ShortPoint2D position, EMaterialType materialType, short requestedAmount) {
		super(grid, position, materialType);
		this.requestedAmount = requestedAmount;
		this.stillToBeRequested = requestedAmount;

		for (int i = 0; i < Constants.STACK_SIZE; i++) {
			requestMaterial();
		}
	}

	@Override
	protected void requestMaterial() {
		if (stillToBeRequested > 0) {
			super.requestMaterial();
			stillToBeRequested--;
		}
	}

	@Override
	public void pop() {
		super.pop();
		poppedMaterials++;
	}

	@Override
	public boolean isFullfilled() {
		return poppedMaterials >= requestedAmount;
	}

	@Override
	public void requestFailed() {
		super.requestFailed();
		stillToBeRequested++;
	}

	public int getNumberOfPopped() {
		return poppedMaterials;
	}
}
