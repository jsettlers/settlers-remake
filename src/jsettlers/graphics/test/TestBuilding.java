package jsettlers.graphics.test;

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

public class TestBuilding implements IBuilding {

	private final ShortPoint2D position;
	private float constructed;
	private final EBuildingType image;

	TestBuilding(ShortPoint2D position, EBuildingType image) {
		this.position = position;
		this.image = image;
		this.constructed = (float) Math.random();
	}

	public void increaseConstructed() {
		this.constructed += 0.005f;
	}

	@Override
	public EBuildingType getBuildingType() {
		return this.image;
	}

	@Override
	public ShortPoint2D getPos() {
		return this.position;
	}

	@Override
	public byte getPlayerId() {
		return 0;
	}

	@Override
	public float getStateProgress() {
		return Math.min(this.constructed, 1);
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
