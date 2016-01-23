package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.BorderButton;
import jsettlers.lookandfeel.ui.ButtonUiStone;
import jsettlers.lookandfeel.ui.ButtonUiStoneOriginalBg;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public class ButtonUiFactory {

	/**
	 * Forward calls
	 */
	public static final ForwardFactory FORWARD = new ForwardFactory();

	/**
	 * Instance of the UI, for all Button the same instance
	 */
	protected static final ButtonUiStone STONE_UI_SMALL = new ButtonUiStone(BorderButton.BUTTON_UP_4PX, BorderButton.BUTTON_DOWN_4PX);

	/**
	 * Instance of the UI, for all Button the same instance
	 */
	protected static final ButtonUiStoneOriginalBg MENU_UI = new ButtonUiStoneOriginalBg();

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		Object style = c.getClientProperty(LFStyle.KEY);
		if (LFStyle.BUTTON_STONE == style) {
			return STONE_UI_SMALL;
		}
		if (LFStyle.BUTTON_MENU == style) {
			return MENU_UI;
		}

		return FORWARD.create(c);
	}
}