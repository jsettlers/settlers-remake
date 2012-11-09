package jsettlers.logic.map.random.settings;

import jsettlers.common.player.IPlayerable;

public class PlayerSetting implements IPlayerable {

	private final byte alliance;
	private final byte player;

	public PlayerSetting(byte player, byte alliance) {
		this.player = player;
		this.alliance = alliance;
	}

	public byte getAlliance() {
		return alliance;
	}

	@Override
	public byte getPlayerId() {
		return player;
	}
}
