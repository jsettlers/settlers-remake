package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public class ToggleButtonUiFactory {

	/**
	 * Forward calls
	 */
	public static final ForwardFactory FORWARD = new ForwardFactory();

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(LFStyle.KEY);
		if (LFStyle.TOGGLE_BUTTON_STONE == style) {
			return ButtonUiFactory.STONE_UI_SMALL;
		}
		if (LFStyle.BUTTON_MENU == style) {
			return ButtonUiFactory.MENU_UI;
		}

		return FORWARD.create(c);
	}
}