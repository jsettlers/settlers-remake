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
package jsettlers.graphics.ui;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.Color;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.action.Action;

/**
 * This is a special button that has a label on it.
 * 
 * @author Michael Zangl
 */
public class LabeledButton extends Button {
	private final String text;
	private final Action action;

	private static final OriginalImageLink BUTTON = new OriginalImageLink(EImageLinkType.GUI,
			3, 324, 0);
	private static final OriginalImageLink BUTTON_ACTIVE = new OriginalImageLink(
			EImageLinkType.GUI, 3, 327, 0);

	private final EFontSize size;
	private boolean enabled = true;

	public LabeledButton(String text, Action action, EFontSize size) {
		super(action, BUTTON, BUTTON_ACTIVE, null);
		this.size = size;
		this.text = text;
		this.action = action;
	}

	public LabeledButton(String text, Action action) {
		this(text, action, EFontSize.NORMAL);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);

		TextDrawer drawer = gl.getTextDrawer(size);
		drawer.setColor(Color.WHITE);
		drawer.renderCentered(getPosition().getCenterX(), getPosition().getCenterY(), text);
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return enabled ? action : null;
	}

	/**
	 * Enables or disables the button. A disabled button won't send any actions.
	 * 
	 * @param enabled
	 *            <code>true</code> to enable the button to send actions (default).
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
