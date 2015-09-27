package jsettlers.logic.player;

/**
 * @author codingberlin
 */
public class PlayerSetting {

	boolean isAvailable;
	boolean isAi;

	public PlayerSetting(boolean isAvailable, boolean isAi) {
		this.isAvailable = isAvailable;
		this.isAi = isAi;

	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public boolean isAi() {
		return isAi;
	}

	@Override
	public String toString() {
		return "PlayerSetting(isAvailable: " + isAvailable + ", isAi: " + isAi + ")";
	}
}
