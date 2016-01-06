package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.PanelUiStoneBackground;

/**
 * Panel UI factory
 * 
 * @author Andreas Butti
 */
public class PanelUiFactory {

	/**
	 * Instance of the UI, for all Panel the same instance
	 */
	private static final PanelUiStoneBackground stoneUI = new PanelUiStoneBackground();

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(LFStyle.KEY);
		if (LFStyle.PANEL_BACKGROUND_STONE == style) {
			return stoneUI;
		}

		return null;
	}
}