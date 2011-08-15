package jsettlers.logic.materials.stack.single;

import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.MaterialJobPart;

/**
 * This is a stack that requests items for it. It is used for buildings that require materials to request them.
 * 
 * @author Andreas Eberle
 */
class RequestStackStrategy extends AbstractStackStrategy {
	protected LinkedList<MaterialJobPart> requests = new LinkedList<MaterialJobPart>();

	/**
	 * Creates a new request stack that requests a infinite number of materials.
	 * 
	 * @param stack
	 *            The stack.
	 */
	RequestStackStrategy(SingleMaterialStack stack) {
		this(stack, true);
	}

	protected RequestStackStrategy(SingleMaterialStack stack, boolean init) {
		super(stack);

		if (init) {
			for (int i = 0; i < Constants.STACK_SIZE; i++) {
				produceRequest();
			}
		}
	}

	@Override
	protected void pop() {
		produceRequest();
		removeFinishedRequests();
	}

	protected void removeFinishedRequests() {
		Iterator<MaterialJobPart> iter = this.requests.iterator();
		while (iter.hasNext()) {
			if (iter.next().isFulfilled())
				iter.remove();
		}
	}

	protected void produceRequest() {
		if (super.stack.getPlayer() >= 0) {
			EMaterialType material = super.stack.getMaterial();
			ISPosition2D position = super.stack.getPos();
			MaterialJobPart request = new MaterialJobPart(material, position, super.stack.getPlayer());
			GameManager.requestMaterial(request);
			this.requests.add(request);
		}
	}

	@Override
	protected void push(EMaterialType m) {
	}

	protected void releaseRequests() {
		removeFinishedRequests();

		for (MaterialJobPart curr : this.requests) {
			curr.cancel();
		}
		this.requests.clear();
	}

	
	@Override
	protected void destroy() {
		for (MaterialJobPart curr : requests) {
			curr.cancel();
		}
	}
}
