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
package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;

/**
 * Renderer for the error list in the sidebar
 * 
 * @author Andreas Butti
 *
 */
public class ErrorListRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * Default font
	 */
	private final Font defaultFont = getFont();

	/**
	 * Header font
	 */
	private final Font headerFont = getFont().deriveFont(Font.BOLD);

	/**
	 * Header background
	 */
	private final Color BACKGROUND_HEADER = new Color(0xE0E0E0);

	/**
	 * Drop down icon
	 */
	private final Icon errorIcon = new ImageIcon(ErrorListRenderer.class.getResource("error.png"));

	/**
	 * Drop down icon
	 */
	private final Icon fixAvailableIcon = new ImageIcon(ErrorListRenderer.class.getResource("fix-available.png"));

	/**
	 * Drop down icon
	 */
	private final Icon warningIcon = new ImageIcon(ErrorListRenderer.class.getResource("warning.png"));

	/**
	 * Icon class to draw two icons
	 */
	private static class DoubleIcon implements Icon {

		/**
		 * Left icon
		 */
		private Icon icon1;

		/**
		 * Right icon
		 */
		private Icon icon2;

		/**
		 * Constructor
		 */
		public DoubleIcon() {
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			int yOffset = y + (getIconHeight() - icon1.getIconHeight()) / 2;
			icon1.paintIcon(c, g, x, y + yOffset);
			yOffset = y + (getIconHeight() - icon2.getIconHeight()) / 2;
			icon2.paintIcon(c, g, icon1.getIconWidth() + 2 + x, y + yOffset);
		}

		@Override
		public int getIconWidth() {
			return icon1.getIconWidth() + icon2.getIconWidth() + 2;
		}

		@Override
		public int getIconHeight() {
			return Math.max(icon1.getIconHeight(), icon2.getIconHeight());
		}
	}

	/**
	 * Icon with two icons
	 */
	private final DoubleIcon doubleIcon = new DoubleIcon();

	/**
	 * Constructor
	 */
	public ErrorListRenderer() {
		doubleIcon.icon2 = fixAvailableIcon;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		AbstractErrorEntry entry = (AbstractErrorEntry) value;
		setText(entry.getText());

		if (entry instanceof ErrorHeader) {
			setFont(headerFont);
			if (!isSelected) {
				setBackground(BACKGROUND_HEADER);
			}

			applyIcon(((ErrorHeader) entry));
		} else {
			setIcon(null);
			setFont(defaultFont);
		}

		return this;
	}

	/**
	 * Set the icon for the Header
	 * 
	 * @param header
	 *            Error header
	 */
	private void applyIcon(ErrorHeader header) {
		Icon typeIcon;
		if (header.isError()) {
			typeIcon = errorIcon;
		} else {
			typeIcon = warningIcon;
		}

		AbstractFix fix = header.getFix();
		if (fix != null && fix.isFixAvailable()) {
			doubleIcon.icon1 = typeIcon;
			setIcon(doubleIcon);
		} else {
			setIcon(typeIcon);
		}
	}
}