/*******************************************************************************
 * Copyright (c) 2016 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.buildings.stack.multi;

import jsettlers.common.material.EMaterialType;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class holds shared data between multiple {@link MultiRequestStack}s of the same building.
 *
 * @author Andreas Eberle
 */
public class MultiRequestStackSharedData implements Serializable {
	private static final long serialVersionUID = 3890128212034591055L;

	final IMultiMaterialRequestSettings requestSettings;
	final byte[] inDelivery = new byte[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	private final Set<MultiRequestStack>[] handlingStacks;

	@SuppressWarnings("unchecked")
	public MultiRequestStackSharedData(IMultiMaterialRequestSettings requestSettings) {
		this.requestSettings = requestSettings;
		this.handlingStacks = new Set[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS; i++) {
			this.handlingStacks[i] = new LinkedHashSet<>();
		}
	}

	public void registerHandlingStack(EMaterialType materialType, MultiRequestStack stack) {
		handlingStacks[materialType.ordinal].add(stack);
	}

	public void unregisterHandlingStack(EMaterialType materialType, MultiRequestStack stack) {
		handlingStacks[materialType.ordinal].remove(stack);
	}

	public short getStillNeededIfNoOthersHandleIt(EMaterialType materialType) {
		Set<MultiRequestStack> stacksHandlingThisMaterial = handlingStacks[materialType.ordinal];

		for (MultiRequestStack stack : stacksHandlingThisMaterial) {
			if (stack.canAcceptMoreDeliveriesOf(materialType)) {
				return 0;
			}
		}

		return getStillNeeded(materialType);
	}

	public short getStillNeeded(EMaterialType materialType) {
		return (short) (requestSettings.getRequestedAmount(materialType) - inDelivery[materialType.ordinal]);
	}
}
