package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.IPlayerSetter;

public class PlaceMovableTool extends PlaceMapObjectTool {

	private final EMovableType type;
	private final IPlayerSetter player;

	public PlaceMovableTool(EMovableType type, IPlayerSetter player) {
		super(null);
		this.type = type;
		this.player = player;
	}

	@Override
	public String getName() {
		return String.format(EditorLabels.getLabel("movabledescr"), Labels.getName(type));
	}

	@Override
	public MapObject getObject() {
	    return new MovableObject(type, player.getActivePlayer());
	}
}
