package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public class ScrollPaneUiFactory {

	/**
	 * make all scrollpanel transparent
	 */
	private static final ScrollPaneUI transparentScollPanel = new ScrollPaneUI() {
		@Override
		public void installUI(JComponent c) {
			super.installUI(c);

			((JScrollPane) c).getViewport().setOpaque(false);
			c.setOpaque(false);
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