/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.button;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.action.Action;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIPanel;

/**
 * A special material button with a green/red dot and a
 * 
 * @author Michael Zangl
 */
public class MaterialButton extends Button {

	public enum DotColor {
		RED(7),
		GREEN(0),
		YELLOW(3);

		private OriginalImageLink image;

		DotColor(int imageIndex) {
			image = new OriginalImageLink(EImageLinkType.SETTLER, 4, 6, imageIndex);
		}

		public OriginalImageLink getImage() {
			return image;
		}
	}

	private final EMaterialType material;
	private final UIPanel dot = new UIPanel();
	private final UIPanel selected = new UIPanel();

	public MaterialButton(Action action, EMaterialType material) {
		super(action, material.getIcon(), material.getIcon(), Labels.getName(material, false));
		this.material = material;
		setBackground(material.getIcon());
		addChild(dot, .1f, .6f, .4f, .9f);
		addChild(selected, 0, 0, 1, 1);
	}

	public void setDotColor(DotColor color) {
		dot.setBackground(color == null ? null : color.image);
	}

	public EMaterialType getMaterial() {
		return material;
	}

	public void setSelected(boolean selected) {
		this.selected.setBackground(selected ? new OriginalImageLink(EImageLinkType.GUI, 3, 339) : null);
	}
}
