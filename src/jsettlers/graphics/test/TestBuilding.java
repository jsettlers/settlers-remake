package jsettlers.graphics.test;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.ISPosition2D;

public class TestBuilding implements IBuilding {

	private final ISPosition2D position;
	private float constructed;
	private final EBuildingType image;

	TestBuilding(ISPosition2D position, EBuildingType image) {
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
	public ISPosition2D getPos() {
		return this.position;
	}

	@Override
	public byte getPlayer() {
		return 0;
	}

	@Override
	public float getStateProgress() {
		return Math.min(this.constructed, 1);
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
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
