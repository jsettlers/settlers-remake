package jsettlers.logic.map.newGrid.partition.manager;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;

public class ProductionRequest implements ILocatable {
	private final ISPosition2D pos;
	private final EMaterialType type;

	public ProductionRequest(EMaterialType type, ISPosition2D pos) {
		this.type = type;
		this.pos = pos;
	}

	@Override
    public ISPosition2D getPos() {
	    return pos;
    }

	public EMaterialType getType() {
	    return type;
    }
}
