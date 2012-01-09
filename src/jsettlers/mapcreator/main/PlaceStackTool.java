package jsettlers.mapcreator.main;

import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.mapcreator.tools.PlaceMapObjectTool;

public class PlaceStackTool extends PlaceMapObjectTool {

	public PlaceStackTool(EMaterialType type, int count) {
		super(new StackObject(type, count));
    }


}
