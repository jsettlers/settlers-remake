package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

public class BuildingWorkAreaMarkObject extends AbstractHexMapObject {

	private final float progress;

	public BuildingWorkAreaMarkObject(float progress) {
		this.progress = progress;
	}

	@Override
	public boolean canBeCut() {
		return false;
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
	public EMapObjectType getObjectType() {
		return EMapObjectType.WORKAREA_MARK;
	}

	@Override
	public float getStateProgress() {
		return progress;
	}

}
