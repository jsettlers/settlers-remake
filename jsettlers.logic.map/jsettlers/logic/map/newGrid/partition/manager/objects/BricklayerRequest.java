package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;

public final class BricklayerRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = -1673422793657988587L;

	public final Building building;
	public final ShortPoint2D bricklayerTargetPos;
	public final EDirection direction;

	public BricklayerRequest(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		this.building = building;
		this.bricklayerTargetPos = bricklayerTargetPos;
		this.direction = direction;
	}

	@Override
	public final ShortPoint2D getPos() {
		return building.getPos();
	}
}