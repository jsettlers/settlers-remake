package jsettlers.mapcreator.main;

import jsettlers.common.material.EMaterialType;
import jsettlers.mapcreator.tools.PlaceStackTool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;

public class PlaceStackToolbox extends ToolBox {

	public PlaceStackToolbox(EMaterialType type, int maxcount) {
		super("Place " + type, getToolArray(type, maxcount));
    }

	private static ToolNode[] getToolArray(EMaterialType type, int maxcount) {
	    ToolNode[] tools = new ToolNode[maxcount];
		for (int i = 0; i < maxcount; i++) {
			tools[i] = new PlaceStackTool(type, i + 1);
		}
	    return tools;
    }


}
