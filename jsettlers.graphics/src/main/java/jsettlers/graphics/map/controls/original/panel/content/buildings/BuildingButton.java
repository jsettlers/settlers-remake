/*
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package jsettlers.graphics.map.controls.original.panel.content.buildings;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.action.ShowConstructionMarksAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.Label.EHorizontalAlignment;
import jsettlers.graphics.ui.Label.EVerticalAlignment;

/**
 * This is a button to construct a building.
 *
 * @author Michael Zangl
 */
public class BuildingButton extends Button implements UiContentUpdater.IUiContentReceiver<IBuildingCounts> {
	private static final OriginalImageLink activeMark = new OriginalImageLink(EImageLinkType.GUI, 3, 123, 0);
	private static final float ICON_BUTTON_RATIO = 0.85f;

	private final ImageLink buildingImageLink;
	private final EBuildingType buildingType;
	private Image buildingImage = NullImage.getInstance();

	private float lastButtonHeight;
	private float lastButtonWidth;
	private float lastImageHeight;
	private float lastImageWidth;
	private float iconLeft;
	private float iconTop;
	private float iconWidth;
	private float iconHeight;
	private final Label constructedLabel = new Label("", EFontSize.SMALL, EHorizontalAlignment.RIGHT, EVerticalAlignment.TOP);

	public BuildingButton(EBuildingType buildingType) {
		super(new ShowConstructionMarksAction(buildingType), null, null, Labels.getName(buildingType));
		this.buildingType = buildingType;
		buildingImageLink = buildingType.getGuiImage();
		addChild(constructedLabel, 0.05f, 0.05f, .95f, .95f);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		drawBackground(gl);
		if (isActive()) {
			FloatRectangle position = getPosition();
			ImageProvider.getInstance().getImage(activeMark, lastButtonWidth, lastButtonHeight)
					.drawImageAtRect(gl, position.getMinX(), position.getMinY(), position.getMaxX()-position.getMinX(), position.getMaxY()-position.getMinY());
		}
		drawChildren(gl);
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		FloatRectangle position = getPosition();
		float buttonHeight = position.getHeight();
		float buttonWidth = position.getWidth();
		float imageHeight = buildingImage.getHeight();
		float imageWidth = buildingImage.getWidth();
		if (buttonHeight != lastButtonHeight || buttonWidth != lastButtonWidth || imageHeight != lastImageHeight || imageWidth != lastImageWidth) {
			if (buildingImageLink instanceof OriginalImageLink) {
				buildingImage = ImageProvider.getInstance().getImage(buildingImageLink, position.getWidth(), position.getHeight());
			} else {
				buildingImage = ImageProvider.getInstance().getImage(buildingImageLink);
			}
			calculateIconCoords(buttonHeight, buttonWidth, position.getCenterX(), position.getCenterY(), imageHeight, imageWidth);
		}

		buildingImage.drawImageAtRect(gl, iconLeft, iconTop, iconWidth, iconHeight);
	}

	private void calculateIconCoords(float buttonHeight, float buttonWidth, float btnXMid, float btnYMid,
			float imageHeight, float imageWidth) {
		float scaling = Math.min(buttonHeight * ICON_BUTTON_RATIO / imageHeight, buttonWidth * ICON_BUTTON_RATIO / imageWidth);
		iconWidth = imageWidth * scaling;
		iconHeight = imageHeight * scaling;
		iconLeft = btnXMid - iconWidth / 2;
		iconTop = btnYMid - iconHeight / 2;
		lastButtonHeight = buttonHeight;
		lastButtonWidth = buttonWidth;
		lastImageHeight = imageHeight;
		lastImageWidth = imageWidth;
	}

	public EBuildingType getBuildingType() {
		return buildingType;
	}

	@Override
	public void update(IBuildingCounts buildingCounts) {
		if (buildingCounts != null) {
			int constructed = buildingCounts.buildingsInPartition(getBuildingType());
			int inConstruction = buildingCounts.buildingsInPartitionUnderConstruction(getBuildingType());
			String text = constructed + (inConstruction == 0 ? "" : "\n+" + inConstruction);
			constructedLabel.setText(text);
		} else {
			constructedLabel.setText("");
		}
	}
}