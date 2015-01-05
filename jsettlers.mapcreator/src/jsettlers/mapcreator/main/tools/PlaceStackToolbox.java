package jsettlers.mapcreator.main.tools;

import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;
import jsettlers.mapcreator.tools.objects.PlaceStackTool;

public class PlaceStackToolbox extends ToolBox {

	public PlaceStackToolbox(EMaterialType type, int maxcount) {
		super(String.format(EditorLabels.getLabel("place_stacks"), Labels.getName(type, true)), getToolArray(type, maxcount));
	}

	private static ToolNode[] getToolArray(EMaterialType type, int maxcount) {
		ToolNode[] tools = new ToolNode[maxcount];
		for (int i = 0; i < maxcount; i++) {
			tools[i] = new PlaceStackTool(type, i + 1);
		}
		return tools;
	}

}
