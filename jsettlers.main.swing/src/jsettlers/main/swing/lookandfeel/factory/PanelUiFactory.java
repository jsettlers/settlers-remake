/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.lookandfeel.factory;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

import jsettlers.main.swing.lookandfeel.DrawHelper;
import jsettlers.main.swing.lookandfeel.LFStyle;
import jsettlers.main.swing.lookandfeel.ui.UIDefaults;

/**
 * Panel UI factory
 * 
 * @author Andreas Butti
 */
public class PanelUiFactory {

	/**
	 * Transparent panel
	 */
	private static final PanelUI defaultPanel = new PanelUI() {

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			c.setOpaque(false);
		}
	};

	/**
	 * Half transparent dark panel
	 */
	private static final PanelUI darkPanel = new PanelUI() {

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			c.setOpaque(false);
		}

		@Override
		public void paint(java.awt.Graphics g1, JComponent c) {
			Graphics2D g = DrawHelper.antialiasingOn(g1);

			g.setColor(UIDefaults.HALFTRANSPARENT_BLACK);
			int arc = 15;
			g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
		};
	};
	/**
	 * Supports transparent background colors
	 */
	private static final PanelUI drawPanelTransparentBg = new PanelUI() {

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			c.setOpaque(false);
		}

		@Override
		public void paint(java.awt.Graphics g, JComponent c) {
			g.setColor(c.getBackground());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		};
	};

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		if (LFStyle.PANEL_DARK == c.getClientProperty(LFStyle.KEY)) {
			return darkPanel;
		}

		if (LFStyle.PANEL_DRAW_BG_CUSTOM == c.getClientProperty(LFStyle.KEY)) {
			return drawPanelTransparentBg;
		}

		return defaultPanel;
	}
}