package jsettlers.input;

import java.io.Serializable;

import jsettlers.graphics.map.UIState;
import jsettlers.logic.algorithms.fogofwar.FogOfWar;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PlayerState implements Serializable {
	private static final long serialVersionUID = -1077800774789265575L;

	private final byte playerId;
	private final UIState uiState;
	private final FogOfWar fogOfWar;

	public PlayerState(byte playerId, UIState uiState, FogOfWar fogOfWar) {
		this.playerId = playerId;
		this.uiState = uiState;
		this.fogOfWar = fogOfWar;
	}

	public PlayerState(byte playerId, UIState uiState) {
		this(playerId, uiState, null);
	}

	public byte getPlayerId() {
		return playerId;
	}

	public UIState getUiState() {
		return uiState;
	}

	public FogOfWar getFogOfWar() {
		return fogOfWar;
	}
}
