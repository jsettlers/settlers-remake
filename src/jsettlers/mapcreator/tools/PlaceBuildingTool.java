package jsettlers.mapcreator.tools;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.mapcreator.main.IPlayerSetter;

public class PlaceBuildingTool extends PlaceMapObjectTool {

	private final EBuildingType type;
	private final IPlayerSetter player;

	public PlaceBuildingTool(EBuildingType type, IPlayerSetter player) {
		super(null);
		this.type = type;
		this.player = player;
    }
	
	@Override
	public MapObject getObject() {
		return new BuildingObject(type, player.getActivePlayer());
	}

	@Override
	public String getName() {
		return "place building " + type;
	}

}
