package jsettlers.logic.player;

import java.util.Arrays;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EWhatToDoAiType;

/**
 * @author codingberlin
 */
public class PlayerSetting {

	private final boolean isAi;
	private final EWhatToDoAiType aiType;
	private boolean isAvailable;

	/**
	 * Creates a new {@link PlayerSetting} object for a human player.
	 * 
	 * @param isAvailable
	 */
	public PlayerSetting(boolean isAvailable) {
		this(isAvailable, null);
	}

	/**
	 * Creates a new {@link PlayerSetting} object for a human player or an AI player.
	 * 
	 * @param isAvailable
	 * @param aiType
	 *            {@link EWhatToDoAiType} defining the type of the AI player. If <code>null</code>, a human player is assumed.
	 */
	public PlayerSetting(boolean isAvailable, EWhatToDoAiType aiType) {
		this.isAvailable = isAvailable;
		this.isAi = (aiType != null);
		this.aiType = aiType;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public boolean isAi() {
		return isAi;
	}

	public EWhatToDoAiType getAiType() {
		return aiType;
	}

	@Override
	public String toString() {
		return "PlayerSetting(isAvailable: " + isAvailable + ", isAi: " + isAi + ", aiType: " + aiType + ")";
	}

	public static PlayerSetting[] createDefaultSettings(byte playerId, byte maxPlayers) {
		playerId = CommonConstants.ENABLE_AI && CommonConstants.ALL_AI ? -1 : playerId;
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];

		for (byte i = 0; i < playerSettings.length; i++) {
			if (i == playerId) {
				playerSettings[playerId] = new PlayerSetting(true);
			} else {
				EWhatToDoAiType aiType;
				if (CommonConstants.FIXED_AI_TYPE != null) {
					aiType = CommonConstants.FIXED_AI_TYPE;
				} else {
					aiType = EWhatToDoAiType.getTypeByIndex(i);
				}
				playerSettings[i] = new PlayerSetting(CommonConstants.ENABLE_AI, aiType);
			}
		}
		System.out.println("created player settings: " + Arrays.toString(playerSettings));

		return playerSettings;
	}
}
