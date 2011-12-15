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
public class StackMapObject extends AbstractHexMapObject implements IStackMapObject {
	private static final long serialVersionUID = -5471566113368524172L;

	private final EMaterialType materialType;
	private byte size;

	public StackMapObject(EMaterialType materialType, byte size) {
		this.materialType = materialType;
		this.size = size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isFull() {
		return size >= Constants.STACK_SIZE;
	}

	public void increment() {
		size++;
	}

	public void decrement() {
		size--;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STACK_OBJECT;
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	@Override
	public byte getSize() {
		return size;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

}
