package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

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
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		return defaultPanel;
	}
}