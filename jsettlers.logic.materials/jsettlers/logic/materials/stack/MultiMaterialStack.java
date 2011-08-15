package jsettlers.logic.materials.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.IStack;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.materials.stack.single.EStackType;
import jsettlers.logic.materials.stack.single.SingleMaterialStack;

/**
 * This class realizes the composite pattern to simulate a stack than can take several materials.
 * 
 * 
 * @author Andreas Eberle
 * 
 */
public class MultiMaterialStack extends AbstractStack {

	private AbstractStack[] stacks;

	/**
	 * Creates a new MultiMaterialStack.
	 * 
	 * @param pos
	 *            location the stack is positioned
	 * @param types
	 *            types the stack should have.
	 */
	public MultiMaterialStack(ISPosition2D pos, byte player, EMaterialType... types) {
		super(pos, player);
		assert types.length >= 1;

		this.stacks = new AbstractStack[types.length];

		int i = 0;
		for (EMaterialType currType : types) {
			this.stacks[i] = new SingleMaterialStack(currType, pos, EStackType.OFFER, player);
			i++;
		}
	}

	@Override
	public void releaseRequests() {
		// multi stacks can only offer things
	}

	@Override
	public EMaterialType getMaterial() {
		return stacks[0].getMaterial();
	}

	@Override
	public byte getNumberOfElements() {
		return stacks[0].getNumberOfElements();
	}

	@Override
	public IStack getNextStack() {
		if (stacks.length > 1) {
			return stacks[1];
		} else {
			return null;
		}
	}

	@Override
	public boolean push(EMaterialType type) {
		AbstractStack stack = getStack(type);

		if (stack != null && !stack.isFull()) {
			if (stack.pop(type)) {
				count++;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean pop(EMaterialType type) {
		AbstractStack stack = getStack(type);

		if (stack != null && !stack.isEmpty()) {
			if (stack.pop(type)) {
				count--;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private AbstractStack getStack(EMaterialType materialType) {
		for (AbstractStack currStack : stacks) {
			if (currStack.getMaterial() == materialType) {
				return currStack;
			}
		}
		return null;
	}

	@Override
	public void destroy() {
		for (AbstractStack currStack : stacks) {
			currStack.destroy();
		}
	}

	@Override
	public boolean isFulfilled() {
		return true;
	}

	@Override
	public boolean canTake(EMaterialType type) {
		return getStack(type) != null;
	}

}
