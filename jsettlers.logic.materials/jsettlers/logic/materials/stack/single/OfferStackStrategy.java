package jsettlers.logic.materials.stack.single;

import java.util.LinkedList;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.MaterialJobPart;
import jsettlers.logic.management.bearer.BearerJobCreator;

/**
 * This is a stack strategy that lets normal barriers take items from it and therefore registers this stack in the global job manager.
 * 
 * @see BearerJobCreator
 * @author Andreas Eberle
 */
class OfferStackStrategy extends AbstractStackStrategy {
	protected LinkedList<MaterialJobPart> offers = new LinkedList<MaterialJobPart>();

	OfferStackStrategy(SingleMaterialStack stack) {
		super(stack);

		for (int i = 0; i < stack.getNumberOfElements(); i++) {
			produceOffer();
		}
	}

	@Override
	protected void push(EMaterialType m) {
		produceOffer();
	}

	private void produceOffer() {
		if (super.stack.getPlayer() >= 0) {
			EMaterialType material = super.stack.getMaterial();
			ISPosition2D position = super.stack.getPos();

			MaterialJobPart newOffer = new MaterialJobPart(material, position, super.stack.getPlayer());
			GameManager.offerMaterial(newOffer);
			offers.add(newOffer);
		}
	}

	@Override
	protected void pop() {
	}

	@Override
	protected void destroy() {
		for (MaterialJobPart curr : offers) {
			curr.cancel();
		}
	}

}
