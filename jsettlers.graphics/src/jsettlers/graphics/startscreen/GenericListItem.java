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
package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.UIListItem;

public class GenericListItem implements UIListItem {

	private FloatRectangle position;
	private boolean highlight;
	private final String title;
	private final String description;

	private static final OriginalImageLink SELECT_MARKER = new OriginalImageLink(
			EImageLinkType.GUI, 3, 330, 0);

	public GenericListItem(String title, String description) {
		this.title = title;
		this.description = description;

	}

	@Override
	public void setPosition(FloatRectangle position) {
		this.position = position;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		float minX = position.getMinX();
		float minY = position.getMinY();
		if (highlight) {
			Image image = ImageProvider.getInstance().getImage(SELECT_MARKER);

			gl.color(1, 1, 1, 1);
			image.drawImageAtRect(gl, minX, minY,
					position.getMaxX(), position.getMaxY());
		}
		TextDrawer textdrawer = gl.getTextDrawer(EFontSize.NORMAL);

		float height = position.getHeight();
		float textx = minX + .2f * position.getWidth();
		textdrawer.drawString(textx, minY + .6f * height, title);
		textdrawer.drawString(textx, minY + .1f * height, description);
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return null;
	}

	@Override
	public String getDescription(float relativex, float relativey) {
		return description;
	}

	@Override
	public void setHighlighted(boolean highlight) {
		this.highlight = highlight;
	}

	@Override
	public void onAttach() {
	}

	@Override
	public void onDetach() {
	}

}
