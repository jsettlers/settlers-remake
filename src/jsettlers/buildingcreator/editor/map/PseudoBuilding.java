package jsettlers.buildingcreator.editor.map;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.ISPosition2D;

public class PseudoBuilding implements IBuilding {
	private final EBuildingType type;
	private final ISPosition2D pos;

	PseudoBuilding(EBuildingType type, ISPosition2D pos) {
		this.type = type;
		this.pos = pos;

	}

	@Override
	public EBuildingType getBuildingType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return 1;
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	@Override
	public byte getPlayer() {
		return 0;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean b) {
	}

	@Override
	public void stopOrStartWorking(boolean stop) {

	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.BUILDING;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public boolean isWorking() {
		// TODO Auto-generated method stub
		return false;
	}

}
