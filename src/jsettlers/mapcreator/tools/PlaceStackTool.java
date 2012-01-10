package jsettlers.mapcreator.tools;

import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;

public class PlaceStackTool extends PlaceMapObjectTool {

	public PlaceStackTool(EMaterialType type, int count) {
		super(new StackObject(type, count));
    }


}
