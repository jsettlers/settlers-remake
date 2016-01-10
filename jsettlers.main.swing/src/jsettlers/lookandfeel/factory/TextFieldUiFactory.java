package jsettlers.lookandfeel.factory;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.TextFieldUiDark;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * Text field UI factory
 * 
 * @author Andreas Butti
 */
public class TextFieldUiFactory {

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
		if (LFStyle.TEXT_DEFAULT == c.getClientProperty(LFStyle.KEY)) {
			return new TextFieldUiDark();
		}
		return FORWARD.create(c);
	}
}