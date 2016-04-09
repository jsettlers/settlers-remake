package jsettlers.logic.player;

import java.util.Arrays;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;

/**
 * @author codingberlin
 */
public class PlayerSetting {

	private final EPlayerType playerType;
	private final ECivilisation civilisation;
	private final byte teamId;
	private boolean isAvailable;

	/**
	 * Creates a new {@link PlayerSetting} object for a human player.
	 * 
	 * @param isAvailable
	 */
	public PlayerSetting(boolean isAvailable, byte teamId) {
		this(isAvailable, EPlayerType.HUMAN, ECivilisation.ROMAN, teamId);
	}

	/**
	 * Creates a new {@link PlayerSetting} object for a human player or an AI player.
	 *
	 * @param isAvailable
	 * @param playerType
	 *            {@link EPlayerType} defining the type of the AI player. If <code>null</code>, a human player is assumed.
	 */
	public PlayerSetting(boolean isAvailable, EPlayerType playerType, ECivilisation civilisation, byte teamId) {
		this.isAvailable = isAvailable;
		this.playerType = playerType;
		this.civilisation = civilisation;
		this.teamId = teamId;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	public ECivilisation getCivilisation() {
		return civilisation;
	}

	@Override
	public String toString() {
		return "PlayerSetting(isAvailable: " + isAvailable + ", playerType: " + playerType + ")";
	}

	public byte getTeamId() {
		return teamId;
	}

	public static PlayerSetting[] createDefaultSettings(byte playerId, byte maxPlayers) {
		playerId = CommonConstants.ENABLE_AI && CommonConstants.ALL_AI ? -1 : playerId;
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];

		byte offsetToSkipHuman = 0;
		for (byte i = 0; i < playerSettings.length; i++) {
			if (i == playerId) {
				playerSettings[playerId] = new PlayerSetting(true, i);
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
				playerSettings[i] = new PlayerSetting(CommonConstants.ENABLE_AI, aiType, ECivilisation.ROMAN, i);
			}
		}
		System.out.println("created player settings: " + Arrays.toString(playerSettings));

		return playerSettings;
	}
}
