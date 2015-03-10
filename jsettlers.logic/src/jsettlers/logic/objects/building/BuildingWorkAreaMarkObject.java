package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;

/**
 * map object to visialize the work area of a building to the user.
 * 
 * @author Andreas Eberle
 * 
 */
public final class BuildingWorkAreaMarkObject extends AbstractHexMapObject {
	private static final long serialVersionUID = 8892749217187685868L;

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
