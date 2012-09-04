package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

public final class ProductionRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = -1849601517609060590L;

	public final ShortPoint2D pos;
	public final EMaterialType type;

	public ProductionRequest(EMaterialType type, ShortPoint2D pos) {
		this.type = type;
		this.pos = pos;
	}

	@Override
	public ShortPoint2D getPos() {
		return pos;
	}

}
