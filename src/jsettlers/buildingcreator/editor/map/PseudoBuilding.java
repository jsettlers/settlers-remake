package jsettlers.buildingcreator.editor.map;

import java.util.Collections;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

public class PseudoBuilding implements IBuilding {
	private final EBuildingType type;
	private final ShortPoint2D pos;

	PseudoBuilding(EBuildingType type, ShortPoint2D pos) {
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
	public ShortPoint2D getPos() {
		return pos;
	}

	@Override
	public byte getPlayerId() {
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
		return false;
	}

	@Override
	public boolean isOccupied() {
		return true;
	}

	@Override
	public ESelectionType getSelectionType() {
		return ESelectionType.BUILDING;
	}

	@Override
	public List<IBuildingMaterial> getMaterials() {
		return Collections.emptyList();
	}

	@Override
	public EPriority getPriority() {
		return EPriority.LOW;
	}

}
