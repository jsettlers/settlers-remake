package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;

/**
 * 
 * @author michael
 * 
 */
public class MillBuilding extends WorkerBuilding implements IBuilding.IMill {
	private static final long serialVersionUID = -8586862770507050382L;

	private boolean rotating;

	public MillBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public boolean isRotating() {
		return rotating;
	}

	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

}
