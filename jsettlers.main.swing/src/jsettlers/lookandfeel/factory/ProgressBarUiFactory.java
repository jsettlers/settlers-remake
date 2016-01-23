package jsettlers.lookandfeel.factory;

import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.ui.SettlerProgressbar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * Button UI factory
 * 
 * @author Andreas Butti
 */
public class ProgressBarUiFactory {

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
		if (LFStyle.PROGRESSBAR_SLIDER == style) {
			return new SettlerProgressbar();
		}

		return FORWARD.create(c);
	}
}