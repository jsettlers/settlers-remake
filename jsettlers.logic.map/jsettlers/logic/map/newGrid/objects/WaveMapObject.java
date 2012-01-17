package jsettlers.logic.map.newGrid.objects;

import jsettlers.common.mapobject.EMapObjectType;

public class WaveMapObject extends AbstractHexMapObject {

	/**
     * 
     */
    private static final long serialVersionUID = -2346002657382329983L;

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.WAVES;
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
		return false;
	}

}
