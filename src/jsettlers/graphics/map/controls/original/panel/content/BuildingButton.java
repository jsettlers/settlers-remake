package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.Button;

/**
 * This is a button to construct a building.
 * 
 * @author michael
 */
public class BuildingButton extends Button {

	private final ImageLink buildingImage;

	private final static ImageLink activeMark = new ImageLink(
	        EImageLinkType.GUI, 3, 123, 0);

	public BuildingButton(EBuildingType buildingType) {
		super(new BuildAction(buildingType), null, null, Labels
		        .getName(buildingType));
		buildingImage = buildingType.getGuiImage();
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		if (isActive()) {
			ImageProvider
			        .getInstance()
			        .getImage(activeMark)
			        .drawImageAtRect(gl, getPosition().getMinX(),
			                getPosition().getMinY(), getPosition().getMaxX(),
			                getPosition().getMaxY());
		}

		drawBackground(gl);
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		IntRectangle position = getPosition();
		Image image =
		        getDetailedImage(buildingImage, position.getWidth(),
		                position.getHeight());

		image.drawImageAtRect(gl, getPosition().getMinX(),
                getPosition().getMinY(), getPosition().getMaxX(),
                getPosition().getMaxY());
	}
}
