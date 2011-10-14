package jsettlers.logic.objects.stone;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.newGrid.interfaces.AbstractHexMapObject;

public class Stone extends AbstractHexMapObject {

	private static final int MAX_CAPACITY = 12;
	public static final float DECOMPOSE_DELAY = 150;
	private byte leftCapacity = 0;

	public Stone() {
		this(MAX_CAPACITY);
	}

	public Stone(int capacity) {
		leftCapacity = (byte) capacity;
	}

	@Override
	public boolean cutOff() {
		if (!canBeCut()) {
			return false;
		}

		this.leftCapacity--;
		return true;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.STONE;
	}

	@Override
	public float getStateProgress() {
		return leftCapacity;
	}

	@Override
	public RelativePoint[] getBlockedTiles() {
		return new RelativePoint[] { new RelativePoint(-1, -1), new RelativePoint(0, -1), new RelativePoint(-1, 0), new RelativePoint(0, 0),
				new RelativePoint(0, 1), new RelativePoint(1, 0), new RelativePoint(1, 1), };
	}

	@Override
	public boolean canBeCut() {
		return leftCapacity > 0;
	}

}
