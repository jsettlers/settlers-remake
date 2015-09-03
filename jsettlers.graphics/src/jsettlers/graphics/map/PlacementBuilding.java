package jsettlers.graphics.map;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

public class PlacementBuilding implements IBuilding, IBuilding.IMill {
	private final EBuildingType type;

	public PlacementBuilding(EBuildingType type) {
		this.type = type;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.PLACEMENT_BUILDING;
	}

	@Override
	public EBuildingType getBuildingType() {
		return type;
	}

	@Override
	public float getStateProgress() {
		return 1f;
	}

	@Override
	public boolean isRotating() {
		return false;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public byte getPlayerId() {
		return 0;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	@Override
	public ESelectionType getSelectionType() {
		return null;
	}

	@Override
	public ShortPoint2D getPos() {
		return null;
	}

	@Override
	public EPriority getPriority() {
		return null;
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		return null;
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public List<IBuildingMaterial> getMaterials() {
		return null;
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return false;
	}

	@Override
	public IMapObject getMapObject(EMapObjectType type) {
		return type == getObjectType() ? this : null;
	}
}
