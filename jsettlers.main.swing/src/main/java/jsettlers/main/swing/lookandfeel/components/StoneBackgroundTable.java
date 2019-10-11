/*******************************************************************************
 * Copyright (c) 2019
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
package jsettlers.main.swing.lookandfeel.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;

import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.factory.LabelUiFactory;
import jsettlers.main.swing.lookandfeel.ui.UIDefaults;
import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;

public class StoneBackgroundTable extends BasicTableUI {
	/**
	 * Foreground color
	 */
	private final Color foregroundColor;

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("ui_static_info_bg/ui_static-info-bg.png");


	/**
	 * Constructor
	 *
	 * @param foregroundColor
	 *            Foreground color of the Label
	 */
	public StoneBackgroundTable(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setFont(UIDefaults.FONT);
		c.setOpaque(false);

		JTable table = (JTable)c;
		table.setGridColor(Color.WHITE);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable jTable, Object o, boolean b, boolean b1, int i, int i1) {
				JComponent comp = new JLabel(o+"");
				comp.putClientProperty(ELFStyle.KEY, i == 0 ? ELFStyle.LABEL_HEADER : ELFStyle.LABEL_LONG);
				LabelUiFactory.createUI(comp).installUI(comp);
				return comp;
			}
		});
	}
	@Override
	public void paint(Graphics g, JComponent c) {
		// Repeat the graphic as much as needed, the graphic is designed for this, so the start fits to the end of the graphic
		for (int i = 0; i < c.getWidth(); i += backgroundImage.getWidth()) {
			g.drawImage(backgroundImage, i, 0, c);
		}
		super.paint(g, c);
	}
}
