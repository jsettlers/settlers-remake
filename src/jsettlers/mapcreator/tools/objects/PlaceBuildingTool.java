package jsettlers.mapcreator.tools.objects;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
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
		return String.format(EditorLabels.getLabel("buildingdescr"), Labels.getName(type));
	}

}
