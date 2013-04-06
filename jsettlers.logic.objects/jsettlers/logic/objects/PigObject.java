package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;
import synchronic.timer.NetworkTimer;

/**
 * A pig that can be placed on the map. it can be "cut" after some time (that means can be cut return true).
 * 
 * @author michael
 */
public class PigObject extends AbstractHexMapObject {
	private static final long serialVersionUID = -3554691277157393770L;

	/**
	 * Time a pig lives in ms
	 */
	private static final int LIVE_TIME = 30000;

	private final int starttime;

	public PigObject() {
		starttime = NetworkTimer.get().getGameTime();
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.PIG;
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
	public boolean canBeCut() {
		return (NetworkTimer.get().getGameTime() - starttime) > LIVE_TIME;
	}

}
