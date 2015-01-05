package jsettlers.graphics.androidui.actions;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.localization.Labels;

public class ConstructBuilding extends ContextAction {
	private EBuildingType type;

	public ConstructBuilding(EBuildingType type) {
		this.type = type;
	}

	@Override
	public String getDesciption() {
		return String.format(Labels.getString("select_to_build"), Labels.getName(type));
	}

}
