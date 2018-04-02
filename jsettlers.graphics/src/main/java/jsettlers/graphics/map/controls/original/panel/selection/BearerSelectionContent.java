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

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.action.ConvertAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIPanel;

/**
 * This is the selection content displayed when bearers are selected.
 * 
 * @author Michael Zangl
 *
 */
public class BearerSelectionContent extends AbstractSelectionContent {
	private final UIPanel panel;
	private final int count;

	public BearerSelectionContent(ISelectionSet selection) {
		panel = new UIPanel();
		count = selection.getMovableCount(EMovableType.BEARER);

		addPioneers(.7f);
		addGeologists(.45f);
		addThieves(.2f);
	}

	private void addPioneers(float bottom) {
		OriginalImageLink imageLink = new OriginalImageLink(EImageLinkType.GUI, 14, 204, 0);

		drawButtongroup(bottom, imageLink, EMovableType.PIONEER);
	}

	private void addThieves(float bottom) {
		OriginalImageLink imageLink = new OriginalImageLink(EImageLinkType.GUI, 14, 183, 0);

		drawButtongroup(bottom, imageLink, EMovableType.THIEF);
	}

	private void addGeologists(float bottom) {
		OriginalImageLink imageLink = new OriginalImageLink(EImageLinkType.GUI, 14, 186, 0);

		drawButtongroup(bottom, imageLink, EMovableType.GEOLOGIST);
	}

	private void drawButtongroup(float bottom, OriginalImageLink imageLink,
			EMovableType type) {
		UIPanel icon = new UIPanel();
		icon.setBackground(imageLink);

		LabeledButton convert1 =
				new LabeledButton(Labels.getString("convert_1_to_" + type),
						new ConvertAction(type, (short) 1));
		LabeledButton convertall =
				new LabeledButton(Labels.getString("convert_all_to_" + type),
						new ConvertAction(type, Short.MAX_VALUE));

		panel.addChild(icon, .1f, bottom, .3f, bottom + .2f);
		panel.addChild(convert1, .3f, bottom + .1f, .9f, bottom + .2f);
		panel.addChild(convertall, .3f, bottom, .9f, bottom + .1f);
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

}
