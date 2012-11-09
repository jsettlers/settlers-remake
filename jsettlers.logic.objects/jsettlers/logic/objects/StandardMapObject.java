package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayerable;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;

public class StandardMapObject extends AbstractHexMapObject implements IPlayerable {
	private static final long serialVersionUID = -7696456932966558840L;

	private final EMapObjectType type;
	private final boolean blocking;
	private final byte player;

	public StandardMapObject(EMapObjectType type, boolean blocking, byte player) {
		this.type = type;
		this.blocking = blocking;
		this.player = player;
	}

	@Override
	public EMapObjectType getObjectType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return blocking;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	public byte getPlayerId() {
		return player;
	}
}
