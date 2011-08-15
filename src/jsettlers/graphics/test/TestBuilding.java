package jsettlers.graphics.test;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ISPosition2D;

public class TestBuilding implements IBuilding {

	private final ISPosition2D position;
	private float constructed;
	private final EBuildingType image;
	private int actionImage = 0;

	TestBuilding(ISPosition2D position, EBuildingType image) {
		this.position = position;
		this.image = image;
		this.constructed = (float) Math.random();
	}

	public void increaseConstructed() {
		this.constructed += 0.005f;
	}

	@Override
	public int getActionImgIdx() {
		return this.actionImage++;
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
	public boolean isOccupied() {
		return false;
	}

	@Override
	public byte getPlayer() {
		return 0;
	}

	@Override
	public float getConstructionState() {
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

}
