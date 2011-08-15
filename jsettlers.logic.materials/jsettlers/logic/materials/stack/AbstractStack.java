package jsettlers.logic.materials.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.hex.interfaces.IHexStack;

public abstract class AbstractStack implements IHexStack {

	protected final ISPosition2D pos;
	private byte player;
	protected byte count = 0;

	public AbstractStack(ISPosition2D pos, byte player) {
		this.pos = pos;
		this.player = player;
	}

	@Override
	public abstract boolean push(EMaterialType m);

	@Override
	public abstract boolean pop(EMaterialType type);

	@Override
	public final boolean isFull() {
		return count >= Constants.STACK_SIZE;
	}

	@Override
	public final boolean isEmpty() {
		return count == 0;
	}

	public final ISPosition2D getPos() {
		return pos;
	}

	@Override
	public abstract void destroy();

	public final boolean hasSpaceLeft() {
		return count < Constants.STACK_SIZE;
	}

	public abstract boolean isFulfilled();

	/**
	 * @param t
	 *            material type to be checked.
	 * @return true if the given material type can be pushed to this stack.<br>
	 *         false otherwise.
	 */
	public abstract boolean canTake(EMaterialType t);

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AbstractStack)) {
			return false;
		}

		AbstractStack other = (AbstractStack) o;
		return other.getPos().equals(this.getPos());
	}

	@Override
	public int hashCode() {
		return this.getPos().hashCode();
	}

	@Override
	public byte getPlayer() {
		return player;
	}
}
