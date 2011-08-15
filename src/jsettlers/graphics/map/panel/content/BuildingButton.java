package jsettlers.graphics.map.panel.content;

import javax.media.opengl.GL2;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.utils.Button;

public class BuildingButton extends Button {

	public BuildingButton(EBuildingType buildingType) {
		super(new BuildAction(buildingType), buildingType.getGuiImage(),
		        buildingType.getGuiImage(), Labels.getName(buildingType));
	}
	
	@Override
	public void drawAt(GL2 gl) {
		if (isActive()) {
			//draw border
		}
	    super.drawAt(gl);
	}

}
