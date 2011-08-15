package jsettlers.logic.materials.stack.single;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.IStack;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.materials.stack.AbstractStack;
import jsettlers.logic.player.ActivePlayer;

/**
 * This is a stack that can hold multiple items of the same material type.
 * 
 * @author Andreas Eberle
 * 
 */
public class SingleMaterialStack extends AbstractStack implements ILocatable {
	private final EMaterialType materialType;
	private AbstractStackStrategy strategy;

	/**
	 * creates a new SingleMaterialStack.
	 * 
	 * @param materialType
	 *            type of material the stack should be able to hold.
	 * @param position
	 *            position of the stack.
	 * @param stackType
	 *            EStackType.OFFER or EStackType.REQUEST<br>
	 *            NOTE: EStackType.LIMITED_REQUEST can't be used here. Use the other constructor.
	 */
	public SingleMaterialStack(EMaterialType materialType, ISPosition2D position, EStackType stackType, byte player) {
		this(materialType, position, stackType, player, (short) 0);
		assert stackType != EStackType.LIMITED_REQUEST : "for limited request stacks, you have to specify an amount; use the other constructor";
	}

	public SingleMaterialStack(EMaterialType materialType, ISPosition2D position, EStackType stackType, byte player, short amount) {
		super(position, player);
		this.materialType = materialType;

		switch (stackType) {
		case LIMITED_REQUEST:
			strategy = new LimitedRequestStackStrategy(this, amount);
			break;
		case OFFER:
			strategy = new OfferStackStrategy(this);
			break;
		case REQUEST:
			strategy = new RequestStackStrategy(this);
			break;
		}
	}

	@Override
	public boolean push(EMaterialType type) {
		if (type == materialType) {
			if (hasSpaceLeft()) {
				ActivePlayer.get().increaseOwned(super.getPlayer(), type);
				count++;
				strategy.push(type);
				return true;
			}
		} else {
			System.err.println("WARNING - pushing wrong material type: " + type + "    to stack of type " + materialType);
		}
		return false;
	}

	@Override
	public boolean pop(EMaterialType type) {
		if (!isEmpty() && canTake(type)) {
			ActivePlayer.get().reduceOwned(super.getPlayer(), type);
			count--;
			strategy.pop();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canTake(EMaterialType type) {
		return this.materialType == type;
	}

	@Override
	public void destroy() {
		strategy.destroy();
	}

	@Override
	public EMaterialType getMaterial() {
		return materialType;
	}

	@Override
	public byte getNumberOfElements() {
		return count;
	}

	@Override
	public IStack getNextStack() {
		return null;
	}

	@Override
	public void releaseRequests() {
		if (this.strategy instanceof RequestStackStrategy) {
			((RequestStackStrategy) this.strategy).releaseRequests();
			this.strategy = new OfferStackStrategy(this);
		}
	}

	@Override
	public boolean isFulfilled() {
		return strategy.isFulfilled();
	}
}
