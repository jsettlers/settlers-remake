package jsettlers.logic.management.workers.construction;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;

public class DiggerRequest extends AbstractConstructionWorkerRequest {

	private final FreeMapArea positions;
	private final byte heightAvg;

	public DiggerRequest(FreeMapArea buildingArea, byte heightAvg, byte player) {
		super(EMovableType.DIGGER, player);
		assert buildingArea.size() > 0 : "positions can not be empty";

		this.positions = buildingArea;
		this.heightAvg = heightAvg;
	}

	@Override
	public ISPosition2D getPos() {
		return getPositions().get(0);
	}

	public byte getHeightAvg() {
		return heightAvg;
	}

	public FreeMapArea getPositions() {
		return positions;
	}

}
