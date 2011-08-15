package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;

/**
 * Object that can show any {@link EMapObjectType} on the map for a given time. When the time is finished, it removes itself from the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class SelfDeletingMapObject extends AbstractHexMapObject implements ITimerable, IPlayerable {

	private final EMapObjectType type;
	private final float progressIncrease;

	private float progress = 0;
	private final byte player;
	private final IMapObjectRemovableGrid grid;
	private final ISPosition2D pos;

	/**
	 * Creates and positions this map object on the map.
	 * 
	 * @param pos
	 *            position the map object should be on the grid
	 * @param type
	 *            type to be returned to jsettlers.graphics
	 * @param duration
	 *            duration of the animation in seconds
	 */
	public SelfDeletingMapObject(IMapObjectRemovableGrid grid, ISPosition2D pos, EMapObjectType type, float duration) {
		this(grid, pos, type, duration, (byte) -1);
	}

	public SelfDeletingMapObject(IMapObjectRemovableGrid grid, ISPosition2D pos, EMapObjectType type, float duration, byte player) {
		this.grid = grid;
		this.pos = pos;
		this.player = player;
		this.type = type;
		this.progressIncrease = 1f / (10 * duration);
		Timer100Milli.add(this);
	}

	@Override
	public void timerEvent() {
		progress += progressIncrease;
		if (progress >= 1) {
			kill();
		}
	}

	@Override
	public void kill() {
		Timer100Milli.remove(this);
		grid.removeMapObject(pos, this);
	}

	@Override
	public EMapObjectType getObjectType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return progress;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	public byte getPlayer() {
		return player;
	}

}
