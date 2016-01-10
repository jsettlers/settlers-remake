package jsettlers.lookandfeel.factory;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.ToggleButtonUiStone;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

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
	 * Instance of the UI, for all Button the same instance
	 */
	private static final ToggleButtonUiStone stoneUI = new ToggleButtonUiStone();

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
			return stoneUI;
		}

		return FORWARD.create(c);
	}
}