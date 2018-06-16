/*******************************************************************************
 * Copyright (c) 2016
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

import java.awt.Dimension;

import javax.swing.JLabel;

import jsettlers.main.swing.lookandfeel.ELFStyle;

/**
 * Panel with background texture and Border
 * 
 * Do not set layout, this class has an integrated layout. You can add two components, first the left, second the right
 * 
 * @author Andreas Butti
 */
public class SplitedBackgroundPanel extends BackgroundPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Title
	 */
	protected JLabel titleLabel = new JLabel("<Title>", JLabel.CENTER);

	/**
	 * Split position
	 */
	private final int splitPosition = 330;

	/**
	 * Constructor
	 */
	public SplitedBackgroundPanel() {
		titleLabel.putClientProperty(ELFStyle.KEY, ELFStyle.LABEL_HEADER);
		add(titleLabel);
	}

	/**
	 * @return Split position
	 */
	public int getSplitPosition() {
		return splitPosition;
	}

	/**
	 * @param splitPosition
	 *            Split position
	 */
	public void setSplitPosition(int splitPosition) {
		int oldSplitPosition = this.splitPosition;
		firePropertyChange("splitPosition", oldSplitPosition, splitPosition);
		if (splitPosition != oldSplitPosition) {
			repaint();
		}
	}

	@Override
	public void doLayout() {
		// Left menu
		if (getComponentCount() >= 2) {
			getComponent(1).setBounds(50, 70, splitPosition - 80, getHeight() - 140);
		}

		// Center contents
		int width = getWidth() - splitPosition - 80;
		if (getComponentCount() >= 3) {
			getComponent(2).setBounds(splitPosition + 30, 80, width, getHeight() - 150);
		}

		// Header
		Dimension preferredLabelSize = titleLabel.getPreferredSize();
		int x = 320 + (width - preferredLabelSize.width) / 2;
		titleLabel.setBounds(x, 40, preferredLabelSize.width, preferredLabelSize.height);
	}

	/**
	 * @return The title label on the right side
	 */
	public JLabel getTitleLabel() {
		return titleLabel;
	}
}
