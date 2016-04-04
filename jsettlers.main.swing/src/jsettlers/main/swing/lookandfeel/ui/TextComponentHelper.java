/*******************************************************************************
 * Copyright (c) 2015 - 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.JTextComponent;

/**
 * Helper class for Textfield and Textarea
 * 
 * @author Andreas Butti
 */
public final class TextComponentHelper {

	/**
	 * Selection color
	 */
	private static final Color SELECTION_COLOR = new Color(0x726825);

	/**
	 * Utility class
	 */
	private TextComponentHelper() {
	}

	/**
	 * Install the border
	 * 
	 * @param txt
	 *            Text component
	 */
	public static void installUi(JTextComponent txt) {
		txt.setOpaque(false);

		Insets margin = txt.getMargin();
		if (margin == null) {
			margin = new Insets(5, 5, 5, 5);
		}
		Border marginBorder = BorderFactory.createEmptyBorder(margin.top + 3, margin.left, margin.bottom + 3, margin.right);
		CompoundBorder border = new CompoundBorder(BorderFactory.createLineBorder(Color.WHITE), marginBorder);

		txt.setBorder(border);
		txt.setBackground(Color.BLUE);
		txt.setForeground(Color.WHITE);
		txt.setSelectionColor(SELECTION_COLOR);
	}

	/**
	 * Uninstall the UI
	 * 
	 * @param c
	 *            Component
	 */
	public static void uninstallUI(JComponent c) {
		c.setOpaque(true);
		c.setBorder(null);
	}

	/**
	 * Paint a half transparency background
	 * 
	 * @param g
	 *            Graphics
	 * @param editor
	 *            Text editor
	 */
	public static void paintBackground(Graphics g, JTextComponent editor) {
		g.setColor(UIDefaults.HALFTRANSPARENT_BLACK);
		g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
	}
}
