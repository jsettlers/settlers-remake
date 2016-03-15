package jsettlers.logic.buildings.stack.multi;

import java.util.LinkedHashSet;
import java.util.Set;

import jsettlers.common.material.EMaterialType;

public class MultiRequestStackSharedData {

	final short[] requestedMaterials;
	final byte[] inDelivery = new byte[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	final Set<MultiRequestStack>[] handlingStacks;

	@SuppressWarnings("unchecked")
	public MultiRequestStackSharedData(short[] requestedMaterials) {
		this.requestedMaterials = requestedMaterials;
		this.handlingStacks = new Set[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS; i++) {
			this.handlingStacks[i] = new LinkedHashSet<MultiRequestStack>();
		}
	}

	public void registerHandlingStack(EMaterialType materialType, MultiRequestStack stack) {
		handlingStacks[materialType.ordinal].add(stack);
	}

	public void unregisterHandlingStack(EMaterialType materialType, MultiRequestStack stack) {
		handlingStacks[materialType.ordinal].remove(stack);
	}

	public short getStillNeededIfNoOthersHandleIt(EMaterialType materialType) {
		for (MultiRequestStack stack : handlingStacks[materialType.ordinal]) {
			if (stack.canAcceptMoreDeliveries()) {
				return 0;
			}
		}

		return getStillNeeded(materialType);
	}

	public short getStillNeeded(EMaterialType materialType) {
		return (short) (requestedMaterials[materialType.ordinal] - inDelivery[materialType.ordinal]);
	}
}
