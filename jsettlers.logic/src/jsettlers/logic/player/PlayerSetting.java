package jsettlers.logic.player;

import java.util.Arrays;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EPlayerType;

/**
 * @author codingberlin
 */
public class PlayerSetting {

	private final EPlayerType playerType;
	private boolean isAvailable;

	/**
	 * Creates a new {@link PlayerSetting} object for a human player.
	 * 
	 * @param isAvailable
	 */
	public PlayerSetting(boolean isAvailable) {
		this(isAvailable, EPlayerType.HUMAN);
	}

	/**
	 * Creates a new {@link PlayerSetting} object for a human player or an AI player.
	 * 
	 * @param isAvailable
	 * @param playerType
	 *            {@link EPlayerType} defining the type of the AI player. If <code>null</code>, a human player is assumed.
	 */
	public PlayerSetting(boolean isAvailable, EPlayerType playerType) {
		this.isAvailable = isAvailable;
		this.playerType = playerType;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	@Override
	public String toString() {
		return "PlayerSetting(isAvailable: " + isAvailable + ", playerType: " + playerType + ")";
	}

	public static PlayerSetting[] createDefaultSettings(byte playerId, byte maxPlayers) {
		playerId = CommonConstants.ENABLE_AI && CommonConstants.ALL_AI ? -1 : playerId;
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];

		byte offsetToSkipHuman = 0;
		for (byte i = 0; i < playerSettings.length; i++) {
			if (i == playerId) {
				playerSettings[playerId] = new PlayerSetting(true);
			} else {
				EPlayerType aiType;
				if (CommonConstants.FIXED_AI_TYPE != null) {
					aiType = CommonConstants.FIXED_AI_TYPE;
				} else {
					aiType = EPlayerType.getTypeByIndex(i + offsetToSkipHuman);
					if (aiType == EPlayerType.HUMAN) {
						offsetToSkipHuman++;
						aiType = EPlayerType.getTypeByIndex(i + offsetToSkipHuman);
					}
				}
				playerSettings[i] = new PlayerSetting(CommonConstants.ENABLE_AI, aiType);
			}
		}
		System.out.println("created player settings: " + Arrays.toString(playerSettings));

		return playerSettings;
	}
}
