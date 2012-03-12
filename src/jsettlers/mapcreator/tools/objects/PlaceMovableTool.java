package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.movable.EMovableType;
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
		return "place movable " + type;
	}

	@Override
	public MapObject getObject() {
	    return new MovableObject(type, player.getActivePlayer());
	}
}
