package jsettlers.lookandfeel.factory;

import jsettlers.lookandfeel.ui.UIDefaults;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.awt.*;

/**
 * ScrollPanel UI factory
 * 
 * @author Andreas Butti
 */
public class ScrollPaneUiFactory {

	/**
	 * make all scrollpanel transparent
	 */
	private static final BasicScrollPaneUI transparentScollPanel = new BasicScrollPaneUI() {
		@Override
		public void installUI(JComponent c) {
			super.installUI(c);

			((JScrollPane) c).getViewport().setOpaque(false);
			((JScrollPane) c).getViewport().setBorder(null);
			c.setOpaque(false);
			c.setBorder(null);
		}

		@Override
		public void paint(Graphics g, JComponent c) {
			g.setColor(UIDefaults.HALFTRANSPARENT_BLACK);
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	};

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		return transparentScollPanel;
	}
}