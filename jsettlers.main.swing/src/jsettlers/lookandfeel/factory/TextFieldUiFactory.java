package jsettlers.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.TextFieldUiDark;

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