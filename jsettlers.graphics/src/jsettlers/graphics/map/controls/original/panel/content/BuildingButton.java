package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
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
	private static final OriginalImageLink activeMark = new OriginalImageLink(EImageLinkType.GUI, 3, 123, 0);
	private static final float ICON_BTN_RATIO = 0.85f;
	private final ImageLink buildingImage;
	private final EBuildingType buildingType;

	private float lastBtnHeight;
	private float lastBtnWidth;
	private float lastImgHeight;
	private float lastImgWidth;
	private float icon_x0;
	private float icon_xnd;
	private float icon_y0;
	private float icon_ynd;

	public BuildingButton(EBuildingType buildingType) {
		super( new BuildAction(buildingType), null, null, Labels.getName(buildingType) );
		this.buildingType = buildingType;
		buildingImage = buildingType.getGuiImage();
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		drawBackground(gl);
		if (isActive()) {
			gl.color(1, 1, 1, 1);
			FloatRectangle position = getPosition();
			ImageProvider.getInstance().getImage( activeMark, lastBtnWidth, lastBtnHeight )
				.drawImageAtRect( gl, position.getMinX(), position.getMinY(), position.getMaxX(), position.getMaxY() );
		}
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		FloatRectangle position = getPosition();
		Image image; //TODO keep reference to image and only refetch & re-calcCoords if the button has resized?
		if (buildingImage instanceof OriginalImageLink) {
			image = ImageProvider.getInstance().getImage( buildingImage, position.getWidth(), position.getHeight() );
		} else {
			image = ImageProvider.getInstance().getImage(buildingImage);
		}

		float btnHeight = position.getHeight();
		float btnWidth = position.getWidth();
		float imgHeight = image.getHeight();
		float imgWidth = image.getWidth();
		if (btnHeight != lastBtnHeight  ||  btnWidth != lastBtnWidth  ||
			imgHeight != lastImgHeight  ||  imgWidth != lastImgWidth) {
			calculateIconCoords( btnHeight, btnWidth, position.getCenterX(), position.getCenterY(), imgHeight, imgWidth );
		}

		gl.color(1, 1, 1, 1);
		image.drawImageAtRect(gl, icon_x0, icon_y0, icon_xnd, icon_ynd);
	}

	private void calculateIconCoords( float btnHeight, float btnWidth, float btnXMid, float btnYMid,
									 	float imgHeight, float imgWidth )
	{
		float scaling = btnHeight * ICON_BTN_RATIO / imgHeight;
		float widthScaling = btnWidth * ICON_BTN_RATIO / imgWidth;
		if( scaling > widthScaling ){
			scaling = widthScaling;
		}
		float imgScaledWidth = imgWidth * scaling;
		float imgScaledHeight = imgHeight * scaling;
		icon_x0 = btnXMid - imgScaledWidth / 2;
		icon_xnd = btnXMid + imgScaledWidth / 2;
		icon_y0 = btnYMid - imgScaledHeight / 2;
		icon_ynd = btnYMid + imgScaledHeight / 2;
		lastBtnHeight = btnHeight;
		lastBtnWidth = btnWidth;
		lastImgHeight = imgHeight;
		lastImgWidth = imgWidth;
	}

	public EBuildingType getBuildingType() {
		return buildingType;
	}
}
