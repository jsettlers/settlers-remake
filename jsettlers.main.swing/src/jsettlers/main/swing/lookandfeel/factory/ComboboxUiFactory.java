package jsettlers.main.swing.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.main.swing.lookandfeel.LFStyle;
import jsettlers.main.swing.lookandfeel.ui.SettlerComboboxUi;

/**
 * Combobox UI factory
 * 
 * @author Andreas Butti
 */
public class ComboboxUiFactory {

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
		if (LFStyle.COMBOBOX == c.getClientProperty(LFStyle.KEY)) {
			return new SettlerComboboxUi();
		}
		return FORWARD.create(c);
	}
}