package jsettlers.main.components.joingame;

import jsettlers.common.ai.EPlayerType;
import jsettlers.graphics.localization.Labels;

/**
 * @author codingberlin
 */
public class PlayerTypeUiWrapper {

	private final EPlayerType playerType;

	public PlayerTypeUiWrapper(EPlayerType playerType) {
		this.playerType = playerType;
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	@Override public String toString() {
		return Labels.getString("player-type-" + playerType.name());
	}
}
