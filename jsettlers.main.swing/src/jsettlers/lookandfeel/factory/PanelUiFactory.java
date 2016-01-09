package jsettlers.lookandfeel.factory;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

import jsettlers.lookandfeel.DrawHelper;
import jsettlers.lookandfeel.LFStyle;

/**
 * Button UI factory
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

			g.setColor(new Color(0, 0, 0, 160));
			int arc = 15;
			g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
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

		return defaultPanel;
	}
}