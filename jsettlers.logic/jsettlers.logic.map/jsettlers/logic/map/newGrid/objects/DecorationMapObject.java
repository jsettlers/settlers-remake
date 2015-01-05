package jsettlers.logic.map.newGrid.objects;

import jsettlers.common.mapobject.EMapObjectType;

public class DecorationMapObject extends AbstractHexMapObject {

	/**
     * 
     */
	private static final long serialVersionUID = -8517316213839905509L;
	private final EMapObjectType type;

	public DecorationMapObject(EMapObjectType type) {
		this.type = type;
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
	public boolean canBeCut() {
		return false;
	}

}
