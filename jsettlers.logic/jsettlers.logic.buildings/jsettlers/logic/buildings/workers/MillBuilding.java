package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.logic.player.Player;

/**
 * This is a mill building with the ability to rotate.
 * 
 * @author Andreas Eberle
 */
public final class MillBuilding extends WorkerBuilding implements IBuilding.IMill {
	private static final long serialVersionUID = -8586862770507050382L;

	private boolean rotating;
	private boolean soundPlayed;

	public MillBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	public boolean isRotating() {
		return rotating;
	}

	public void setRotating(boolean rotating) {
		this.rotating = rotating;
		this.soundPlayed = false;
	}

	@Override
	public void setSoundPlayed() {
		soundPlayed = true;
	}

	@Override
	public boolean isSoundPlayed() {
		return soundPlayed;
	}

}
