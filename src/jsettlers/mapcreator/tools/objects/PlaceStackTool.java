package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;

public class PlaceStackTool extends PlaceMapObjectTool {

	private final int count;
	private final EMaterialType type;

	public PlaceStackTool(EMaterialType type, int count) {
		super(new StackObject(type, count));
		this.type = type;
		this.count = count;
    }

	@Override
	public String getName() {
	    return "place " + count + " " + type;
	}
}
