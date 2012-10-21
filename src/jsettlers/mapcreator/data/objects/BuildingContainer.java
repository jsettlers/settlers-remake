package jsettlers.mapcreator.data.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.mapcreator.data.LandscapeConstraint;

public class BuildingContainer implements ObjectContainer, IBuilding, LandscapeConstraint, IBuilding.IMill, IBuilding.IOccupyed {

	private final BuildingObject object;
	private final ShortPoint2D pos;

	public BuildingContainer(BuildingObject object, ShortPoint2D pos) {
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
	public ShortPoint2D getPos() {
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

	@Override
    public List<IBuildingMaterial> getMaterials() {
	    return Collections.emptyList();
    }

	@Override
    public int getMaximumRequestedSoldiers(ESoldierType type) {
	    return 0;
    }

	@Override
    public void setMaximumRequestedSoldiers(ESoldierType type, int max) {	    
    }

	@Override
    public int getCurrentlyCommingSoldiers(ESoldierType type) {
	    return 0;
    }
}
