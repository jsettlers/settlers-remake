package jsettlers.lookandfeel.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.JTextComponent;
import java.awt.*;

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
		JTextComponent txt = (JTextComponent) c;

		txt.setOpaque(false);

		Insets margin = txt.getMargin();
		Border marginBorder = BorderFactory.createEmptyBorder(margin.top + 3, margin.left, margin.bottom + 3, margin.right);
		CompoundBorder border = new CompoundBorder(BorderFactory.createLineBorder(Color.WHITE), marginBorder);

		txt.setBorder(border);
		txt.setBackground(Color.BLUE);
		txt.setForeground(Color.WHITE);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.setOpaque(true);
		c.setBorder(null);
	}

	@Override
	protected void paintSafely(Graphics g) {
		// this hack is needed to paint a half transparency background
		paintBackground(g);
		super.paintSafely(g);
	}

	@Override
	protected void paintBackground(Graphics g) {
		g.setColor(UIDefaults.HALFTRANSPARENT_BLACK);
		JTextComponent editor = getComponent();
		g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
	}

}