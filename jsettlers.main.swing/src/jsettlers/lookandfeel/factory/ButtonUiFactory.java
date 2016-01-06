package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.ButtonUIStone;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public class ButtonUiFactory {

	/**
	 * Instance of the UI, for all Button the same instance
	 */
	private static final ButtonUIStone stoneUI = new ButtonUIStone();

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(LFStyle.KEY);
		if (LFStyle.BUTTON_MENU == style) {
			return stoneUI;
		}

		return null;
	}
}