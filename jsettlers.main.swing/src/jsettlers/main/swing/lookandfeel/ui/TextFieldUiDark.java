package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.JTextComponent;

/**
 * Text field UI for dark backgrounds
 * 
 * @author Andreas Butti
 */
public class TextFieldUiDark extends MetalTextFieldUI {

	/**
	 * Constructor
	 */
	public TextFieldUiDark() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		TextComponentHelper.installUi((JTextComponent) c);
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
		TextComponentHelper.paintBackground(g, getComponent());
	}

}