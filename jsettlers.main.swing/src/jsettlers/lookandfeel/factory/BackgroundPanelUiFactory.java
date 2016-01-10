package jsettlers.lookandfeel.factory;

import jsettlers.lookandfeel.ui.BackgroundPanelUi;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * Factory for Background panel UIs
 * 
 * @author Andreas Butti
 */
public class BackgroundPanelUiFactory {

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		return new BackgroundPanelUi();
	}
}
