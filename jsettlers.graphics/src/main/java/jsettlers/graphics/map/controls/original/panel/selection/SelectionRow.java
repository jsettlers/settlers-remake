/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.selection;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.Color;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageFlavor;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.ui.UIPanel;

public class SelectionRow extends UIPanel {

	private final Image movableImage;
	private String localizedLabelName;
	private final int count;

	/**
	 * Creates a new row in the selection view.
	 *
	 * @param movableImage
	 * @param localizedLabelName
	 * @param selectionCount how many are selected.
	 */
	public SelectionRow(Image movableImage, String localizedLabelName, int selectionCount) {
		this.count = selectionCount;
		this.movableImage = movableImage;
		this.localizedLabelName = localizedLabelName;
	}

	static SelectionRow createFromMovableType(EMovableType type, int count) {
		SettlerImageFlavor flavor = new SettlerImageFlavor(type, EMovableAction.NO_ACTION, EMaterialType.NO_MATERIAL, EDirection.SOUTH_EAST);
		return new SelectionRow(SettlerImageMap.getInstance().getImageForSettler(flavor, 0.0f), Labels.getName(type), count);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		float width = getPosition().getWidth();

		Color color = getColor();
		float bottomY = getPosition().getMinY() + getPosition().getHeight() / 4;
		float left = getPosition().getMinX();
		float imageX = left + width / 20;
		movableImage.drawAt(gl, imageX, bottomY, color);

		TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);
		drawer.drawString(left + width / 5, getPosition().getMinY() + getPosition().getHeight() * .75f, "" + count);
		drawer.drawString(left + width / 5, bottomY, localizedLabelName);
	}

	private Color getColor() {
		return count != 0 ? Color.RED : Color.BLACK;
	}
}
