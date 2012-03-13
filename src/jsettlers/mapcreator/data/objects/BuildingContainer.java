package jsettlers.mapcreator.data.objects;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.mapcreator.data.LandscapeConstraint;

public class BuildingContainer implements ObjectContainer, IBuilding, LandscapeConstraint, IBuilding.IMill, IBuilding.IOccupyed {

	private final BuildingObject object;
	private final ISPosition2D pos;

	public BuildingContainer(BuildingObject object, ISPosition2D pos) {
		this.object = object;
		this.pos = pos;
	}

	@Override
	public BuildingObject getMapObject() {
		return object;
	}

	@Override
	public RelativePoint[] getProtectedArea() {
		return object.getType().getProtectedTiles();
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.BUILDING;
	}

	@Override
	public float getStateProgress() {
		return 1;
	}

	@Override
	public IMapObject getNextObject() {
		return null;
	}

	@Override
	public byte getPlayer() {
		return object.getPlayer();
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	@Override
	public EBuildingType getBuildingType() {
		return object.getType();
	}

	@Override
	public boolean isWorking() {
		return false;
	}

	@Override
	public ELandscapeType[] getAllowedLandscapes() {
		return object.getType().getGroundtypes();
	}

	@Override
	public boolean needsFlatGround() {
		return object.getType().getGroundtypes()[0] != ELandscapeType.MOUNTAIN;
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return true;
	}

	@Override
	public List<? extends IBuildingOccupyer> getOccupyers() {
		return new LinkedList<IBuildingOccupyer>();
	}

	@Override
	public boolean isRotating() {
		return true;
	}

	@Override
	public ESelectionType getSelectionType() {
		return ESelectionType.BUILDING;
	}

}
