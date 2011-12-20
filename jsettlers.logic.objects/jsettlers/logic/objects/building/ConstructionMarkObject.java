package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;

/**
 * This map objects represent a construction marking used to show the user where he is able to construct a building.<br>
 * 
 * @see EMapObjectType.CONSTRUCTION_MARK
 * 
 * @author Andreas Eberle
 * 
 */
public class ConstructionMarkObject extends AbstractHexMapObject {
	private static final long serialVersionUID = 4420024473109760614L;

	private float constructionValue;

	public ConstructionMarkObject(byte constructionValue) {
		this.setConstructionValue(constructionValue);
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.CONSTRUCTION_MARK;
	}

	@Override
	public float getStateProgress() {
		return constructionValue;
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
	public boolean canBeCut() {
		return false;
	}

	public void setConstructionValue(byte constructionValue) {
		assert constructionValue >= 0 : "construction value must be >= 0";
		this.constructionValue = ((float) constructionValue) / Byte.MAX_VALUE;
	}

}
