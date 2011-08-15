package jsettlers.logic.materials.stack.single;

import jsettlers.common.material.EMaterialType;

abstract class AbstractStackStrategy {
	protected final SingleMaterialStack stack;

	protected AbstractStackStrategy(SingleMaterialStack stack) {
		this.stack = stack;
	}

	protected abstract void push(EMaterialType m);

	protected abstract void pop();

	protected boolean isFulfilled() {
		return true;
	}

	protected abstract void destroy();
}
