package jsettlers.mapcreator.tools.objects;

import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;

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
		return String.format(EditorLabels.getLabel("stackdescr"), count, Labels.getName(type, count != 1));
	}
}
