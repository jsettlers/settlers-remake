package jsettlers.logic.materials.stack.single;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.constants.Constants;

/**
 * This is a stack that requests items for it. It is used for buildings that require materials to request them.
 * 
 * @author Andreas Eberle
 */
class LimitedRequestStackStrategy extends RequestStackStrategy {

	private final short targetAmount;
	private short received = 0;

	/**
	 * Creates a new request stack that only requests a specified number of items.
	 * 
	 * @param stack
	 *            The stack it is for
	 * @param amount
	 *            The number of items to order.
	 */
	LimitedRequestStackStrategy(SingleMaterialStack stack, short amount) {
		super(stack, false);

		this.targetAmount = amount;
		for (int i = 0; i < Constants.STACK_SIZE; i++) {
			produceRequest();
		}
	}

	@Override
	protected void produceRequest() {
		super.removeFinishedRequests();

		if (targetAmount > received + super.requests.size()) {
			super.produceRequest();
		}
	}

	@Override
	protected void push(EMaterialType m) {
		received++;
	}

	@Override
	public boolean isFulfilled() {
		return received == targetAmount;
	}

}
