package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
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

	public static int ITEM_WIDTH = 60;
	public static int ITEM_HEIGHT = 36;

	private final EBuildingType buildingType;

	public BuildingButton(EBuildingType buildingType) {
		super(new BuildAction(buildingType), null, null, Labels
		        .getName(buildingType));
		this.buildingType = buildingType;
		buildingImage = buildingType.getGuiImage();
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		if (isActive()) {
			gl.color(1,1,1,1);
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
		FloatRectangle position = getPosition();
		Image image =
		        getDetailedImage(buildingImage, position.getWidth(),
		                position.getHeight());

		float width =
		        (float) image.getWidth() / ITEM_WIDTH
		                * getPosition().getWidth();
		float height =
		        (float) image.getHeight() / ITEM_HEIGHT
		                * getPosition().getHeight();

		float cx = getPosition().getCenterX();
		float cy = getPosition().getCenterY();

		gl.color(1,1,1,1);
		image.drawImageAtRect(gl, cx - width / 2, cy - height / 2, cx + width
		        / 2, cy + height / 2);
	}

	public EBuildingType getBuildingType() {
		return buildingType;
	}
}
