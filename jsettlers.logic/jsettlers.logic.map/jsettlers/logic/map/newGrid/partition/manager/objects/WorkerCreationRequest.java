package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

public final class WorkerCreationRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = 3047014371520017602L;

	public final EMovableType movableType;
	public final ShortPoint2D position;

	public WorkerCreationRequest(EMovableType movableType, ShortPoint2D position) {
		this.movableType = movableType;
		this.position = position;
	}

	@Override
	public String toString() {
		return movableType + "    " + position;
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}
}