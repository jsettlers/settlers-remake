package jsettlers.main.swing.lookandfeel.factory;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import jsettlers.main.swing.lookandfeel.ui.SettlerScrollPanelUi;

/**
 * ScrollPanel UI factory
 * 
 * @author Andreas Butti
 */
public class ScrollPaneUiFactory {

	/**
	 * Create PLAF
	 * 
	 * @param c
	 *            Component which need the UI
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		return new SettlerScrollPanelUi();
	}
}