package jsettlers.lookandfeel.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextAreaUI;

/**
 * Text field UI for dark backgrounds
 * 
 * @author Andreas Butti
 */
public class TextAreaUiDark extends BasicTextAreaUI {

	/**
	 * Constructor
	 */
	public TextAreaUiDark() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		TextComponentHelper.installUi(c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		TextComponentHelper.uninstallUI(c);
	}

	@Override
	protected void paintSafely(Graphics g) {
		// this hack is needed to paint a half transparency background
		paintBackground(g);
		super.paintSafely(g);
	}

	@Override
	protected void paintBackground(Graphics g) {
		TextComponentHelper.paintBackground(g);
	}

}