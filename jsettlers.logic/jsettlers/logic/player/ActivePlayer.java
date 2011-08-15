package jsettlers.logic.player;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

/**
 * This is a class for the active player (the player playing this game).
 * 
 * @author Andreas Eberle
 * 
 */
public class ActivePlayer {
	private static ActivePlayer instance = null;
	private final byte player;
	private final PlayerStatistics statistics;

	private ActivePlayer(byte player) {
		this.player = player;
		this.statistics = new PlayerStatistics();
	}

	public static void instantiate(byte player) {
		if (instance == null) {
			instance = new ActivePlayer(player);
		}
	}

	public static ActivePlayer get() {
		return instance;
	}

	public byte getPlayer() {
		return player;
	}

	public PlayerStatistics getStatistics() {
		return statistics;
	}

	public void reduceOwned(byte player, EMaterialType materialType) {
		statistics.reduceOwned(materialType);
	}

	public void increaseOwned(byte player, EMaterialType materialType) {
		if (this.player == player)
			statistics.increaseOwned(materialType);
	}

	public void reduceOwned(byte player, EMovableType movableType) {
		if (this.player == player)
			statistics.reduceOwned(movableType);
	}

	public void increaseOwned(byte player, EMovableType movableType) {
		statistics.increaseOwned(movableType);
	}

}
