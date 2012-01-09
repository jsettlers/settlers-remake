package jsettlers.mapcreator.tools;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.movable.EMovableType;

public class PlaceMovableTool extends PlaceMapObjectTool {

	private final EMovableType type;

	public PlaceMovableTool(EMovableType type) {
		super(null);
		this.type = type;
	}

	@Override
	public String getName() {
		return "place movable " + type;
	}

	@Override
	public MapObject getObject() {
	    return new MovableObject(type, (byte) 0);
	}
}
