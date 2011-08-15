package jsettlers.logic.map.random.grid;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayerable;

public class MovableObject implements MapObject, IPlayerable {

	private final EMovableType type;
	private final byte player;

	public MovableObject(EMovableType type, byte player) {
		this.type = type;
		this.player = player;
	}

	public EMovableType getType() {
		return type;
	}

	@Override
	public byte getPlayer() {
		return player;
	}

}
