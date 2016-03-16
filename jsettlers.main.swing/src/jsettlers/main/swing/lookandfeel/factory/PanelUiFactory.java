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