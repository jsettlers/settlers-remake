package jsettlers.graphics.map.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.utils.Button;

/**
 * This is a button to construct a building.
 * @author michael
 *
 */
public class BuildingButton extends Button {

	private final ImageLink buildingImage;

	public BuildingButton(EBuildingType buildingType) {
		super(new BuildAction(buildingType), null,
		        null, Labels.getName(buildingType));
		buildingImage = buildingType.getGuiImage();
	}
	
	@Override
	public void drawAt(GLDrawContext gl) {
		if (isActive()) {
			//draw border
		}
		
		drawBackground(gl);
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		IntRectangle position = getPosition();
		Image image =
		        getDetailedImage(buildingImage, position.getWidth(),
		                position.getHeight());
		
		gl.glPushMatrix();
		gl.glTranslatef(getPosition().getCenterX(), getPosition().getCenterY(), 0);
		image.drawAt(gl, 0, 0);
		gl.glPopMatrix();
	}
}
