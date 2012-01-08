package jsettlers.logic.objects.stack;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;

/**
 * This is a stack object that can be located on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public final class StackMapObject extends AbstractHexMapObject implements IStackMapObject {
	private static final long serialVersionUID = -5471566113368524172L;

	private final EMaterialType materialType;
	private byte size;
	private byte markedStolen = 0;

	public StackMapObject(EMaterialType materialType, byte size) {
		this.materialType = materialType;
		this.size = size;
	}

	public final boolean isEmpty() {
		return size == 0;
	}

	public final boolean isFull() {
		return size >= Constants.STACK_SIZE;
	}

	public final void incrementStolenMarks() {
		markedStolen++;
	}

	public final void decrementStolenMarks() {
		markedStolen--;
	}

	/**
	 * 
	 * @return true if the stack has a material that is not already marked by a thief.<br>
	 *         false if all materials are already marked by thiefs.
	 */
	public final boolean hasUnstolen() {
		return markedStolen < size;
	}

	public final boolean hasStolenMarks() {
		return markedStolen > 0;
	}

	public final void increment() {
		size++;
	}

	public final void decrement() {
		size--;
	}

	@Override
	public final EMapObjectType getObjectType() {
		return EMapObjectType.STACK_OBJECT;
	}

	@Override
	public final float getStateProgress() {
		return 0;
	}

	@Override
	public final EMaterialType getMaterialType() {
		return materialType;
	}

	@Override
	public final byte getSize() {
		return size;
	}

	@Override
	public final boolean cutOff() {
		return false;
	}

	@Override
	public final boolean canBeCut() {
		return false;
	}

}
